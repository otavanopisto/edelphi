package fi.internetix.edelphi.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import fi.internetix.edelphi.dao.base.DelfoiDAO;
import fi.internetix.edelphi.dao.panels.PanelDAO;
import fi.internetix.edelphi.dao.resources.ResourceDAO;
import fi.internetix.edelphi.domainmodel.base.Delfoi;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.resources.Folder;
import fi.internetix.edelphi.domainmodel.resources.Resource;

public class ResourceUtils {

  public static String getUrlName(String name) {
    if (name == null) {
      return null;
    }
    String urlName = name.trim().toLowerCase().replace(' ', '-').replace('/', '-');
    while (urlName.indexOf("--") > 0) {
      urlName = urlName.replace("--", "-");
    }
    try {
      urlName = URLEncoder.encode(urlName, "UTF-8");
    }
    catch (UnsupportedEncodingException uee) {
      // TODO orly?
    }
    return urlName;
  }

  public static String decodeUrlName(String name) {
//    String urlName = name.trim().toLowerCase().replace(' ', '-').replace('/', '-');
//    while (urlName.indexOf("--") > 0) {
//      urlName = urlName.replace("--", "-");
//    }
    try {
      return URLDecoder.decode(name, "UTF-8");
    }
    catch (UnsupportedEncodingException uee) {
      // TODO orly?
    }
    return name;
  }
  
  public static boolean isUrlNameAvailable(String urlName, Folder parentFolder) {
    if (StringUtils.isEmpty(urlName)) {
      return false;
    }
    ResourceDAO resourceDAO = new ResourceDAO();
    Resource resource = resourceDAO.findByUrlNameAndParentFolder(urlName, parentFolder);
    return resource == null;
  }

  public static boolean isUrlNameAvailable(String urlName, Folder parentFolder, Resource ownerResource) {
    if (StringUtils.isEmpty(urlName)) {
      return false;
    }
    ResourceDAO resourceDAO = new ResourceDAO();
    Resource resource = resourceDAO.findByUrlNameAndParentFolder(urlName, parentFolder);
    return resource == null || resource.getId().equals(ownerResource.getId());
  }
  
  public static String getUniqueUrlName(String name, Folder parentFolder) {
    int i = 1;
    String urlName = getUrlName(name);
    while (!isUrlNameAvailable(urlName, parentFolder)) {
      urlName = getUrlName(name == null ? ++i + "" : name + " (" + (++i) + ")");
    }
    return urlName;
  }
  
  public static Panel getResourcePanel(Resource resource) {
    PanelDAO panelDAO = new PanelDAO();
    
    List<Folder> resourceFolders = new ArrayList<Folder>();
    Resource current = resource;
    Folder folder = current instanceof Folder ? (Folder) current : current == null ? null : current.getParentFolder();
    while (folder != null) {
      resourceFolders.add(folder);
      current = folder;
      folder = current.getParentFolder();
    }
    
    Folder panelFolder = null;
    int panelIndex = resourceFolders.size() - 2;

    if (panelIndex >= 0) {
      panelFolder = resourceFolders.get(panelIndex);
    }
   
    if (panelFolder != null) {
      return panelDAO.findByRootFolder(panelFolder);
    }
    
    return null;
  }

  public static Delfoi getResourceDelfoi(Resource resource) {
    DelfoiDAO delfoiDAO = new DelfoiDAO();
    
    Resource current = resource;
    Folder parentFolder;
    
    if (current instanceof Folder)
      parentFolder = (Folder) current;
    else
      parentFolder = current.getParentFolder();

    while (parentFolder.getParentFolder() != null) {
      parentFolder = parentFolder.getParentFolder();
    }
    
    return delfoiDAO.findByRootFolder(parentFolder);
  }
  
  public static Integer getNextIndexNumber(Folder parentFolder) {
    ResourceDAO resourceDAO = new ResourceDAO();
    
    Integer num = resourceDAO.findMaxIndexNumber(parentFolder);
    if (num != null)
      return num.intValue() + 1;
    else
      return new Integer(0);
  }
  
}
