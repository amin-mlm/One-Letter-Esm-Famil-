package com.example.newesmfamil2;

import io.github.palexdev.materialfx.controls.MFXButton;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;

public class GameScreenController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private FlowPane fieldPane;

    @FXML
    private MFXButton finishButton;

    @FXML
    private Label timeLabel;

    private ArrayList<String> fields = new ArrayList<>();


    @FXML
    void initialize() {
        for(String s : fields){
//            fieldPane.getChildren().add(s);
        }


    }

    public void addFields(ArrayList<String> fields){
        this.fields = fields;
    }

}
