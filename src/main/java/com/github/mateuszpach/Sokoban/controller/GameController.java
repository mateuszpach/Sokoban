package com.github.mateuszpach.Sokoban.controller;

import com.github.mateuszpach.Sokoban.model.Game;
import com.github.mateuszpach.Sokoban.model.Level;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;

public class GameController {

    private static GameController gameController;

    public BorderPane root;
    public GridPane gridPane;
    public StackPane gridPaneBox;
    private ArrayList<ArrayList<Pane>> squares;
    private Level level;

    public static void registerKeyHandlers(Scene gameScene) {
        gameScene.setOnKeyPressed(e -> {
            Game.Direction direction = null;
            switch (e.getCode()) {
                case UP:
                    direction = Game.Direction.LEFT;
                    break;
                case RIGHT:
                    direction = Game.Direction.DOWN;
                    break;
                case DOWN:
                    direction = Game.Direction.RIGHT;
                    break;
                case LEFT:
                    direction = Game.Direction.UP;
                    break;
                case ESCAPE:
                    if (SceneManager.getCurrentSceneType() == SceneManager.SceneType.GAME) {
                        SceneManager.changeScene(SceneManager.SceneType.EXIT);
                    }
                    break;
            }
            if (direction != null) {
                ArrayList<Game.Update> updates = Game.move(direction);
                for (Game.Update update : updates) {
                    Circle circle = (Circle) gameController.squares.get(update.prev.x).get(update.prev.y).getChildren().get(0);
                    gameController.squares.get(update.curr.x).get(update.curr.y).getChildren().add(circle);
//                    switch (update.type) {
//                        case BOX:
//                            Circle circle = (Circle) gameController.squares.get(update.prev.x).get(update.prev.y).getChildren().get(0);
//                            gameController.squares.get(update.curr.x).get(update.curr.y).getChildren().add(circle);
//                            break;
//                        case PLAYER:
//                            Circle circle2 = (Circle) gameController.squares.get(update.prev.x).get(update.prev.y).getChildren().get(0);
//                            gameController.squares.get(update.curr.x).get(update.curr.y).getChildren().add(circle2);
//                            break;
//                    }
                }
                if (Game.is_finished()) {
                    SceneManager.changeScene(SceneManager.SceneType.WIN);
                }
            }
        });
    }

    public void drawBoard() {
        level = Game.getLevel();

        NumberBinding binding = Bindings.min(root.widthProperty(), root.heightProperty());
        gridPaneBox.minHeightProperty().bind(binding);
        gridPaneBox.maxHeightProperty().bind(binding);
        gridPane.minWidthProperty().bind(gridPane.heightProperty());
        gridPane.maxWidthProperty().bind(gridPane.heightProperty());

        gridPane.getChildren().clear();
        gridPane.getColumnConstraints().clear();
        gridPane.getRowConstraints().clear();

        for (int i = 0; i < level.board.size(); i++) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPercentWidth(100d / level.boardSize);
            gridPane.getColumnConstraints().add(columnConstraints);
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPercentHeight(100d / level.boardSize);
            gridPane.getRowConstraints().add(rowConstraints);
        }

        squares = new ArrayList<>();

        int rowI = 0;
        int colI;
        for (ArrayList<Level.Field> row : level.board) {
            colI = 0;
            squares.add(new ArrayList<>());
            for (Level.Field field : row) {
                Pane square = new AnchorPane();
//                square.getChildren().add(new Label(rowI + " " + colI));
                squares.get(rowI).add(square);
                String color;

                if (field.type == Level.Field.FieldType.WALL) {
                    color = "#516A7B";
                } else if (field.type == Level.Field.FieldType.EMPTY) {
                    color = "#FCFAFA";
                } else {
                    color = "#B2FBCA";
                }

                square.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);

                if (field.hasBox) {
                    Circle boxCircle = new Circle();
                    boxCircle.centerXProperty().bind(square.widthProperty().divide(2));
                    boxCircle.centerYProperty().bind(square.heightProperty().divide(2));
                    boxCircle.radiusProperty().bind(square.heightProperty().divide(4));
                    boxCircle.setFill(Color.web("#0CCA4A"));
                    square.getChildren().add(boxCircle);
                }

                square.setStyle("-fx-background-color: " + color + ";");
                gridPane.add(square, colI, rowI);
                colI++;
            }
            rowI++;
        }

        Circle playerCircle = new Circle();
        Pane square = squares.get(0).get(0);
        playerCircle.centerXProperty().bind(square.widthProperty().divide(2));
        playerCircle.centerYProperty().bind(square.heightProperty().divide(2));
        playerCircle.radiusProperty().bind(square.heightProperty().divide(4));
        playerCircle.setFill(javafx.scene.paint.Color.RED);
        squares.get(level.playerPos.x).get(level.playerPos.y).getChildren().add(playerCircle);
    }

    public void initialize() {
        gameController = this;
    }

    public static void initNewGame() {
        gameController.drawBoard();
    }

}