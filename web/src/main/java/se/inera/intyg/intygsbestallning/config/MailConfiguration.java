package se.inera.intyg.intygsbestallning.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfiguration {

    @Value("${mail.host}")
    private String mailHost;

    @Value("${mail.username}")
    private String mailUsername;

    @Value("${mail.password}")
    private String mailPassword;

    @Value("${mail.smtps.auth}")
    private String mailSmtpsAuth;

    @Value("${mail.smtps.starttls.enable}")
    private String mailSmtpsStarttlsEnable;

    @Value("${mail.smtps.debug}")
    private String mailSmtpsDebug;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setDefaultEncoding("UTF-8");
        mailSender.setHost(mailHost);
        mailSender.setUsername(mailUsername);
        mailSender.setPassword(mailPassword);
        mailSender.setProtocol("stmps");
        Properties props = new Properties();
        props.setProperty("mail.smtps.auth", mailSmtpsAuth);
        props.setProperty("mail.smtps.starttls.enable", mailSmtpsStarttlsEnable);
        props.setProperty("mail.smtps.debug", mailSmtpsDebug);
        mailSender.setJavaMailProperties(props);
        return mailSender;
    }
}
