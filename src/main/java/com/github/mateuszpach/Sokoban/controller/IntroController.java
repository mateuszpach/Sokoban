package com.github.mateuszpach.Sokoban.controller;

import com.github.mateuszpach.Sokoban.model.Game;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.scene.control.Label;

public class IntroController {

    public JFXButton buttonNew;
    public JFXButton buttonMinusBoxes;
    public JFXButton buttonPlusBoxes;
    public JFXButton buttonMinusSize;
    public JFXButton buttonPlusSize;
    public JFXButton buttonQuit;

    public Label labelBoxes;
    public Label labelSize;

    public void initialize() {
        buttonNew.setOnMouseEntered(mouseEvent -> buttonNew.requestFocus());
        buttonMinusBoxes.setOnMouseEntered(mouseEvent -> buttonMinusBoxes.requestFocus());
        buttonPlusBoxes.setOnMouseEntered(mouseEvent -> buttonPlusBoxes.requestFocus());
        buttonMinusSize.setOnMouseEntered(mouseEvent -> buttonMinusSize.requestFocus());
        buttonPlusSize.setOnMouseEntered(mouseEvent -> buttonPlusSize.requestFocus());
        buttonQuit.setOnMouseEntered(mouseEvent -> buttonQuit.requestFocus());
    }

    public void play() {
        Game.generate();
        SceneManager.changeScene(SceneManager.SceneType.GAME);
    }

    public void quit() {
        Platform.exit();
        System.exit(0);
    }

    public void minusBoxes() {
        int value = Integer.parseInt(labelBoxes.getText());
        if (value > 3) {
            value--;
            labelBoxes.setText(String.valueOf(value));
            Game.setTargets(Integer.parseInt(labelBoxes.getText()));
        }
    }

    public void plusBoxes() {
        int value = Integer.parseInt(labelBoxes.getText());
        if (value < 10) {
            value++;
            labelBoxes.setText(String.valueOf(value));
            Game.setTargets(Integer.parseInt(labelBoxes.getText()));
        }
    }

    public void minusSize() {
        String valueS = labelSize.getText();
        int value = Integer.parseInt(valueS.substring(0, valueS.indexOf(' ')));
        if (value > 5) {
            value--;
            labelSize.setText(value + " x " + value);
            Game.setSize(value);
        }
    }

    public void plusSize() {
        String valueS = labelSize.getText();
        int value = Integer.parseInt(valueS.substring(0, valueS.indexOf(' ')));
        if (value < 14) {
            value++;
            labelSize.setText(value + " x " + value);
            Game.setSize(value);
        }
    }
}
