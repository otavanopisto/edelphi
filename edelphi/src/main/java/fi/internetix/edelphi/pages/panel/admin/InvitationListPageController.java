package fi.internetix.edelphi.pages.panel.admin;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.panels.PanelInvitationDAO;
import fi.internetix.edelphi.dao.panels.PanelUserDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.panels.PanelInvitation;
import fi.internetix.edelphi.domainmodel.panels.PanelInvitationState;
import fi.internetix.edelphi.domainmodel.panels.PanelUser;
import fi.internetix.edelphi.pages.panel.PanelPageController;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.PageNotFoundException;
import fi.internetix.smvc.controllers.PageRequestContext;

public class InvitationListPageController extends PanelPageController {

  public InvitationListPageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL_INVITATIONS, DelfoiActionScope.PANEL);
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    
    Panel panel = RequestUtils.getPanel(pageRequestContext);
    if (panel == null) {
      throw new PageNotFoundException(pageRequestContext.getRequest().getLocale());
    }
    
    PanelInvitationDAO panelInvitationDAO = new PanelInvitationDAO();
    List<PanelInvitation> invitations = panelInvitationDAO.listByPanel(panel);
    Collections.sort(invitations, new Comparator<PanelInvitation>() {
      public int compare(PanelInvitation o1, PanelInvitation o2) {
        return o1.getEmail().compareTo(o2.getEmail());
      }
    });
    int failedCount = 0;
    int queuedCount = 0;
    int declinedCount = 0;
    int pendingCount = 0;
    // Prune accepted invitations as the user is listed as a panelist 
    for (int i = invitations.size() - 1; i >= 0; i--) {
      if (invitations.get(i).getState() == PanelInvitationState.ACCEPTED) {
        invitations.remove(i);
      }
    }
    for (PanelInvitation invitation : invitations) {
      switch (invitation.getState()) {
        case ACCEPTED:
          break;
        case DECLINED:
          declinedCount++;
          break;
        case PENDING:
          pendingCount++;
          break;
        case SEND_FAIL:
          failedCount++;
          break;
        case IN_QUEUE:
        case BEING_SENT:
          queuedCount++;
          break;
      }
    }
    pageRequestContext.getRequest().setAttribute("invitations", invitations);
    pageRequestContext.getRequest().setAttribute("declinedCount", declinedCount);
    pageRequestContext.getRequest().setAttribute("pendingCount", pendingCount);
    pageRequestContext.getRequest().setAttribute("queuedCount", queuedCount);
    pageRequestContext.getRequest().setAttribute("failedCount", failedCount);

    PanelUserDAO panelUserDAO = new PanelUserDAO();
    List<PanelUser> panelUsers = panelUserDAO.listByPanelAndRoleAndStamp(panel, panel.getDefaultPanelUserRole(), panel.getCurrentStamp());
    int addedCount = 0;
    int acceptedCount = 0;
    int registeredCount = 0;
    for (PanelUser panelUser : panelUsers) {
      switch (panelUser.getJoinType()) {
        case ADDED:
          addedCount++;
          break;
        case INVITED:
          acceptedCount++;
          break;
        case REGISTERED:
          registeredCount++;
          break;
      }
    }
    Collections.sort(panelUsers, new Comparator<PanelUser>() {
      public int compare(PanelUser o1, PanelUser o2) {
        String s1 = o1.getUser().getFullName() == null ? "" : o1.getUser().getFullName().toLowerCase();
        String s2 = o2.getUser().getFullName() == null ? "" : o2.getUser().getFullName().toLowerCase();
        return s1.compareTo(s2);
      }
    });
    pageRequestContext.getRequest().setAttribute("panelUsers", panelUsers);
    pageRequestContext.getRequest().setAttribute("addedCount", addedCount);
    pageRequestContext.getRequest().setAttribute("acceptedCount", acceptedCount);
    pageRequestContext.getRequest().setAttribute("registeredCount", registeredCount);
    
    pageRequestContext.setIncludeJSP("/jsp/pages/panel/admin/inviteusers_listusers.jsp");
  }

}