public class PriorityQueue {
    private final Node[] queue;
    private int currSize;

    PriorityQueue(int n) {
        queue = new Node[n];
        currSize = 0;
    }

    public void addToQueue(Node x) {
        queue[currSize] = x;
        x.setQueueIndex(currSize);
        x.setInQueue(true);
        int newIndex = currSize;
        currSize++;
        swimUp(newIndex);
    }

    public void updateQueue(Node x, int newD) {
        if (newD > queue[x.getQueueIndex()].getWeight()) {
            sinkDown(x.getQueueIndex());
        }
        else {
            swimUp(x.getQueueIndex());
        }
    }

    public Node deleteMin() {
        Node min = queue[0];
        min.setInQueue(false);
        currSize--;
        Node last = queue[currSize];
        if (last == min) {
            queue[0] = null;
        }
        else {
            queue[currSize] = null;
            queue[0] = last;
            sinkDown(0);
        }
        return min;
    }

    private void swimUp(int newIndex) {
        int parentIndex = (newIndex - 1) / 2;
        if (queue[newIndex].getWeight() < queue[parentIndex].getWeight()) {
            // swap
            Node temp = queue[parentIndex];
            queue[parentIndex] = queue[newIndex];
            queue[newIndex] = temp;

            // update indices
            queue[parentIndex].setQueueIndex(parentIndex);
            queue[newIndex].setQueueIndex(newIndex);
            swimUp(parentIndex);
        }
    }

    private void sinkDown(int index) {
        int newIndex = -1;
        int leftIndex = -1;
        int rightIndex = -1;
        if ((2 * index) + 1 < currSize) {
            leftIndex = (2 * index) + 1;
        }
        if ((2 * index) + 2 < currSize) {
            rightIndex = (2 * index) + 2;
        }

        if (leftIndex == -1 && rightIndex == -1) {
            // no children
            return;
        }
        else if (rightIndex == -1) {
            if (queue[leftIndex].getWeight() < queue[index].getWeight()) {
                // no right child
                Node temp = queue[index];
                queue[index] = queue[leftIndex];
                queue[leftIndex] = temp;

                queue[index].setQueueIndex(index);
                queue[leftIndex].setQueueIndex(leftIndex);
                newIndex = leftIndex;
            }
        }
        else {
            if (queue[leftIndex].getWeight() <= queue[rightIndex].getWeight()) {
                if (queue[leftIndex].getWeight() < queue[index].getWeight()) {
                    Node temp = queue[index];
                    queue[index] = queue[leftIndex];
                    queue[leftIndex] = temp;

                    queue[index].setQueueIndex(index);
                    queue[leftIndex].setQueueIndex(leftIndex);
                    newIndex = leftIndex;
                }
            }
            else {
                if (queue[rightIndex].getWeight() < queue[index].getWeight()) {
                    Node temp = queue[index];
                    queue[index] = queue[rightIndex];
                    queue[rightIndex] = temp;

                    queue[index].setQueueIndex(index);
                    queue[rightIndex].setQueueIndex(rightIndex);
                    newIndex = rightIndex;
                }
            }
        }
        if (newIndex != -1) {
            sinkDown(newIndex);
        }
    }

    public int getCurrSize() {
        return currSize;
    }
}
