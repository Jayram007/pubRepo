package com.ExpenceManager.expencemanager.service;

import com.ExpenceManager.expencemanager.dto.UserDto;
import com.ExpenceManager.expencemanager.repo.UserRepo;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Service
@Transactional
public class EmailServiceImpl implements EmailService {

    @Autowired
    UserRepo userRepo;

    public static boolean isLoginEmailSent= false;

    static{

        boolean b = sendApplicationStartEmail();
        if (b) {
            System.out.println("Email is sent successfully");
        } else {
            System.out.println("There is problem in sending email");
        }

    }

    private static boolean sendApplicationStartEmail() {
        String to = "jayramkatkar007@gmail.com";
        String from = "testjayram@gmail.com";
        String subject = "Alert! Your application is started";
        String text = "This is a Alert email send your application is stated";
        return sendEmail(to, from, subject, text);
    }

    public static boolean sendEmail(String to, String from, String subject, String text) {
        boolean flag = false;

        //logic

        //smtp properties
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.starttls.enable", true);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.host", "smtp.gmail.com");

        String username = "testjayram@gmail.com";
        String password = "pass key";


        //session
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {

            Message message = new MimeMessage(session);
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setFrom(new InternetAddress(from));
            message.setSubject(subject);
            message.setText(text);
           // Transport.send(message);//todo:uncomment later
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    @Override
    public void sendLoginEmail(String name) {
        System.out.println("loginEmail is activate successfully");
        long start=System.currentTimeMillis();
        String subject="Login in your App";
        String text=name+" logged in App";

        EmailServiceImpl.sendEmail("jayramkatkar007@gmail.com","testjayram@gmail.com",subject,text);
        isLoginEmailSent= true;
        System.out.println("loginEmail is sent successfully");
        long end=System.currentTimeMillis();
        System.out.println("loginEmail time........"+(end-start));
    }

    @Override
    public void sendChangeProfileEmail(String name, UserDto userDto) {

         EmailServiceImpl.sendEmail(userRepo.findByFirstName(name).getEmail(),"testjayram@gmail.com","Profile Change","Your Username and Password has been changed.....");
    }
}
