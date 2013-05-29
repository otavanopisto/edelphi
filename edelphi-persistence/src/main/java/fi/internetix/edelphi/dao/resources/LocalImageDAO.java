package fi.internetix.edelphi.dao.resources;

import java.util.Date;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.edelphi.domainmodel.resources.Folder;
import fi.internetix.edelphi.domainmodel.resources.LocalImage;
import fi.internetix.edelphi.domainmodel.users.User;

public class LocalImageDAO extends GenericDAO<LocalImage> {

  public LocalImage create(String name, String urlName, String contentType, byte[] data, Folder parentFolder, User user, Integer indexNumber) {
    LocalImage localImage = new LocalImage();
    localImage.setCreator(user);
    localImage.setCreated(new Date());
    localImage.setLastModifier(user);
    localImage.setLastModified(new Date());
    localImage.setName(name);
    localImage.setUrlName(urlName);
    localImage.setParentFolder(parentFolder);
    localImage.setContentType(contentType);
    localImage.setData(data);
    localImage.setIndexNumber(indexNumber);
    getEntityManager().persist(localImage);
    return localImage;
  }
 
  public LocalImage updateData(LocalImage localImage, String contentType, byte[] data, User modifier) {
    localImage.setData(data);
    localImage.setContentType(contentType);
    localImage.setLastModifier(modifier);
    localImage.setLastModified(new Date());
    getEntityManager().persist(localImage);
    return localImage;
  }
  
  public LocalImage updateName(LocalImage localImage, String name, String urlName, User modifier) {
    localImage.setName(name);
    localImage.setUrlName(urlName);
    localImage.setLastModifier(modifier);
    localImage.setLastModified(new Date());
    getEntityManager().persist(localImage);
    return localImage;
  }

  public LocalImage updateParentFolder(LocalImage localImage, Folder parentFolder, User modifier) {
    localImage.setParentFolder(parentFolder);
    localImage.setLastModifier(modifier);
    localImage.setLastModified(new Date());
    getEntityManager().persist(localImage);
    return localImage;
  }
}
