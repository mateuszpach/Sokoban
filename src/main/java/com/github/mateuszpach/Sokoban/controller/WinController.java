package com.github.mateuszpach.Sokoban.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;

public class WinController {
    public void back(ActionEvent actionEvent) {
        SceneManager.changeScene(SceneManager.SceneType.INTRO);
    }

    public void quit(ActionEvent actionEvent) {
        Platform.exit();
        System.exit(0);
    }
}
