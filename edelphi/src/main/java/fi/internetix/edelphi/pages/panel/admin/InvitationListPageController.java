package fi.internetix.edelphi.pages.panel.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.panels.PanelInvitationDAO;
import fi.internetix.edelphi.dao.panels.PanelUserDAO;
import fi.internetix.edelphi.dao.users.UserEmailDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.panels.PanelInvitation;
import fi.internetix.edelphi.domainmodel.panels.PanelInvitationState;
import fi.internetix.edelphi.domainmodel.panels.PanelUser;
import fi.internetix.edelphi.domainmodel.users.UserEmail;
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
    
    // Statistic variables to help creating the user interface
    
    int addedCount = 0;
    int acceptedCount = 0;
    int registeredCount = 0;
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
    
    // Prune duplicates (e.g. same user invited to two different queries in the same panel)
    
    PanelInvitation currentInvitation, storedInvitation;
    Map<String, Integer> userIndices = new HashMap<String, Integer>();
    for (int i = 0; i < invitations.size(); i++) {
      currentInvitation = invitations.get(i);
      Integer storedIndex = userIndices.get(currentInvitation.getEmail());
      if (storedIndex != null) {
        storedInvitation = invitations.get(storedIndex);
        long currentStamp = currentInvitation.getLastModified() == null ? 0 : currentInvitation.getLastModified().getTime(); 
        long storedStamp = storedInvitation.getLastModified() == null ? 0 : storedInvitation.getLastModified().getTime(); 
        if (storedStamp < currentStamp) {
          userIndices.put(currentInvitation.getEmail(), i - 1);
          invitations.remove(storedIndex.intValue());
        }
        else {
          invitations.remove(i);
        }
        i--;
      }
      else {
        userIndices.put(currentInvitation.getEmail(), i);
      }
    }

    List<UserBean> userBeans = new ArrayList<UserBean>();

    // Convert panel users to user beans
    
    PanelUserDAO panelUserDAO = new PanelUserDAO();
    List<PanelUser> panelUsers = panelUserDAO.listByPanelAndRoleAndStamp(panel, panel.getDefaultPanelUserRole(), panel.getCurrentStamp());
    for (PanelUser panelUser : panelUsers) {
      String type = UserBean.ADDED;
      switch (panelUser.getJoinType()) {
        case ADDED:
          addedCount++;
          break;
        case INVITED:
          acceptedCount++;
          type = UserBean.ACCEPTED;
          break;
        case REGISTERED:
          registeredCount++;
          type = UserBean.REGISTERED;
          break;
      }
      Long userId = panelUser.getUser().getId();
      String firstName = panelUser.getUser().getFirstName();
      String lastName = panelUser.getUser().getLastName();
      String fullName = panelUser.getUser().getFullName(true,  false);
      UserEmail userEmail = panelUser.getUser().getDefaultEmail();
      String email = userEmail == null ? null : userEmail.getObfuscatedAddress();
      userBeans.add(new UserBean(type, userId, firstName, lastName, fullName, email));
    }
    
    // Convert invitations to user beans
    
    UserEmailDAO userEmailDAO = new UserEmailDAO();
    for (PanelInvitation invitation : invitations) {
      String type = UserBean.ACCEPTED;
      switch (invitation.getState()) {
        case ACCEPTED:
          acceptedCount++;
          break;
        case DECLINED:
          declinedCount++;
          type = UserBean.DECLINED;
          break;
        case PENDING:
          pendingCount++;
          type = UserBean.PENDING;
          break;
        case SEND_FAIL:
          failedCount++;
          type = UserBean.FAILED;
          break;
        case IN_QUEUE:
        case BEING_SENT:
          queuedCount++;
          type = UserBean.QUEUED;
          break;
      }
      UserEmail userEmail = userEmailDAO.findByAddress(invitation.getEmail());
      Long userId = userEmail == null ? null : userEmail.getUser().getId();
      String firstName = userEmail == null ? null : userEmail.getUser().getFirstName();
      String lastName = userEmail == null ? null : userEmail.getUser().getLastName();
      String fullName = userEmail == null ? null : userEmail.getUser().getFullName(true,  false);
      String email = userEmail == null ? invitation.getEmail() : invitation.getObfuscatedEmail();
      userBeans.add(new UserBean(type, userId, firstName, lastName, fullName, email));
    }

    Collections.sort(userBeans, new Comparator<UserBean>() {
      public int compare(UserBean o1, UserBean o2) {
        String s1 = o1.getFullName() == null ? o1.getEmail() : o1.getFullName();
        s1 = s1 == null ? "" : s1.toLowerCase();
        String s2 = o2.getFullName() == null ? o2.getEmail() : o2.getFullName();
        s2 = s2 == null ? "" : s2.toLowerCase();
        return s1.compareTo(s2);
      }
    });
    
    // Add request attributes
    
    pageRequestContext.getRequest().setAttribute("addedCount", addedCount);
    pageRequestContext.getRequest().setAttribute("acceptedCount", acceptedCount);
    pageRequestContext.getRequest().setAttribute("registeredCount", registeredCount);
    pageRequestContext.getRequest().setAttribute("declinedCount", declinedCount);
    pageRequestContext.getRequest().setAttribute("pendingCount", pendingCount);
    pageRequestContext.getRequest().setAttribute("queuedCount", queuedCount);
    pageRequestContext.getRequest().setAttribute("failedCount", failedCount);
    pageRequestContext.getRequest().setAttribute("userBeans", userBeans);
    
    pageRequestContext.setIncludeJSP("/jsp/pages/panel/admin/inviteusers_listusers.jsp");
  }

  public class UserBean {
    public UserBean(String type, Long userId, String firstName, String lastName, String fullName, String email) {
      this.setType(type);
      this.setUserId(userId);
      this.setFirstName(firstName);
      this.setLastName(lastName);
      this.setFullName(fullName);
      this.setEmail(email);
    }
    public String getType() {
      return type;
    }
    public void setType(String type) {
      this.type = type;
    }
    public Long getUserId() {
      return userId;
    }
    public void setUserId(Long userId) {
      this.userId = userId;
    }
    public String getFullName() {
      return fullName;
    }
    public void setFullName(String fullName) {
      this.fullName = fullName;
    }
    public String getEmail() {
      return email;
    }
    public void setEmail(String email) {
      this.email = email;
    }
    public String getFirstName() {
      return firstName;
    }
    public void setFirstName(String firstName) {
      this.firstName = firstName;
    }
    public String getLastName() {
      return lastName;
    }
    public void setLastName(String lastName) {
      this.lastName = lastName;
    }
    private String type;
    private Long userId;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    public static final String ADDED = "ADDED";
    public static final String ACCEPTED = "ACCEPTED";
    public static final String REGISTERED = "REGISTERED";
    public static final String DECLINED = "DECLINED";
    public static final String PENDING = "PENDING";
    public static final String QUEUED = "QUEUED";
    public static final String FAILED = "FAILED";
  }
}
