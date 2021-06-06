import java.io.File;
import java.util.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Graph {
    private String name;
    private boolean directed;
    private List<Vertex> vertices;
    private List<Edge> edges;
    private List<List<Integer>> adjacentMatrix;
    private long lastDuration;

    public Graph(List<Edge> newEdges, List<Vertex> newVertices)
    {
        this.edges = newEdges;
        this.vertices = newVertices;
        this.generateAdjacencyMatrix();
        this.setVerticesParents();
    }

    public Graph(String name, boolean directed, List<Vertex> vertices, List<Edge> edges)
    {
        this(edges, vertices);
        this.name = name;
        this.directed = directed;
    }

    public Graph(String file)
    {
        try {
            Graph newGraph = Graph.readGraphFile(file);
            this.edges = newGraph.edges;
            this.vertices = newGraph.vertices;
            this.name = newGraph.name;
            this.directed = newGraph.directed;
            this.setVerticesParents();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static class subset {
        int parent, rank;
    };

    private static int find(subset subsets[], int i) {
        if (subsets[i].parent != i)
            subsets[i].parent = find(subsets, subsets[i].parent);
        return subsets[i].parent;
    }

    private static void union(subset subsets[], int x, int y) {
        int xroot = find(subsets, x);
        int yroot = find(subsets, y);

        if (subsets[xroot].rank < subsets[yroot].rank)
            subsets[xroot].parent = yroot;
        else if (subsets[xroot].rank > subsets[yroot].rank)
            subsets[yroot].parent = xroot;
        else {
            subsets[yroot].parent = xroot;
            subsets[xroot].rank++;
        }
    }

    /**
     * Complexity : O(m*log(m))
     * @return
     */
    public Graph Kruskal1()
    {
        System.out.println("Execution of Kruskal 1...");
        long startTime = System.nanoTime();

        Graph kruskal = new Graph(new LinkedList<Edge>(), this.vertices);
        kruskal.setName("Kruskal 1");
        int i = 0;

        List<Edge> edgesSorted = new LinkedList<>(this.edges);

        Collections.sort(edgesSorted, new Comparator<Edge>() {
            @Override
            public int compare(Edge edge1, Edge edge2) {
                return edge1.getValue() - edge2.getValue();
            }
        });

        subset subsets[] = new subset[this.vertices.size()];
        for(i = 0; i < this.vertices.size(); ++i)
            subsets[i]=new subset();

        for (int v = 0; v < this.vertices.size(); ++v)
        {
            subsets[v].parent = v;
            subsets[v].rank = 0;
        }

        i = 0;

        while (kruskal.edges.size() < this.vertices.size() - 1)
        {
            Edge currentEdge = edgesSorted.get(i++);

            int x = find(subsets, currentEdge.getStart().getId());
            int y = find(subsets, currentEdge.getEnd().getId());

            if (x != y)
            {
                kruskal.edges.add(currentEdge);
                union(subsets, x, y);
            }
        }

        long endTime = System.nanoTime();

        kruskal.setLastDuration(endTime-startTime);
        this.setLastDuration(endTime-startTime);

        System.out.println("End of execution of Kruskal 1 !");

        return kruskal;
    }

    public Graph Kruskal3()
    {
        System.out.println("Execution of Kruskal 3...");
        long startTime = System.nanoTime();

        List<Edge> edgesSorted = new LinkedList<>(this.edges);

        Collections.sort(edgesSorted, new Comparator<Edge>() {
            @Override
            public int compare(Edge edge1, Edge edge2) {
                return edge2.getValue() - edge1.getValue();
            }
        });

        Graph kruskal = new Graph(edgesSorted, this.vertices);
        kruskal.setName("Kruskal 3");
        int i = 0;

        // Subset initialisation
        subset subsets[] = new subset[this.vertices.size()];
        for(i = 0; i < this.vertices.size(); ++i)
            subsets[i]=new subset();

        for (int v = 0; v < this.vertices.size(); ++v)
        {
            subsets[v].parent = v;
            subsets[v].rank = 0;
        }

        i = 0;

        while (kruskal.edges.size() >= this.vertices.size())
        {
            Edge currentEdge = edgesSorted.get(i++);

            int x = find(subsets, currentEdge.getStart().getId());
            int y = find(subsets, currentEdge.getEnd().getId());

            kruskal.edges.remove(currentEdge);
            union(subsets, x, y);

            if (x == y)
            {
                kruskal.edges.add(currentEdge);
            }
        }

        long endTime = System.nanoTime();

        kruskal.setLastDuration(endTime-startTime);
        this.setLastDuration(endTime-startTime);

        System.out.println("End of execution of Kruskal 3 !");

        return kruskal;
    }

    /**
     * Complexity : O(m*n)
     * @return
     */
    public Graph Kruskal1V1()
    {
        long startTime = System.nanoTime();

        Graph kruskal = new Graph(new LinkedList<Edge>(), this.vertices);
        kruskal.setName("Kruskal 1");
        int i = 0;

        // Edge list ordered in increasing order
        List<Edge> edgesSorted = new LinkedList<>(this.edges);

        Collections.sort(edgesSorted, new Comparator<Edge>() {
            @Override
            public int compare(Edge edge1, Edge edge2) {
                return edge1.getValue() - edge2.getValue();
            }
        });
//        System.out.println(edgesSorted);
        while (kruskal.edges.size() < this.vertices.size() - 1 && edgesSorted.size() > i)
        {
            Edge currentEdge = edgesSorted.get(i++);
            kruskal.edges.add(currentEdge);
//            System.out.println(i);
            if (kruskal.hasCycle())
            {
//                System.out.println(currentEdge);
                kruskal.edges.remove(currentEdge);
            }
        }

//        System.out.println("L'arbre recouvrant de poids minimum est : ");
        long endTime = System.nanoTime();

        kruskal.setLastDuration(endTime-startTime);
        this.setLastDuration(endTime-startTime);

        return kruskal;
    }

    /**
     * Complexity : O(m*n)
     * @return
     */
    public Graph Kruskal2()
    {
        System.out.println("Execution of Kruskal 2...");

        long startTime = System.nanoTime();
        // Edge list ordered in decreasing order
        List<Edge> edgesSorted = new LinkedList<>(this.edges);

        Collections.sort(edgesSorted, new Comparator<Edge>() {
            @Override
            public int compare(Edge edge1, Edge edge2) {
                return edge2.getValue() - edge1.getValue();
            }
        });

        Graph kruskal = new Graph(edgesSorted, new LinkedList<>(this.vertices));
        kruskal.setName("Kruskal 2");
//        kruskal.getVerticesParents();
//        System.out.println(kruskal);
        int i = 0, count = 0;

        while (kruskal.edges.size() >= this.vertices.size())
        {
            if(i == this.vertices.size())
            {
//                System.out.println("Error of : " + (kruskal.edges.size() - this.vertices.size()) + " edges !");
//                System.out.println("i : " + i);
                System.out.println("count : " + count);
//                System.out.println("kSize : " + kruskal.edges.size());
//                break;
            }

            Edge currentEdge = edgesSorted.get(i % kruskal.edges.size());
            Vertex start = currentEdge.getStart();
            Vertex end = currentEdge.getEnd();

//            System.out.println(i + " -                               Arc : " + currentEdge);

            kruskal.edges.remove(currentEdge);
            start.getParents().remove(end);
            end.getParents().remove(start);

            if(!kruskal.isConnexe())
            {
//                System.out.println(currentEdge);
                kruskal.edges.add(i % kruskal.edges.size(), currentEdge);
                start.getParents().add(end);
                end.getParents().add(start);
            }
            else
            {
                i--;
            }

            i++;
            count++;
        }

//        System.out.println("L'arbre recouvrant de poids minimum est : ");
        long endTime = System.nanoTime();

        kruskal.setLastDuration(endTime-startTime);
        this.setLastDuration(endTime-startTime);

        System.out.println("End of execution of Kruskal 2 !");

        return kruskal;
    }

    /**
     * Complexity : O(n²)
     * @return
     */
    public Graph Prim()
    {
        System.out.println("Execution of Prim...");
        Graph prim = new Graph(new LinkedList<Edge>(), this.vertices);
        prim.setName("Prim");
//        System.out.println("Prim");

        long startTime = System.nanoTime();

        Random rand = new Random();
        int sommetIndice = rand.nextInt(vertices.size());

        Vertex randVertex = this.vertices.get(sommetIndice);
        List<Vertex> visitedVertices = new LinkedList<>();
        visitedVertices.add(randVertex);

        while(this.vertices.size() != visitedVertices.size())
        {
            int minimumWeight = Integer.MAX_VALUE;
            Edge minimumEdge = new Edge();
            Vertex vertexZ = new Vertex(-1);
            for(Edge e : this.edges)
            {
                if(e.getValue() < minimumWeight)
                {
                    if(visitedVertices.contains(e.getEnd()) && !visitedVertices.contains(e.getStart()))
                    {
                        vertexZ = e.getStart();
                        minimumWeight = e.getValue();
                        minimumEdge = e;
                    }
                    else if(visitedVertices.contains(e.getStart()) && !visitedVertices.contains(e.getEnd()))
                    {
                        vertexZ = e.getEnd();
                        minimumWeight = e.getValue();
                        minimumEdge = e;
                    }
                }
            }

            prim.edges.add(minimumEdge);
            visitedVertices.add(vertexZ);
        }

        long endTime = System.nanoTime();

        prim.setLastDuration(endTime-startTime);
        this.setLastDuration(endTime-startTime);

        System.out.println("End of execution of Prim !");

        return prim;
        }

     int vertexDegree(int vertexID)
     {
         int degree = 0;

         for(int i = 0; i < this.vertices.size(); i++)
         {
             if(this.adjacentMatrix.get(vertexID).get(i) != 0)
                 degree++;
         }

         return degree;
     }

     public Graph dMST(int degre)
     {
        Graph dMST = new Graph(new LinkedList<Edge>(), this.vertices);

         System.out.println("Execution of d-MST...");
         dMST.setName("D-MST");

         long startTime = System.nanoTime();

         Random rand = new Random();
         int sommetIndice = rand.nextInt(vertices.size());

         Vertex randVertex = this.vertices.get(sommetIndice);
         List<Vertex> visitedVertices = new LinkedList<>();
         visitedVertices.add(randVertex);

         while(this.vertices.size() != visitedVertices.size())
         {
             int minimumWeight = Integer.MAX_VALUE;
             Edge minimumEdge = new Edge();
             Vertex vertexZ = new Vertex(-1);
             Vertex vertexY = new Vertex(-1);
             for(Edge e : this.edges)
             {
                 if(e.getValue() < minimumWeight)
                 {
                     if(visitedVertices.contains(e.getEnd()) && !visitedVertices.contains(e.getStart()))
                     {
                         vertexZ = e.getStart();
                         vertexY = e.getEnd();
                         minimumWeight = e.getValue();
                         minimumEdge = e;
                     }
                     else if(visitedVertices.contains(e.getStart()) && !visitedVertices.contains(e.getEnd()))
                     {
                         vertexZ = e.getEnd();
                         vertexY = e.getStart();
                         minimumWeight = e.getValue();
                         minimumEdge = e;
                     }
                 }
             }

             if(vertexDegree(vertexY.getId()) < degre && vertexDegree(vertexZ.getId()) < degre)
             {
                 dMST.edges.add(minimumEdge);
                 visitedVertices.add(vertexZ);
                 dMST.generateAdjacencyMatrix();
             }
             else
             {
                 this.vertices.remove(minimumEdge);
                 visitedVertices.add(vertexZ);
             }

             System.out.println("Degree de " + vertexZ.getName() + " : " + dMST.vertexDegree(vertexZ.getId()) );
             System.out.println("Degree de " + vertexY.getName() + " : " + dMST.vertexDegree(vertexY.getId()) );
             System.out.println("----");
         }

         long endTime = System.nanoTime();

         dMST.setLastDuration(endTime-startTime);
         this.setLastDuration(endTime-startTime);

         System.out.println("End of execution of d-MST !");

         dMST.setVerticesParents();
        if(dMST.isConnexe())
            return dMST;
        else
            System.out.print("Pas d'arbre recouvrant de degrée " + degre + " pour ce graphe.");
            return null;
     }

    public List<List<Integer>> generateAdjacencyMatrix()
    {
        List<List<Integer>> adjacencyMatrix = new ArrayList<>();

        for(int i = 0; i < this.vertices.size(); i++)
        {
            List<Integer> vertexiWeigthes = new ArrayList<>();
            for(int j = 0; j < this.vertices.size(); j++)
            {
                vertexiWeigthes.add( this.findEdge(this.vertices.get(i), this.vertices.get(j)).getValue() );
            }
            adjacencyMatrix.add(vertexiWeigthes);
        }
        this.adjacentMatrix = adjacencyMatrix;
        return adjacencyMatrix;
    }

    public Edge findEdge(Vertex v1, Vertex v2)
    {
        Edge result = new Edge();
        for (Edge e :
             this.edges) {
            if( (e.getStart().equals(v1) && e.getEnd().equals(v2)) || (e.getEnd().equals(v1) && e.getStart().equals(v2)) )
            {
                result = e;
            }
        }

        return result;
    }

    public boolean hasCycle()
    {
        HashMap<Vertex, Integer> clusteredVisitedVertices = new HashMap<>();

        int nbClusters = 0;

        for (int i = 0; i < this.edges.size(); i++)
        {
            Vertex start = this.edges.get(i).getStart();
            Vertex end = this.edges.get(i).getEnd();

            // New cluster
            if(!clusteredVisitedVertices.containsKey(start) && !clusteredVisitedVertices.containsKey(end))
            {
                nbClusters++;
                clusteredVisitedVertices.put(start, nbClusters);
                clusteredVisitedVertices.put(end, nbClusters);
            }
            // Edge added to a cluster
            else if(clusteredVisitedVertices.containsKey(start) ^ clusteredVisitedVertices.containsKey(end))
            {
                int cluster = nbClusters;
                if(clusteredVisitedVertices.containsKey(start))
                    cluster = clusteredVisitedVertices.get(start);
                else if(clusteredVisitedVertices.containsKey(end))
                    cluster = clusteredVisitedVertices.get(end);

                clusteredVisitedVertices.put(start, cluster);
                clusteredVisitedVertices.put(end, cluster);
            }
            // Edge merging two clusters
            else if(clusteredVisitedVertices.containsKey(start) && clusteredVisitedVertices.containsKey(end))
            {
                if(clusteredVisitedVertices.get(start) == clusteredVisitedVertices.get(end))
                {
                    return true;
                }
                else
                {
                    nbClusters--;
                    for (Map.Entry<Vertex, Integer> vertexMapped:
                            clusteredVisitedVertices.entrySet())
                    {
                        if(vertexMapped.getValue() >= nbClusters)
                        {
                            vertexMapped.setValue(nbClusters);
                        }
                    }
                }
            }

//            System.out.println("Current edge : (" + start + ", " + end + ")");
//            System.out.println(clusteredVisitedVertices.entrySet());
        }

        return false;
    }

    /**
     * Complexity : O(m)
     * @return
     */
    public boolean isConnexe()
    {
        List<Integer> clusterIds = IntStream.rangeClosed(0, this.vertices.size()).boxed().collect(Collectors.toList());

        HashMap<Vertex, Integer> clusteredVisitedVertices = new HashMap<>(ToolBox.zipToMap(new LinkedList<>(this.vertices), clusterIds));

        for (Vertex vertex:
             this.vertices)
        {
            if(vertex.getDegree() == 0)
            {
//                System.out.println("0 Degree !");
                break;
            }

            for (Vertex parent : vertex.getParents())
            {
                if(vertex.getParents().contains(parent))
                {
                    if(clusteredVisitedVertices.get(parent) < clusteredVisitedVertices.get(vertex))
                        clusteredVisitedVertices.replace(vertex, clusteredVisitedVertices.get(parent));
                    else if(clusteredVisitedVertices.get(parent) > clusteredVisitedVertices.get(vertex))
                        clusteredVisitedVertices.replace(parent, clusteredVisitedVertices.get(vertex));
                }
            }
        }

//        System.out.println(clusteredVisitedVertices);
//        System.out.println("------------------");
        int firstValue = clusteredVisitedVertices.get(this.vertices.get(0));
        for (Map.Entry<Vertex, Integer> vertexMapped:
                clusteredVisitedVertices.entrySet())
        {
            if(vertexMapped.getValue() != firstValue)
                return false;
        }

        return true;
    }

    public String toString()
    {
        StringBuilder str = new StringBuilder("Name : " + this.name + "\n"
                + "Directed : " + this.directed + "\n"
                + "nBVertices : " + this.vertices.size() + "\n"
                + "nBEdges : " + this.edges.size() + "\n");
        for (Edge edge:
             this.edges) {
            str.append(edge.toString()).append("\n");
        }
        return str.toString();
    }

    public static Graph readGraphFile(String nomFichier)
    {
        String name = "";
        boolean directed = false;
        List<Vertex> vertices = new LinkedList<>();
        List<Edge> edges = new LinkedList<>();

        File fichier = new File(nomFichier);

        List<String> partSeparator = new ArrayList<>();
        partSeparator.add("--- Liste des sommets");
        partSeparator.add("--- Liste des aretes");

        try {
            if(fichier.exists()) {
                FileReader reader = new FileReader(fichier);
                BufferedReader br = new BufferedReader(reader);
                String lastPart = "";
                String line;
                String [] spllitedLine;
                while((line = br.readLine()) != null)
                {
                    if(partSeparator.contains(line))
                    {

                        lastPart = line;
                    }
                    else
                    {
                        if(lastPart == "")
                        {
                            spllitedLine = line.replace(" ", "").split(":");

                            switch (spllitedLine[0])
                            {
                                case "Nom":
                                    name = spllitedLine[1];
                                    break;
                                case "Oriente":
                                    directed = spllitedLine[1] == "oui" ? true : false;
                                    break;
                                case "NbSommets":
                                    break;
                                case "NbValSommet":
                                    break;
                                case "NbArcs":
                                    break;
                                case "NbValArc":
                                    break;
                            }
                        }
                        else if(lastPart.equals(partSeparator.get(0)))
                        {
                            spllitedLine = line.split(" ");
                            Vertex vert = new Vertex(Integer.parseInt(spllitedLine[0]), spllitedLine[1]);
                            vertices.add(vert);
                        }
                        else if(lastPart.equals(partSeparator.get(1)))
                        {
                            spllitedLine = line.split(" ");

                            Vertex start = vertices.get(Integer.parseInt(spllitedLine[0]));
                            Vertex end = vertices.get(Integer.parseInt(spllitedLine[1]));

                            Edge edge = new Edge(start, end, Integer.parseInt(spllitedLine[2]));

                            if( !edges.contains(new Edge(end, start, Integer.parseInt(spllitedLine[2]))) )
                                edges.add(edge);
                        }
                    }
                }
                br.close();
                reader.close();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        Graph GraphFile = new Graph(name, directed, vertices, edges);

        return GraphFile;
    }

    public void setVerticesParents()
    {
        for (Vertex v:
             this.vertices) {
            v.getParents().clear();
        }

        for (Edge edge : this.edges)
        {
            Vertex start = edge.getStart();
            Vertex end = edge.getEnd();

            if(!start.getParents().contains(end))
            {
                start.getParents().add(end);
            }

            if(!end.getParents().contains(start))
            {
                end.getParents().add(start);
            }
        }
    }

    public void getVerticesParents()
    {
        for (Vertex v:
                this.getVertices()) {
            System.out.println("Vertex : " + v + " - Parents : " + v.getParents());
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDirected() {
        return directed;
    }

    public List<Vertex> getVertices() {
        return vertices;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public long getLastDuration() {
        return lastDuration;
    }

    public long getLastDurationInMs() {
        return lastDuration / (1000 * 1000);
    }

    public void setLastDuration(long lastDuration) {
        this.lastDuration = lastDuration;
    }

    public List<List<Integer>> getAdjacentMatrix() {
        return adjacentMatrix;
    }
}
