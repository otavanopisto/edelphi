package fi.internetix.edelphi.pages.panel.admin;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.EdelfoiStatusCode;
import fi.internetix.edelphi.dao.resources.LocalDocumentDAO;
import fi.internetix.edelphi.dao.resources.LocalDocumentPageDAO;
import fi.internetix.edelphi.dao.users.UserDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.resources.LocalDocument;
import fi.internetix.edelphi.domainmodel.resources.LocalDocumentPage;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.edelphi.pages.panel.PanelPageController;
import fi.internetix.edelphi.utils.ActionUtils;
import fi.internetix.edelphi.utils.MaterialUtils;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.edelphi.utils.ResourceLockUtils;
import fi.internetix.smvc.PageNotFoundException;
import fi.internetix.smvc.Severity;
import fi.internetix.smvc.SmvcRuntimeException;
import fi.internetix.smvc.controllers.PageRequestContext;

public class EditLocalDocumentPageController extends PanelPageController {

  public EditLocalDocumentPageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL_MATERIALS, DelfoiActionScope.PANEL);
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    LocalDocumentDAO localDocumentDAO = new LocalDocumentDAO();
    LocalDocumentPageDAO localDocumentPageDAO = new LocalDocumentPageDAO();
    UserDAO userDAO = new UserDAO();
    
    Panel panel = RequestUtils.getPanel(pageRequestContext);
    if (panel == null) {
      throw new PageNotFoundException(pageRequestContext.getRequest().getLocale());
    }
    
    Long localDocumentId = pageRequestContext.getLong("localDocumentId");
    LocalDocument localDocument = localDocumentDAO.findById(localDocumentId);
    if (localDocument == null) {
      throw new PageNotFoundException(pageRequestContext.getRequest().getLocale());
    }
    
    Locale locale = pageRequestContext.getRequest().getLocale();
    Messages messages = Messages.getInstance();
    boolean resourceLocked = false;
    
    User loggedUser = userDAO.findById(pageRequestContext.getLoggedUserId());
    if (ResourceLockUtils.isLocked(loggedUser, localDocument)) {
      User lockCreator = ResourceLockUtils.getResourceLockCreator(localDocument);
      pageRequestContext.addMessage(Severity.WARNING, messages.getText(locale, "panelAdmin.block.localDocumentEditor.lockedMessage", new Object[] { lockCreator.getFullName(false, true) }));
      resourceLocked = true;
    }
    
    if (resourceLocked == false) {
      ResourceLockUtils.lockResource(loggedUser, localDocument);
      
      List<LocalDocumentPage> localDocumentPages = localDocumentPageDAO.listByDocument(localDocument);
      
      Collections.sort(localDocumentPages, new Comparator<LocalDocumentPage>() {
        @Override
        public int compare(LocalDocumentPage o1, LocalDocumentPage o2) {
          return o1.getPageNumber() - o2.getPageNumber();
        }
      });

      pageRequestContext.getRequest().setAttribute("localDocument", localDocument);
      pageRequestContext.getRequest().setAttribute("localDocumentPages", localDocumentPages);

      // Populate pages to js data
      JSONArray localDocumentPagesJs = new JSONArray();
      for (LocalDocumentPage page : localDocumentPages) {
        JSONObject pageJs = new JSONObject();
        pageJs.put("id", page.getId().toString());
        pageJs.put("title", page.getTitle().toString());
        pageJs.put("content", page.getContent() != null ? page.getContent().toString() : "");
        localDocumentPagesJs.add(pageJs);
      }
      setJsDataVariable(pageRequestContext, "localDocumentPages", localDocumentPagesJs.toString());
    }
    
    pageRequestContext.getRequest().setAttribute("panel", panel);
    try {
      pageRequestContext.getRequest().setAttribute("materials", MaterialUtils.listPanelMaterials(panel, true));
    } catch (IOException e) {
      throw new SmvcRuntimeException(EdelfoiStatusCode.GOOGLE_DOCS_FAILURE, messages.getText(locale, "exception.1012.googleDocsFailure"), e);
    }
    
    ActionUtils.includeRoleAccessList(pageRequestContext);
    pageRequestContext.getRequest().setAttribute("resourceLocked", resourceLocked);
    pageRequestContext.setIncludeJSP("/jsp/pages/panel/admin/editlocaldocument.jsp");
  }

}