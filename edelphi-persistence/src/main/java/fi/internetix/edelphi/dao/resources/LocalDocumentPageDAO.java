package fi.internetix.edelphi.dao.resources;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.edelphi.domainmodel.resources.LocalDocument;
import fi.internetix.edelphi.domainmodel.resources.LocalDocumentPage;
import fi.internetix.edelphi.domainmodel.resources.LocalDocumentPage_;
import fi.internetix.edelphi.domainmodel.users.User;

public class LocalDocumentPageDAO extends GenericDAO<LocalDocumentPage> {

  public LocalDocumentPage create(LocalDocument localDocument, User creator, String title, Integer pageNumber, String content) {
    EntityManager entityManager = getEntityManager();
    
    localDocument.setLastModifier(creator);
    localDocument.setLastModified(new Date());
    entityManager.persist(localDocument);
    
    LocalDocumentPage localDocumentPage = new LocalDocumentPage();
    localDocumentPage.setDocument(localDocument);
    localDocumentPage.setTitle(title);
    localDocumentPage.setContent(content);
    localDocumentPage.setPageNumber(pageNumber);
    entityManager.persist(localDocumentPage);
    
    return localDocumentPage;
  }
  
  public LocalDocumentPage findByDocumentAndPageNumber(LocalDocument document, Integer pageNumber) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<LocalDocumentPage> criteria = criteriaBuilder.createQuery(LocalDocumentPage.class);
    Root<LocalDocumentPage> root = criteria.from(LocalDocumentPage.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(LocalDocumentPage_.document), document),
        criteriaBuilder.equal(root.get(LocalDocumentPage_.pageNumber), pageNumber)
      )
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }

  public List<LocalDocumentPage> listByDocument(LocalDocument document) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<LocalDocumentPage> criteria = criteriaBuilder.createQuery(LocalDocumentPage.class);
    Root<LocalDocumentPage> root = criteria.from(LocalDocumentPage.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(LocalDocumentPage_.document), document)
    );

    return entityManager.createQuery(criteria).getResultList();
  }
  
  public LocalDocumentPage updateTitle(LocalDocumentPage localDocumentPage, User modifier, String title) {
    EntityManager entityManager = getEntityManager();
    
    LocalDocument localDocument = localDocumentPage.getDocument();
    
    localDocument.setLastModifier(modifier);
    localDocument.setLastModified(new Date());
    entityManager.persist(localDocument);
    
    localDocumentPage.setTitle(title);
    entityManager.persist(localDocumentPage);

    return localDocumentPage;
  }

  public LocalDocumentPage updateContent(LocalDocumentPage localDocumentPage, User modifier, String content) {
    EntityManager entityManager = getEntityManager();
    
    LocalDocument localDocument = localDocumentPage.getDocument();
    
    localDocument.setLastModifier(modifier);
    localDocument.setLastModified(new Date());
    entityManager.persist(localDocument);
    
    localDocumentPage.setContent(content);
    entityManager.persist(localDocumentPage);

    return localDocumentPage;
  }
  
  public LocalDocumentPage updatePageNumber(LocalDocumentPage localDocumentPage, User modifier, Integer pageNumber) {
    EntityManager entityManager = getEntityManager();
    
    LocalDocument localDocument = localDocumentPage.getDocument();
    
    localDocument.setLastModifier(modifier);
    localDocument.setLastModified(new Date());
    entityManager.persist(localDocument);
    
    localDocumentPage.setPageNumber(pageNumber);
    entityManager.persist(localDocumentPage);

    return localDocumentPage;
  }
}
