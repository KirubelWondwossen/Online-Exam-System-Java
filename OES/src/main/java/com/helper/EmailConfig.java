package com.helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class EmailConfig {
    private static final Logger logger = Logger.getLogger(EmailConfig.class.getName());
    
    private static Properties properties;
    private static final String CONFIG_FILE = "email.properties";
    
    static {
        loadProperties();
    }
    
    private static void loadProperties() {
        properties = new Properties();
        try (InputStream input = EmailConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                properties.load(input);
                logger.info("Email configuration loaded successfully");
            } else {
                logger.warning("Email configuration file not found: " + CONFIG_FILE + ", using defaults");
                setDefaultProperties();
            }
        } catch (IOException ex) {
            logger.severe("Error loading email configuration: " + ex.getMessage());
            setDefaultProperties();
        }
    }
    
    private static void setDefaultProperties() {
        properties.setProperty("email.smtp.host", "smtp.gmail.com");
        properties.setProperty("email.smtp.port", "587");
        properties.setProperty("email.smtp.auth", "true");
        properties.setProperty("email.smtp.starttls.enable", "true");
        properties.setProperty("email.from", "systemonlineexamination@gmail.com");
    }
    
    public static String getFromEmail() {
        return properties.getProperty("email.from", "systemonlineexamination@gmail.com");
    }
    
    public static String getSmtpHost() {
        String provider = getProvider();
        String host = properties.getProperty("email." + provider + ".smtp.host");
        if (host != null && !host.isEmpty()) {
            return host;
        }
        return properties.getProperty("email.smtp.host", "smtp.gmail.com");
    }
    
    public static String getSmtpPort() {
        String provider = getProvider();
        String port = properties.getProperty("email." + provider + ".smtp.port");
        if (port != null && !port.isEmpty()) {
            return port;
        }
        return properties.getProperty("email.smtp.port", "587");
    }
    
    public static boolean getSmtpAuth() {
        String provider = getProvider();
        String auth = properties.getProperty("email." + provider + ".smtp.auth");
        if (auth != null && !auth.isEmpty()) {
            return Boolean.parseBoolean(auth);
        }
        return Boolean.parseBoolean(properties.getProperty("email.smtp.auth", "true"));
    }
    
    public static boolean getStartTlsEnable() {
        String provider = getProvider();
        String starttls = properties.getProperty("email." + provider + ".smtp.starttls.enable");
        if (starttls != null && !starttls.isEmpty()) {
            return Boolean.parseBoolean(starttls);
        }
        return Boolean.parseBoolean(properties.getProperty("email.smtp.starttls.enable", "true"));
    }
    
    public static boolean getSslEnable() {
        String provider = getProvider();
        String ssl = properties.getProperty("email." + provider + ".smtp.ssl.enable");
        if (ssl != null && !ssl.isEmpty()) {
            return Boolean.parseBoolean(ssl);
        }
        return Boolean.parseBoolean(properties.getProperty("email.smtp.ssl.enable", "false"));
    }
    
    public static String getEmailPassword() {
        // Priority: Environment variable > System property > Default (null)
        String password = System.getenv("EMAIL_PASSWORD");
        if (password == null) {
            password = System.getProperty("email.password");
        }
        if (password == null) {
            password = System.getenv("EMAIL_APP_PASSWORD");
        }
        return password;
    }
    
    public static String getEmailUsername() {
        // Priority: Environment variable > System property > From email
        String username = System.getenv("EMAIL_USERNAME");
        if (username == null) {
            username = System.getProperty("email.username");
        }
        if (username == null) {
            username = getFromEmail();
        }
        return username;
    }
    
    public static boolean isConfigured() {
        String host = getSmtpHost();
        String username = getEmailUsername();
        String password = getEmailPassword();
        return host != null && !host.isEmpty() && 
               username != null && !username.isEmpty() && 
               password != null && !password.isEmpty();
    }
    
    public static String getProvider() {
        return properties.getProperty("email.provider", "gmail").toLowerCase();
    }
    
    public static void logConfiguration() {
        logger.info("Email Configuration:");
        logger.info("  Provider: " + getProvider());
        logger.info("  SMTP Host: " + getSmtpHost());
        logger.info("  SMTP Port: " + getSmtpPort());
        logger.info("  SMTP Auth: " + getSmtpAuth());
        logger.info("  STARTTLS: " + getStartTlsEnable());
        logger.info("  SSL: " + getSslEnable());
        logger.info("  From Email: " + getFromEmail());
        logger.info("  Username: " + getEmailUsername());
        logger.info("  Password Configured: " + (getEmailPassword() != null));
    }
}
