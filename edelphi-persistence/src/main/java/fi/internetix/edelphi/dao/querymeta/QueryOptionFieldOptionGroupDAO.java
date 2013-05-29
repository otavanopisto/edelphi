package fi.internetix.edelphi.dao.querymeta;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.edelphi.domainmodel.querymeta.QueryOptionField;
import fi.internetix.edelphi.domainmodel.querymeta.QueryOptionFieldOptionGroup;
import fi.internetix.edelphi.domainmodel.querymeta.QueryOptionFieldOptionGroup_;

public class QueryOptionFieldOptionGroupDAO extends GenericDAO<QueryOptionFieldOptionGroup> {

  public QueryOptionFieldOptionGroup create(QueryOptionField optionField, String name) {
    QueryOptionFieldOptionGroup queryOptionFieldOptionGroup = new QueryOptionFieldOptionGroup();
    queryOptionFieldOptionGroup.setOptionField(optionField);
    queryOptionFieldOptionGroup.setName(name);
    queryOptionFieldOptionGroup.setArchived(Boolean.FALSE);
    getEntityManager().persist(queryOptionFieldOptionGroup);
    return queryOptionFieldOptionGroup;
  }
  
  public QueryOptionFieldOptionGroup findByQueryFieldAndName(QueryOptionField optionField, String name) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryOptionFieldOptionGroup> criteria = criteriaBuilder.createQuery(QueryOptionFieldOptionGroup.class);
    Root<QueryOptionFieldOptionGroup> root = criteria.from(QueryOptionFieldOptionGroup.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(QueryOptionFieldOptionGroup_.optionField), optionField),
        criteriaBuilder.equal(root.get(QueryOptionFieldOptionGroup_.name), name),
        criteriaBuilder.equal(root.get(QueryOptionFieldOptionGroup_.archived), Boolean.FALSE)
      )
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }
  
  public List<QueryOptionFieldOptionGroup> listByQueryField(QueryOptionField queryField) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryOptionFieldOptionGroup> criteria = criteriaBuilder.createQuery(QueryOptionFieldOptionGroup.class);
    Root<QueryOptionFieldOptionGroup> root = criteria.from(QueryOptionFieldOptionGroup.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.and(
            criteriaBuilder.equal(root.get(QueryOptionFieldOptionGroup_.optionField), queryField),
            criteriaBuilder.equal(root.get(QueryOptionFieldOptionGroup_.archived), Boolean.FALSE)
        )
    );
    
    return entityManager.createQuery(criteria).getResultList();

  }
  
  public QueryOptionFieldOptionGroup updateName(QueryOptionFieldOptionGroup queryOptionFieldOptionGroup, String name) {
    queryOptionFieldOptionGroup.setName(name);
    getEntityManager().persist(queryOptionFieldOptionGroup);
    return queryOptionFieldOptionGroup;
  }
}