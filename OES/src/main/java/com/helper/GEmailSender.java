package com.helper;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;
import java.util.logging.Logger;
import java.util.Date;

public class GEmailSender {
    
    private static final Logger logger = Logger.getLogger(GEmailSender.class.getName());

    public boolean sendEmail(String to, String from, String subject, String text) {
        logger.info("=== Email Send Attempt ===");
        logger.info("To: " + to);
        logger.info("Subject: " + subject);
        
        // Check email configuration
        if (!EmailConfig.isConfigured()) {
            logger.severe("Email configuration is incomplete. Check EMAIL_USERNAME and EMAIL_PASSWORD environment variables.");
            EmailConfig.logConfiguration();
            return false;
        }
        
        // Log configuration (without sensitive data)
        EmailConfig.logConfiguration();
        
        // Build SMTP properties from configuration
        Properties smtpProperties = buildSmtpProperties();
        
        final String username = EmailConfig.getEmailUsername();
        final String password = EmailConfig.getEmailPassword();
        
        try {
            // Create session with authentication
            Session session = createSmtpSession(smtpProperties, username, password);
            
            // Create and configure message
            Message message = createMessage(session, from, to, subject, text);
            
            // Send email with detailed logging
            return sendEmailWithLogging(session, message, to);
            
        } catch (Exception e) {
            logEmailError("Failed to send email", e, to);
            return false;
        }
    }
    
    private Properties buildSmtpProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", EmailConfig.getSmtpAuth());
        props.put("mail.smtp.starttls.enable", EmailConfig.getStartTlsEnable());
        props.put("mail.smtp.ssl.enable", EmailConfig.getSslEnable());
        props.put("mail.smtp.port", EmailConfig.getSmtpPort());
        props.put("mail.smtp.host", EmailConfig.getSmtpHost());
        
        // Additional properties for reliability
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.writetimeout", "10000");
        
        logger.info("SMTP Properties configured for provider: " + EmailConfig.getProvider());
        return props;
    }
    
    private Session createSmtpSession(Properties props, String username, String password) {
        try {
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
            
            // Enable debug logging for troubleshooting
            session.setDebug(true);
            logger.info("SMTP session created successfully");
            return session;
            
        } catch (Exception e) {
            logger.severe("Failed to create SMTP session: " + e.getMessage());
            throw e;
        }
    }
    
    private Message createMessage(Session session, String from, String to, String subject, String text) throws Exception {
        Message message = new MimeMessage(session);
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setFrom(new InternetAddress(from));
        message.setSubject(subject);
        message.setText(text);
        message.setSentDate(new Date());
        
        logger.info("Email message created successfully");
        return message;
    }
    
    private boolean sendEmailWithLogging(Session session, Message message, String to) {
        try {
            logger.info("Attempting to connect to SMTP server: " + EmailConfig.getSmtpHost() + ":" + EmailConfig.getSmtpPort());
            
            Transport transport = session.getTransport("smtp");
            transport.connect();
            
            logger.info("SMTP connection established successfully");
            logger.info("Sending email to: " + to);
            
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            
            logger.info("Email sent successfully to: " + to);
            return true;
            
        } catch (AuthenticationFailedException e) {
            logger.severe("SMTP Authentication Failed - Check username/password");
            logger.severe("Username used: " + EmailConfig.getEmailUsername());
            logger.severe("Error: " + e.getMessage());
            return false;
            
        } catch (SendFailedException e) {
            logger.severe("Email Send Failed - Recipient may be invalid");
            logger.severe("Error: " + e.getMessage());
            return false;
            
        } catch (MessagingException e) {
            logger.severe("SMTP Messaging Error: " + e.getMessage());
            if (e.getNextException() != null) {
                logger.severe("Nested Exception: " + e.getNextException().getMessage());
            }
            return false;
            
        } catch (Exception e) {
            logger.severe("Unexpected error during email send: " + e.getMessage());
            return false;
        }
    }
    
    private void logEmailError(String message, Exception e, String recipient) {
        logger.severe("=== EMAIL ERROR ===");
        logger.severe("Message: " + message);
        logger.severe("Recipient: " + recipient);
        logger.severe("Provider: " + EmailConfig.getProvider());
        logger.severe("SMTP Host: " + EmailConfig.getSmtpHost());
        logger.severe("SMTP Port: " + EmailConfig.getSmtpPort());
        logger.severe("Exception: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        
        if (e.getCause() != null) {
            logger.severe("Root Cause: " + e.getCause().getMessage());
        }
    }
    
    /**
     * Test email configuration by sending a test email
     * @param testRecipient Email address to send test to
     * @return true if test successful, false otherwise
     */
    public boolean testEmailConfiguration(String testRecipient) {
        logger.info("=== Testing Email Configuration ===");
        
        if (!EmailConfig.isConfigured()) {
            logger.severe("Cannot test - email configuration incomplete");
            return false;
        }
        
        String testSubject = "Email Configuration Test";
        String testText = "This is a test email to verify your email configuration is working correctly.\n\n" +
                        "If you receive this email, your email system is properly configured.\n\n" +
                        "Provider: " + EmailConfig.getProvider() + "\n" +
                        "SMTP Host: " + EmailConfig.getSmtpHost() + "\n" +
                        "SMTP Port: " + EmailConfig.getSmtpPort() + "\n\n" +
                        "Test sent at: " + new Date();
        
        return sendEmail(testRecipient, EmailConfig.getFromEmail(), testSubject, testText);
    }
}
