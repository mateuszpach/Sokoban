package com.github.mateuszpach.Sokoban.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Paths {
    private final static int WALL_COST = 100;
    private final static int BOX_COST = 10000;
    private final static int FREE_COST = 1;
    private final static int FREE_COST_PLAYER = -1;

    protected ArrayList<ArrayList<Integer>> dist;
    protected ArrayList<ArrayList<Coords>> prev;

    Level level;
    Coords source;
    int padding;

    Paths(Level level, Coords source, int padding) {
        dist = Stream.generate(
                () -> Stream.generate(() -> Integer.MAX_VALUE).limit(level.boardSize).collect(Collectors.toCollection(ArrayList::new))
        ).limit(level.boardSize).collect(Collectors.toCollection(ArrayList::new));

        prev = Stream.generate(
                () -> Stream.generate(Coords::new).limit(level.boardSize).collect(Collectors.toCollection(ArrayList::new))
        ).limit(level.boardSize).collect(Collectors.toCollection(ArrayList::new));

        this.level = level;
        this.source = source;
        this.padding = padding;

        dijkstra();
    }

    // class used for dijkstra()
    private class Node implements Comparable<Node> {
        Coords coords;

        Node(Coords coords) {
            this.coords = coords;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Node)) {
                return false;
            }
            Node o = (Node) obj;
            return coords.x == o.coords.x && coords.y == o.coords.y;
        }

        @Override
        public int hashCode() {
            return coords.x * 100 + coords.y;
        }

        @Override
        public int compareTo(Node o) {
            int distDiff = dist.get(coords.x).get(coords.y) - dist.get(o.coords.x).get(o.coords.y);
            if (distDiff != 0) {
                return distDiff;
            } else if (coords.x != o.coords.x) {
                return coords.x - o.coords.x;
            } else {
                return coords.y - o.coords.y;
            }
        }
    }

    private void dijkstra() {
        // find paths preferring the ones being long (when are of player type) and avoiding walls and boxes
        TreeSet<Node> nodes = new TreeSet<>();

        int[] dx = {0, 1, 0, -1};
        int[] dy = {1, 0, -1, 0};

        dist.get(source.x).set(source.y, 0);
        prev.get(source.x).set(source.y, source);
        nodes.add(new Node(source));

        while (!nodes.isEmpty()) {
            Node u = nodes.pollFirst();

            for (int i = 0; i < 4; i++) {
                Coords vCoords = new Coords(u.coords.x + dx[i], u.coords.y + dy[i]);
                if (padding <= vCoords.x && vCoords.x < level.boardSize - padding
                        && padding <= vCoords.y && vCoords.y < level.boardSize - padding) {
                    Node v = new Node(vCoords);
                    int alt = dist.get(u.coords.x).get(u.coords.y);
                    Level.Field vField = level.board.get(vCoords.x).get(vCoords.y);
                    if (vField.hasBox) {
                        alt += BOX_COST;
                    } else if (vField.type == Level.Field.FieldType.WALL) {
                        alt += WALL_COST;
                    } else {
                        if (level.playerPos.x == source.x && level.playerPos.y == source.y) {
                            alt += FREE_COST_PLAYER;
                        } else {
                            alt += FREE_COST;
                        }
                    }
                    int vDist = dist.get(vCoords.x).get(vCoords.y);
                    if (alt < vDist) {
                        if (nodes.contains(v)) {
                            nodes.remove(v);
                            dist.get(vCoords.x).set(vCoords.y, alt);
                            prev.get(vCoords.x).set(vCoords.y, u.coords);
                            nodes.add(v);
                        } else if (vDist == Integer.MAX_VALUE) {
                            dist.get(vCoords.x).set(vCoords.y, alt);
                            prev.get(vCoords.x).set(vCoords.y, u.coords);
                            nodes.add(v);
                        }
                    }
                }
            }
        }
    }

    protected Coords getPlayerStart(Coords target) {
        Coords next = null;
        while (!(target.x == source.x && target.y == source.y)) {
            next = target;
            target = prev.get(target.x).get(target.y);
        }
        return new Coords(2 * source.x - next.x, 2 * source.y - next.y);
    }

    protected int weightTo(Coords target) {
        return dist.get(target.x).get(target.y);
    }

    protected ArrayList<Coords> pathTo(Coords target) {
        ArrayList<Coords> path = new ArrayList<>();
        while (!(target.x == source.x && target.y == source.y)) {
            path.add(new Coords(target));
            target = prev.get(target.x).get(target.y);
        }
        Collections.reverse(path);
        return path;
    }
}
