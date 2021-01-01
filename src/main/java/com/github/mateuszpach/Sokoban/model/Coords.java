package com.github.mateuszpach.Sokoban.model;

public class Coords {
    public int x;
    public int y;

    public Coords(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Coords() {
        this.x = -1;
        this.y = -1;
    }

    public Coords(Coords coords) {
        this.x = coords.x;
        this.y = coords.y;
    }

    @Override
    public String toString() {
        return "x: " + x + ", y: " + y;
    }
}
