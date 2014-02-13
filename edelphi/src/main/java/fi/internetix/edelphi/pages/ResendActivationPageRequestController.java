package fi.internetix.edelphi.pages;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import fi.internetix.edelphi.EdelfoiStatusCode;
import fi.internetix.edelphi.dao.base.DelfoiBulletinDAO;
import fi.internetix.edelphi.dao.panels.PanelDAO;
import fi.internetix.edelphi.dao.users.UserActivationDAO;
import fi.internetix.edelphi.dao.users.UserEmailDAO;
import fi.internetix.edelphi.domainmodel.base.Delfoi;
import fi.internetix.edelphi.domainmodel.base.DelfoiBulletin;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.panels.PanelAccessLevel;
import fi.internetix.edelphi.domainmodel.panels.PanelState;
import fi.internetix.edelphi.domainmodel.users.UserActivation;
import fi.internetix.edelphi.domainmodel.users.UserEmail;
import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.edelphi.utils.ActionUtils;
import fi.internetix.edelphi.utils.AuthUtils;
import fi.internetix.edelphi.utils.MailUtils;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.Severity;
import fi.internetix.smvc.SmvcRuntimeException;
import fi.internetix.smvc.controllers.PageRequestContext;

public class ResendActivationPageRequestController extends PageController {

  @Override
  public void process(PageRequestContext pageRequestContext) {
    UserEmailDAO userEmailDAO = new UserEmailDAO();
    String email = StringUtils.lowerCase(pageRequestContext.getString("email"));
    UserEmail userEmail = email == null ? null : userEmailDAO.findByAddress(email);
    if (userEmail == null) {
      Messages messages = Messages.getInstance();
      Locale locale = pageRequestContext.getRequest().getLocale();
      throw new SmvcRuntimeException(EdelfoiStatusCode.INVALID_ACCOUNT_ACTIVATION, messages.getText(locale, "exception.1015.invalidAccountActivation"));
    }
    UserActivationDAO userActivationDAO = new UserActivationDAO();
    UserActivation userActivation = userActivationDAO.findByUser(userEmail.getUser());
    if (userActivation == null) {
      Messages messages = Messages.getInstance();
      Locale locale = pageRequestContext.getRequest().getLocale();
      throw new SmvcRuntimeException(EdelfoiStatusCode.INVALID_ACCOUNT_ACTIVATION, messages.getText(locale, "exception.1015.invalidAccountActivation"));
    }
    Messages messages = Messages.getInstance();
    Locale locale = pageRequestContext.getRequest().getLocale();
    String mailSubject = messages.getText(locale, "userRegistration.mailSubject");
    String verificationLink = RequestUtils.getBaseUrl(pageRequestContext.getRequest()) + "/activateaccount.page?email=" + userActivation.getEmail() + "&hash=" + userActivation.getHash();
    String mailContent = messages.getText(locale, "userRegistration.mailTemplate", new String [] { email, verificationLink });
    String infoMessage = messages.getText(locale, "userRegistration.infoMessage", new String [] { email });
    MailUtils.sendMail(locale, email, mailSubject, mailContent);
    pageRequestContext.addMessage(Severity.INFORMATION, infoMessage);
    
    // Material for proper index page rendering (redirect would be better but message queue doesn't support it...) 
    
    PanelDAO panelDAO = new PanelDAO();
    DelfoiBulletinDAO bulletinDAO = new DelfoiBulletinDAO();
    Delfoi delfoi = RequestUtils.getDelfoi(pageRequestContext);
    AuthUtils.includeAuthSources(pageRequestContext, "DELFOI", delfoi.getId());
    List<Panel> openPanels = panelDAO.listByDelfoiAndAccessLevelAndState(delfoi, PanelAccessLevel.OPEN, PanelState.IN_PROGRESS); 
    Collections.sort(openPanels, new Comparator<Panel>() {
      @Override
      public int compare(Panel o1, Panel o2) {
        return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
      }
    });
    pageRequestContext.getRequest().setAttribute("openPanels", openPanels);
    List<DelfoiBulletin> bulletins = bulletinDAO.listByDelfoiAndArchived(delfoi, Boolean.FALSE);
    Collections.sort(bulletins, new Comparator<DelfoiBulletin>() {
      @Override
      public int compare(DelfoiBulletin o1, DelfoiBulletin o2) {
        return o2.getCreated().compareTo(o1.getCreated());
      }
    });
    pageRequestContext.getRequest().setAttribute("bulletins", bulletins);
    ActionUtils.includeRoleAccessList(pageRequestContext);
    
    pageRequestContext.setIncludeJSP("/jsp/pages/index.jsp");
  }
  
}
