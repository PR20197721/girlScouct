package org.girlscouts.tools.datamigrate;

public class Facade {
	private static Logger log = LoggerFactory.getLogger(Facade.class);
    public static void main(String[] args)
    {
        if (args.length < 4) {
            log.info("Girl Scouts AEM data migrate tool");
            log.info("Params: server username password cmd [nodePath] [isDryRun]");
            log.info("Server example: http://localhost:4502/crx/server");
            log.info("Available commands:");
            log.info("removedeactivated: remove deactivated nodes. (Can be dry run)");
            log.info("checkout: checkout checked-in node.");
            log.info("unlock: unlock the locked node.");
            log.info("updatelink: update the link from my.girlscouts.org to my-stage.girlscouts.org.");
            System.exit(-1);
        }
        String server = args[0];
        String username = args[1];
        String password = args[2];
        String cmd = args[3];
        String nodePath = args.length >= 5 ? args[4] : "/content";
        boolean isDryRun = args.length >= 6 && args[5].equalsIgnoreCase("true");

        try {
            if (cmd.equals("removedeactivated")) {
                log.info("Scanning repo for deactivated nodes ...");
                DeactivatedRemover remover = new DeactivatedRemover(server, username, password, isDryRun);
                remover.scan(nodePath);
                remover.doRemove();
            } else if (cmd.equals("checkout")) {
                NodeTool synchronizer = new NodeTool(server, username, password, nodePath);
                synchronizer.checkout();
            } else if (cmd.equals("unlock")){
                NodeTool synchronizer = new NodeTool(server, username, password, nodePath);
                synchronizer.unlock();
            } else if (cmd.equals("updatelink")) {
                LinkUpdater updater = new LinkUpdater(server, username, password);
                updater.doUpdate();
            } else {
                log.info("Command not supported.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}