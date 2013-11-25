package fi.internetix.edelphi.pages;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fi.internetix.edelphi.dao.base.DelfoiBulletinDAO;
import fi.internetix.edelphi.dao.panels.PanelDAO;
import fi.internetix.edelphi.domainmodel.base.Delfoi;
import fi.internetix.edelphi.domainmodel.base.DelfoiBulletin;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.panels.PanelAccessLevel;
import fi.internetix.edelphi.domainmodel.panels.PanelState;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.utils.ActionUtils;
import fi.internetix.edelphi.utils.AuthUtils;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.controllers.PageRequestContext;

public class IndexPageController extends PageController {

  @Override
  public void process(PageRequestContext pageRequestContext) {
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

    User loggedUser = RequestUtils.getUser(pageRequestContext);
    if (loggedUser != null) {
      List<Panel> myPanels = ActionUtils.isSuperUser(pageRequestContext) ? panelDAO.listByDelfoi(delfoi) : panelDAO.listByDelfoiAndUser(delfoi, loggedUser);
      Collections.sort(myPanels, new Comparator<Panel>() {
        @Override
        public int compare(Panel o1, Panel o2) {
          return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
        }
      });
      pageRequestContext.getRequest().setAttribute("myPanels", myPanels);
    }

    List<DelfoiBulletin> bulletins = bulletinDAO.listByDelfoiAndArchived(delfoi, Boolean.FALSE);
    Collections.sort(bulletins, new Comparator<DelfoiBulletin>() {
      @Override
      public int compare(DelfoiBulletin o1, DelfoiBulletin o2) {
        return o2.getCreated().compareTo(o1.getCreated());
      }
    });
    pageRequestContext.getRequest().setAttribute("bulletins", bulletins);
    
    // Action access information
    ActionUtils.includeRoleAccessList(pageRequestContext);
    
    pageRequestContext.setIncludeJSP("/jsp/pages/index.jsp");
  }
}
