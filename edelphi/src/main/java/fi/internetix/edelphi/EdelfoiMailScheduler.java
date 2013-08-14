package fi.internetix.edelphi;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.edelphi.dao.base.EmailMessageDAO;
import fi.internetix.edelphi.dao.base.MailQueueItemDAO;
import fi.internetix.edelphi.domainmodel.base.MailQueueItem;
import fi.internetix.edelphi.domainmodel.base.MailQueueItemState;
import fi.internetix.edelphi.utils.MailUtils;
import fi.internetix.edelphi.utils.SystemUtils;
import fi.internetix.smvc.logging.Logging;

@Singleton
public class EdelfoiMailScheduler {

  @PersistenceContext
  private EntityManager entityManager;

  @Schedule(minute = "*/5", hour = "*", persistent = false)
  public void sendPendingMails() {
    GenericDAO.setEntityManager(entityManager);
    try {
      if (SystemUtils.isProductionEnvironment()) {
        Date start = new Date();
        Date end = new Date(start.getTime() + 300000);
        long maxTime = 240000;
        MailQueueItemDAO mailQueueItemDAO = new MailQueueItemDAO();
        EmailMessageDAO emailMessageDAO = new EmailMessageDAO();
        List<MailQueueItem> mailQueueItems = mailQueueItemDAO.listByStateAndArchived(MailQueueItemState.IN_QUEUE, Boolean.FALSE); 
        if (mailQueueItems.size() > 0) {
          Logging.logInfo("sendPendingMails:" + mailQueueItems.size() + " mails in queue");
        }
        int mailsProcessed = 0;
        for (MailQueueItem mailQueueItem : mailQueueItems) {
          mailQueueItemDAO.updateState(mailQueueItem, MailQueueItemState.BEING_SENT, mailQueueItem.getCreator());
          try {
            MailUtils.sendMail(Locale.getDefault(), mailQueueItem.getEmailMessage());
            mailQueueItemDAO.updateState(mailQueueItem, MailQueueItemState.SENT, mailQueueItem.getCreator());
            mailQueueItemDAO.archive(mailQueueItem);
            emailMessageDAO.archive(mailQueueItem.getEmailMessage());
          }
          catch (Exception e) {
            Logging.logError("sendPendingMails:sending mail " + mailQueueItem.getId() + " failed");
            Logging.logException(e);
            mailQueueItemDAO.updateState(mailQueueItem, MailQueueItemState.SEND_FAIL, mailQueueItem.getCreator());
          }
          mailsProcessed++;
          Date now = new Date();
          if (now.getTime() + maxTime > end.getTime()) {
            Logging.logInfo("sendPendingMails:timeout");
            break;
          }
        }
        if (mailQueueItems.size() > 0) {
          Logging.logInfo("sendPendingMails:" + mailsProcessed + "/" + mailQueueItems.size() + " mails processed");
        }
      }
    }
    catch (Exception e) {
      Logging.logException(e);
    }
    finally {
      GenericDAO.setEntityManager(null);
    }
  }
}