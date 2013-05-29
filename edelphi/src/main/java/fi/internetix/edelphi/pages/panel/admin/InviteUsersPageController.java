package fi.internetix.edelphi.pages.panel.admin;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.resources.QueryDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.resources.Query;
import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.edelphi.pages.panel.PanelPageController;
import fi.internetix.edelphi.utils.ActionUtils;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.PageNotFoundException;
import fi.internetix.smvc.controllers.PageRequestContext;

public class InviteUsersPageController extends PanelPageController {

  public InviteUsersPageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL_INVITATIONS, DelfoiActionScope.PANEL);
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    Panel panel = RequestUtils.getPanel(pageRequestContext);
    if (panel == null) {
      throw new PageNotFoundException(pageRequestContext.getRequest().getLocale());
    }
    pageRequestContext.getRequest().setAttribute("panel", panel);
    
    String mailTemplate = panel.getInvitationTemplate();
    if (mailTemplate == null) {
      mailTemplate = Messages.getInstance().getText(pageRequestContext.getRequest().getLocale(), "panel.admin.inviteUsers.mailTemplate");
    }
    pageRequestContext.getRequest().setAttribute("mailTemplate", mailTemplate);
    
    ActionUtils.includeRoleAccessList(pageRequestContext);
    
    setJsDataVariable(pageRequestContext, "panelId", panel.getId().toString());
    
    QueryDAO queryDAO = new QueryDAO();
    List<Query> queries = queryDAO.listByFolderAndArchived(panel.getRootFolder(), Boolean.FALSE);
    Collections.sort(queries, new Comparator<Query>() {
      @Override
      public int compare(Query o1, Query o2) {
        return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
      }
    });
    StringBuilder ids = new StringBuilder();
    for (int i = 0; i < queries.size(); i++) {
      ids.append(queries.get(i).getId());
      if (i < queries.size() - 1) {
        ids.append(',');
      }
    }
    pageRequestContext.getRequest().setAttribute("queryIds", ids);
    pageRequestContext.getRequest().setAttribute("queries", queries);
      
    pageRequestContext.setIncludeJSP("/jsp/pages/panel/admin/inviteusers.jsp");
  }

}