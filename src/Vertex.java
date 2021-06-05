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
    }

    public Vertex(int id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public String toString()
    {
        return name;
    }

    public int getId() {
        return id;
    }
}
