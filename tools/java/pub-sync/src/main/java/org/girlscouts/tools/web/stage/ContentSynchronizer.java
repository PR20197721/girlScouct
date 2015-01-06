package org.girlscouts.tools.web.stage;

import java.text.ParseException;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.version.Version;
import javax.jcr.version.VersionManager;

import org.apache.jackrabbit.commons.JcrUtils;

import com.day.cq.commons.jcr.JcrUtil;

// Params: src_server src_username src_password dst_server dst_username dst_password
// Synchronize src and dst repo
// server example: http://localhost:4502/crx/server/

public class ContentSynchronizer 
{
    public static void main(String[] args)
    {
        if (args.length < 6) {
            System.out.println("Stage Updater: adjust links in stage");
            System.out.println("Params: src_server src_username src_password dst_server dst_username dst_password");
            System.out.println("Server example: http://localhost:4502/crx/server");
            System.exit(-1);
        }
        String srcServer = args[0];
        String srcUsername = args[1];
        String srcPassword = args[2];
        String dstServer = args[3];
        String dstUsername = args[4];
        String dstPassword = args[5];

        try {
            ContentSynchronizer synchronizer = new ContentSynchronizer(srcServer, srcUsername, srcPassword, dstServer, dstUsername, dstPassword);
            synchronizer.sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private String srcServer;
    private String srcUsername;
    private String srcPassword;
    private Session srcSession;
    private String dstServer;
    private String dstUsername;
    private String dstPassword;
    private Session dstSession;
    
    public ContentSynchronizer(String srcServer, String srcUsername, String srcPassword, String dstServer, String dstUsername, String dstPassword) throws RepositoryException {
        this.srcServer = srcServer;
        this.srcUsername = srcUsername;
        this.srcPassword = srcPassword;
        this.dstServer = dstServer;
        this.dstUsername = dstUsername;
        this.dstPassword = dstPassword;
        this.srcSession = getSession(srcServer, srcUsername, srcPassword);
        this.dstSession = getSession(dstServer, dstUsername, dstPassword);
    }
    
    private Session getSession(String server, String username, String password) throws RepositoryException {
        Repository repository = JcrUtils.getRepository(server);
        SimpleCredentials creds = new SimpleCredentials(username, password.toCharArray());
        Session session = repository.login(creds);
        return session;
    }
    
    private void sync() throws RepositoryException, ParseException {
        Node src = srcSession.getNode("/content/abc");
        Node dst = dstSession.getNode("/content");
        JcrUtil.copy(src, dst, null).addMixin("mix:versionable");
        dstSession.save();
        VersionManager manager = dstSession.getWorkspace().getVersionManager();
        //Version version = manager.checkin("/content/abc");
        //System.out.println("version = " + version.getIndex());
        manager.checkout("/content/abc");
        dstSession.save();
    }
}