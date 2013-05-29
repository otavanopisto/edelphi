package fi.internetix.edelphi.dao.querylayout;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.edelphi.domainmodel.base.LocalizedEntry;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPageTemplate;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPageTemplateLocalizedSetting;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPageSettingKey;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPageTemplateLocalizedSetting_;

public class QueryPageTemplateLocalizedSettingDAO extends GenericDAO<QueryPageTemplateLocalizedSetting> {
  
  public QueryPageTemplateLocalizedSetting create(QueryPageSettingKey key, QueryPageTemplate queryPageTemplate, LocalizedEntry value) {
    EntityManager entityManager = getEntityManager(); 
    
    QueryPageTemplateLocalizedSetting queryPageLocalizedSetting = new QueryPageTemplateLocalizedSetting();
    queryPageLocalizedSetting.setKey(key);
    queryPageLocalizedSetting.setValue(value);
    queryPageLocalizedSetting.setQueryPageTemplate(queryPageTemplate);
    
    entityManager.persist(queryPageLocalizedSetting);
    
    return queryPageLocalizedSetting;
  }

  public QueryPageTemplateLocalizedSetting findByKeyAndQueryPage(QueryPageSettingKey key, QueryPageTemplate queryPageTemplate) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryPageTemplateLocalizedSetting> criteria = criteriaBuilder.createQuery(QueryPageTemplateLocalizedSetting.class);
    Root<QueryPageTemplateLocalizedSetting> root = criteria.from(QueryPageTemplateLocalizedSetting.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(QueryPageTemplateLocalizedSetting_.key), key),
        criteriaBuilder.equal(root.get(QueryPageTemplateLocalizedSetting_.queryPageTemplate), queryPageTemplate)
      )
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }

  public List<QueryPageTemplateLocalizedSetting> listByQueryPageTemplate(QueryPageTemplate queryPageTemplate) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryPageTemplateLocalizedSetting> criteria = criteriaBuilder.createQuery(QueryPageTemplateLocalizedSetting.class);
    Root<QueryPageTemplateLocalizedSetting> root = criteria.from(QueryPageTemplateLocalizedSetting.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(QueryPageTemplateLocalizedSetting_.queryPageTemplate), queryPageTemplate)
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

  public QueryPageTemplateLocalizedSetting updateValue(QueryPageTemplateLocalizedSetting queryPageTemplateLocalizedSetting, LocalizedEntry value) {
    queryPageTemplateLocalizedSetting.setValue(value);
    getEntityManager().persist(queryPageTemplateLocalizedSetting);
    return queryPageTemplateLocalizedSetting;
  }
}
