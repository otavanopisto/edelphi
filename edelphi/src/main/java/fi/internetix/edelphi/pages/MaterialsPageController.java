package fi.internetix.edelphi.pages;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fi.internetix.edelphi.EdelfoiStatusCode;
import fi.internetix.edelphi.dao.resources.LocalDocumentDAO;
import fi.internetix.edelphi.dao.resources.LocalDocumentPageDAO;
import fi.internetix.edelphi.dao.resources.ResourceDAO;
import fi.internetix.edelphi.domainmodel.base.Delfoi;
import fi.internetix.edelphi.domainmodel.resources.Document;
import fi.internetix.edelphi.domainmodel.resources.Folder;
import fi.internetix.edelphi.domainmodel.resources.LocalDocument;
import fi.internetix.edelphi.domainmodel.resources.LocalDocumentPage;
import fi.internetix.edelphi.domainmodel.resources.Resource;
import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.edelphi.utils.ActionUtils;
import fi.internetix.edelphi.utils.MaterialBean;
import fi.internetix.edelphi.utils.MaterialUtils;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.SmvcRuntimeException;
import fi.internetix.smvc.StatusCode;
import fi.internetix.smvc.controllers.PageRequestContext;

public class MaterialsPageController extends DelfoiPageController {

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    Delfoi delfoi = RequestUtils.getDelfoi(pageRequestContext);
    
    Messages messages = Messages.getInstance();
    Locale locale = pageRequestContext.getRequest().getLocale();
    String lang = locale.getLanguage();
    
    Long resourceId = pageRequestContext.getLong("documentId");
    Integer pageId = pageRequestContext.getInteger("page");
    
    pageRequestContext.getRequest().setAttribute("delfoi", delfoi);

    try {
      Folder materialFolder = MaterialUtils.getDelfoiMaterialFolder(delfoi, lang, RequestUtils.getUser(pageRequestContext));

      List<MaterialBean> folderMaterials = MaterialUtils.listFolderMaterials(materialFolder, true, true);
      Collections.sort(folderMaterials, new Comparator<MaterialBean>() {
        @Override
        public int compare(MaterialBean o1, MaterialBean o2) {
          return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
        }
      });
      Map<Long, List<MaterialBean>> materialTrees = MaterialUtils.listMaterialTrees(materialFolder, true, true);

      Document document = resolveDocument(resourceId, folderMaterials, materialTrees);
      appendDocument(pageRequestContext, document, resourceId, pageId);
      
      pageRequestContext.getRequest().setAttribute("materialDocument", document);
      pageRequestContext.getRequest().setAttribute("materials", folderMaterials);
      pageRequestContext.getRequest().setAttribute("materialCount", MaterialUtils.getMaterialCount(materialFolder, true));
      pageRequestContext.getRequest().setAttribute("materialFolderId", materialFolder.getId());
      pageRequestContext.getRequest().setAttribute("materialTrees", materialTrees);
    } catch (IOException e) {
      throw new SmvcRuntimeException(EdelfoiStatusCode.GOOGLE_DOCS_FAILURE, messages.getText(locale, "exception.1012.googleDocsFailure"), e);
    }
 
    ActionUtils.includeRoleAccessList(pageRequestContext);
    
    pageRequestContext.setIncludeJSP("/jsp/pages/materials.jsp");
  }

  private Document resolveDocument(Long resourceId, List<MaterialBean> folderMaterials, Map<Long, List<MaterialBean>> materialTrees) {
    ResourceDAO resourceDAO = new ResourceDAO();

    if ((resourceId == null) && (folderMaterials.size() > 0))
      resourceId = folderMaterials.get(0).getId();

    Document document = null;
    
    while ((resourceId != null) && (document == null)) {
      Resource resource = resourceDAO.findById(resourceId);
      
      if (resource instanceof Document) {
        // when resource is document we found the document and can break
        document = (Document) resource;
        break;
      } else {
        if (resource instanceof Folder) {
          // for folders we need to find the first document under it
          List<MaterialBean> list = materialTrees.get(resourceId);
          if ((list != null) && (list.size() > 0))
            resourceId = list.get(0).getId();
          else
            break;
        } else {
          // ??
          break;
        }
      }
    }

    return document;
  }

  private void appendDocument(PageRequestContext pageRequestContext, Document document, Long documentId, Integer page) {
    if (document == null)
      return;
    
    if (page == null)
      page = 0;
    
    LocalDocumentDAO localDocumentDAO = new LocalDocumentDAO();
    LocalDocumentPageDAO localDocumentPageDAO = new LocalDocumentPageDAO(); 

    if (document instanceof LocalDocument) {
      LocalDocument localDocument = (LocalDocument) document;
      LocalDocumentPage localDocumentPage = localDocumentPageDAO.findByDocumentAndPageNumber(localDocument, page);
      Long pageCount = localDocumentDAO.countByDocument(localDocument);
      pageRequestContext.getRequest().setAttribute("title", localDocumentPage.getTitle());
      pageRequestContext.getRequest().setAttribute("content", localDocumentPage.getContent());
      pageRequestContext.getRequest().setAttribute("page", page);
      pageRequestContext.getRequest().setAttribute("pageCount", pageCount);
    } else {
      throw new SmvcRuntimeException(StatusCode.UNDEFINED, "Info pages support only LocalDocuments");
    }
    
    pageRequestContext.getRequest().setAttribute("type", document.getType());
    pageRequestContext.getRequest().setAttribute("name", document.getName());
  }
}