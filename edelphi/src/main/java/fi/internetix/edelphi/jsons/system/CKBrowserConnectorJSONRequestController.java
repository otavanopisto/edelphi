package fi.internetix.edelphi.jsons.system;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.panels.PanelDAO;
import fi.internetix.edelphi.dao.resources.ResourceDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.resources.Folder;
import fi.internetix.edelphi.domainmodel.resources.Resource;
import fi.internetix.edelphi.domainmodel.resources.ResourceType;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.edelphi.utils.SessionUtils;
import fi.internetix.smvc.controllers.JSONRequestContext;
import fi.internetix.smvc.controllers.RequestContext;

public class CKBrowserConnectorJSONRequestController extends JSONController {
  
  public CKBrowserConnectorJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL_MATERIALS, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    Action action = Action.valueOf(jsonRequestContext.getString("action"));
    
    switch (action) {
      case LIST_MATERIALS:
        handleListMaterials(jsonRequestContext);
      break;
    }
  }
  
  private void handleListMaterials(JSONRequestContext requestContext) {
    Long panelId = requestContext.getLong("panelId");
    String dialog = requestContext.getString("dialog");
        
    PanelDAO panelDAO = new PanelDAO();
    Panel panel = panelDAO.findById(panelId);
    
    List<ResourceType> resourceTypes = null;
    
    if ("image".equals(dialog)) {
      resourceTypes = Arrays.asList(new ResourceType[]{
        ResourceType.FOLDER,
        ResourceType.GOOGLE_IMAGE, 
        ResourceType.LINKED_IMAGE, 
        ResourceType.LOCAL_IMAGE, 
      });
    } else {
      resourceTypes = Arrays.asList(new ResourceType[]{
        ResourceType.FOLDER,
        ResourceType.GOOGLE_DOCUMENT, 
        ResourceType.GOOGLE_IMAGE, 
        ResourceType.LINKED_IMAGE, 
        ResourceType.LOCAL_DOCUMENT, 
        ResourceType.LOCAL_IMAGE, 
        ResourceType.VIDEO
      });
    }
    
    requestContext.addResponseParameter("materials", createMaterialsList(requestContext, panel.getRootFolder(), resourceTypes));
    requestContext.addResponseParameter("status", Status.OK);
  }

  private JSONArray createMaterialsList(RequestContext requestContext, Folder parentFolder, List<ResourceType> types) {
    JSONArray materials = new JSONArray();
    
    ResourceDAO resourceDAO = new ResourceDAO();
    List<Resource> resources = resourceDAO.listByTypesAndFolderAndArchived(types, parentFolder, Boolean.FALSE);

    for (Resource resource : resources) {
      ResourceType type = resource.getType();
      String iconUrl = "";
      String path = "";
      
      switch (type) {
        case FOLDER:
          iconUrl = SessionUtils.getThemePath(requestContext) + "/gfx/icons/16x16/apps/folder.png";
          path = resource.getFullPath();
        break;
        case GOOGLE_DOCUMENT:
          iconUrl = SessionUtils.getThemePath(requestContext) + "/gfx/icons/16x16/apps/google_document.png";
          path = resource.getFullPath();
        break;
        case GOOGLE_IMAGE:
          iconUrl = SessionUtils.getThemePath(requestContext) + "/gfx/icons/16x16/apps/google_image.png";
          path = "/resources/viewimage.binary?imageId=" + resource.getId();
        break;
        case LINKED_IMAGE:
          iconUrl = SessionUtils.getThemePath(requestContext) + "/gfx/icons/16x16/apps/linked_image.png";
          path = "/resources/viewimage.binary?imageId=" + resource.getId();
        break;
        case LOCAL_DOCUMENT:
          iconUrl = SessionUtils.getThemePath(requestContext) + "/gfx/icons/16x16/apps/local_document.png";
          path = resource.getFullPath();
        break;
        case LOCAL_IMAGE:
          iconUrl = SessionUtils.getThemePath(requestContext) + "/gfx/icons/16x16/apps/local_image.png";
          path = "/resources/viewimage.binary?imageId=" + resource.getId();
        break;
        case QUERY:
          iconUrl = SessionUtils.getThemePath(requestContext) + "/gfx/icons/16x16/apps/query.png";
          path = resource.getFullPath();
        break;
        case VIDEO:
          iconUrl = SessionUtils.getThemePath(requestContext) + "/gfx/icons/16x16/apps/video.png";
          path = resource.getFullPath();
        break;
      }
      
      materials.add(createMaterialObject(resource.getName(), RequestUtils.getBaseUrl(requestContext.getRequest()) + path, iconUrl, resource.getLastModified(), 0l));
    }
    
    return materials;
  }

  private JSONObject createMaterialObject(String name, String path, String iconUrl, Date date, long size) {
    JSONObject fileObject = new JSONObject();
    fileObject.put("name", name);
    fileObject.put("path", path);
    fileObject.put("iconUrl", iconUrl);
    fileObject.put("date", DATE_FORMAT.format(date));
    fileObject.put("size", getSize(size));
    return fileObject;
  }
  
  private String getSize(long size) {
    if (size > 0 && size < 1024) {
      return "1";
    } else {
      return String.valueOf(Math.round(size / 1024));
    }
  }
  
  private static DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm");
  
  private enum Action {
    LIST_MATERIALS
  }
  
  private enum Status {
    OK
  }
}