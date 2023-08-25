package com.java.mailsender.service.impl;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import com.java.mailsender.entity.EmailDetails;
import com.java.mailsender.service.EmailService;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    public AmazonSimpleEmailService amazonSimpleEmailService;

    public static String CHARSET = "UTF-8";

    public String sendEmail(EmailDetails emailDetails) {

        final HashMap<String, String> hashMap = new HashMap<String, String>() {
            {
                put("name", "Rajesh");
                put("bar", "bar1");
                put("teamName", "BMW");
                put("msgContent", "MAN");
                put("email", emailDetails.getToEmail());
            }
        };


        StringBuilder contentBuilder = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(new FileReader("D:\\AWS email service with spring boot\\Google\\java-email-aws\\src\\main\\resources\\email_reset_login.html"));
            String str;
            while ((str = in.readLine()) != null) {
                contentBuilder.append(str);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String emailContent = contentBuilder.toString();
        emailContent = replaceParams(hashMap, emailContent);

        Content htmlContent = new Content();
        htmlContent.withData(emailContent).withCharset(CHARSET);

        Content subject = new Content();
        subject.withData("Test Mail").withCharset(CHARSET);

        Body messageBody = new Body();
        messageBody.setHtml(htmlContent);

        com.amazonaws.services.simpleemail.model.Message message = new com.amazonaws.services.simpleemail.model.Message();
        message.setBody(messageBody);
        message.setSubject(subject);

        Destination destination = new Destination();
//        destination.setToAddresses(Collections.singleton(emailDetails.getToEmail()));
        destination.getToAddresses().add(emailDetails.getToEmail());
        SendEmailRequest sendEmailRequest = new SendEmailRequest();
        String senderEmail = "ExpView Alerts <" + emailDetails.getFromEmail() + ">";
        sendEmailRequest.setSource(senderEmail);
        sendEmailRequest.setMessage(message);
        sendEmailRequest.setDestination(destination);

        amazonSimpleEmailService.sendEmail(sendEmailRequest);

        return "Email sent successfully...";
    }


    public static String replaceParams(Map<String, String> hashMap, String template) {
        /*return hashMap.entrySet().stream().reduce(template, (s, e) -> s.replace("%(" + e.getKey() + ")", e.getValue()),
                (s, s2) -> s);*/
        return hashMap.entrySet().stream().reduce(template, (s, e) -> s.replace("{" + e.getKey() + "}", e.getValue()),
                (s, s2) -> s);
    }

    public String sendEmailWithAttachment(EmailDetails emailDetails) {
        Session session = Session.getInstance(new Properties(System.getProperties()));

        String filePath = "D:\\itextExamples\\addingParagraph.pdf";
        String fileName = "addingParagraph.pdf";

        MimeMessage mimeMessage = new MimeMessage(session);
        try {
            mimeMessage.setSubject(emailDetails.getSubject());
            mimeMessage.setFrom(emailDetails.getFromEmail());
            mimeMessage.setRecipients(Message.RecipientType.TO, emailDetails.getToEmail());

            MimeBodyPart wrap = new MimeBodyPart();
            wrap.setContent(emailDetails.getBody(), "text/html");

            MimeMultipart msg = new MimeMultipart("mixed");
            mimeMessage.setContent(msg);
            msg.addBodyPart(wrap);

            MimeBodyPart messageBodyPart = new MimeBodyPart();

            // Attachment pdf file
            DataSource source = new FileDataSource(filePath);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(fileName);

            msg.addBodyPart(messageBodyPart);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            mimeMessage.writeTo(outputStream);

            RawMessage rawMessage = new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));

            SendRawEmailRequest rawEmailRequest = new SendRawEmailRequest(rawMessage);

            amazonSimpleEmailService.sendRawEmail(rawEmailRequest);
            System.out.println("Email sent with attachment....");
        } catch (Exception e) {
            System.out.println("Exception occured in sending mail with attachment!!!!");
            e.printStackTrace();
            return e.getMessage();
        }
        return "Email sent with attachment....";
    }
}
