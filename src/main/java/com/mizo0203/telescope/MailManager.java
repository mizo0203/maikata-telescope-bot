package com.mizo0203.telescope;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Properties;

public final class MailManager {
    public final void sendSimpleMail(String message, String... addresses) {
        try {
            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("noreply@stellar-river-459.appspotmail.com", "まいかたてれすこーぷ"));
            for (String address : addresses) {
                msg.addRecipient(Message.RecipientType.TO, new InternetAddress(address));
            }
            msg.setSubject("まいかたてれすこーぷ");
            msg.setText(message);
            Transport.send(msg);
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }
    }
}
