package fi.internetix.edelphi.dao.querymeta;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.edelphi.domainmodel.querymeta.QueryOptionField;
import fi.internetix.edelphi.domainmodel.querymeta.QueryOptionFieldOption;
import fi.internetix.edelphi.domainmodel.querymeta.QueryOptionFieldOption_;

public class QueryOptionFieldOptionDAO extends GenericDAO<QueryOptionFieldOption> {

  public QueryOptionFieldOption create(QueryOptionField optionField, String text, String value) {
    QueryOptionFieldOption queryOptionFieldOption = new QueryOptionFieldOption();
    queryOptionFieldOption.setOptionField(optionField);
    queryOptionFieldOption.setText(text);
    queryOptionFieldOption.setValue(value);
    queryOptionFieldOption.setArchived(Boolean.FALSE);
    getEntityManager().persist(queryOptionFieldOption);
    return queryOptionFieldOption;
  }
  
  public QueryOptionFieldOption findByQueryFieldAndValue(QueryOptionField optionField, String value) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryOptionFieldOption> criteria = criteriaBuilder.createQuery(QueryOptionFieldOption.class);
    Root<QueryOptionFieldOption> root = criteria.from(QueryOptionFieldOption.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(QueryOptionFieldOption_.optionField), optionField),
        criteriaBuilder.equal(root.get(QueryOptionFieldOption_.value), value),
        criteriaBuilder.equal(root.get(QueryOptionFieldOption_.archived), Boolean.FALSE)
      )
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }
  
  public List<QueryOptionFieldOption> listByQueryField(QueryOptionField queryField) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryOptionFieldOption> criteria = criteriaBuilder.createQuery(QueryOptionFieldOption.class);
    Root<QueryOptionFieldOption> root = criteria.from(QueryOptionFieldOption.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(QueryOptionFieldOption_.optionField), queryField),
        criteriaBuilder.equal(root.get(QueryOptionFieldOption_.archived), Boolean.FALSE)
      )
    );
    
    return entityManager.createQuery(criteria).getResultList();

  }

  public Long countByQueryField(QueryOptionField queryField) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<QueryOptionFieldOption> root = criteria.from(QueryOptionFieldOption.class);
    criteria.select(criteriaBuilder.count(root));
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(QueryOptionFieldOption_.optionField), queryField),
        criteriaBuilder.equal(root.get(QueryOptionFieldOption_.archived), Boolean.FALSE)
      )
    );
    
    return entityManager.createQuery(criteria).getSingleResult();
  }
  
  public QueryOptionFieldOption updateText(QueryOptionFieldOption queryOptionFieldOption, String text) {
    queryOptionFieldOption.setText(text);
    getEntityManager().persist(queryOptionFieldOption);
    return queryOptionFieldOption;
  }
}