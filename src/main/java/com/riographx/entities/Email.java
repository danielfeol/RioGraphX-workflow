/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.riographx.entities;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class Email {
  private static final String senderEmail = "";//change with your sender email
  private static final String senderPassword = "";//change with your sender password

  public static void sendAsHtml(String to, String title, String html) throws MessagingException {
      System.out.println("Sending email to " + to);

      Session session = createSession();

      //create message using session
      MimeMessage message = new MimeMessage(session);
      prepareEmailMessage(message, to, title, html);

      //sending message
      Transport t;
      t = session.getTransport("smtps");
      t.send(message);
      System.out.println("Done");
  }

  private static void prepareEmailMessage(MimeMessage message, String to, String title, String html)
          throws MessagingException {
      message.setContent(html, "text/html; charset=utf-8");
      message.setFrom(new InternetAddress(senderEmail));
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
      message.setSubject(title);
  }

  private static Session createSession() {
      Properties props = new Properties();
      props.put("mail.smtp.auth", "true");//Outgoing server requires authentication
      props.put("mail.smtp.starttls.enable", "true");//TLS must be activated
      props.put("mail.smtp.ssl.enable", "true");
      props.put("mail.smtp.host", ""); //Outgoing server (SMTP) - change it to your SMTP server
      props.put("mail.smtp.port", "");//Outgoing port

      Session session = Session.getInstance(props, new javax.mail.Authenticator() {
          protected PasswordAuthentication getPasswordAuthentication() {
              return new PasswordAuthentication(senderEmail, senderPassword);
          }
      });
      return session;
  }
}