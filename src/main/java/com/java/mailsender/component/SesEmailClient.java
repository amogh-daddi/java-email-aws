package com.java.mailsender.component;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import com.java.mailsender.entity.EmailDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component
public class SesEmailClient {

    @Bean
    public static AmazonSimpleEmailService amazonSimpleEmailService() {
        return AmazonSimpleEmailServiceClientBuilder
                .standard()
                .withCredentials(
                        new AWSStaticCredentialsProvider(
                                new BasicAWSCredentials("AKIA2M7KPNGCQ5JE7EVR",
                                        "uSY/eN/s8nXepXi5IBB0sqk6vy1BtF11vCqDsmMP")))
                .withRegion("ap-south-1").build();
    }

    public void sendEmail(HashMap<String, String> hashMap) {
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
        htmlContent.setData(emailContent);
//        htmlContent.withData(emailContent).withCharset(CHARSET);

        Content subject = new Content();
//        subject.withData("Test Mail").withCharset(CHARSET);
        subject.setData("Test Mail");

        Body messageBody = new Body();
        messageBody.setHtml(htmlContent);

        Message message = new Message();
        message.setBody(messageBody);
        message.setSubject(subject);

        Destination destination = new Destination();
        destination.getToAddresses().add("d.amogh@agathsya.net");

        SendEmailRequest sendEmailRequest = new SendEmailRequest();
        sendEmailRequest.setSource("info@exponentialists.ai");
        sendEmailRequest.setMessage(message);
        sendEmailRequest.setDestination(destination);

        amazonSimpleEmailService().sendEmail(sendEmailRequest);
    }

    public static void main(String[] args) {
        SesEmailClient sesEmailClient = new SesEmailClient();

        final HashMap<String, String> hashMap = new HashMap<>() {
            {
                put("name", "Rajesh");
                put("bar", "bar1");
                put("teamName", "BMW");
                put("msgContent", "MAN");
            }
        };

//        sesEmailClient.sendEmail(hashMap);
        sesEmailClient.sendEmailWithAttachment();
    }

    public static String replaceParams(Map<String, String> hashMap, String template) {
        return hashMap.entrySet().stream().reduce(template, (s, e) -> s.replace("{" + e.getKey() + "}", e.getValue()), (s, s2) -> s);
    }

    public void sendEmailWithAttachment() {
        Session session = Session.getInstance(new Properties(System.getProperties()));

        String filePath = "D:\\itextExamples\\addingParagraph.pdf";
        String fileName = "addingParagraph.pdf";

        MimeMessage mimeMessage = new MimeMessage(session);
        try {
            mimeMessage.setSubject("Test mail");
            mimeMessage.setFrom("info@exponentialists.ai");
            mimeMessage.addRecipients(javax.mail.Message.RecipientType.TO, "d.amogh@agathsya.net");
//            mimeMessage.setRecipients(javax.mail.Message.RecipientType.TO, "d.amogh@agathsya.net");

            MimeBodyPart wrap = new MimeBodyPart();
            wrap.setContent("Email sent for testing", "text/html");

            MimeMultipart msg = new MimeMultipart("mixed");
            msg.addBodyPart(wrap);
            mimeMessage.setContent(msg);

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

            amazonSimpleEmailService().sendRawEmail(rawEmailRequest);
            System.out.println("Email sent with attachment....");
        } catch (Exception e) {
            System.out.println("Exception occured in sending mail with attachment!!!!");
            e.printStackTrace();
        }
    }
}
