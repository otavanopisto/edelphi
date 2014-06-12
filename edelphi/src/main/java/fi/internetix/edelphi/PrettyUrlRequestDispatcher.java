package fi.internetix.edelphi;

import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import fi.internetix.edelphi.dao.panels.PanelDAO;
import fi.internetix.edelphi.dao.resources.FolderDAO;
import fi.internetix.edelphi.dao.resources.ResourceDAO;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.resources.Folder;
import fi.internetix.edelphi.domainmodel.resources.Resource;
import fi.internetix.edelphi.domainmodel.resources.ResourceType;
import fi.internetix.smvc.controllers.RequestController;
import fi.internetix.smvc.controllers.RequestControllerMapper;
import fi.internetix.smvc.dispatcher.RequestDispatchContext;
import fi.internetix.smvc.dispatcher.RequestDispatcher;

public class PrettyUrlRequestDispatcher implements RequestDispatcher {
  
  public boolean canHandle(HttpServletRequest request, HttpServletResponse response) {
    String uri = request.getRequestURI();
    String ctxPath = request.getContextPath();
    String controllerName = uri.substring(ctxPath.length() + 1);
    return !controllerName.endsWith(".page") && !controllerName.endsWith(".json") && !controllerName.endsWith(".binary");
  }
  
  public RequestDispatchContext getContext(HttpServletRequest request, HttpServletResponse response) {
    RequestController requestController = null;
    PrettyUrlParameterHandler parameterHandler = new PrettyUrlParameterHandler(request);

    FolderDAO folderDAO = new FolderDAO();
    
    // Delfoi root folder

    HttpSession session = request.getSession();
    Long delfoiId = (Long) session.getAttribute("delfoiId");
    Folder delfoiRootFolder = folderDAO.findById(delfoiId);
    
    // Request path

    String uri = request.getRequestURI();
    String path = uri.substring(request.getContextPath().length() + 1);
    StringTokenizer tokenizer = new StringTokenizer(path, "/");
    int tokenCount = tokenizer.countTokens();
    if (tokenCount <= 1) {
      
      // Delfoi front page
      
      requestController = RequestControllerMapper.getRequestController(INDEX_PAGE);
    }
    else {
      tokenizer.nextElement(); // eat _app
      PanelDAO panelDAO = new PanelDAO();
      ResourceDAO resourceDAO = new ResourceDAO();

      Panel panel = null;
      Resource resource = null;
      String panelUrlName = tokenizer.nextToken();
      Folder panelRootFolder = folderDAO.findByUrlNameAndParentFolderAndArchived(panelUrlName, delfoiRootFolder, Boolean.FALSE);
      if (panelRootFolder != null) {
        panel = panelDAO.findByRootFolder(panelRootFolder);
        Folder parentFolder = panelRootFolder;
        while (tokenizer.hasMoreTokens()) {
          String token = tokenizer.nextToken();
          resource = resourceDAO.findByUrlNameAndParentFolder(token, parentFolder);
          if (tokenizer.hasMoreTokens()) {
            if (resource.getType() == ResourceType.FOLDER) {
              parentFolder = (Folder) resource;
            }
            else {
              panel = null;
              resource = null;
              break;
            }
          }
        }
        if (panel != null) {
          parameterHandler.addParameter("panelId", panel.getId().toString());
          if (resource == null) {
            requestController = RequestControllerMapper.getRequestController(PANEL_PAGE);
          }
          else {
            switch (resource.getType()) {
              case LOCAL_DOCUMENT:
              case GOOGLE_DOCUMENT:
                parameterHandler.addParameter("documentId", resource.getId().toString());
                requestController = RequestControllerMapper.getRequestController(DOCUMENT_PAGE);
              break;
              case LOCAL_IMAGE:
              case LINKED_IMAGE:
              case GOOGLE_IMAGE:
                parameterHandler.addParameter("imageId", resource.getId().toString());
                requestController = RequestControllerMapper.getRequestController(IMAGE_PAGE);
              break;
              case QUERY:
                parameterHandler.addParameter("queryId", resource.getId().toString());
                requestController = RequestControllerMapper.getRequestController(QUERY_PAGE);
              break;
              default:
              break;
            }
          }
        }
      }
    }
    return new RequestDispatchContext(requestController, parameterHandler);
  }

  private static final String INDEX_PAGE = "index.page";
  private static final String PANEL_PAGE = "panel/viewpanel.page";
  private static final String QUERY_PAGE = "panel/viewquery.page";
  private static final String DOCUMENT_PAGE = "panel/viewdocument.page";
  private static final String IMAGE_PAGE = "panel/viewimage.page";

}
