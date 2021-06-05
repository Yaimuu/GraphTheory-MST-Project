import java.io.File;
import java.util.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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
    }

    public Graph(String name, boolean directed, List<Vertex> vertices, List<Edge> edges)
    {
        this(edges, vertices);
        this.name = name;
        this.directed = directed;
    }

    public Graph(Graph newGraph)
    {

    }

    public Graph(String file)
    {
        try {
            Graph newGraph = Graph.readGraphFile(file);
            this.edges = newGraph.edges;
            this.vertices = newGraph.vertices;
            this.name = newGraph.name;
            this.directed = newGraph.directed;
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

    // Complexity : O(m*log(m))
    public Graph Kruskal1()
    {
        long startTime = System.nanoTime();

        Graph kruskal = new Graph(new LinkedList<Edge>(), this.vertices);
        kruskal.setName("Kruskal 1");
        int i = 0;

        List<Edge> edgesSorted = this.edges;

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

        return kruskal;
    }

    // Complexity : O(m*n)
    public Graph Kruskal1V1()
    {
        long startTime = System.nanoTime();

        Graph kruskal = new Graph(new LinkedList<Edge>(), this.vertices);
        kruskal.setName("Kruskal 1");
        int i = 0;

        // Edge list ordered in increasing order
        List<Edge> edgesSorted = this.edges;

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

    // Complexity : O(m*n)
    public Graph Kruskal2()
    {
        long startTime = System.nanoTime();
        // Edge list ordered in decreasing order
        List<Edge> edgesSorted = this.edges;

        Collections.sort(edgesSorted, new Comparator<Edge>() {
            @Override
            public int compare(Edge edge1, Edge edge2) {
                return edge2.getValue() - edge1.getValue();
            }
        });

        Graph kruskal = new Graph(edgesSorted, this.vertices);
        kruskal.setName("Kruskal 2");
        int i = 0;

        while (this.edges.size() >= this.vertices.size())
        {
            Edge currentEdge = kruskal.edges.get(i);

            kruskal.edges.remove(currentEdge);
            if(!kruskal.isConnexe())
            {
                kruskal.edges.add(i, currentEdge);
            }
            else
            {
                i--;
            }

            i++;
        }

//        System.out.println("L'arbre recouvrant de poids minimum est : ");
        long endTime = System.nanoTime();

        kruskal.setLastDuration(endTime-startTime);
        this.setLastDuration(endTime-startTime);

        return kruskal;
    }

    public Graph Prim3()
    {
        System.out.println("Prim");

        long startTime = System.nanoTime();

        int parent[] = new int[this.vertices.size()];

        int key[] = new int[this.vertices.size()];

        List<Vertex> visitedVertices = new LinkedList<>();

        for (int i = 0; i < this.vertices.size(); i++) {
            key[i] = Integer.MAX_VALUE;
        }

        key[0] = 0;
        parent[0] = -1;

        while(visitedVertices.size() != this.vertices.size()) {

            int min = Integer.MAX_VALUE;
            int u = -1;

            for (int v = 0; v < this.vertices.size(); v++)
                if (!visitedVertices.contains(this.vertices.get(v)) && key[v] < min) {
                    min = key[v];
                    u = v;
                }

            visitedVertices.add(this.vertices.get(u));

            for (int v = 0; v < this.vertices.size(); v++)
                if (adjacentMatrix.get(u).get(v) != 0 && !visitedVertices.contains(this.vertices.get(v)) && adjacentMatrix.get(u).get(v) < key[v]) {
                    parent[v] = u;
                    key[v] = adjacentMatrix.get(u).get(v);
                }
        }

        long endTime = System.nanoTime();

        Graph prim = new Graph(new LinkedList<Edge>(), this.vertices);

        prim.setLastDuration(endTime-startTime);
        this.setLastDuration(endTime-startTime);

        return prim;
    }

    public Graph Prim2()
    {
        Graph prim = new Graph(new LinkedList<Edge>(), this.vertices);
        prim.setName("Prim");
        System.out.println("Prim");

        long startTime = System.nanoTime();

        Random rand = new Random();
        int sommetIndice = rand.nextInt(vertices.size());

        Vertex randVertex = this.vertices.get(sommetIndice);
        List<Vertex> savedVertices = new LinkedList<>(Collections.nCopies(vertices.size(), randVertex));
//        savedVertices.add(randVertex);
        List<Integer> savedWeigthes = new ArrayList<>(Collections.nCopies(vertices.size(), Integer.MAX_VALUE));

        List<Edge> edgesSorted = this.edges;

        Collections.sort(edgesSorted, new Comparator<Edge>() {
            @Override
            public int compare(Edge edge1, Edge edge2) {
                return edge1.getValue() - edge2.getValue();
            }
        });

        savedWeigthes.set(0,0);

        for(int i = 0; i < this.vertices.size() - 1; i++)
        {
            int finalI = i;
            int min = Integer.MAX_VALUE;

            for (int n = 0; n < adjacentMatrix.get(i).size() - 1; n++)
            {
                int node = adjacentMatrix.get(i).get(n), nextNode = adjacentMatrix.get(i).get(n+1);

                if(node < nextNode && node != 0 && !savedVertices.contains(vertices.get(n)))
                {
                    min = node;
                }
                else if(node < nextNode && nextNode != 0 && !savedVertices.contains(vertices.get(n+1)))
                    min = nextNode;
            }

            int minId = adjacentMatrix.get(i).indexOf(min);

            Vertex minVertex = this.vertices.get(minId);
            System.out.println(savedWeigthes);
            System.out.println("min : " + min + " - minId : " + minId);

            for(int j = 0; j < this.vertices.size(); j++)
            {
                if(adjacentMatrix.get(minId).get(j) != 0 &&
                        adjacentMatrix.get(minId).get(j) < savedWeigthes.get(j) &&
                        savedVertices.contains(vertices.get(j)))
                {
                    System.out.println("i : " + i + " - j : " + j);

                    prim.edges.add(this.findEdge(vertices.get(minId), vertices.get(j)));
                    savedWeigthes.set(j, adjacentMatrix.get(minId).get(j));
                    savedVertices.set(minId, this.vertices.get(j));

                    System.out.println(savedVertices);
                }
            }

        }

        long endTime = System.nanoTime();

        prim.setLastDuration(endTime-startTime);
        this.setLastDuration(endTime-startTime);

        return prim;
    }

public Graph Prim()
{
    Graph prim = new Graph(new LinkedList<Edge>(), this.vertices);
    prim.setName("Prim");
    System.out.println("Prim");

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

    return prim;
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

    public boolean isConnexe()
    {
        List<Vertex> savedVertices = new LinkedList<>();

        for (Edge edge:
             this.edges)
        {
            Vertex start = edge.getStart();
            Vertex end = edge.getEnd();

            if(!savedVertices.contains(start))
            {
                savedVertices.add(start);
            }
            if(!savedVertices.contains(end))
            {
                savedVertices.add(end);
            }

            if(savedVertices.size() == this.vertices.size())
            {
                return true;
            }
        }

        return false;
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
                            Edge edge = new Edge(vertices.get(Integer.parseInt(spllitedLine[0])), vertices.get(Integer.parseInt(spllitedLine[1])), Integer.parseInt(spllitedLine[2]));
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
