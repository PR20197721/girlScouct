package org.girlscouts.tools.datamigrate;

import java.text.ParseException;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Value;

import org.apache.jackrabbit.commons.JcrUtils;

public class LinkUpdater 
{
    private String server;
    private String username;
    private String password;
    private Session session;
    private int count;
    
    public LinkUpdater(String server, String username, String password) throws RepositoryException {
        this.server = server;
        this.username = username;
        this.password = password;
        this.count = 0;
        getSession();
    }
    
    private void getSession() throws RepositoryException {
        Repository repository = JcrUtils.getRepository(server);
        SimpleCredentials creds = new SimpleCredentials(username, password.toCharArray());
        Session session = repository.login(creds);
        this.session = session;
    }
    
    private static String GLOBAL_NAV_PATH = "jcr:content/header/global-nav";
    
    void doUpdate() throws RepositoryException, ParseException {
        Node rootNode = session.getNode("/content");
        NodeIterator iterBranch = rootNode.getNodes();
        while (iterBranch.hasNext()) {
            try {
                Node branchNode = iterBranch.nextNode();
                NodeIterator iterLang = branchNode.getNodes();
                while (iterLang.hasNext()) {
                    Node langNode = iterLang.nextNode();
                    if (!langNode.hasNode(GLOBAL_NAV_PATH)) {
                        continue;
                    }

                    Node propNode = langNode.getNode(GLOBAL_NAV_PATH);
                    if (propNode.hasProperty("links")) {
                        Value[] values = propNode.getProperty("links").getValues();
                        boolean needModify = false;
                        for (Value value : values) {
                            if (value.getString().contains("my.girlscouts.org")) {
                                needModify = true;
                                break;
                            }
                        }
                        
                        if (needModify) {
                            String[] strValues = new String[values.length];
                            for (int i = 0; i < values.length; i++) {
                                Value value = values[i];
                                strValues[i] = value.getString().replaceAll("my.girlscouts.org", "my-stage.girlscouts.org");
                            }
                            propNode.setProperty("links", strValues);
                            session.save();
                            System.out.println("Property updated: " + propNode.getPath());
                            count++;
                        }
                    }
                }
            } catch (RepositoryException e) {
                System.err.println(e.getMessage());
            }
        }
        System.out.println("# of councils with VTK link updated: " + Integer.toString(this.count));
    }
}