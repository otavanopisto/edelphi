package fi.internetix.edelphi.dao.base;

import java.util.Date;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.edelphi.domainmodel.base.EmailMessage;
import fi.internetix.edelphi.domainmodel.users.User;

public class EmailMessageDAO extends GenericDAO<EmailMessage> {

  public EmailMessage create(String fromAddress, String toAddress, String content, User creator) {
    return create(fromAddress, toAddress, null, content, creator);
  }

  public EmailMessage create(String fromAddress, String toAddress, String subject, String content, User creator) {
    Date now = new Date();

    EmailMessage emailMessage = new EmailMessage();
    emailMessage.setFromAddress(fromAddress);
    emailMessage.setToAddress(toAddress);
    emailMessage.setSubject(subject);
    emailMessage.setContent(content);
    emailMessage.setCreator(creator);
    emailMessage.setCreated(now);
    emailMessage.setLastModifier(creator);
    emailMessage.setLastModified(now);
    emailMessage.setArchived(Boolean.FALSE);

    getEntityManager().persist(emailMessage);
    
    return emailMessage;
  }

  public EmailMessage update(EmailMessage emailMessage, String fromAddress, String toAddress, String subject, String content, User modifier) {
    emailMessage.setFromAddress(fromAddress);
    emailMessage.setToAddress(toAddress);
    emailMessage.setSubject(subject);
    emailMessage.setContent(content);
    emailMessage.setLastModifier(modifier);
    emailMessage.setLastModified(new Date());
    emailMessage.setArchived(Boolean.FALSE);

    getEntityManager().persist(emailMessage);
    
    return emailMessage;
  }
  
}
