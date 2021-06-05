import java.util.*;

public class main
{
    public static void main (String[] args)
    {
        Graph testGraph = Graph.readGraphFile("petitGraph.gsb");

        List<Edge> edgesSorted = testGraph.getEdges();

        Collections.sort(edgesSorted, new Comparator<Edge>() {
            @Override
            public int compare(Edge edge1, Edge edge2) {
                return edge1.getValue() - edge2.getValue();
            }
        });

//        for (Edge e:
//        edgesSorted) {
//            System.out.println(e);
//        }

        System.out.println(testGraph);
        System.out.println( Arrays.toString(testGraph.getAdjacentMatrix().toArray()).replace("],", "]\n"));
//        System.out.println("-------------------------------------------------------");
//        System.out.println(testGraph.Kruskal1());
//        System.out.println("-------------------------------------------------------");
//        System.out.println(testGraph.Kruskal2());
        System.out.println("-------------------------------------------------------");
        System.out.println(testGraph.Prim());

        testGraph.Kruskal1();
        System.out.println("Temps d'execution de Kruskal 1 : " + testGraph.getLastDurationInMs() + " ms");
        testGraph.Kruskal2();
        System.out.println("Temps d'execution de Kruskal 2 : " + testGraph.getLastDurationInMs() + " ms");
        testGraph.Prim();
        System.out.println("Temps d'execution de Prim : " + testGraph.getLastDurationInMs() + " ms");
    }
}
