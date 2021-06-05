import java.util.LinkedList;
import java.util.List;

public class Vertex {
    private int id;
    private String name;
    private List<Vertex> parents;
    private List<Edge> edges;
    private int mDegree;
    private int mDegreePos;
    private int mDegreeNeg;
    private int mColor;
    private int mNbValues;
    private double mValues[];

    public Vertex(int id)
    {
        this.id = id;
        this.name = id + "";
        this.parents = new LinkedList<>();
        this.edges = new LinkedList<>();
    }

    public Vertex(int id, String name)
    {
        this.id = id;
        this.name = name;
        this.parents = new LinkedList<>();
        this.edges = new LinkedList<>();
    }

    public String toString()
    {
        return name;
    }

    public int getId() {
        return id;
    }

    public List<Vertex> getParents() {
        return parents;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public int getDegree()
    {
        return this.parents.size();
    }

    public String getName() {
        return name;
    }
}
