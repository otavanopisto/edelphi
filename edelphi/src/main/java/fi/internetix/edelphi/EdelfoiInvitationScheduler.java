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
import fi.internetix.edelphi.dao.panels.PanelInvitationDAO;
import fi.internetix.edelphi.domainmodel.panels.PanelInvitation;
import fi.internetix.edelphi.domainmodel.panels.PanelInvitationState;
import fi.internetix.edelphi.utils.MailUtils;
import fi.internetix.edelphi.utils.SystemUtils;
import fi.internetix.smvc.logging.Logging;

@Singleton
public class EdelfoiInvitationScheduler {

  @PersistenceContext
  private EntityManager entityManager;

  @Schedule(minute = "*/5", hour = "*", persistent = false)
  public void sendPendingInvitations() {
    GenericDAO.setEntityManager(entityManager);
    try {
      if (SystemUtils.isProductionEnvironment()) {
        Date start = new Date();
        Date end = new Date(start.getTime() + 300000);
        long maxTime = 240000;
        PanelInvitationDAO panelInvitationDAO = new PanelInvitationDAO();
        EmailMessageDAO emailMessageDAO = new EmailMessageDAO();
        List<PanelInvitation> invitations = panelInvitationDAO.listByStateAndArchived(PanelInvitationState.IN_QUEUE, Boolean.FALSE);
        if (invitations.size() > 0) {
          Logging.logInfo("sendPendingInvitations:" + invitations.size() + " invitations in queue");
        }
        int invitationsProcessed = 0;
        for (PanelInvitation invitation : invitations) {
          if (invitation.getEmailMessage() == null) {
            panelInvitationDAO.updateState(invitation, PanelInvitationState.SEND_FAIL, invitation.getCreator());
            Logging.logError("sendPendingInvitations:invitation " + invitation.getId() + " has no emailMessage");
          }
          else {
            panelInvitationDAO.updateState(invitation, PanelInvitationState.BEING_SENT, invitation.getCreator());
            try {
              MailUtils.sendMail(Locale.getDefault(), invitation.getEmailMessage());
              panelInvitationDAO.updateState(invitation, PanelInvitationState.PENDING, invitation.getCreator());
              emailMessageDAO.archive(invitation.getEmailMessage());
            } catch (Exception e) {
              Logging.logError("sendPendingInvitations:sending invitation " + invitation.getId() + " failed");
              Logging.logException(e);
              panelInvitationDAO.updateState(invitation, PanelInvitationState.SEND_FAIL, invitation.getCreator());
            }
            invitationsProcessed++;
          }
          Date now = new Date();
          if (now.getTime() + maxTime > end.getTime()) {
            Logging.logInfo("sendPendingInvitations:timeout");
            break;
          }
        }
        if (invitations.size() > 0) {
          Logging.logInfo("sendPendingInvitations:" + invitationsProcessed + "/" + invitations.size() + " invitations processed");
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