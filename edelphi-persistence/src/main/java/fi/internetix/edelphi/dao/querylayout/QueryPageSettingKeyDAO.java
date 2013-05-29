package fi.internetix.edelphi.dao.querylayout;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPageSettingKey;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPageSettingKey_;

public class QueryPageSettingKeyDAO extends GenericDAO<QueryPageSettingKey> {

  public QueryPageSettingKey create(String name) {
    QueryPageSettingKey queryPageSettingKey = new QueryPageSettingKey();
    queryPageSettingKey.setName(name);
    
    getEntityManager().persist(queryPageSettingKey);

    return queryPageSettingKey;
  }
  
  public QueryPageSettingKey findByName(String name) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryPageSettingKey> criteria = criteriaBuilder.createQuery(QueryPageSettingKey.class);
    Root<QueryPageSettingKey> root = criteria.from(QueryPageSettingKey.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(QueryPageSettingKey_.name), name));

    return getSingleResult(entityManager.createQuery(criteria));
  }

}
