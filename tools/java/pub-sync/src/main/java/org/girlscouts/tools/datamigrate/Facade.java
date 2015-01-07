package org.girlscouts.tools.datamigrate;

public class Facade {
    public static void main(String[] args)
    {
        if (args.length < 5) {
            System.out.println("Girl Scouts AEM data migrate tool");
            System.out.println("Params: server username password cmd nodePath [dryrun]");
            System.out.println("Server example: http://localhost:4502/crx/server");
            System.out.println("Available commands:");
            System.out.println("removedeactivated: remove deactivated nodes. (Can be dry run)");
            System.out.println("checkout: checkout checked-in node.");
            System.out.println("unlock: unlock the locked node.");
            System.exit(-1);
        }
        String server = args[0];
        String username = args[1];
        String password = args[2];
        String cmd = args[3];
        String nodePath = args[4];
        boolean isDryRun = args.length >= 6 && args[5].equals("dryrun");

        try {
            if (cmd.equals("removedeactivated")) {
                System.out.println("Scanning repo for deactivated nodes ...");
                DeactivatedRemover remover = new DeactivatedRemover(server, username, password, isDryRun);
                remover.scan(nodePath);
                remover.doRemove();
            } else if (cmd.equals("checkout")) {
                NodeTool synchronizer = new NodeTool(server, username, password, nodePath);
                synchronizer.checkout();
            } else if (cmd.equals("unlock")){
                NodeTool synchronizer = new NodeTool(server, username, password, nodePath);
                synchronizer.unlock();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}