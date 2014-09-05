package fi.internetix.edelphi.pages.panel.admin.report;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.pages.panel.PanelPageController;
import fi.internetix.smvc.controllers.PageRequestContext;

public class ExportLinksPageController extends PanelPageController {

  public ExportLinksPageController() {
    setAccessAction(DelfoiActionName.MANAGE_PANEL, DelfoiActionScope.PANEL);
  }
  
  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    pageRequestContext.getRequest().setAttribute("panelId", pageRequestContext.getLong("panelId"));
    pageRequestContext.getRequest().setAttribute("queryId", pageRequestContext.getLong("queryId"));
    pageRequestContext.getRequest().setAttribute("queryPageId", pageRequestContext.getLong("queryPageId"));
    pageRequestContext.getRequest().setAttribute("stampId", pageRequestContext.getLong("stampId"));
    pageRequestContext.getRequest().setAttribute("serializedContext", pageRequestContext.getString("serializedReportContext"));
    pageRequestContext.setIncludeJSP("/jsp/blocks/panel/admin/report/exportlinks.jsp");
  }

}
