package in.ac.amu.zhcet.service.notification.email;

import in.ac.amu.zhcet.configuration.ApplicationProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;

@Slf4j
@Service
public class EmailService{
    private final JavaMailSender sender;
    private final ApplicationProperties applicationProperties;
    private final String senderEmail;

    @Autowired
    public EmailService(JavaMailSender sender, ApplicationProperties applicationProperties) {
        this.sender = sender;
        this.applicationProperties = applicationProperties;
        this.senderEmail = applicationProperties.getEmail().getAddress();
    }

    private static void setBasicInfo(MimeMessageHelper mailMessage, String from, String to, String subject, String[] bcc) throws MessagingException {
        mailMessage.setSubject(subject);
        mailMessage.setFrom(from);
        mailMessage.setTo(to);
        if (bcc != null)
            mailMessage.setBcc(bcc);
    }

    private MimeMessage constructEmail(String emailAddress, String subject, String body, String[] bcc) throws MessagingException {
        MimeMessage mimeMessage = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setText(body);
        setBasicInfo(helper, senderEmail, emailAddress, subject, bcc);
        return mimeMessage;
    }

    private MimeMessage constructHtmlEmail(String emailAddress, String subject, String body, String[] bcc) throws MessagingException {
        MimeMessage mimeMessage = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setText(body, true);
        setBasicInfo(helper, senderEmail, emailAddress, subject, bcc);
        return mimeMessage;
    }

    private void sendMail(MimeMessage mimeMessage) throws MessagingException {
        if (applicationProperties.isSkipEmails()) {
            try {
                log.info("Skipping mail because of property override.\n" +
                        "Sender: {}\n" +
                        "Recipients: {}\n" +
                        "Subject: {}\n" +
                        "Content: {}",
                        mimeMessage.getSender(), mimeMessage.getAllRecipients(),
                        mimeMessage.getSubject(), mimeMessage.getContent());
            } catch (IOException e) {
                log.error("Error extracting information", e);
            }
            return;
        }

        sender.send(mimeMessage);
    }

    public void sendHtmlMail(String email, String subject, String html, String[] bcc) {
        try {
            sendMail(constructHtmlEmail(email, subject, html, bcc));
        } catch (MessagingException e) {
            log.error("Error sending mail", e);
        }
    }

    public void sendHtmlMail(String email, String subject, String html) {
        sendHtmlMail(email, subject, html, null);
    }

    public void sendSimpleMail(String email, String subject, String message) {
        try {
            sendMail(constructEmail(email, subject, message, null));
        } catch (MessagingException e) {
            log.error("Error sending mail", e);
        }
    }

}
