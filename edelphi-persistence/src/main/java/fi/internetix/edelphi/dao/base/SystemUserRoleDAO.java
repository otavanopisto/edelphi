package fi.internetix.edelphi.dao.base;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.edelphi.domainmodel.base.SystemUserRole;
import fi.internetix.edelphi.domainmodel.base.SystemUserRoleType;
import fi.internetix.edelphi.domainmodel.base.SystemUserRole_;

public class SystemUserRoleDAO extends GenericDAO<SystemUserRole> {

  public SystemUserRole findByType(SystemUserRoleType type) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<SystemUserRole> criteria = criteriaBuilder.createQuery(SystemUserRole.class);
    Root<SystemUserRole> root = criteria.from(SystemUserRole.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(SystemUserRole_.type), type));
    
    return getSingleResult(entityManager.createQuery(criteria));
  }
  
}
