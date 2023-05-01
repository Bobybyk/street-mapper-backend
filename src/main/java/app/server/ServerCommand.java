package app.server;

import app.map.Plan;
import app.map.PlanParser;

public interface ServerCommand {

    String getdescription();
    void execute(Server server, String... args) throws IllegalArgumentException, Exception;
}

class SCUpdateMapFile implements ServerCommand {

    @Override
    public String getdescription() {
        return "commande permettant de changer le ficher de plan";
    }

    @Override
    public void execute(Server server, String... args) throws IllegalArgumentException, Exception {
        if (args.length != 2) 
            throw new IllegalArgumentException("UpdateMap s'attend à recevoir uniquement le chemin vers le nouveau fichier");
        String filePath = args[1];
        Plan plan = PlanParser.planFromSectionCSV(filePath);
        server.updateMap(plan);
    }
}

class SCUpdateTimeFile implements ServerCommand {

    @Override
    public String getdescription() {
        return "commande permettant de changer les informations de temps au plan";
    }

    @Override
    public void execute(Server server, String... args) throws IllegalArgumentException, Exception {
        if (args.length != 2) 
            throw new IllegalArgumentException("S'attend à recevoir uniquement le chemin vers le nouveau fichier");
        String filePath = args[1];

        Plan copyPlan = new Plan(server.getPlan());
        PlanParser.addTimeFromCSV(copyPlan, filePath);
        server.updateMap(copyPlan);
    }
    
}