package org.girlscouts.tools.web;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.commons.JcrUtils;

// server example: http://localhost:4503/crx/server/
public class ReplicationChecker 
{
    private static class Server {
        public String server, username, password;
        public Server(String server, String username, String password) {
            this.server = server;
            this.username = username;
            this.password = password;
        }
    }
    
    public static void main(String[] args)
    {
        if (args.length < 6) {
            System.out.println("Replication Checker");
            System.out.println("Params: auth_server username password pub_server username password");
            System.out.println("Server example: http://localhost:4503/crx/server");
            System.exit(-1);
        }
        Server authServer = new Server(args[0], args[1], args[2]);
        Server pubServer = new Server(args[3], args[4], args[5]);

        try {
            List<String> rootList = new ArrayList<String>();
            rootList.add("/content/dam");
            ReplicationChecker updater = new ReplicationChecker(authServer, pubServer, rootList);
            updater.check();
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }
    
    private Server authServer, pubServer;
    private Session authSession, pubSession;
    private List<String> rootList = new ArrayList<String>();
    
    private int allCount = 0;
    private int updatedCount = 0;
    
    public ReplicationChecker(Server authServer, Server pubServer, List<String> rootList) throws RepositoryException {
        this.authServer = authServer;
        this.pubServer = pubServer;
        this.rootList = rootList;
        authSession = getSession(authServer);
        pubSession = getSession(pubServer);
    }
    
    private Session getSession(Server server) throws RepositoryException {
        Repository repository = JcrUtils.getRepository(server.server);
        SimpleCredentials creds = new SimpleCredentials(server.username, server.password.toCharArray());
        Session session = repository.login(creds);
        return session;
    }
    
    private void check() throws RepositoryException {
    }
    
}