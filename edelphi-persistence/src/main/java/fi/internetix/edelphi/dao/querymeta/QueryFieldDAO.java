package fi.internetix.edelphi.dao.querymeta;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;
import fi.internetix.edelphi.domainmodel.querymeta.QueryField;
import fi.internetix.edelphi.domainmodel.querymeta.QueryField_;

public class QueryFieldDAO extends GenericDAO<QueryField> {

  public QueryField findByQueryPageAndName(QueryPage queryPage, String name) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryField> criteria = criteriaBuilder.createQuery(QueryField.class);
    Root<QueryField> root = criteria.from(QueryField.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(QueryField_.queryPage), queryPage),
        criteriaBuilder.equal(root.get(QueryField_.name), name),
        criteriaBuilder.equal(root.get(QueryField_.archived), Boolean.FALSE)
      )
    );
    

    return getSingleResult(entityManager.createQuery(criteria));
  }
  
  public List<QueryField> listByQueryPage(QueryPage queryPage) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryField> criteria = criteriaBuilder.createQuery(QueryField.class);
    Root<QueryField> root = criteria.from(QueryField.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.and(
            criteriaBuilder.equal(root.get(QueryField_.queryPage), queryPage),
            criteriaBuilder.equal(root.get(QueryField_.archived), Boolean.FALSE)
        )
    );

    return entityManager.createQuery(criteria).getResultList();
  }
  
  public Long countByQueryPage(QueryPage queryPage) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<QueryField> root = criteria.from(QueryField.class);
    criteria.select(criteriaBuilder.count(root));
    criteria.where(
        criteriaBuilder.and(
            criteriaBuilder.equal(root.get(QueryField_.queryPage), queryPage),
            criteriaBuilder.equal(root.get(QueryField_.archived), Boolean.FALSE)
        )
    );

    return entityManager.createQuery(criteria).getSingleResult();
  }
  
  public QueryField updateMandatory(QueryField queryField, Boolean mandatory) {
    queryField.setMandatory(mandatory);
    getEntityManager().persist(queryField);
    return queryField;
  }
  
  public QueryField updateCaption(QueryField queryField, String caption) {
    queryField.setCaption(caption);
    getEntityManager().persist(queryField);
    return queryField;
  }
  
}