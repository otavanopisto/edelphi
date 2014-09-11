package fi.internetix.edelphi.dao.querylayout;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPageSetting;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPageSettingKey;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPageSetting_;

public class QueryPageSettingDAO extends GenericDAO<QueryPageSetting> {
  
  public QueryPageSetting create(QueryPageSettingKey key, QueryPage queryPage, String value) {
    EntityManager entityManager = getEntityManager(); 
    
    QueryPageSetting queryPageSetting = new QueryPageSetting();
    queryPageSetting.setKey(key);
    queryPageSetting.setValue(value);
    queryPageSetting.setQueryPage(queryPage);
    
    entityManager.persist(queryPageSetting);
    
    return queryPageSetting;
  }

  public QueryPageSetting findByKeyAndQueryPage(QueryPageSettingKey key, QueryPage queryPage) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryPageSetting> criteria = criteriaBuilder.createQuery(QueryPageSetting.class);
    Root<QueryPageSetting> root = criteria.from(QueryPageSetting.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(QueryPageSetting_.key), key),
        criteriaBuilder.equal(root.get(QueryPageSetting_.queryPage), queryPage)
      )
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }

  public List<QueryPageSetting> listByQueryPage(QueryPage queryPage) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryPageSetting> criteria = criteriaBuilder.createQuery(QueryPageSetting.class);
    Root<QueryPageSetting> root = criteria.from(QueryPageSetting.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(QueryPageSetting_.queryPage), queryPage)
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }
  
  public QueryPageSetting updateValue(QueryPageSetting queryPageSetting, String value) {
    queryPageSetting.setValue(value);
    getEntityManager().persist(queryPageSetting);
    return queryPageSetting;
  }
}
