package com.github.mateuszpach.Sokoban;

import com.github.mateuszpach.Sokoban.controller.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        SceneManager.start(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}