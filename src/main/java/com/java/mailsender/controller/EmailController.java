package com.java.mailsender.controller;

import com.java.mailsender.entity.EmailDetails;
import com.java.mailsender.service.EmailService;
import com.java.mailsender.service.impl.EmailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/sendemail")
    public ResponseEntity<String> sendEmail(@RequestBody EmailDetails emailDetails) {
        String message = emailService.sendEmail(emailDetails);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @PostMapping("/emailwithattachment")
    public ResponseEntity<String> emailSendWithAttachment(@RequestBody EmailDetails emailDetails){
        String message =  emailService.sendEmailWithAttachment(emailDetails);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }
}
