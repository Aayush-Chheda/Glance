package com.glance.backend.utility;

import com.glance.backend.model.AppUser;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
public class EmailConstructor {

    @Autowired
    private Environment env;

    @Autowired
    private TemplateEngine templateEngine;

    public MimeMessagePreparator constructNewUserEmail(AppUser appUser,String password){
        Context context = new Context();
        context.setVariable("user", appUser);
        context.setVariable("password",password);

        String text = templateEngine.process("newUserEmailTemplate",context);

        MimeMessagePreparator messagePrepartor = new MimeMessagePreparator() {
            @Override
            public void prepare (MimeMessage mimeMessage) throws Exception{
                MimeMessageHelper email = new MimeMessageHelper(mimeMessage);
                email.setPriority(1);
                email.setTo(appUser.getEmail());
                email.setSubject("Welcome To Glance");
                email.setText(text, true);
                email.setFrom(new InternetAddress(env.getProperty("spring.mail.username")));
            }
        };

        return messagePrepartor;
    }

}
