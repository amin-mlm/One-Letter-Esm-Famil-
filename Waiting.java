package com.example.newesmfamil2.animaition;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class Waiting {
    private final FadeTransition fadeTransition = new FadeTransition();
    private final Label label;
    private final String mainText;

    static boolean isEnough;

    public Waiting(Label label) {
        isEnough = false;

        this.label = label;
        mainText = label.getText();

        fadeTransition.setNode(label);
        fadeTransition.setDuration(Duration.millis(800));
        fadeTransition.setFromValue(1f);
        fadeTransition.setToValue(0f);
        fadeTransition.setCycleCount(1);
    }

    public void waiting(/*String text*/){
        new Thread(()->{
            while (!isEnough && label.getScene().getWindow().isShowing()) {
                for (int i = 0; i < 3; i++) {
                    Platform.runLater(()->{
                        label.setText(label.getText() + ".");
                    });
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
//                fadeTransition.play();

                Platform.runLater(()->{
                    label.setText(mainText);
                });

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void setIsAnimationEnough(boolean isEnough) {
        Waiting.isEnough = isEnough;
    }
}
