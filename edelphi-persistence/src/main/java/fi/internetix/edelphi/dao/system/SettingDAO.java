package fi.internetix.edelphi.dao.system;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.edelphi.domainmodel.system.Setting;
import fi.internetix.edelphi.domainmodel.system.SettingKey;
import fi.internetix.edelphi.domainmodel.system.Setting_;

public class SettingDAO extends GenericDAO<Setting> {

  public Setting findByKey(SettingKey settingKey) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Setting> criteria = criteriaBuilder.createQuery(Setting.class);
    Root<Setting> root = criteria.from(Setting.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(Setting_.key), settingKey));
    
    return getSingleResult(entityManager.createQuery(criteria));
  }

}
