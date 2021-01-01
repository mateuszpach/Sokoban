package com.github.mateuszpach.Sokoban.model;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class LevelFactory {
    private final static int MAX_STEPS = 400;

    private final static int MIN_GEN_EMPTY = 1;
    private final static int MAX_GEN_EMPTY = 5;

    private static ArrayList<Coords> coordsPool;

    public static Level getLevel(int size, int targets) {
        Level level = new Level(size, targets);
        try {
            LevelFactory.prepareCoordsPool(size);
            LevelFactory.placePlayer(level);
            LevelFactory.placeBoxesAndTargets(level);
            LevelFactory.generateEmptyFields(level, ThreadLocalRandom.current().nextInt(MIN_GEN_EMPTY, MAX_GEN_EMPTY));
            LevelFactory.generatePaths(level);
        } catch (Exception e) {
            return getLevel(size, targets);
        }
        return level;
    }

    private static void prepareCoordsPool(int size) {
        coordsPool = new ArrayList<>();
        for (int i = 1; i < size - 2; i++) {
            for (int j = 1; j < size - 2; j++) {
                coordsPool.add(new Coords(i, j));
            }
        }
        Collections.shuffle(coordsPool);
    }

    private static Coords getRandomCoords(Level level) {
        if (!coordsPool.isEmpty()) {
            int index = coordsPool.size() - 1;
            Coords coords = coordsPool.get(index);
            coordsPool.remove(index);
            Level.Field center = level.board.get(coords.x).get(coords.y);
            center.type = Level.Field.FieldType.EMPTY;
            level.board.get(coords.x).get(coords.y).type = Level.Field.FieldType.EMPTY;

            //assert center is not blocked by boxes
            Level.Field n = level.board.get(coords.x).get(coords.y - 1);
            Level.Field ne = level.board.get(coords.x + 1).get(coords.y - 1);
            Level.Field e = level.board.get(coords.x + 1).get(coords.y);
            Level.Field se = level.board.get(coords.x + 1).get(coords.y + 1);
            Level.Field s = level.board.get(coords.x).get(coords.y + 1);
            Level.Field sw = level.board.get(coords.x - 1).get(coords.y + 1);
            Level.Field w = level.board.get(coords.x - 1).get(coords.y);
            Level.Field nw = level.board.get(coords.x - 1).get(coords.y - 1);
            if ((n.hasBox && ne.hasBox && e.hasBox) || (e.hasBox && se.hasBox && s.hasBox)
                    || (s.hasBox && sw.hasBox && w.hasBox) || (w.hasBox && nw.hasBox && n.hasBox)) {
                return getRandomCoords(level);
            }

            return coords;
        } else {
            return null;
        }
    }

    private static void placePlayer(Level level) throws IllegalStateException {
        level.playerPos = getRandomCoords(level);
        if (level.playerPos == null) {
            throw new IllegalStateException();
        }
    }

    private static void placeBoxesAndTargets(Level level) throws IllegalStateException {
        for (int i = 0; i < level.targets; i++) {
            Coords boxCoords = getRandomCoords(level);
            Coords targetCoords = getRandomCoords(level);
            if (boxCoords == null || targetCoords == null) {
                level.targets = i;
                break;
            }
            level.boxesCoords.add(boxCoords);
            level.board.get(boxCoords.x).get(boxCoords.y).hasBox = true;
            level.targetsCoords.add(targetCoords);
            level.board.get(targetCoords.x).get(targetCoords.y).type = Level.Field.FieldType.TARGET;
        }
        if (level.targets < 1) {
            throw new IllegalStateException();
        }
    }

    private static void generateEmptyFields(Level level, int n) {
        Coords coords;
        do {
            coords = getRandomCoords(level);
            n--;
        } while (coords != null && n > 0);
    }

    private static void generatePaths(Level level) throws IllegalStateException {
        ArrayList<Coords> boxesBackup = level.boxesCoords.stream().map(Coords::new).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<Coords> targetsBackup = level.targetsCoords.stream().map(Coords::new).collect(Collectors.toCollection(ArrayList::new));
        Coords playerPosBackup = new Coords(level.playerPos);
        int steps = 0;

        while (level.matched < level.targets) {
            // find optimal paths
            Paths pathsFromPlayer = new Paths(level, level.playerPos, 0);

            int bestWeight = Integer.MAX_VALUE;
            Coords bestBox = null;
            Coords bestBoxTarget = null;
            Paths pathsFromBestBox = null;
            Coords bestBoxPlayerStart = null;

            Iterator<Coords> boxesIt = level.boxesCoords.iterator();
            Iterator<Coords> targetsIt = level.targetsCoords.iterator();
            while (boxesIt.hasNext() && targetsIt.hasNext()) {
                Coords boxCoords = boxesIt.next();
                Coords targetCoords = targetsIt.next();

                Paths pathsFromBox = new Paths(level, boxCoords, 1);
                Coords playerStart = pathsFromBox.getPlayerStart(targetCoords);

                int weight = pathsFromBox.weightTo(targetCoords) + pathsFromPlayer.weightTo(playerStart);
                if (weight < bestWeight) {
                    bestWeight = weight;
                    bestBox = boxCoords;
                    bestBoxTarget = targetCoords;
                    pathsFromBestBox = pathsFromBox;
                    bestBoxPlayerStart = playerStart;
                }
            }

            // empty fields on player's path
            for (Coords coords : pathsFromPlayer.pathTo(bestBoxPlayerStart)) {
                Level.Field field = level.board.get(coords.x).get(coords.y);
                if (field.hasBox) {
                    throw new IllegalStateException();
                }
                if (field.type == Level.Field.FieldType.WALL) {
                    field.type = Level.Field.FieldType.EMPTY;
                }
            }

            // empty fields on box's path until a turn
            ArrayList<Coords> path = pathsFromBestBox.pathTo(bestBoxTarget);
            Coords firstOnPath = path.get(0);
            int lastEmptiedOnPath = -1;
            for (Coords coords : path) {
                if ((firstOnPath.x != bestBox.x && firstOnPath.y != coords.y)
                        || (firstOnPath.y != bestBox.y && firstOnPath.x != coords.x)) {
                    break;
                }
                Level.Field field = level.board.get(coords.x).get(coords.y);
                if (field.hasBox) {
                    throw new IllegalStateException();
                }
                if (field.type == Level.Field.FieldType.WALL) {
                    field.type = Level.Field.FieldType.EMPTY;
                }
                lastEmptiedOnPath++;
            }

            // update player's and box's position (we know path is not empty)
            if (lastEmptiedOnPath > 0) {
                level.playerPos = path.get(lastEmptiedOnPath - 1);
            } else {
                level.playerPos = bestBox;
            }
            level.board.get(bestBox.x).get(bestBox.y).hasBox = false;
            level.boxesCoords.set(level.boxesCoords.indexOf(bestBox), new Coords(path.get(lastEmptiedOnPath)));
            bestBox = new Coords(path.get(lastEmptiedOnPath));
            level.board.get(bestBox.x).get(bestBox.y).hasBox = true;

            // check if box reached its target
            if (bestBox.x == bestBoxTarget.x && bestBox.y == bestBoxTarget.y) {
                steps = 0;
                level.matched++;
                Coords finalBestBox = bestBox;
                Coords finalBestBoxTarget = bestBoxTarget;
                level.boxesCoords.removeIf((c) -> c.x == finalBestBox.x && c.y == finalBestBox.y);
                level.targetsCoords.removeIf((c) -> c.x == finalBestBoxTarget.x && c.y == finalBestBoxTarget.y);
            }

            steps++;
            if (steps > MAX_STEPS) {
                throw new IllegalStateException();
            }
        }

        level.playerPos = playerPosBackup;
        for (Coords coords : boxesBackup) {
            level.board.get(coords.x).get(coords.y).hasBox = true;
        }
        for (Coords coords : targetsBackup) {
            level.board.get(coords.x).get(coords.y).hasBox = false;
        }

        level.matched = 0;
    }
}
