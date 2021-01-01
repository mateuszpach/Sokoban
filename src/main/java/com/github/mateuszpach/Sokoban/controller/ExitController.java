package com.github.mateuszpach.Sokoban.controller;

import com.github.mateuszpach.Sokoban.model.Game;
import javafx.application.Platform;

public class ExitController {

    public void resume() {
        SceneManager.changeScene(SceneManager.SceneType.GAME);
    }

    public void reset() {
        Game.recover();
        SceneManager.changeScene(SceneManager.SceneType.GAME);
    }

    public void back() {
        SceneManager.changeScene(SceneManager.SceneType.INTRO);
    }

    public void quit() {
        Platform.exit();
        System.exit(0);
    }
}
