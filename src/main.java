import java.io.File;

public class main
{
    public static String root = "Graphs/";

    public static void main (String[] args)
    {
//        executeFile("crd1000.gsb");
        executeAllGraphTests();
        // Création du graphe
//        Graph testGraph = Graph.readGraphFile(root + "peterson_double.gsb");

//        System.out.println(testGraph.getVertices().get(0));
//        System.out.println(testGraph.getVertices().get(1));
//        System.out.println(testGraph.getVertices().get(0).getEdge(testGraph.getVertices().get(2)));

//        System.out.println(testGraph);
//        System.out.println(testGraph.getEdges().get(1));
//        testGraph.getEdges().remove(1);
//        testGraph.setVerticesParents();
//
//        System.out.println("Connexe : " + testGraph.isConnexe());

//        System.out.println(testGraph);
//        System.out.println("Adjacency Matrix :");
//        System.out.println( Arrays.toString(testGraph.getAdjacentMatrix().toArray()).replace("],", "]\n"));

//        System.out.println();

//        Graph kruskal1 = testGraph.Kruskal1();
//        Graph kruskal2 = testGraph.Kruskal2();
//        Graph kruskal2 = testGraph.Kruskal2();
//        Graph kruskal3 = testGraph.Kruskal3();
//        Graph prim = testGraph.Prim();

//        System.out.println("-------------------------------------------------------");
//        System.out.println(kruskal1);
//        System.out.println("-------------------------------------------------------");
//        System.out.println(kruskal2);
//        System.out.println("-------------------------------------------------------");
//        System.out.println(kruskal3);
//        System.out.println("-------------------------------------------------------");
//        System.out.println(prim);


//        testGraph.Kruskal1();
//        System.out.println("Temps d'execution de Kruskal 1 : " + testGraph.getLastDurationInMs() + " ms");
//        System.out.println(testGraph.Kruskal2());
//        System.out.println("Temps d'execution de Kruskal 2 : " + testGraph.getLastDurationInMs() + " ms");
//        testGraph.Prim();
//        System.out.println("Temps d'execution de Prim : " + testGraph.getLastDurationInMs() + " ms");

//          Graph dMST = testGraph.dMST(4);
//          System.out.println(dMST);
    }

    public static void executeGraphTest(String filename)
    {
        Graph graph = Graph.readGraphFile(root + filename);
        System.out.println("--------------- " + filename);
        System.out.println("nBVertices : " + graph.getVertices().size());
        System.out.println("nBEdges : " + graph.getEdges().size());

        Graph kruskal1 = graph.Kruskal1();
        Graph kruskal2 = graph.Kruskal2();
        Graph prim = graph.Prim();

        if( !(kruskal1.getTotalWeigth() == kruskal2.getTotalWeigth() && kruskal1.getTotalWeigth() == prim.getTotalWeigth()) )
        {
            System.out.println("/!\\ ERROR /!\\");
            System.out.print("Kruskal1 weigth : " + kruskal1.getTotalWeigth());
            System.out.print("Kruskal2 weigth : " + kruskal2.getTotalWeigth());
            System.out.println("Prim weigth : " + prim.getTotalWeigth());
        }
        else
        {
            System.out.println("-- Total weigth : " + kruskal1.getTotalWeigth());
        }

        System.out.print("Kruskal 1 : " + kruskal1.getLastDurationInMs() + " ms -- ");
        System.out.print("Kruskal 2 : " + kruskal2.getLastDurationInMs() + " ms -- ");
        System.out.println("Prim : " + prim.getLastDurationInMs() + " ms");
    }

    public static void executeAllGraphTests()
    {
        File folder = new File(root);
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if(file.getName().endsWith(".gsb"))
            {
                executeGraphTest(file.getName());
            }
        }
    }
}
