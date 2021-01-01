package com.github.mateuszpach.Sokoban.model;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Level {

    public static class Field {

        public enum FieldType {TARGET, EMPTY, WALL}

        public FieldType type;

        public boolean hasBox;

        public Field() {
            type = FieldType.WALL;
            hasBox = false;
        }

        public Field(Field field) {
            type = field.type;
            hasBox = field.hasBox;
        }

        @Override
        public String toString() {
            return "type: " + type + ", hasBox: " + hasBox;
        }
    }

    public ArrayList<ArrayList<Field>> board;
    public int boardSize;
    public Coords playerPos;
    public int matched;
    public int targets;

    protected ArrayList<Coords> boxesCoords;
    protected ArrayList<Coords> targetsCoords;

    protected Level(int size, int targets) {
        board = Stream.generate(
                () -> Stream.generate(Level.Field::new).limit(size).collect(Collectors.toCollection(ArrayList::new))
        ).limit(size).collect(Collectors.toCollection(ArrayList::new));
        boardSize = size;
        this.matched = 0;
        this.targets = targets;

        boxesCoords = new ArrayList<>();
        targetsCoords = new ArrayList<>();
    }

    protected Level(Level level) {
        board = new ArrayList<>();
        for (ArrayList<Field> row : level.board) {
            ArrayList<Field> newRow = new ArrayList<>();
            board.add(newRow);
            for (Field field : row) {
                newRow.add(new Field(field));
            }
        }
        boardSize = level.boardSize;
        matched = level.matched;
        targets = level.targets;
        playerPos = new Coords(level.playerPos);

        boxesCoords = level.boxesCoords.stream().map(Coords::new).collect(Collectors.toCollection(ArrayList::new));
        targetsCoords = level.targetsCoords.stream().map(Coords::new).collect(Collectors.toCollection(ArrayList::new));
    }
}
