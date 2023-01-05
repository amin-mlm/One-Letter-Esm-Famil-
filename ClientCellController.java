package com.example.newesmfamil2;

import io.github.palexdev.materialfx.controls.legacy.MFXLegacyListCell;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class ClientCellController extends MFXLegacyListCell<Client> {
    @FXML
    private Label nameLabel;

    @FXML
    private Label scoreLabel;

    @FXML
    private AnchorPane rootPane;

    FXMLLoader fxmlLoader;

    @Override
    protected void updateItem(Client item, boolean empty) {
        super.updateItem(item, empty);
        if(empty || item==null){
            setText(null);
            setGraphic(null);
        }else{
            if (fxmlLoader == null ) {
                fxmlLoader = new FXMLLoader(getClass()
                        .getResource("/com/example/newesmfamil2/clientCell.fxml"));
                fxmlLoader.setController(this);
                try {
                    fxmlLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            nameLabel.setText(item.getName());
            scoreLabel.setText(item.getFinalScore()+"");


            setText(null);
            setGraphic(rootPane);
        }
    }
}

