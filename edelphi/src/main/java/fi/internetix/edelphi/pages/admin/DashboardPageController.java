package fi.internetix.edelphi.pages.admin;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.EdelfoiStatusCode;
import fi.internetix.edelphi.dao.base.DelfoiBulletinDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.base.Delfoi;
import fi.internetix.edelphi.domainmodel.base.DelfoiBulletin;
import fi.internetix.edelphi.domainmodel.resources.Folder;
import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.edelphi.pages.DelfoiPageController;
import fi.internetix.edelphi.utils.ActionUtils;
import fi.internetix.edelphi.utils.MaterialBean;
import fi.internetix.edelphi.utils.MaterialUtils;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.SmvcRuntimeException;
import fi.internetix.smvc.controllers.PageRequestContext;

public class DashboardPageController extends DelfoiPageController {

  public DashboardPageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_DELFOI, DelfoiActionScope.DELFOI);
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    Delfoi delfoi = RequestUtils.getDelfoi(pageRequestContext);
    DelfoiBulletinDAO bulletinDAO = new DelfoiBulletinDAO();
    
    Messages messages = Messages.getInstance();
    Locale locale = pageRequestContext.getRequest().getLocale();
    String lang = pageRequestContext.getString("lang");
    if (lang == null)
      lang = locale.getLanguage();
    
    List<DelfoiBulletin> bulletins = bulletinDAO.listByDelfoiAndArchived(delfoi, Boolean.FALSE);
    Collections.sort(bulletins, new Comparator<DelfoiBulletin>() {
      @Override
      public int compare(DelfoiBulletin o1, DelfoiBulletin o2) {
        return o2.getCreated().compareTo(o1.getCreated());
      }
    });
    
    pageRequestContext.getRequest().setAttribute("bulletins", bulletins);
    pageRequestContext.getRequest().setAttribute("dashboardLang", lang);
    pageRequestContext.getRequest().setAttribute("delfoi", delfoi);

    try {
      Folder materialFolder = MaterialUtils.getDelfoiMaterialFolder(delfoi, lang, RequestUtils.getUser(pageRequestContext));
      
      List<MaterialBean> materialMaterials = MaterialUtils.listFolderMaterials(materialFolder, true, true);
      Collections.sort(materialMaterials, new Comparator<MaterialBean>() {
        @Override
        public int compare(MaterialBean o1, MaterialBean o2) {
          return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
        }
      });
      while (materialMaterials.size() > 5) {
        materialMaterials.remove(materialMaterials.size() - 1);
      }

      pageRequestContext.getRequest().setAttribute("materialMaterials", materialMaterials);
      pageRequestContext.getRequest().setAttribute("materialMaterialCount", MaterialUtils.getMaterialCount(materialFolder, true));
      pageRequestContext.getRequest().setAttribute("materialMaterialFolderId", materialFolder.getId());
      pageRequestContext.getRequest().setAttribute("materialMaterialTrees", MaterialUtils.listMaterialTrees(materialFolder, true, true));

      Folder helpFolder = MaterialUtils.getDelfoiHelpFolder(delfoi, lang, RequestUtils.getUser(pageRequestContext));
      
      List<MaterialBean> helpMaterials = MaterialUtils.listFolderMaterials(helpFolder, true, true);
      Collections.sort(helpMaterials, new Comparator<MaterialBean>() {
        @Override
        public int compare(MaterialBean o1, MaterialBean o2) {
          return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
        }
      });
      while (helpMaterials.size() > 5) {
        helpMaterials.remove(helpMaterials.size() - 1);
      }
      
      pageRequestContext.getRequest().setAttribute("helpMaterials", helpMaterials);
      pageRequestContext.getRequest().setAttribute("helpMaterialCount", MaterialUtils.getMaterialCount(helpFolder, true));
      pageRequestContext.getRequest().setAttribute("helpMaterialFolderId", helpFolder.getId());
      pageRequestContext.getRequest().setAttribute("helpMaterialTrees", MaterialUtils.listMaterialTrees(helpFolder, true, true));
    } catch (IOException e) {
      throw new SmvcRuntimeException(EdelfoiStatusCode.GOOGLE_DOCS_FAILURE, messages.getText(locale, "exception.1012.googleDocsFailure"), e);
    }
 
    ActionUtils.includeRoleAccessList(pageRequestContext);
    
    pageRequestContext.setIncludeJSP("/jsp/pages/admin/dashboard.jsp");
  }

}