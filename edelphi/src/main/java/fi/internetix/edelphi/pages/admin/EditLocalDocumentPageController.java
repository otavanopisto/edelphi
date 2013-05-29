package fi.internetix.edelphi.pages.admin;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.EdelfoiStatusCode;
import fi.internetix.edelphi.dao.resources.LocalDocumentDAO;
import fi.internetix.edelphi.dao.resources.LocalDocumentPageDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.base.Delfoi;
import fi.internetix.edelphi.domainmodel.resources.Folder;
import fi.internetix.edelphi.domainmodel.resources.LocalDocument;
import fi.internetix.edelphi.domainmodel.resources.LocalDocumentPage;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.edelphi.pages.DelfoiPageController;
import fi.internetix.edelphi.utils.ActionUtils;
import fi.internetix.edelphi.utils.MaterialBean;
import fi.internetix.edelphi.utils.MaterialUtils;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.edelphi.utils.ResourceLockUtils;
import fi.internetix.smvc.PageNotFoundException;
import fi.internetix.smvc.Severity;
import fi.internetix.smvc.SmvcRuntimeException;
import fi.internetix.smvc.controllers.PageRequestContext;

public class EditLocalDocumentPageController extends DelfoiPageController {

  public EditLocalDocumentPageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_DELFOI_MATERIALS, DelfoiActionScope.DELFOI);
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    LocalDocumentDAO localDocumentDAO = new LocalDocumentDAO();
    LocalDocumentPageDAO localDocumentPageDAO = new LocalDocumentPageDAO();

    Delfoi delfoi = RequestUtils.getDelfoi(pageRequestContext);
    if (delfoi == null) {
      throw new PageNotFoundException(pageRequestContext.getRequest().getLocale());
    }

    Messages messages = Messages.getInstance();
    Locale locale = pageRequestContext.getRequest().getLocale();
    User loggedUser = RequestUtils.getUser(pageRequestContext);
    
    Folder folder = null;
    String category = pageRequestContext.getString("cat");
    String language = pageRequestContext.getString("lang");
    if (StringUtils.isEmpty(language))
      language = locale.getLanguage();

    try {
      if ("help".equals(category)) {
        folder = MaterialUtils.getDelfoiHelpFolder(delfoi, language, loggedUser); 
      } else {
        if ("materials".equals(category))
          folder = MaterialUtils.getDelfoiMaterialFolder(delfoi, language, loggedUser); 
      }
    } catch (Exception ex) {
    }
    
    if (folder == null)
      throw new PageNotFoundException(pageRequestContext.getRequest().getLocale());
    
    Long localDocumentId = pageRequestContext.getLong("localDocumentId");
    LocalDocument localDocument = localDocumentDAO.findById(localDocumentId);
    if (localDocument == null) {
      throw new PageNotFoundException(pageRequestContext.getRequest().getLocale());
    }

    boolean resourceLocked = ResourceLockUtils.isLocked(loggedUser, localDocument);
    
    if (resourceLocked) {
      User lockCreator = ResourceLockUtils.getResourceLockCreator(localDocument);
      pageRequestContext.addMessage(Severity.WARNING, messages.getText(locale, "panelAdmin.block.localDocumentEditor.lockedMessage", new Object[] { lockCreator.getFullName(false, true) }));
    } else  {
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

    pageRequestContext.getRequest().setAttribute("parentFolder", folder);
    pageRequestContext.getRequest().setAttribute("delfoi", delfoi);
    pageRequestContext.getRequest().setAttribute("dashboardCategory", category);
    pageRequestContext.getRequest().setAttribute("dashboardLang", pageRequestContext.getString("lang"));
    
    try {
      List<MaterialBean> materials = MaterialUtils.listFolderMaterials(folder, true, true);
      Collections.sort(materials, new Comparator<MaterialBean>() {
        @Override
        public int compare(MaterialBean o1, MaterialBean o2) {
          return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
        }
      });
      pageRequestContext.getRequest().setAttribute("materials", materials);
      pageRequestContext.getRequest().setAttribute("materialTrees", MaterialUtils.listMaterialTrees(folder, true, true));
    } catch (IOException e) {
      throw new SmvcRuntimeException(EdelfoiStatusCode.GOOGLE_DOCS_FAILURE, messages.getText(locale, "exception.1012.googleDocsFailure"), e);
    }

    ActionUtils.includeRoleAccessList(pageRequestContext);
    
    pageRequestContext.setIncludeJSP("/jsp/pages/admin/editlocaldocument.jsp");
  }

}