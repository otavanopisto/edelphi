package fi.internetix.edelphi.pages.panel.admin;

import java.util.Locale;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.EdelfoiStatusCode;
import fi.internetix.edelphi.dao.panels.PanelDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.edelphi.pages.panel.PanelPageController;
import fi.internetix.edelphi.utils.ActionUtils;
import fi.internetix.edelphi.utils.MaterialUtils;
import fi.internetix.smvc.SmvcRuntimeException;
import fi.internetix.smvc.controllers.PageRequestContext;

public class ManagePanelMaterialsPageController extends PanelPageController {

  public ManagePanelMaterialsPageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL_MATERIALS, DelfoiActionScope.PANEL);
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    PanelDAO panelDAO = new PanelDAO();

    Long panelId = pageRequestContext.getLong("panelId");

    Panel panel = panelDAO.findById(panelId);

    ActionUtils.includeRoleAccessList(pageRequestContext);
    
    try {
      pageRequestContext.getRequest().setAttribute("panel", panel);
      pageRequestContext.getRequest().setAttribute("materials", MaterialUtils.listPanelMaterials(panel, true));

      pageRequestContext.setIncludeJSP("/jsp/pages/panel/admin/managematerials.jsp");
    }
    catch (Exception e) {
      Messages messages = Messages.getInstance();
      Locale locale = pageRequestContext.getRequest().getLocale();
      throw new SmvcRuntimeException(EdelfoiStatusCode.GOOGLE_DOCS_FAILURE, messages.getText(locale, "exception.1012.googleDocsFailure"), e);
    }
  }

}