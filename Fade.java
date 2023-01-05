package com.example.newesmfamil2.animaition;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.awt.*;

public class Fade {
    private final FadeTransition fadeTransition = new FadeTransition();
    private final Node node;

    public Fade(Node node) {
        this.node = node;
    }

    public void fadeIn(){
        fadeTransition.setNode(node);
        fadeTransition.setDuration(Duration.millis(200));
        fadeTransition.setFromValue(0f);
        fadeTransition.setToValue(1f);
        fadeTransition.setCycleCount(3);
        fadeTransition.setAutoReverse(true);

        fadeTransition.play();

        new Thread(()->{
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            fadeOut();
        }).start();

    }

    private void fadeOut(){
        fadeTransition.setNode(node);
        fadeTransition.setDuration(Duration.millis(1100));
        fadeTransition.setFromValue(1f);
        fadeTransition.setToValue(0f);
        fadeTransition.setCycleCount(1);

        fadeTransition.play();
    }
}
