public class ArrayDeque<T> implements  Deque<T> {
    private T[] items;
    private int size;
    private int first;
    private int last;

    public ArrayDeque() {
        this.items = (T[]) new Object[8];
        this.size = 0;
        this.first = 0;
        this.last = 1;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public T get(int i) {
        if (i >= this.items.length) {
            return null;
        }
        return this.items[plusIndex(i + this.first)];
    }

    private int plusIndex(int index) {
        return ((index + 1) % this.items.length);
    }

    private int minusIndex(int index) {
        return ((index - 1 + this.items.length) % this.items.length);
    }

    private boolean isDown() {
        double sizes = this.size;
        double length = this.items.length;
        double usage = sizes / length;
        if (length >= 16 && usage < 0.25) {
            return true;
        }
        return false;
    }

    private boolean isFull() {
        return (this.size == this.items.length);
    }

    @Override
    public boolean isEmpty() {
        return (this.size < 1);
    }

    private void resize(int capacity) {
        T[] item = (T[]) new Object[capacity];
        int second = plusIndex(this.first);
        for (int i = 1; i <= this.size; i++) {
            item[i] = this.items[second];
            second = plusIndex(second);
        }
        this.items = item;
        this.first = 0;
        this.last = this.size + 1;
    }

    @Override
    public void addFirst(T item) {
        if (isFull()) {
            this.resize(size * 2);
        }
        this.items[this.first] = item;
        this.first = minusIndex(this.first);
        this.size += 1;
    }

    @Override
    public void addLast(T item) {
        if (isFull()) {
            this.resize(size * 2);
        }
        this.items[this.last] = item;
        this.last = plusIndex(this.last);
        this.size += 1;
    }

    @Override
    public T removeLast() {
        if (this.isEmpty()) {
            return null;
        }
        if (isDown()) {
            this.resize(this.items.length / 2);
        }
        this.last = minusIndex(this.last);
        T val = this.items[last];
        this.items[last] = null;
        this.size -= 1;
        return val;
    }

    @Override
    public T removeFirst() {
        if (this.isEmpty()) {
            return null;
        }
        if (isDown()) {
            this.resize(this.items.length / 2);
        }
        this.first = plusIndex(this.first);
        T val = this.items[first];
        this.items[first] = null;
        this.size -= 1;
        return val;
    }

    @Override
    public void printDeque() {
        for (int i = 0; i < this.items.length; i++) {
            System.out.print(this.items[i] + " ");
        }
    }
}

