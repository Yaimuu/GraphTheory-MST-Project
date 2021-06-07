import java.util.LinkedList;
import java.util.List;

public class Vertex
{
    private int id;
    private String name;
    private List<Vertex> neighbours;
    private List<Edge> edges;

    public Vertex(int id)
    {
        this.id = id;
        this.name = id + "";
        this.neighbours = new LinkedList<>();
        this.edges = new LinkedList<>();
    }

    public Vertex(int id, String name)
    {
        this.id = id;
        this.name = name;
        this.neighbours = new LinkedList<>();
        this.edges = new LinkedList<>();
    }

    public Edge getEdge(Vertex v)
    {
        return this.edges.stream().filter(e -> (e.getStart() == this && e.getEnd() == v) || (e.getStart() == v && e.getEnd() == this) )
                .findFirst().get();
    }

    public String toString()
    {
        return name;
    }

    public int getId() {
        return id;
    }

    public List<Vertex> getNeighbours() {
        return neighbours;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public int getDegree()
    {
        return this.neighbours.size();
    }

    public String getName() {
        return name;
    }
    
}
