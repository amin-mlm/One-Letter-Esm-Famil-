package com.example.newesmfamil2.animaition;

import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class Shaker {
    private TranslateTransition translateTransition = new TranslateTransition();

    public Shaker(Node node) {
        translateTransition.setNode(node);
    }

    public void shake(){
        translateTransition.setDuration(Duration.millis(50));
        translateTransition.setFromX(0f);
        translateTransition.setByX(15f);
        translateTransition.setCycleCount(4);
        translateTransition.setAutoReverse(true);

        translateTransition.playFromStart();
    }

    public void transit(){
        translateTransition.setDuration(Duration.millis(600));
        translateTransition.setFromX(0f);
        translateTransition.setByX(640f);
        translateTransition.setCycleCount(1);
        translateTransition.setAutoReverse(false);

        translateTransition.playFromStart();
    }

}
