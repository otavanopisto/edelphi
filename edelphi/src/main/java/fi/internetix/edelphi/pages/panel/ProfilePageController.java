package fi.internetix.edelphi.pages.panel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.panels.PanelDAO;
import fi.internetix.edelphi.dao.panels.PanelInvitationDAO;
import fi.internetix.edelphi.dao.users.UserEmailDAO;
import fi.internetix.edelphi.dao.users.UserIdentificationDAO;
import fi.internetix.edelphi.dao.users.UserPasswordDAO;
import fi.internetix.edelphi.dao.users.UserSettingDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.base.Delfoi;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.panels.PanelAccessLevel;
import fi.internetix.edelphi.domainmodel.panels.PanelInvitation;
import fi.internetix.edelphi.domainmodel.panels.PanelInvitationState;
import fi.internetix.edelphi.domainmodel.panels.PanelState;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.domainmodel.users.UserEmail;
import fi.internetix.edelphi.domainmodel.users.UserIdentification;
import fi.internetix.edelphi.domainmodel.users.UserPassword;
import fi.internetix.edelphi.domainmodel.users.UserSetting;
import fi.internetix.edelphi.domainmodel.users.UserSettingKey;
import fi.internetix.edelphi.utils.ActionUtils;
import fi.internetix.edelphi.utils.AuthUtils;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.controllers.PageRequestContext;

public class ProfilePageController extends PanelPageController {

  public ProfilePageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_USER_PROFILE, DelfoiActionScope.DELFOI);
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    PanelDAO panelDAO = new PanelDAO();
    Delfoi delfoi = RequestUtils.getDelfoi(pageRequestContext);
    User loggedUser = RequestUtils.getUser(pageRequestContext);
    
    if (loggedUser != null) {
      UserPasswordDAO userPasswordDAO = new UserPasswordDAO();
      pageRequestContext.getRequest().setAttribute("user", loggedUser);

      UserPassword userPassword = userPasswordDAO.findByUser(loggedUser);
      boolean hasPassword = userPassword != null;
      
      // Logon types
      
      AuthUtils.includeAuthSources(pageRequestContext);
      UserIdentificationDAO userIdentificationDAO = new UserIdentificationDAO();
      List<UserIdentification> userIdentifications = userIdentificationDAO.listByUser(loggedUser);
      pageRequestContext.getRequest().setAttribute("userIdentifications", userIdentifications);
      
      // Invitations
      
      if (loggedUser.getDefaultEmail() != null) {
        UserEmailDAO userEmailDAO = new UserEmailDAO();
        PanelInvitationDAO panelInvitationDAO = new PanelInvitationDAO();
        List<PanelInvitation> myInvitations = new ArrayList<PanelInvitation>();
        List<UserEmail> emails = userEmailDAO.listByUser(loggedUser);
        for (UserEmail email : emails) {
          List<PanelInvitation> invitations = panelInvitationDAO.listByEmailAndState(email.getAddress(), PanelInvitationState.PENDING);
          myInvitations.addAll(invitations);
        }
        Collections.sort(myInvitations, new Comparator<PanelInvitation>() {
          @Override
          public int compare(PanelInvitation o1, PanelInvitation o2) {
            return o1.getPanel().getName().toLowerCase().compareTo(o2.getPanel().getName().toLowerCase());
          }
        });
        pageRequestContext.getRequest().setAttribute("myInvitations", myInvitations);
      }

      // Ensures returning to the profile page if an external authentication source is added to the profile
      AuthUtils.storeRedirectUrl(pageRequestContext, RequestUtils.getCurrentUrl(pageRequestContext.getRequest(), true));

      pageRequestContext.getRequest().setAttribute("userHasPassword", hasPassword);
    }

    ActionUtils.includeRoleAccessList(pageRequestContext);

    pageRequestContext.getRequest().setAttribute("panel", RequestUtils.getPanel(pageRequestContext));
    
    // Open panels
    
    List<Panel> openPanels = panelDAO.listByDelfoiAndAccessLevelAndState(delfoi, PanelAccessLevel.OPEN, PanelState.IN_PROGRESS); 
    Collections.sort(openPanels, new Comparator<Panel>() {
      @Override
      public int compare(Panel o1, Panel o2) {
        return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
      }
    });
    pageRequestContext.getRequest().setAttribute("openPanels", openPanels);

    // User settings (essentially just comment reply mails for now)
    
    UserSettingDAO userSettingDAO = new UserSettingDAO();
    UserSetting userSetting = userSettingDAO.findByUserAndKey(loggedUser, UserSettingKey.MAIL_COMMENT_REPLY);
    pageRequestContext.getRequest().setAttribute("userCommentMail", userSetting != null && "1".equals(userSetting.getValue()));

    pageRequestContext.setIncludeJSP("/jsp/pages/panel/profile.jsp");
  }

}
