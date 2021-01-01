package com.github.mateuszpach.Sokoban.model;

import java.util.ArrayList;

public class Game {
    public static class Update {
        public enum UpdateType {BOX, PLAYER}

        public UpdateType type;
        public Coords prev;
        public Coords curr;

        Update(UpdateType type, Coords prev, Coords curr) {
            this.type = type;
            this.prev = prev;
            this.curr = curr;
        }
    }

    public enum Direction {UP, RIGHT, DOWN, LEFT}

    private static Level level;
    private static Level level_backup;

    private static int size = 7;
    private static int targets = 3;

    public static void setSize(int size) {
        Game.size = size;
    }

    public static void setTargets(int targets) {
        Game.targets = targets;
    }

    public static void generate() {
        level = LevelFactory.getLevel(size, targets);
        level_backup = new Level(level);
    }

    public static void recover() {
        level = level_backup;
        level_backup = new Level(level);
    }

    public static Level getLevel() {
        return level;
    }

    public static boolean is_finished() {
        return level.matched == level.targets;
    }

    public static ArrayList<Update> move(Direction direction) {
        ArrayList<Update> updates = new ArrayList<>();

        // determine destinations
        Coords playerDest = new Coords(level.playerPos);
        Coords boxDest = new Coords(level.playerPos);
        switch (direction) {
            case UP:
                playerDest.y -= 1;
                boxDest.y -= 2;
                break;
            case RIGHT:
                playerDest.x += 1;
                boxDest.x += 2;
                break;
            case DOWN:
                playerDest.y += 1;
                boxDest.y += 2;
                break;
            case LEFT:
                playerDest.x -= 1;
                boxDest.x -= 2;
                break;
        }

        // assert player_dest is a valid field
        if (!(0 <= playerDest.y && playerDest.y < level.boardSize
                && 0 <= playerDest.x && playerDest.x < level.boardSize)) {
            return updates;
        }
        Level.Field playerDestField = level.board.get(playerDest.x).get(playerDest.y);
        if (playerDestField.type == Level.Field.FieldType.WALL) {
            return updates;
        }

        // check if there is a box on the player destination
        if (playerDestField.hasBox) {
            // assert box can be moved
            if (!(0 <= boxDest.y && boxDest.y < level.boardSize

                    && 0 <= boxDest.x && boxDest.x < level.boardSize)) {
                return updates;
            }
            Level.Field boxDestField = level.board.get(boxDest.x).get(boxDest.y);
            if (boxDestField.type == Level.Field.FieldType.WALL || boxDestField.hasBox) {
                return updates;
            }

            // update box
            playerDestField.hasBox = false;
            boxDestField.hasBox = true;
            if (playerDestField.type == Level.Field.FieldType.TARGET && boxDestField.type != Level.Field.FieldType.TARGET) {
                level.matched--;
            } else if (playerDestField.type != Level.Field.FieldType.TARGET && boxDestField.type == Level.Field.FieldType.TARGET) {
                level.matched++;
            }
            updates.add(new Update(Update.UpdateType.BOX, playerDest, boxDest));
        }

        // update player
        updates.add(new Update(Update.UpdateType.PLAYER, level.playerPos, playerDest));
        level.playerPos = playerDest;

        return updates;
    }

}
