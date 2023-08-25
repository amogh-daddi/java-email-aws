package com.java.mailsender.service;

import com.java.mailsender.entity.EmailDetails;

public interface EmailService {

    String sendEmail(EmailDetails emailDetails);

    String sendEmailWithAttachment(EmailDetails emailDetails);
}
