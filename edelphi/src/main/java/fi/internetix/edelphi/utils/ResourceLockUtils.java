package fi.internetix.edelphi.utils;

import java.util.Date;

import fi.internetix.edelphi.dao.resources.ResourceLockDAO;
import fi.internetix.edelphi.domainmodel.resources.Resource;
import fi.internetix.edelphi.domainmodel.resources.ResourceLock;
import fi.internetix.edelphi.domainmodel.users.User;

public class ResourceLockUtils {

  public static boolean isLocked(User user, Resource resource) {
    ResourceLockDAO resourceLockDAO = new ResourceLockDAO();
    ResourceLock resourceLock = resourceLockDAO.findByResource(resource);
    
    if (resourceLock != null) {
      if (resourceLock.getCreator().getId().equals(user.getId()))
        return false;
      
      Date now = new Date();
      if (resourceLock.getExpires().before(now))
        return false;
      
      return true;
    }
    
    return false;
  }
  
  public static ResourceLock lockResource(User user, Resource resource) {
    ResourceLockDAO resourceLockDAO = new ResourceLockDAO();
    ResourceLock resourceLock = resourceLockDAO.findByResource(resource);
    if (resourceLock != null) {
      resourceLockDAO.delete(resourceLock);
    }

    return resourceLockDAO.create(resource, user, new Date(System.currentTimeMillis() + EXPIRE_TIME));
  }
  
  public static ResourceLock touchResourceLock(Resource resource) {
    ResourceLockDAO resourceLockDAO = new ResourceLockDAO();
    ResourceLock resourceLock = resourceLockDAO.findByResource(resource);
    Date expires = new Date(System.currentTimeMillis() + EXPIRE_TIME);
    return resourceLockDAO.updateExpires(resourceLock, expires);
  }
  
  public static User getResourceLockCreator(Resource resource) {
    ResourceLockDAO resourceLockDAO = new ResourceLockDAO();
    ResourceLock resourceLock = resourceLockDAO.findByResource(resource);
    if (resourceLock != null)
      return resourceLock.getCreator();
    return null;
  }

  private static long EXPIRE_TIME = 1000 * 30;

}
