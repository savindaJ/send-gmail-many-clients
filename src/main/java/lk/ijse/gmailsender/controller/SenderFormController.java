package lk.ijse.gmailsender.controller;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Set;

public class SenderFormController {
    public TextArea areaEmails;
    public TextArea areaSendFinish;
    public Button btnStart;
    public TextArea areaMsg;
    public Button btnCheck;

    String [] split;

    int countAt=0;

    public static  String TEST_MAIL;
    private Gmail service;

    @FXML
    void initialize() throws GeneralSecurityException, IOException {
        NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        GsonFactory gsonFactory=GsonFactory.getDefaultInstance();
        service = new Gmail.Builder(HTTP_TRANSPORT, gsonFactory, getCredentials(HTTP_TRANSPORT,gsonFactory))
                .setApplicationName("sendmail")
                .build();
    }

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT, GsonFactory gsonFactory)
            throws IOException {
        // Load client secrets.
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(gsonFactory,
                new InputStreamReader(SenderFormController.class.getResourceAsStream
                        ("/api/"))); // enter your credential
        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, gsonFactory, clientSecrets, Set.of(GmailScopes.GMAIL_SEND))
                .setDataStoreFactory(new FileDataStoreFactory(Paths.get("tokens").toFile()))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public void btnStartOnAction(ActionEvent actionEvent) {

    }

    public void btnCheckOnAction(ActionEvent actionEvent) {
        countAt=0;

        for (int i = 0; i < areaEmails.getText().length(); i++) {
            if (areaEmails.getText().charAt(i)=='@'){
                countAt++;
                System.out.println(countAt);
            }
        }
        split = areaEmails.getText().split("\n");
        System.out.println(split.length);

        if (split.length == countAt){
            System.out.println("finalize !");
        }else {
            new Alert(Alert.AlertType.ERROR,"please enter emails one by one !").show();
        }

    }
}
