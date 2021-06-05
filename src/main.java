import java.io.File;
import java.util.*;

public class main
{
    public static String root = "Graphs/";

    public static void main (String[] args)
    {
        executeFile("peterson_double.gsb");
//        executeAllFiles();
        // Création du graphe
        Graph testGraph = Graph.readGraphFile(root + "peterson.gsb");
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
//        Graph kruskal3 = testGraph.Kruskal3();
//        Graph prim = testGraph.Prim();

//        System.out.println("-------------------------------------------------------");
//        System.out.println(kruskal1);
//        System.out.println("-------------------------------------------------------");
//        System.out.println(kruskal2);
//        System.out.println("-------------------------------------------------------");
//        System.out.println(prim);


//        testGraph.Kruskal1();
//        System.out.println("Temps d'execution de Kruskal 1 : " + testGraph.getLastDurationInMs() + " ms");
//        System.out.println(testGraph.Kruskal2());
//        System.out.println("Temps d'execution de Kruskal 2 : " + testGraph.getLastDurationInMs() + " ms");
//        testGraph.Prim();
//        System.out.println("Temps d'execution de Prim : " + testGraph.getLastDurationInMs() + " ms");

          Graph dMST = testGraph.dMST(4);
          System.out.println(dMST);
    }

    public static void executeFile(String filename)
    {
        Graph graph = Graph.readGraphFile(root + filename);
        System.out.println("--------------- " + filename);

        Graph kruskal1 = graph.Kruskal1();
        Graph kruskal2 = graph.Kruskal2();
        Graph prim = graph.Prim();

        System.out.print("Kruskal 1 : " + kruskal1.getLastDurationInMs() + " ms - ");
        System.out.print("Kruskal 2 : " + kruskal2.getLastDurationInMs() + " ms - ");
        System.out.println("Prim : " + prim.getLastDurationInMs() + " ms");
    }

    public static void executeAllFiles()
    {
        File folder = new File(root);
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if(file.getName().endsWith(".gsb"))
            {
                executeFile(file.getName());
            }
        }
    }
}
