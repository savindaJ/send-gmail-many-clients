package lk.ijse.gmailsender.controller;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.apache.commons.codec.binary.Base64;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Properties;
import java.util.Set;

public class SenderFormController {
    public TextArea areaEmails;
    public TextArea areaSendFinish;
    public Button btnStart;
    public TextArea areaMsg;
    public Button btnCheck;
    public TextField txtTitle;

    String [] split;

    int countAt=0;

    public static  String TEST_MAIL;
    private Gmail service;

    private Message msg;

    @FXML
    void initialize() throws GeneralSecurityException, IOException {
        btnStart.setDisable(true);
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

    public void sendMail(String subject, String massage,String TEST_MAIL) throws  IOException, MessagingException {
        SenderFormController.TEST_MAIL =TEST_MAIL;

        // Encode as MIME message
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(TEST_MAIL));
        email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(TEST_MAIL));
        email.setSubject(subject);
        email.setText(massage);

        // Encode and wrap the MIME message into a gmail message
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);
        byte[] rawMessageBytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(rawMessageBytes);
        msg = new Message();
        msg.setRaw(encodedEmail);

        try {
            // Create the draft message
            msg = service.users().messages().send("me", msg).execute();
            System.out.println("Draft id: " + msg.getId());
            System.out.println(msg.toPrettyString());
        } catch (GoogleJsonResponseException e) {
            GoogleJsonError error = e.getDetails();
            if (error.getCode() == 403) {
                System.err.println("Unable to create draft: " + e.getDetails());
            } else {
                throw e;
            }
        }
    }

    public void btnStartOnAction(ActionEvent actionEvent) {
        for (int i = 0; i < split.length; i++) {
            try {
                sendMail(String.valueOf(txtTitle),areaMsg.getText(),TEST_MAIL);

                areaSendFinish.appendText(i+1+") successfully send = \n"+split[i]+"\n msg id"+msg.getId());
            } catch (IOException | MessagingException e) {
                areaSendFinish.appendText(i+1+") not send = \n"+split[i]);
                throw new RuntimeException(e);
            }
        }
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
            btnStart.setDisable(false);
        }else {
            new Alert(Alert.AlertType.ERROR,"please enter emails one by one !").show();
        }

    }
}
