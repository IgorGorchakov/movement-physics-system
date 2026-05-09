package com.example.ui;

import java.awt.Color;

public record DragState(int startX, int startY, int curX, int curY, double mass, Color color) {
}
