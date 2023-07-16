package lk.ijse.gmailsender.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class SenderFormController {
    public TextArea areaEmails;
    public TextArea areaSendFinish;
    public Button btnStart;
    public TextArea areaMsg;
    public Button btnCheck;

    String [] split;

    int countAt;

    public void btnStartOnAction(ActionEvent actionEvent) {

    }

    public void btnCheckOnAction(ActionEvent actionEvent) {
        for (int i = 0; i < areaEmails.getText().length(); i++) {
            if (areaEmails.getText().charAt(i)=='@'){
                countAt++;
            }
        }
        split = areaEmails.getText().split("\n");

        if (split.length == countAt){
            System.out.println("finalize !");
        }else {
            new Alert(Alert.AlertType.ERROR,"please enter emails one by one !").show();
        }

    }
}
