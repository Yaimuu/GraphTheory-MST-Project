import java.io.File;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Graph
{
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

    /**
     * Subset structure allowing union-find method to be applied
     */
    private static class subset
    {
        int parent, rank;
    };

    /** Find
     * Finds the root of the unioned vertex
     * @param subsets
     * @param i
     * @return the root of the vertex i
     */
    private static int find(subset subsets[], int i)
    {
        if (subsets[i].parent != i)
            subsets[i].parent = find(subsets, subsets[i].parent);
        return subsets[i].parent;
    }

    /** Union
     * Union two vertices in the tree newly created
     * @param subsets
     * @param x
     * @param y
     */
    private static void union(subset subsets[], int x, int y)
    {
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

    /** Kruskal algorithm version 1, using union-find algorithm
     * Complexity : O(m*log(m))
     * @return MST graph of the current graph
     */
    public Graph Kruskal1()
    {
//        System.out.println("Execution of Kruskal 1...");
        long startTime = System.nanoTime();

        // Initializing components
        Graph kruskal = new Graph(new LinkedList<Edge>(), this.vertices);
        kruskal.setName("Kruskal 1");
        int i = 0;

        // Edges sorted in the ascending order
        List<Edge> edgesSorted = new LinkedList<>(this.edges);

        Collections.sort(edgesSorted, new Comparator<Edge>() {
            @Override
            public int compare(Edge edge1, Edge edge2) {
                return edge1.getValue() - edge2.getValue();
            }
        });

        // Subset initialization
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

//        System.out.println("End of execution of Kruskal 1 !");

        return kruskal;
    }

    /** DFS (Depth-First-Search)
     * Algorithm trying to access to all the vertices the graph contains
     * @param v
     * @param visitedVertices
     */
    private void dfs(Vertex v, List<Vertex> visitedVertices)
    {
        visitedVertices.add(v);

        for(int i = 0; i < v.getNeighbours().size(); ++i)
        {
            Vertex currentVertex = v.getNeighbours().get(i);
            if (!visitedVertices.contains(currentVertex)) {
                dfs(currentVertex, visitedVertices);
            }
        }
    }

    /** Connexity verification algorithm
     * This algorithm checks if the DFS returned all vertices of the graph
     * if the DFS returns all vertices, then it's connected. Elsewhere it's not.
     *
     * Complexity : O(log(n))
     *
     * @return a boolean according to the connexion of the graph
     */
    public boolean isConnected()
    {
        List<Vertex> visitedVertices = new LinkedList<>();

        dfs(vertices.get(0), visitedVertices);

        for(int i = 1; i < vertices.size(); i++)
        {
            Vertex currentVertex = vertices.get(i);
            if(!visitedVertices.contains(currentVertex))
            {
                return false;
            }
        }

        return true;
    }

    /** Kruskal algorithm version 2
     * This algorithm removes edges not leading the graph to be disconnected
     *
     * Complexity : O(m²)
     *
     * @return MST graph of the current graph
     */
    public Graph Kruskal2()
    {
//        System.out.println("Execution of Kruskal 2...");

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

        int i = 0;

        // Remove all edges not leading the graph to be disconnected
        while (kruskal.edges.size() >= this.vertices.size())
        {
            Edge currentEdge = kruskal.edges.get(i);
            kruskal.edges.remove(currentEdge);
            currentEdge.getStart().getNeighbours().remove(currentEdge.getEnd());
            currentEdge.getEnd().getNeighbours().remove(currentEdge.getStart());

            if(!kruskal.isConnected())
            {
                kruskal.edges.add(i, currentEdge);
                currentEdge.getStart().getNeighbours().add(currentEdge.getEnd());
                currentEdge.getEnd().getNeighbours().add(currentEdge.getStart());
                i++;
            }
        }

        long endTime = System.nanoTime();
        kruskal.setLastDuration(endTime-startTime);
        this.setLastDuration(endTime-startTime);

//        System.out.println("End of execution of Kruskal 2 !");
        return kruskal;
    }

    /** First and non-optimal implementation of kruskal version 1 algorithm
     * Using a naïve cycle detection algorithm
     *
     * Complexity : O(m^3)
     *
     * @return MST graph of the current graph
     */
    public Graph Kruskal1_OLD()
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


        while (kruskal.edges.size() < this.vertices.size() - 1 && edgesSorted.size() > i)
        {
            Edge currentEdge = edgesSorted.get(i++);
            kruskal.edges.add(currentEdge);

            if (kruskal.hasCycle())
            {
                kruskal.edges.remove(currentEdge);
            }
        }

        long endTime = System.nanoTime();

        kruskal.setLastDuration(endTime-startTime);
        this.setLastDuration(endTime-startTime);

        return kruskal;
    }

    /** First and non-optimal implementation of kruskal version 2 algorithm
     * Using a naïve connection detection algorithm
     *
     * Complexity : O(m^3)
     *
     * @return MST graph of the current graph
     */
    public Graph Kruskal2_OLD()
    {
//        System.out.println("Execution of Kruskal 2...");
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

        int i = 0;

        while (kruskal.edges.size() >= this.vertices.size())
        {
            Edge currentEdge = edgesSorted.get(i % kruskal.edges.size());
            Vertex start = currentEdge.getStart();
            Vertex end = currentEdge.getEnd();

            kruskal.edges.remove(currentEdge);
            start.getNeighbours().remove(end);
            end.getNeighbours().remove(start);

            if(!kruskal.isConnexe())
            {
                kruskal.edges.add(i % kruskal.edges.size(), currentEdge);
                start.getNeighbours().add(end);
                end.getNeighbours().add(start);
            }
            else
            {
                i--;
            }

            i++;
        }

        long endTime = System.nanoTime();
        kruskal.setLastDuration(endTime-startTime);
        this.setLastDuration(endTime-startTime);

//        System.out.println("End of execution of Kruskal 2 !");

        return kruskal;
    }

    /** Prim algorithm implementation
     *
     * Browses all vertices by following each minimal edge
     *
     * Complexity : O(n²)
     *
     * @return MST graph of the current graph
     */
    public Graph Prim()
    {
//        System.out.println("Execution of Prim...");
        Graph prim = new Graph(new LinkedList<Edge>(), this.vertices);
        prim.setName("Prim");

        long startTime = System.nanoTime();

        Random rand = new Random();
        int vertexindex = rand.nextInt(vertices.size());

        Vertex randVertex = this.vertices.get(vertexindex);
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

//        System.out.println("End of execution of Prim !");

        return prim;
        }

    /** Degree constrainted of Minimal Spanning Tree search
     *
     * Algorithm based on Prim algorithm.
     * Finds the MST of the graph but replaces every edge disrespecting the degree constraint.
     *
     * @param degre
     * @return MST graph of the current graph with a degree constraint
     */
    public Graph dMST(int degre)
    {
        Graph dMST = new Graph(new LinkedList<Edge>(), this.vertices);

        System.out.println("Execution of d-MST of degree : " + degre + "...");
        dMST.setName("D-MST");

        long startTime = System.nanoTime();

        Random rand = new Random();
        int sommetIndice = rand.nextInt(vertices.size());

        Vertex randVertex = this.vertices.get(sommetIndice);
        List<Vertex> visitedVertices = new LinkedList<>();
        visitedVertices.add(randVertex);

        List<Edge> tmpEdges = new LinkedList<>();

        while(this.vertices.size() != visitedVertices.size())
        {
            int minimumWeight = Integer.MAX_VALUE;
            Edge minimumEdge = new Edge();
            Vertex vertexZ = new Vertex(-1);
            Vertex vertexY = new Vertex(-1);

            // Search for the edge with the minimum weight
            for(Edge e : this.edges)
            {
                if(e.getValue() < minimumWeight && !tmpEdges.contains(e))
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

            dMST.edges.add(minimumEdge);
            vertexZ.getNeighbours().add(vertexY);
            vertexY.getNeighbours().add(vertexZ);

            // Checking the degree of the minimum edge's vertices
            if(vertexZ.getNeighbours().size() > degre || vertexY.getNeighbours().size() > degre)
            {
                dMST.edges.remove(minimumEdge);
                vertexZ.getNeighbours().remove(vertexY);
                vertexY.getNeighbours().remove(vertexZ);
                tmpEdges.add(minimumEdge);
            }
            else {
                visitedVertices.add(vertexZ);
            }
        }

        long endTime = System.nanoTime();
        dMST.setLastDuration(endTime-startTime);
        this.setLastDuration(endTime-startTime);

        System.out.println("End of execution of d-MST !");

        if (!dMST.isConnected())
        {
            System.out.println("Il n'est pas possible de créer un arbre recouvrant de degré " + degre + " !");
        }

        return dMST;
    }

    /** Graph's non-optimal cycle detection algorithm
     *
     * Complecity : O(m)
     *
     * @return if the graph has a cycle
     */
    public boolean hasCycle()
    {
        HashMap<Vertex, Integer> clusteredVisitedVertices = new HashMap<>();

        int nbClusters = 0;

        for (int i = 0; i < this.edges.size(); i++)
        {
            Vertex start = this.edges.get(i).getStart();
            Vertex end = this.edges.get(i).getEnd();

            // New cluster : increase nbClusters and push end and start into the map
            if(!clusteredVisitedVertices.containsKey(start) && !clusteredVisitedVertices.containsKey(end))
            {
                nbClusters++;
                clusteredVisitedVertices.put(start, nbClusters);
                clusteredVisitedVertices.put(end, nbClusters);
            }
            // Edge added to a cluster : add the vertex to the correct cluster
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
            // Edge merging two clusters : set all of the vertices involved to the lowest cluster
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
        }
        return false;
    }

    /** Graph's non-optimal connection algorithm
     *
     * Complexity : O(n²)
     *
     * @return if the graph
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
                break;
            }

            for (Vertex parent : vertex.getNeighbours())
            {
                if(vertex.getNeighbours().contains(parent))
                {
                    if(clusteredVisitedVertices.get(parent) < clusteredVisitedVertices.get(vertex))
                        clusteredVisitedVertices.replace(vertex, clusteredVisitedVertices.get(parent));
                    else if(clusteredVisitedVertices.get(parent) > clusteredVisitedVertices.get(vertex))
                        clusteredVisitedVertices.replace(parent, clusteredVisitedVertices.get(vertex));
                }
            }
        }

        int firstValue = clusteredVisitedVertices.get(this.vertices.get(0));
        for (Map.Entry<Vertex, Integer> vertexMapped : clusteredVisitedVertices.entrySet())
        {
            if(vertexMapped.getValue() != firstValue)
                return false;
        }

        return true;
    }

    /** Method generating the graph's adjacency Matrix
     *
     * @return graph's adjacency Matrix
     */
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

    /** Finds edge vertices's edge
     *
     * @param v1 Vertex 1
     * @param v2 Vertex 2
     * @return Vertices's edge
     */
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
                            {
                                edges.add(edge);
                                start.getEdges().add(edge);
                                end.getEdges().add(edge);
                            }

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
            v.getNeighbours().clear();
        }

        for (Edge edge : this.edges)
        {
            Vertex start = edge.getStart();
            Vertex end = edge.getEnd();

            if(!start.getNeighbours().contains(end))
            {
                start.getNeighbours().add(end);
            }

            if(!end.getNeighbours().contains(start))
            {
                end.getNeighbours().add(start);
            }
        }
    }

    public void getVerticesNeighbours()
    {
        for (Vertex v:
                this.getVertices()) {
            System.out.println("Vertex : " + v + " - Neighbours : " + v.getNeighbours());
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

    public double getLastDurationInMs() {
        return (double)lastDuration / (double)(1000 * 1000);
    }

    public void setLastDuration(long lastDuration) {
        this.lastDuration = lastDuration;
    }

    public List<List<Integer>> getAdjacentMatrix() {
        return adjacentMatrix;
    }

    public int getTotalWeigth()
    {
        int total = 0;
        for (Edge e:
             this.edges) {
            total += e.getValue();
        }
        return total;
    }
}
