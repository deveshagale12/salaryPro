package com.salarypro;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // 1. Existing method: Sends the message TO YOU
    public void sendContactEmail(String fromEmail, String name, String subject, String messageContent) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("deveshagale@gmail.com"); 
        message.setSubject("New Contact Form: " + subject);
        message.setText("Name: " + name + "\nEmail: " + fromEmail + "\n\nMessage:\n" + messageContent);
        mailSender.send(message);
    }

    // 2. NEW method: Sends a "Thank You" TO THE USER
    public void sendAutoReply(String userEmail, String userName) {
        SimpleMailMessage reply = new SimpleMailMessage();
        reply.setTo(userEmail);
        reply.setSubject("We've received your message - SalaryPro");
        reply.setText("Hi " + userName + ",\n\n" +
                  "Thank you for reaching out to SalaryPro! We've received your inquiry and our team is currently reviewing it.\n\n" +
                  "You can expect a response from one of our specialists within 24 business hours.\n\n" +
                  "Best regards,\n" +
                  "The SalaryPro Support Team\n" +
                  "https://github.com/deveshagale12");
        
        mailSender.send(reply);
    }
}