import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

public class Shape implements Iterable<Dot> {
    Node header;

    @Override
    public Iterator<Dot> iterator() {
        return new Iterator<>() {
            Node currNode = header;

            @Override
            public boolean hasNext() {
                return currNode.next != header;
            }

            @Override
            public Dot next() {
                currNode = currNode.next;
                return currNode.element;
            }
        };
    }
    class Node {
        Node next, prev;
        Dot element;

        public Node(Node next, Node prev, Dot element) {
            this.next = next;
            this.prev = prev;
            this.element = element;
        }
    }

    public Shape(Dot... dots) {
        header = new Node(null, null, dots[0]);
        Node currNode = header;
        for (int a = 1; a < dots.length; a++) {
            currNode.next = new Node(null, null, dots[a]);
            currNode.next.prev = currNode;
            currNode = currNode.next;
        }
        //TODO undo comment
        //currNode.next = header;
        //header.prev = currNode;
    }

    public int getCornerCount() {
        int size = 1;
        Node currNode = header.next;
        while (!(currNode == header || currNode == null)) {
            size++;
            currNode = currNode.next;
        }
        return size;
    }

    public Dot getDot(int index) {
        if (index >= getCornerCount()) throw new IndexOutOfBoundsException(index + " >= " + getCornerCount());
        Node currNode = header;
        for (int a = 0; a < index; a++) {
            currNode = currNode.next;
        }
        return currNode.element;
    }
}
