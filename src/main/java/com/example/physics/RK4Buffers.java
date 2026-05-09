package com.example.physics;

class RK4Buffers {
    double[] m, x, y, vx, vy;
    double[] k1vx, k1vy, k2vx, k2vy, k3vx, k3vy, k4vx, k4vy;
    double[] k2x, k2y, k3x, k3y, k4x, k4y;
    double[] tx, ty, tvx, tvy;
    private int capacity = 0;

    void ensureCapacity(int n) {
        if (n <= capacity) return;
        int cap = Math.max(16, capacity == 0 ? n : capacity);
        while (cap < n) cap *= 2;
        m = new double[cap];
        x = new double[cap]; y = new double[cap];
        vx = new double[cap]; vy = new double[cap];
        k1vx = new double[cap]; k1vy = new double[cap];
        k2vx = new double[cap]; k2vy = new double[cap];
        k3vx = new double[cap]; k3vy = new double[cap];
        k4vx = new double[cap]; k4vy = new double[cap];
        k2x = new double[cap]; k2y = new double[cap];
        k3x = new double[cap]; k3y = new double[cap];
        k4x = new double[cap]; k4y = new double[cap];
        tx = new double[cap]; ty = new double[cap];
        tvx = new double[cap]; tvy = new double[cap];
        capacity = cap;
    }
}
