package fi.internetix.edelphi.dao.resources;

import java.util.Date;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.edelphi.domainmodel.resources.Folder;
import fi.internetix.edelphi.domainmodel.resources.LinkedImage;
import fi.internetix.edelphi.domainmodel.users.User;

public class LinkedImageDAO extends GenericDAO<LinkedImage> {

  public LinkedImage create(String name, String urlName, String url, Folder parentFolder, User user, Integer indexNumber) {
    LinkedImage linkedImage = new LinkedImage();
    linkedImage.setCreator(user);
    linkedImage.setCreated(new Date());
    linkedImage.setLastModifier(user);
    linkedImage.setLastModified(new Date());
    linkedImage.setName(name);
    linkedImage.setUrlName(urlName);
    linkedImage.setParentFolder(parentFolder);
    linkedImage.setUrl(url);
    linkedImage.setIndexNumber(indexNumber);
    getEntityManager().persist(linkedImage);
    return linkedImage;
  }
  
  public LinkedImage updateUrl(LinkedImage linkedImage, String url, User modifier) {
    linkedImage.setUrl(url);
    linkedImage.setLastModifier(modifier);
    linkedImage.setLastModified(new Date());
    getEntityManager().persist(linkedImage);
    return linkedImage;
  }
  
  public LinkedImage updateName(LinkedImage linkedImage, String name, String urlName, User modifier) {
    linkedImage.setName(name);
    linkedImage.setUrlName(urlName);
    linkedImage.setLastModifier(modifier);
    linkedImage.setLastModified(new Date());
    getEntityManager().persist(linkedImage);
    return linkedImage;
  }

  public LinkedImage updateParentFolder(LinkedImage linkedImage, Folder parentFolder, User modifier) {
    linkedImage.setParentFolder(parentFolder);
    linkedImage.setLastModifier(modifier);
    linkedImage.setLastModified(new Date());
    getEntityManager().persist(linkedImage);
    return linkedImage;
  }
}
