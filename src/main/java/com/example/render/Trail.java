package com.example.render;

class Trail {
    private final double[] x;
    private final double[] y;
    private final int capacity;
    private int head = 0;
    private int fill = 0;

    Trail(int capacity) {
        this.capacity = capacity;
        this.x = new double[capacity];
        this.y = new double[capacity];
    }

    void push(double px, double py) {
        x[head] = px;
        y[head] = py;
        head = (head + 1) % capacity;
        if (fill < capacity) fill++;
    }

    int size() { return fill; }

    double xAt(int i) { return x[(head - fill + capacity + i) % capacity]; }
    double yAt(int i) { return y[(head - fill + capacity + i) % capacity]; }
}
