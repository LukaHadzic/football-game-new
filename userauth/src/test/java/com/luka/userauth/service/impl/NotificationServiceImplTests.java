package com.luka.userauth.service.impl;

import com.luka.userauth.config.TestClockConfig;
import com.luka.userauth.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@Import(TestClockConfig.class)
@SpringBootTest
@ActiveProfiles("test")
public class NotificationServiceImplTests {

    @Autowired
    private NotificationService notificationService;

    @MockitoBean
    private JavaMailSender mailSender;

    private final String MAIL_TO = "user1@gmail.com";
    private final String MAIL_FROM = "football.simulation@ggmail.com";
    private final String MAIL_SUBJECT = "Please confirm Your e-mail address";
    private final String TOKEN_STRING = "tokenLengthShouldBeExactly36CharsABC";
    private final String MAIL_TEXT = "Use link provided below to confirm Your e-mail address.\n\n"
            +"http://localhost:8080/auth/validate-email?token=" + TOKEN_STRING +
            "\n\nThank You for playing with us!\n\n"+"Sincerely,\n"+"Football simulation game team";

    @Test
    public void serviceSuccessfullyCalledMethodTest(){
        String providedEmail = "user1@gmail.com";
        String providedToken = "tokenLengthShouldBeExactly36Chars";

        notificationService.sendVerificationEmail(MAIL_TO, TOKEN_STRING);

        Mockito.verify(mailSender).send(Mockito.any(SimpleMailMessage.class));
    }

    @Test
    public void sameMessageSuccessTest(){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(MAIL_TO);
        message.setFrom(MAIL_FROM);
        message.setSubject(MAIL_SUBJECT);
        message.setText(MAIL_TEXT);

        notificationService.sendVerificationEmail(MAIL_TO, TOKEN_STRING);

        Mockito.verify(mailSender).send(message);
    }

}

//class MessageAgregator implements ArgumentsAggregator{
//
//    @Override
//    public @Nullable Object aggregateArguments(ArgumentsAccessor accessor, ParameterContext context) throws ArgumentsAggregationException {
//        ????return new SimpleMailMessage(accessor.getLong());
//    }
//
//}
