import java.io.File;
import java.io.PrintWriter;

public class main
{
    public static String rootGraphs = "Graphs/";
    public static String rootData = "Data/";
    public static String results = "results.csv";
    public static String results_dMST = "results_d-MST.csv";

    public static void main (String[] args)
    {
        /**
         *  Uncomment lines to test the program
         */

        /**
         * Question 3
         */
//        executeFile("connexe.gsb");
//        executeFile("petitGraph.gsb");
//        executeFile("peterson.gsb");

        /**
         * Questions 4 et 5
         */
//        executeAllGraphTests();

        /**
         * Questions 6 et 7
         */
        executeAll_dMST_Tests();

        /**
         * For more tests uncomment following lines
         */
        // Create graph
        Graph testGraph = Graph.readGraphFile(rootGraphs + "peterson.gsb");

//        System.out.println(testGraph);

//        Graph kruskal1 = testGraph.Kruskal1();
//        Graph kruskal2 = testGraph.Kruskal2();
//        Graph prim = testGraph.Prim();

//        System.out.println("-------------------------------------------------------");
//        System.out.println(kruskal1);
//        System.out.println("-------------------------------------------------------");
//        System.out.println(kruskal2);
//        System.out.println("-------------------------------------------------------");
//        System.out.println(prim);

//        System.out.println("Temps d'execution de Kruskal 1 : " + kruskal1.getLastDurationInMs() + " ms");
//        System.out.println("Temps d'execution de Kruskal 2 : " + kruskal2.getLastDurationInMs() + " ms");
//        System.out.println("Temps d'execution de Prim : " + prim.getLastDurationInMs() + " ms");
    }

    /**
     * Method used to get datas to analyse the dMST algorithm performances
     */
    public static void executeAll_dMST_Tests()
    {
        File folder = new File(rootGraphs);
        File results = new File(rootData + main.results_dMST);
        File[] listOfFiles = folder.listFiles();

        new File(rootData).mkdir();

        try
        {
            if(results.createNewFile())
            {
                System.out.println(main.results + " file created !");
            }
            else
            {
                System.out.println(main.results + " file opened !");
            }

            PrintWriter writer = new PrintWriter(new File(rootData + main.results_dMST));
            StringBuilder sb = new StringBuilder();

            for (int i = 2; i <= 5; i++)
            {
                sb.append("Fichier;Nb Sommets;Nb Arcs;Prim;" + i + "-MST;Poids d-MST;Rapport;;");
            }

            sb.append("\n");

            for (File graphFile : listOfFiles)
            {
                if (graphFile.getName().endsWith(".gsb"))
                {
                    String filename = graphFile.getName();

                    for (int i = 2; i <= 5; i++)
                    {
                        Graph graph = Graph.readGraphFile(rootGraphs + graphFile.getName());
                        System.out.println("--------------- " + graph.getName());
                        System.out.println("nBVertices : " + graph.getVertices().size());
                        System.out.println("nBEdges : " + graph.getEdges().size());

                        Graph prim = graph.Prim();
                        Graph dMST = graph.dMST(i);

                        System.out.print("Prim : " + prim.getLastDurationInMs() + " ms -- ");
                        System.out.println(i + "-MST Prim : " + dMST.getLastDurationInMs() + " ms");

                        sb.append(filename + ";"
                                + graph.getVertices().size() + ";"
                                + graph.getEdges().size() + ";"
                                + Double.toString(prim.getLastDurationInMs()).replace(".", ",") + ";"
                                + Double.toString(dMST.getLastDurationInMs()).replace(".", ",") + ";"
                                + dMST.getTotalWeigth() + ";"
                                + Double.toString(((double)dMST.getTotalWeigth() / (double)prim.getTotalWeigth())).replace(".", ",") + ";;");
                    }

                    sb.append("\n");
                }
            }
            writer.write(sb.toString());
            writer.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        System.out.println("Done !");
    }

    /**
     * Method used to compare Kruskal v1, v2 and Prim algorithm performances on a given graph
     * @param filename
     * @return
     */
    public static String executeGraphTest(String filename)
    {
        Graph graph = Graph.readGraphFile(rootGraphs + filename);
        System.out.println("--------------- " + filename);
        System.out.println("nBVertices : " + graph.getVertices().size());
        System.out.println("nBEdges : " + graph.getEdges().size());

        Graph kruskal1 = graph.Kruskal1();
        Graph kruskal2 = graph.Kruskal2();
        Graph prim = graph.Prim();

        if( kruskal1.getTotalWeigth() == kruskal2.getTotalWeigth() && kruskal1.getTotalWeigth() == prim.getTotalWeigth() )
        {
            System.out.println("-- Total weigth : " + kruskal1.getTotalWeigth());
        }
        else
        {
            System.out.println("/!\\ ERROR /!\\");
            System.out.print("Kruskal1 weigth : " + kruskal1.getTotalWeigth());
            System.out.print("Kruskal2 weigth : " + kruskal2.getTotalWeigth());
            System.out.println("Prim weigth : " + prim.getTotalWeigth());
        }

        System.out.print("Kruskal 1 : " + kruskal1.getLastDurationInMs() + " ms -- ");
        System.out.print("Kruskal 2 : " + kruskal2.getLastDurationInMs() + " ms -- ");
        System.out.println("Prim : " + prim.getLastDurationInMs() + " ms");

        return filename + ";"
                + graph.getVertices().size() + ";"
                + graph.getEdges().size() + ";"
                + Double.toString(kruskal1.getLastDurationInMs()).replace(".", ",")  + ";"
                + Double.toString(kruskal2.getLastDurationInMs()).replace(".", ",") + ";"
                + Double.toString(prim.getLastDurationInMs()).replace(".", ",") + ";"
                + kruskal1.getTotalWeigth() + "\n";
    }

    /**
     * Executes all Graphs conained in the "Graphs/" folder
     */
    public static void executeAllGraphTests()
    {
        File folder = new File(rootGraphs);
        File results = new File(rootData + main.results);
        File[] listOfFiles = folder.listFiles();

        new File(rootData).mkdir();

        try
        {
            if(results.createNewFile())
            {
                System.out.println(main.results + " file created !");
            }
            else
            {
                System.out.println(main.results + " file opened !");
            }

            PrintWriter writer = new PrintWriter(new File(rootData + main.results));
            StringBuilder sb = new StringBuilder();

            sb.append("Fichier;Nb Sommets;Nb Arcs;Kruskal 1 (en ms);Kruskal 2 (en ms);Prim (en ms);Poids Total\n");

            for (File graph : listOfFiles) {
                if(graph.getName().endsWith(".gsb"))
                {
                    sb.append(executeGraphTest(graph.getName()));
                }
            }
            writer.write(sb.toString());
            writer.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        System.out.println("Done !");
    }

}
