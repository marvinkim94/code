public class LinkedListDeque<T> implements Deque<T> {
    private class Node {
        T item;
        Node prev;
        Node next;

        Node(Node p, T i, Node n) {
            item = i;
            prev = p;
            next = n;
        }
    }

    private Node sentinel;
    private Node first;
    private Node last;
    private int size;

    public LinkedListDeque() {
        sentinel = new Node(sentinel, null, sentinel);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }

    @Override
    public int size() {
        return this.size;
    }

    public boolean isEmpty() {
        return (this.size == 0);
    }

    @Override
    public void addLast(T item) {
        last = sentinel.prev;
        sentinel.prev = new Node(last, item, sentinel);
        last.next = sentinel.prev;
        size++;
    }

    @Override
    public void addFirst(T item) {
        first = sentinel.next;
        sentinel.next = new Node(sentinel, item, first);
        first.prev = sentinel.next;
        size++;
    }

    @Override
    public T get(int index) {
        Node curr = sentinel.next;
        while (index > 0) {
            curr = curr.next;
            index--;
        }
        return curr.item;
    }

    private T recursiveHelper(Node curr, int index) {
        if (index == 0) {
            return curr.item;
        }
        if (index < 0) {
            return null;
        }
        return recursiveHelper(curr.next, index - 1);
    }

    public T getRecursive(int index) {
        if (index > size) {
            return null;
        }
        return recursiveHelper(sentinel.next, index);
    }

    @Override
    public void printDeque() {
        Node curr = sentinel.next;
        while (curr != sentinel) {
            System.out.print(curr.item + " ");
            curr = curr.next;
        }
    }

    @Override
    public T removeFirst() {
        if (sentinel.next == sentinel) {
            return null;
        }
        first = sentinel.next;
        sentinel.next = first.next;
        first.next.prev = sentinel;
        size--;
        return first.item;
    }

    @Override
    public T removeLast() {
        if (sentinel.next == sentinel) {
            return null;
        }
        last = sentinel.prev;
        sentinel.prev = last.prev;
        last.prev.next = sentinel;
        size--;
        return last.item;
    }
}
