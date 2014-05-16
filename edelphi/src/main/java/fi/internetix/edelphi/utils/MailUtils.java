package fi.internetix.edelphi.utils;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import fi.internetix.edelphi.EdelfoiStatusCode;
import fi.internetix.edelphi.domainmodel.base.EmailMessage;
import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.smvc.SmvcRuntimeException;

public class MailUtils {
  
  public static final String PLAIN = "text/plain; charset=UTF-8";
  public static final String HTML = "text/html; charset=UTF-8";
  
  public static void sendMail(Locale locale, EmailMessage emailMessage) {
    InternetAddress from = null;
    try {
      from = new InternetAddress(emailMessage.getFromAddress());
      sendMail(locale, new String[] { emailMessage.getToAddress() }, from, emailMessage.getSubject(), emailMessage.getContent(), PLAIN);
    }
    catch (AddressException e) {
      throw new RuntimeException(e);
    }
  }
  
  public static void sendMail(Locale locale, String address, String subject, String content) {
    sendMail(locale, new String[] { address }, null, subject, content, PLAIN);
  }
  
  public static void sendMail(Locale locale, String[] addresses, String subject, String content) {
    sendMail(locale, addresses, null, subject, content, PLAIN);
  }

  public static void sendMail(Locale locale, String address, String subject, String content, String mimetype) {
    sendMail(locale, new String[] { address }, null, subject, content, mimetype);
  }
  
 public static void sendMail(Locale locale, String[] addresses, InternetAddress from, String subject, String content, String mimetype) {
   try {
      Properties props = new Properties();
   
      InitialContext ictx = new InitialContext(props);
      Session mailSession = (Session) ictx.lookup("java:/Mail");
      
      // TODO: Remove this ugly hack after JBoss start supporting sending email by tsl + ssl 
      // -----------------------------------------------------------------------------
      uglyHack(mailSession);
      Properties properties = mailSession.getProperties();
      properties.put("mail.smtp.starttls.enable","true");
      properties.put("mail.smtp.auth", "true");
      properties.put("mail.smtp.socketFactory.fallback", "false");
      properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
      // -----------------------------------------------------------------------------

      MimeMessage m = new MimeMessage(mailSession);
      Address[] to = new InternetAddress[addresses.length];
      for (int i = 0; i < addresses.length; i++) {
        to[i] = new InternetAddress(addresses[i]);
      }
      
      m.setRecipients(Message.RecipientType.TO, to);
      m.setSubject(subject);
      m.setSentDate(new Date());
      m.setContent(content, mimetype);
      
      if (from != null) {
        m.setFrom(from);
        m.setReplyTo(new InternetAddress[] { from });
      }
      
      Transport.send(m);      
    }
    catch (NamingException ne) {
      Messages messages = Messages.getInstance();
      throw new SmvcRuntimeException(EdelfoiStatusCode.INVALID_MAIL_SERVER, messages.getText(locale, "exception.1001.invalidMailServer"), ne);
    }
    catch (AddressException ae) {
      Messages messages = Messages.getInstance();
      throw new SmvcRuntimeException(EdelfoiStatusCode.INVALID_EMAIL_ADDRESS, messages.getText(locale, "exception.1002.invalidEmail", new String[] { toCDT(addresses) }), ae);
    }
    catch (MessagingException me) {
      Messages messages = Messages.getInstance();
      throw new SmvcRuntimeException(EdelfoiStatusCode.MESSAGING_EXCEPTION, messages.getText(locale, "exception.1003.messagingException"), me);
    }
  }
 
  private static void uglyHack(Session session) {
    try {
      Field authTableField = session.getClass().getDeclaredField("authTable");
      authTableField.setAccessible(true);
      @SuppressWarnings("unchecked") javax.mail.PasswordAuthentication authentication = ((Hashtable<?, javax.mail.PasswordAuthentication>) authTableField.get(session)).values().iterator().next();
      Field authenticatorField = session.getClass().getDeclaredField("authenticator");
      authenticatorField.setAccessible(true);
      authenticatorField.set(session, new PasswordAuthenticator(authentication));
    } catch (Exception e) {
    } 
  }

 private static String toCDT(String[] addresses) {
   StringBuilder sb = new StringBuilder();
   for (int i = 0; i < addresses.length; i++) {
     sb.append(addresses[i]);
     if (i < addresses.length - 1) {
       sb.append("; ");
     }
   }
   return sb.toString();
 }

 private static class PasswordAuthenticator extends Authenticator {
   
   public PasswordAuthenticator(javax.mail.PasswordAuthentication authenticator) {
     this.authenticator = authenticator;
   }
   
   @Override
   protected PasswordAuthentication getPasswordAuthentication() {
     return authenticator;
   }
   
   private javax.mail.PasswordAuthentication authenticator;
 }
}
