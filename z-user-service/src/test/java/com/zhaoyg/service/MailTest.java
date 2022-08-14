package com.zhaoyg.service;

import com.zhaoyg.UserServiceTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

/**
 * @author zhao
 * @date 2022/8/12
 */

class MailTest extends UserServiceTest {
    @Autowired
    private MailSender mailSender;

    @Value("${MAIL_USERNAME}")
    private String from;

    @Test
    void testMail() {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(from);
        simpleMailMessage.setTo("zhaoyg@wisevirtue.com");
        simpleMailMessage.setText("t");
        mailSender.send(simpleMailMessage);
    }
}
