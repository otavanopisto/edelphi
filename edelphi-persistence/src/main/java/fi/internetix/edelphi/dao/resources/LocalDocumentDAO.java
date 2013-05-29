package fi.internetix.edelphi.dao.resources;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.edelphi.domainmodel.resources.Folder;
import fi.internetix.edelphi.domainmodel.resources.LocalDocument;
import fi.internetix.edelphi.domainmodel.resources.LocalDocumentPage;
import fi.internetix.edelphi.domainmodel.resources.LocalDocumentPage_;
import fi.internetix.edelphi.domainmodel.users.User;

public class LocalDocumentDAO extends GenericDAO<LocalDocument> {

  public LocalDocument create(String name, String urlName, Folder parentFolder, User user, Integer indexNumber) {
    LocalDocument localDocument = new LocalDocument();
    localDocument.setCreator(user);
    localDocument.setCreated(new Date());
    localDocument.setLastModifier(user);
    localDocument.setLastModified(new Date());
    localDocument.setName(name);
    localDocument.setUrlName(urlName);
    localDocument.setParentFolder(parentFolder);
    localDocument.setIndexNumber(indexNumber);
    getEntityManager().persist(localDocument);
    return localDocument;
  }
  
  public Long countByDocument(LocalDocument document) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<LocalDocumentPage> root = criteria.from(LocalDocumentPage.class);
    criteria.select(criteriaBuilder.count(root));
    criteria.where(
      criteriaBuilder.equal(root.get(LocalDocumentPage_.document), document)
    );
    
    return entityManager.createQuery(criteria).getSingleResult();
  }
  
  public LocalDocument updateName(LocalDocument localDocument, String name, String urlName, User modifier) {
    localDocument.setName(name);
    localDocument.setUrlName(urlName);
    localDocument.setLastModifier(modifier);
    localDocument.setLastModified(new Date());
    getEntityManager().persist(localDocument);
    return localDocument;
  }

  public LocalDocument updateParentFolder(LocalDocument localDocument, Folder parentFolder, User modifier) {
    localDocument.setParentFolder(parentFolder);
    localDocument.setLastModifier(modifier);
    localDocument.setLastModified(new Date());
    getEntityManager().persist(localDocument);
    return localDocument;
  }
}
