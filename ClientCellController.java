package com.example.newesmfamil2;

import io.github.palexdev.materialfx.controls.legacy.MFXLegacyListCell;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.io.IOException;

public class ClientCellController extends MFXLegacyListCell<Client> {
    @FXML
    private Label nameLabel;

    @FXML
    private Label scoreLabel;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private ImageView userImage;

    @FXML
    private ImageView rank1Image;

    @FXML
    private ImageView rank2Image;

    @FXML
    private ImageView rank3Image;

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
            switch (item.getRank()) {
                case 1 -> {
                    rank1Image.setVisible(true);
//                    BackgroundFill myBF1 = new BackgroundFill(Color.GOLD, new CornerRadii(1),
//                            new Insets(0));// or null for the padding
//                    rootPane.setBackground(new Background(myBF1));
                }
                case 2 -> {
                    rank2Image.setVisible(true);
//                    BackgroundFill myBF2 = new BackgroundFill(Color.SILVER, new CornerRadii(1),
//                            new Insets(0.0, 0.0, 0.0, 0.0));// or null for the padding
//                    rootPane.setBackground(new Background(myBF2));
                }
                case 3 -> {
                    rank3Image.setVisible(true);
//                    BackgroundFill myBF3 = new BackgroundFill(Color.ORANGERED, new CornerRadii(1),
//                            new Insets(0.0, 0.0, 0.0, 0.0));// or null for the padding
//                    rootPane.setBackground(new Background(myBF3));
                }
                default -> {
//                    BackgroundFill myBF0 = new BackgroundFill(Color.LIGHTGREEN, new CornerRadii(1),
//                            new Insets(0.0, 0.0, 0.0, 0.0));// or null for the padding
//                    rootPane.setBackground(new Background(myBF0));
                }

//                case 1: BackgroundImage myBI1= new BackgroundImage(new Image("C:\\Users\\USER\\Desktop\\java desktop\\esmFamilAll\\newEsmFamil2\\src\\main\\resources\\com\\example\\newesmfamil2\\assets\\no1.jpg",400,95,false,true),
//                        BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
//                        BackgroundSize.DEFAULT);
//                        rootPane.setBackground(new Background(myBI1));
//                        break;
            }


            nameLabel.setText(item.getName());
            scoreLabel.setText(item.getFinalScore()+"");
            if(GameScreenController.client.getName().equals(item.getName())){
                userImage.setVisible(true);
            }


            setText(null);
            setGraphic(rootPane);
        }
    }
}

