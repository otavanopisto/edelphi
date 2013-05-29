package fi.internetix.edelphi.dao.resources;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.edelphi.domainmodel.resources.Folder;
import fi.internetix.edelphi.domainmodel.resources.Folder_;
import fi.internetix.edelphi.domainmodel.users.User;

public class FolderDAO extends GenericDAO<Folder> {

  public Folder create(User creator, String name, String urlName, Folder parentFolder, Integer indexNumber) {
    Date now = new Date();

    Folder folder = new Folder();
    folder.setArchived(Boolean.FALSE);
    folder.setCreated(now);
    folder.setCreator(creator);
    folder.setLastModified(now);
    folder.setLastModifier(creator);
    folder.setName(name);
    folder.setUrlName(urlName);
    folder.setParentFolder(parentFolder);
    folder.setIndexNumber(indexNumber);
    
    getEntityManager().persist(folder);
    
    return folder;
  }
  
  public Folder findByUrlNameAndParentFolderAndArchived(String urlName, Folder parentFolder, Boolean archived) {
    EntityManager entityManager = getEntityManager(); 
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Folder> criteria = criteriaBuilder.createQuery(Folder.class);
    Root<Folder> root = criteria.from(Folder.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.and(
            criteriaBuilder.equal(root.get(Folder_.urlName), urlName),
            criteriaBuilder.equal(root.get(Folder_.parentFolder), parentFolder),
            criteriaBuilder.equal(root.get(Folder_.archived), archived)
        )
    );
    return getSingleResult(entityManager.createQuery(criteria));
  }

  public Folder updateName(Folder folder, String name, String urlName, User lastModifier) {
    folder.setName(name);
    folder.setUrlName(urlName);
    folder.setLastModified(new Date());
    folder.setLastModifier(lastModifier);

    getEntityManager().persist(folder);
    
    return folder;
  }

  public Folder updateParentFolder(Folder folder, Folder parentFolder, User lastModifier) {
    folder.setParentFolder(parentFolder);
    folder.setLastModified(new Date());
    folder.setLastModifier(lastModifier);

    getEntityManager().persist(folder);
    
    return folder;
  }
  
}
