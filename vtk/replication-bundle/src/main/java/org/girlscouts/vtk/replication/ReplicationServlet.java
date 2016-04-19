package org.girlscouts.vtk.replication;

import com.day.cq.replication.AccessDeniedException;
import com.day.cq.replication.Outbox;
import com.day.cq.replication.OutboxManager;
import com.day.cq.replication.ReplicationAction;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.ReplicationReceiver;
import com.day.cq.replication.impl.OutboxImpl;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Calendar;
import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.NullOutputStream;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.util.ISO8601;
import org.apache.jackrabbit.util.Text;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* Girl Scouts Customization BEGIN */
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.felix.scr.annotations.Activate;
import org.girlscouts.vtk.modifiedcheck.ModifiedChecker;
/* Girl Scouts Customization END*/

@Component(metatype=false)
@Service({Servlet.class})
/* Girl Scouts Customization BEGIN */
/* @Property(name="sling.servlet.paths", value={"/bin/receive"}) */
@Property(name="sling.servlet.paths", value={"/bin/vtk-receive"})
/* Girl Scouts Customization END*/
public class ReplicationServlet extends SlingAllMethodsServlet
{
  private final Logger logger;
  private static final String NO_INSTALL = "noinstall";
  private static final String PN_TIMELINE = "timeline";
  private static final String PN_OUTBOX = "outbox";
  private static final String PN_SINK = "sink";
  

  @Reference
  protected OutboxManager outboxManager;

  /* Girl Scouts Customization BEGIN */
  @Reference
  //protected ReplicationReceiver receiver;
  protected VTKReplicationReceiver receiver;
  private static final Pattern TROOP_PATTERN = Pattern.compile("(/vtk[0-9]*/[0-9]+/troops/[^/]+/)");
  
  private Session session;
  
  @Reference
  protected ReplicationManager replicationManager;
  
  @Reference
  protected ModifiedChecker modifiedChecker;
  
  @Activate
  protected void activete() {
      this.session = replicationManager.getSession();
  }
  /* Girl Scouts Customization END */

  public ReplicationServlet()
  {
    this.logger = LoggerFactory.getLogger(getClass());
  }

  protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
    throws ServletException, IOException
  {
    response.setContentType("text/plain");
/* In Koo removed incompatible method for AEM 6.1 upgrade
    response.setCharacterEncoding("utf-8");
*/
    try
    {
      ReplicationActionType actionType = ReplicationActionType.fromName(request.getHeader("Action"));
      if (actionType == null) {
        throw new ReplicationException("Illegal action: " + request.getHeader("Action"));
      }

      String path = request.getHeader("Path");
      if ((path == null) || (path.length() == 0)) {
        throw new ReplicationException("No replication path.");
      }
      path = Text.unescape(path);
      
      /* Girl Scouts Customization BEGIN */
      // Notify the modifiedChecker that this node has been modified externally.
      // sessionId == null because it is from another server.
      modifiedChecker.setModified("X", path);
      
      Matcher matcher = TROOP_PATTERN.matcher(path);
      if (matcher.find()) {
          String yearPlanPath = matcher.group(1) + "yearPlan";
          modifiedChecker.setModified("X", yearPlanPath);
      }
      /* Girl Scouts Customization END */

      long start = System.currentTimeMillis();
      if ("true".equals(request.getParameter("sink"))) {
        this.logger.info("Sinking replication {} of {}", actionType, path);
        InputStream in = request.getInputStream();
        IOUtils.copy(in, new NullOutputStream());
        response.getWriter().print("ReplicationAction " + actionType + " ok.");
      }
      else {
        /* Girl Scouts Customization START */
    	// In AEM 5.6, We used to use this.session instead to prevent event avalanche
    	// However, this is not applicable in AEM 6 any more since it adopts a new saving model.
    	// Thus, we put it back.
        Session session = (Session)request.getResourceResolver().adaptTo(Session.class);
        /* Girl Scouts Customization END */
        String noInstall = request.getParameter("noinstall");
        boolean install = true;

        if ((noInstall != null) && (!"".equals(noInstall))) {
          try {
            install = !Boolean.valueOf(noInstall).booleanValue();
          }
          catch (Exception e) {
            this.logger.warn("Problem parsing {} parameter value : {}", new Object[] { "noinstall", noInstall });
          }

        }

        String binaryLessValue = request.getParameter("binaryless");
        boolean binaryLess = false;
        if ((binaryLessValue != null) && (!"".equals(binaryLessValue))) {
          try {
            binaryLess = Boolean.valueOf(binaryLessValue).booleanValue();
          }
          catch (Exception e) {
            this.logger.warn("Problem parsing {} parameter value : {}", new Object[] { "binaryless", Boolean.valueOf(binaryLess) });
          }
        }

        Writer out = response.getWriter();
        InputStream is = request.getInputStream();

        if (path.equals("batch:")) {
          this.logger.info("Processing batch replication");
          StringBuilder sb = new StringBuilder();

          byte[] lengthBytes = readByteArray(is, 8);
          long pathInfoSize = ByteBuffer.wrap(lengthBytes).order(ByteOrder.BIG_ENDIAN).getLong();
          if (pathInfoSize > 2147483647L) {
            throw new IOException("Can't process pathInfo larger than Integer.MAX_VALUE!");
          }
          byte[] pathInfoBytes = readByteArray(is, (int)pathInfoSize);

          String[] paths = new String(pathInfoBytes, "UTF-8").split(":");
          this.logger.debug("Received batch replication {}", Arrays.toString(paths));
          int index = 0;
          while (index < paths.length) {
            String p = decode(paths[index]);
            ReplicationActionType rType = ReplicationActionType.fromName(paths[(index + 1)]);
            if (index > 0) {
              sb.append(", ");
            }
            sb.append(p);
            sb.append("(");
            sb.append(rType);
            sb.append(")");
            index += 2;

            byte[] sizeBytes = readByteArray(is, 8);
            long size = ByteBuffer.wrap(sizeBytes).order(ByteOrder.BIG_ENDIAN).getLong();

            this.logger.info("Processing replication: {}:{}, size: {}", new Object[] { rType, p, Long.valueOf(size) });
            ReplicationAction action = new ReplicationAction(rType, p);
            this.receiver.receive(session, action, new LimitInputStream(is, size), size, out, install, binaryLess);
          }

          IOUtils.closeQuietly(is);
          path = sb.toString();
        } else {
          ReplicationAction action = new ReplicationAction(actionType, path);
          /* Girl Scouts Customization START */
          // Explicitly use this.session to prevent event avalanche.
          // Not applicable in AEM 6 any more. Revert back.
          //this.receiver.receive(this.session, action, is, request.getContentLength(), out, install, binaryLess);
          this.receiver.receive(session, action, is, request.getContentLength(), out, install, binaryLess);
          /* Girl Scouts Customization START */
        }

      }

      long end = System.currentTimeMillis();
      this.logger.info("Processed replication action in {}ms: {} of {}", new Object[] { Long.valueOf(end - start), actionType, path });
    } catch (Exception e) {
      response.setStatus(400);
      this.logger.error("Error during replication: " + e.getMessage(), e);
      response.getWriter().print("error: " + e.toString());
    }
  }

