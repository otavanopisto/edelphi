package fi.internetix.edelphi.dao.querylayout;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPageTemplate;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPageTemplateSimpleSetting;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPageSettingKey;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPageTemplateSimpleSetting_;

public class QueryPageTemplateSimpleSettingDAO extends GenericDAO<QueryPageTemplateSimpleSetting> {
  
  public QueryPageTemplateSimpleSetting create(QueryPageSettingKey key, QueryPageTemplate queryPageTemplate, String value) {
    EntityManager entityManager = getEntityManager(); 
    
    QueryPageTemplateSimpleSetting queryPageSimpleSetting = new QueryPageTemplateSimpleSetting();
    queryPageSimpleSetting.setKey(key);
    queryPageSimpleSetting.setValue(value);
    queryPageSimpleSetting.setQueryPageTemplate(queryPageTemplate);
    
    entityManager.persist(queryPageSimpleSetting);
    
    return queryPageSimpleSetting;
  }

  public QueryPageTemplateSimpleSetting findByKeyAndQueryPage(QueryPageSettingKey key, QueryPageTemplate queryPageTemplate) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryPageTemplateSimpleSetting> criteria = criteriaBuilder.createQuery(QueryPageTemplateSimpleSetting.class);
    Root<QueryPageTemplateSimpleSetting> root = criteria.from(QueryPageTemplateSimpleSetting.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(QueryPageTemplateSimpleSetting_.key), key),
        criteriaBuilder.equal(root.get(QueryPageTemplateSimpleSetting_.queryPageTemplate), queryPageTemplate)
      )
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }

  public List<QueryPageTemplateSimpleSetting> listByQueryPageTemplate(QueryPageTemplate queryPageTemplate) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryPageTemplateSimpleSetting> criteria = criteriaBuilder.createQuery(QueryPageTemplateSimpleSetting.class);
    Root<QueryPageTemplateSimpleSetting> root = criteria.from(QueryPageTemplateSimpleSetting.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(QueryPageTemplateSimpleSetting_.queryPageTemplate), queryPageTemplate)
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

  public QueryPageTemplateSimpleSetting updateValue(QueryPageTemplateSimpleSetting queryPageTemplateSimpleSetting, String value) {
    queryPageTemplateSimpleSetting.setValue(value);
    getEntityManager().persist(queryPageTemplateSimpleSetting);
    return queryPageTemplateSimpleSetting;
  }
}
