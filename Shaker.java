package com.example.newesmfamil2.animaition;
//
//import javafx.animation.TranslateTransition;
//import javafx.scene.Node;
//import javafx.util.Duration;
//
//public class Shaker {
//    public static void error (Node node) {
//
//    }
//}

import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class Shaker {
    private final TranslateTransition translateTransition;

    public Shaker(Node node) {
        translateTransition =
                new TranslateTransition(Duration.millis(50), node);
        translateTransition.setFromX(0f);
        translateTransition.setByX(15f);
        translateTransition.setCycleCount(4);
        translateTransition.setAutoReverse(true);
    }

    public void shake() {
        translateTransition.playFromStart();
    }
}
