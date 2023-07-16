package lk.ijse.gmailsender.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class SenderFormController {
    public TextArea areaEmails;
    public TextArea areaSendFinish;
    public Button btnStart;
    public TextArea areaMsg;
    public Button btnCheck;

    String [] split;

    public void btnStartOnAction(ActionEvent actionEvent) {

    }

    public void btnCheckOnAction(ActionEvent actionEvent) {
        split = areaEmails.getText().split("\n");

    }
}
