package fi.internetix.edelphi.dao.resources;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.edelphi.domainmodel.resources.Document;
import fi.internetix.edelphi.domainmodel.resources.Document_;
import fi.internetix.edelphi.domainmodel.resources.Folder;

public class DocumentDAO extends GenericDAO<Document> {

  public List<Document> listByFolderAndArchived(Folder folder, Boolean archived) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Document> criteria = criteriaBuilder.createQuery(Document.class);
    Root<Document> root = criteria.from(Document.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(Document_.archived), archived),
        criteriaBuilder.equal(root.get(Document_.parentFolder), folder)
      )
    );

    return entityManager.createQuery(criteria).getResultList();
  }

  public List<Document> listByFolder(Folder folder, Boolean archived, int firstResult, int maxResults) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Document> criteria = criteriaBuilder.createQuery(Document.class);
    Root<Document> root = criteria.from(Document.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(Document_.archived), archived),
        criteriaBuilder.equal(root.get(Document_.parentFolder), folder)
      )
    );

    TypedQuery<Document> query = entityManager.createQuery(criteria);
    query.setFirstResult(firstResult);
    query.setMaxResults(maxResults);
    
    return query.getResultList();
  }

  public Long countByFolderAndArchived(Folder folder, Boolean archived) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<Document> root = criteria.from(Document.class);
    criteria.select(criteriaBuilder.count(root));
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(Document_.archived), archived),
        criteriaBuilder.equal(root.get(Document_.parentFolder), folder)
      )
    );
    
    return entityManager.createQuery(criteria).getSingleResult();
  }  
}
