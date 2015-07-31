package org.girlscouts.vtk.replication;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeDefinition;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.OsgiUtil;
import org.girlscouts.vtk.helpers.TroopHashGenerator;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.replication.ReplicationAction;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.impl.content.durbo.DurboImportResult;
import com.day.cq.replication.impl.content.durbo.DurboImporter;

@Component(metatype=true, immediate=true)
@Service(VTKReplicationReceiver.class)
public class ReplicationReceiverImpl
  implements VTKReplicationReceiver
{
  private static final Logger log = LoggerFactory.getLogger(ReplicationReceiverImpl.class);
  private static final Pattern TROOP_PATTERN = Pattern.compile("/vtk[0-9]*/[0-9]+/troops/([^/]+)");

  @Property(longValue=1048576L)
  public static final String OSGI_PROP_TMPFILE_THRESHOLD = "receiver.tmpfile.threshold";
  public static final int DEFAULT_SAVE_EVERY_HOW_MANY = 1000;

  @Reference
  private EventAdmin eventAdmin;
  private long tmpfileThreshold;
  private DurboImporter durboImporter;
  @Reference
  private VTKDataCacheInvalidator invalidator;
  @Reference
  private TroopHashGenerator troopHashGenerator;

  @Activate
  protected void activate(Map<String, Object> props)
  {
    this.durboImporter = new DurboImporter();
    this.tmpfileThreshold = OsgiUtil.toLong(props.get("receiver.tmpfile.threshold"), 1048576L);
    log.info("Receiver started. threshold set to {}", Long.valueOf(this.tmpfileThreshold));
    this.durboImporter.setTempFileThreshold(this.tmpfileThreshold);
  }

  public void receive(Session session, ReplicationAction action, InputStream in, long size, Writer writer)
    throws ReplicationException, IOException
  {
    receive(session, action, in, size, writer, true);
  }

  public void receive(Session session, ReplicationAction action, InputStream in, long size, Writer writer, boolean install)
    throws IOException, ReplicationException
  {
    receive(session, action, in, size, writer, true, false);
  }

  public void receive(Session session, ReplicationAction action, InputStream in, long size, Writer writer, boolean install, boolean binaryLess)
    throws ReplicationException, IOException
  {
    Node receivedNode = null;
    if (action.getType() == ReplicationActionType.ACTIVATE)
    {
      // Invalidate cache if it is troop data
      String path = action.getPath();
      Matcher troopMatcher = TROOP_PATTERN.matcher(path);
      String affectedTroop = null;
      while (troopMatcher.find()) {
          affectedTroop = troopMatcher.group(1);
          log.debug("Affected Troop found: " + affectedTroop);
      }
      if (affectedTroop != null) {
          String troopPath = troopHashGenerator.getPath(affectedTroop);
          log.debug("Invalidate troop: " + troopPath);
          invalidator.addPath(troopPath);
      }
      
      DurboImportResult importResult = this.durboImporter.createNode(session, action.getPath(), in, size, binaryLess);
      receivedNode = importResult.getCreatedNode();

      List failedPaths = importResult.getFailedPaths();
      if ((failedPaths != null) && (failedPaths.size() > 0)) {
        writeFailedPaths(failedPaths, writer);
      }
    }

    new ReceiveListener(session, action, writer, install).onReceived(receivedNode);

    // Girl Scouts customization: do not send notifications.
    //Event replicationEvent = new ReplicationEvent(action).toNonDistributableEvent();
    //this.eventAdmin.postEvent(replicationEvent);
  }

  private void writeFailedPaths(List<String> failedPaths, Writer writer) throws IOException {
    writer.write("FAILED PATHS START\n");
    for (String failingPath : failedPaths) {
      writer.write(failingPath);
      writer.write("\n");
    }
    writer.write("FAILED PATHS END\n");
  }

  private int removeRecursive(Node n)
    throws RepositoryException
  {
    String path = n.getPath();
    int result = remove(n, n.getPath(), 0);
    log.info("removeRecursive({}) done: {} nodes deleted, saving...", path, Integer.valueOf(result));
    n.getSession().save();
    return result;
  }

  private int remove(Node n, String startPath, int deletedCount)
    throws RepositoryException
  {
    NodeIterator ni = n.getNodes();
    while (ni.hasNext()) {
      deletedCount = remove(ni.nextNode(), startPath, deletedCount);
    }

    NodeDefinition def = n.getDefinition();
    if ((!def.isProtected()) && (!def.isMandatory())) {
      n.remove();
      deletedCount++;
    }

    if (deletedCount % 1000 == 0) {
      log.info("removeRecursive({}) in progress: {} nodes deleted, saving...", startPath, Integer.valueOf(deletedCount));
      n.getSession().save();
    }

    return deletedCount;
  }

  protected void bindEventAdmin(EventAdmin paramEventAdmin)
  {
    this.eventAdmin = paramEventAdmin;
  }

  protected void unbindEventAdmin(EventAdmin paramEventAdmin)
  {
    if (this.eventAdmin == paramEventAdmin)
      this.eventAdmin = null;
  }

  private class ReceiveListener
  {
    private final Session session;
    private final ReplicationAction action;
    private final Writer writer;
    private final boolean install;

    private ReceiveListener(Session session, ReplicationAction action, Writer writer, boolean install)
    {
      this.session = session;
      this.action = action;
      this.writer = writer;
      this.install = install;
    }

    private void onReceived(Node node)
      throws ReplicationException, IOException
    {
      try
      {
        ReplicationActionType type = this.action.getType();
        switch (type) {
        case ACTIVATE:
          onActivate(node);
          break;
        case DELETE:
          onDelete();
          break;
        case DEACTIVATE:
          onDeactivate();
          break;
        case TEST:
          onTest();
          break;
        default:
          throw new ReplicationException("Unknown ReplicationActionType: " + type);
        }

        if (this.writer != null)
          this.writer.write("ReplicationAction " + type.toString() + " ok.");
      }
      catch (ReplicationException e)
      {
        ReplicationReceiverImpl.log.error("Unable to receive replication.", e);
        throw e;
      }
    }

    private void onActivate(Node node)
      throws ReplicationException
    {
        // Does nothing. No need for package unwrapping.
    }

    private void onDelete()
      throws ReplicationException
    {
      try
      {
        String path = this.action.getPath();
        remove(path);
      } catch (RepositoryException e) {
        throw new ReplicationException(e);
      }
    }

    private void onDeactivate()
      throws ReplicationException
    {
      try
      {
        String path = this.action.getPath();
        remove(path);
      } catch (RepositoryException e) {
        throw new ReplicationException(e);
      }
    }

    private void remove(String path) throws RepositoryException {
      if (this.session.itemExists(path)) {
        Item item = this.session.getItem(path);
        if ((item instanceof Node)) {
          ReplicationReceiverImpl.this.removeRecursive((Node)item);
        } else {
          this.session.getItem(path).remove();
          this.session.save();
        }
      }
    }

    private void onTest()
    {
    }
  }
}