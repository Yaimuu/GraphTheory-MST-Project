public class Edge {
    private int id;
    private int value;
    private String mNameInitialVertex;
    private String mNameFinalVertex;
    private Vertex start;
    private Vertex end;

    public Edge()
    {

    }

    public Edge(Vertex start, Vertex end)
    {
        this.start = start;
        this.end = end;
    }

    public Edge(Vertex start, Vertex end, int value)
    {
        this(start, end);
        this.value = value;
    }

    public String toString()
    {
        return "Start : " + start.toString() + " - End : " + end.toString() + " - Value : " + this.value;
    }

    public Vertex getStart() {
        return start;
    }

    public void setStart(Vertex start) {
        this.start = start;
    }

    public Vertex getEnd() {
        return end;
    }

    public void setEnd(Vertex end) {
        this.end = end;
    }

    public int getValue() {
        return value;
    }

    public int getId() {
        return id;
    }
}
