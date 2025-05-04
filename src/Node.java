public class Node {
    private int weight;
    private Node next;
    private int row;
    private int col;

    private int queueIndex;
    private boolean inQueue;

    public Node(int weight, int row, int col) {
        this.weight = weight;
        this.next = null;
        this.row = row;
        this.col = col;
        queueIndex = -1;
        inQueue = false;
    }

    public int getWeight() {
        return weight;
    }

    public Node getNext() {
        return next;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getQueueIndex() {
        return queueIndex;
    }

    public boolean getInQueue() {
        return inQueue;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public void setQueueIndex(int queueIndex) {
        this.queueIndex = queueIndex;
    }

    public void setInQueue(boolean inQueue) {
        this.inQueue = inQueue;
    }
}