  private byte[] readByteArray(InputStream is, int size) throws IOException {
    byte[] bytes = new byte[size];
    int read = 0;
    while (read < size) {
      read += is.read(bytes, read, size - read);
    }
    return bytes;
  }

  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
    throws ServletException, IOException
  {
    ReplicationActionType action = ReplicationActionType.fromName(request.getHeader("Action"));
    if (action == ReplicationActionType.TEST) {
      response.setStatus(200);
      response.setContentType("text/plain");
/* In Koo removed incompatible method for AEM 6.1 upgrade
      response.setCharacterEncoding("utf-8");
*/
      response.getWriter().println("ok");
      response.flushBuffer();
      return;
    }

    Session session = (Session)request.getResourceResolver().adaptTo(Session.class);
    String outboxName = request.getParameter("outbox");
    try {
      Calendar timeline = null;
      String timelineS = request.getParameter("timeline");
      if (timelineS != null) {
        timeline = ISO8601.parse(timelineS);
      }

      Outbox outbox = (outboxName == null) || (outboxName.length() == 0) ? this.outboxManager.getDefaultOutbox(session) : this.outboxManager.getOutbox(session, outboxName, false);

      response.setContentType("application/octet-stream");
      if (outbox == null) {
        this.logger.warn("Outbox '{}' does not exist. Either never created or wrongly configured", outboxName);
        OutboxImpl.writeOutbox(null, response.getOutputStream());
      } else {
        outbox.fetch(timeline, response.getOutputStream());
      }
    } catch (AccessDeniedException e) {
      response.setStatus(403);
    } catch (ReplicationException e) {
      response.setStatus(400);
      this.logger.error("Error while fetching outbox: " + e.getMessage(), e);
    }
  }

  private String decode(String value)
  {
    try
    {
      return URLDecoder.decode(value, "UTF-8");
    }
    catch (UnsupportedEncodingException uee) {
    }
    return value;
  }

  protected void bindOutboxManager(OutboxManager paramOutboxManager)
  {
    this.outboxManager = paramOutboxManager;
  }

  protected void unbindOutboxManager(OutboxManager paramOutboxManager)
  {
    if (this.outboxManager == paramOutboxManager)
      this.outboxManager = null;
  }

  /* Girl Scouts Customization BEGIN */
  //protected void bindReceiver(ReplicationReceiver paramReplicationReceiver)
  protected void bindReceiver(VTKReplicationReceiver paramReplicationReceiver)
  /* Girl Scouts Customization END */
  {
    this.receiver = paramReplicationReceiver;
  }

  protected void unbindReceiver(ReplicationReceiver paramReplicationReceiver)
  {
    if (this.receiver == paramReplicationReceiver)
      this.receiver = null;
  }

  public final class LimitInputStream extends FilterInputStream
  {
    private long available;

    public LimitInputStream(InputStream in, long limit)
    {
      super(in);
      this.available = limit;
    }

    public int available() throws IOException
    {
      return (int)Math.min(this.in.available(), this.available);
    }

    public int read() throws IOException
    {
      if (this.available == 0L) {
        return -1;
      }

      int result = this.in.read();
      if (result != -1) {
        this.available -= 1L;
      }
      return result;
    }

    public int read(byte[] b, int off, int len)
      throws IOException
    {
      if (this.available == 0L) {
        return -1;
      }

      int readLen = (int)Math.min(len, this.available);
      int result = this.in.read(b, off, readLen);
      if (result != -1) {
        this.available -= result;
      }
      return result;
    }

    public void close()
      throws IOException
    {
    }
  }
}
