package org.girlscouts.vtk.replication;

import com.day.cq.replication.ReplicationAction;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationEvent;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.ReplicationReceiver;
import com.day.cq.replication.impl.content.durbo.DurboImportConfigurationProvider;
import com.day.cq.replication.impl.content.durbo.DurboImportResult;
import com.day.cq.replication.impl.content.durbo.DurboImporter;
import com.day.jcr.vault.fs.io.ImportOptions;
import com.day.jcr.vault.packaging.JcrPackage;
import com.day.jcr.vault.packaging.JcrPackageManager;
import com.day.jcr.vault.packaging.Packaging;
import com.day.jcr.vault.util.DefaultProgressListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeDefinition;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* Girl Scouts Customization BEGIN */
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Calendar;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Random;
import org.girlscouts.vtk.helpers.TroopHashGenerator;
import com.day.cq.replication.impl.content.durbo.DurboImportConfiguration;
/* Girl Scouts Customization END */

@Component(metatype=true, immediate=true)
@Service(VTKReplicationReceiver.class)
public class ReplicationReceiverImpl
  implements VTKReplicationReceiver
{
  private static final Logger log = LoggerFactory.getLogger(ReplicationReceiverImpl.class);
  
  /* Girl Scouts Customization BEGIN */
  private static final Pattern TROOP_PATTERN = Pattern.compile("/vtk[0-9]*/[0-9]+/troops/([^/]+)");
  private static final Pattern COUNCILINFO_PATTERN = Pattern.compile("/vtk[0-9]*/[0-9]+/councilInfo/.*");
  
  @Reference
  private VTKDataCacheInvalidator invalidator;
  @Reference
  private TroopHashGenerator troopHashGenerator;
  @Reference
  private ReplicationReceiverFilter replicationReceiverFilter;
  /* Girl Scouts Customization END */

  @Property(longValue={1048576L})
  public static final String OSGI_PROP_TMPFILE_THRESHOLD = "receiver.tmpfile.threshold";
  public static final int DEFAULT_SAVE_EVERY_HOW_MANY = 1000;

  /* Girl Scouts Customization BEGIN */
//  @Reference
//  private Packaging pkgSvc;
  /* Girl Scouts Customization END */

  @Reference
  private EventAdmin eventAdmin;

  /* Girl Scouts Customization BEGIN */
//  @Reference
//  private DurboImportConfigurationProvider durboImportConfigurationProvider;
  /* Girl Scouts Customization END */
  private long tmpfileThreshold;
  private DurboImporter durboImporter;

  public ReplicationReceiverImpl() { 
  /* Girl Scouts Customization BEGIN */
	  //this.pkgSvc = null; 
  /* Girl Scouts Customization END */
  }


  @Activate
  protected void activate(Map<String, Object> props)
  {
    /* Girl Scouts Customization BEGIN */
    //update(props);
   /*
	DurboImportConfiguration conf = new DurboImportConfiguration(null, null, new ArrayList<String>(), false, -1);
    this.durboImporter = new DurboImporter(conf);
    this.tmpfileThreshold = 100000L;
    if (log.isInfoEnabled()) {
	  log.info("Receiver started. threshold set to {}", Long.valueOf(this.tmpfileThreshold));
    }
    this.durboImporter.setTempFileThreshold(this.tmpfileThreshold);
    */
    /* Girl Scouts Customization END */
  }

  /* Girl Scouts Customization BEGIN */
//  @Modified
//  protected void modified(Map<String, Object> props) {
//     update(props);
//  }
//
//  private void update(Map<String, Object> props) {
//    this.durboImporter = new DurboImporter(this.durboImportConfigurationProvider.getConfiguartion());
//    Object thresholdValue = props.get("receiver.tmpfile.threshold");
//    this.tmpfileThreshold = Long.valueOf(thresholdValue != null ? thresholdValue.toString() : "100000").longValue();
//    if (log.isInfoEnabled()) {
//      log.info("Receiver started. threshold set to {}", Long.valueOf(this.tmpfileThreshold));
//    }
//    this.durboImporter.setTempFileThreshold(this.tmpfileThreshold);
//  }
  /* Girl Scouts Customization END */

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
    /* Girl Scouts Customization BEGIN */
    String path = action.getPath();

    // Drop the node if the filter rejects it
    if (!replicationReceiverFilter.accept(path)) {
    	return;
    }
    
    // Invalidate cache if it is troop data
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
      
    // Invalidate the entire /vtk-data cache if council info changed.
    if (COUNCILINFO_PATTERN.matcher(path).matches()) {
      invalidator.addPath(troopHashGenerator.getBase());
    }
    /* Girl Scouts Customization END */

    /* Girl Scouts Customization BEGIN */
    try {
    /* Girl Scouts Customization END */
      Node receivedNode = null;
      if (action.getType() == ReplicationActionType.ACTIVATE)
      {
        DurboImportResult importResult = this.durboImporter.createNode(session, action.getPath(), in, size, binaryLess);
        receivedNode = importResult.getCreatedNode();

        List failedPaths = importResult.getFailedPaths();
        if ((failedPaths != null) && (failedPaths.size() > 0)) {
          writeFailedPaths(failedPaths, writer);
        }
      }
      new ReceiveListener(session, action, writer, install).onReceived(receivedNode);

    /* Girl Scouts Customization BEGIN */
    } catch (Exception e) {
      // Catch all exceptions here to prevent vtk replication queue from blocking.
      // Then log this path to a special log node
      log.error("VTK receiver exception. Trying to save a log node into /var/vtk-replication/log. Original message: " + e.getMessage(), e);
      String dateString = (new SimpleDateFormat("yyyy/MM/dd")).format(Calendar.getInstance().getTime());
      try {
        String logPath = "/var/vtk-replication/log/" + dateString;
        // create log node and intermediate nodes
        Node dateNode = session.getNode("/");
        String[] pathSegments = logPath.split("/");
        for (int i = 1; i < pathSegments.length; i++) {
          String name = pathSegments[i];
          if (!dateNode.hasNode(name)) {
            dateNode = dateNode.addNode(name, "nt:unstructured");
          } else {
            dateNode = dateNode.getNode(name);
          }
        }
            
        String nodeName = Long.toString(Calendar.getInstance().getTime().getTime()) + "-" +
          Integer.toString((int)(100 + (new Random().nextFloat() * 900)));
        Node logNode = dateNode.addNode(nodeName, "nt:unstructured");
        logNode.setProperty("errorType", e.getClass().getName());
        logNode.setProperty("timestamp", Calendar.getInstance());
        logNode.setProperty("path", action.getPath());
        logNode.setProperty("msg", e.getMessage());
        session.save();
      } catch (Exception e1) {
        log.error("Even there is error trying to save the error log node. " + e1.getMessage());
      }	
    }

    // Comment out so flush agent won't be triggerred for /vtk<year> since it is not cached.
    //Event replicationEvent = new ReplicationEvent(action).toNonDistributableEvent();
    //this.eventAdmin.postEvent(replicationEvent);
    /* Girl Scouts Customization END */
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

  /* Girl Scouts Customization BEGIN */
//  protected void bindPkgSvc(Packaging paramPackaging)
//  {
//    this.pkgSvc = paramPackaging;
//  }
//
//  protected void unbindPkgSvc(Packaging paramPackaging)
//  {
//    if (this.pkgSvc == paramPackaging)
//      this.pkgSvc = null;
//  }
  /* Girl Scouts Customization END */

  protected void bindEventAdmin(EventAdmin paramEventAdmin)
  {
    this.eventAdmin = paramEventAdmin;
  }

  protected void unbindEventAdmin(EventAdmin paramEventAdmin)
  {
    if (this.eventAdmin == paramEventAdmin)
      this.eventAdmin = null;
  }

  /* Girl Scouts Customization BEGIN */
//  protected void bindDurboImportConfigurationProvider(DurboImportConfigurationProvider paramDurboImportConfigurationProvider)
//  {
//    this.durboImportConfigurationProvider = paramDurboImportConfigurationProvider;
//  }
//
//  protected void unbindDurboImportConfigurationProvider(DurboImportConfigurationProvider paramDurboImportConfigurationProvider)
//  {
//    if (this.durboImportConfigurationProvider == paramDurboImportConfigurationProvider)
//      this.durboImportConfigurationProvider = null;
//  }
  /* Girl Scouts Customization END */

  private final class ReceiveListener
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

      /* Girl Scouts Customization BEGIN */
      // Do nothing. No need for package unwrapping.
//      String path = null;
//      try {
//        path = node.getPath();
//      } catch (Exception e) {
//        ReplicationReceiverImpl.log.error("Error while retrieving path of node.", e);
//      }
//      try
//      {
//        if ((this.install) && (path != null) && (path.startsWith("/etc/packages/")) && (node.isNodeType("{http://www.jcp.org/jcr/nt/1.0}file")))
//        {
//          this.writer.write("Content package received at " + path + ". Starting import.\n");
//          JcrPackageManager packMgr = ReplicationReceiverImpl.this.pkgSvc.getPackageManager(node.getSession());
//          JcrPackage pack = packMgr.open(node);
//          ImportOptions opts = new ImportOptions();
//          opts.setListener(new DefaultProgressListener(new PrintWriter(this.writer)));
//          pack.extract(opts);
//        }
//      } catch (Exception e) {
//        ReplicationReceiverImpl.log.error("Error while unpacking package at " + path, e);
//      }
      /* Girl Scouts Customization END */
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