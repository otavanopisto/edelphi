package fi.internetix.edelphi.dao.actions;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiAction;
import fi.internetix.edelphi.domainmodel.actions.DelfoiUserRoleAction;
import fi.internetix.edelphi.domainmodel.actions.DelfoiUserRoleAction_;
import fi.internetix.edelphi.domainmodel.base.Delfoi;
import fi.internetix.edelphi.domainmodel.users.UserRole;

public class DelfoiUserRoleActionDAO extends GenericDAO<DelfoiUserRoleAction> {

  public boolean hasDelfoiActionAccess(Delfoi delfoi, UserRole userRole, DelfoiAction delfoiAction) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<DelfoiUserRoleAction> criteria = criteriaBuilder.createQuery(DelfoiUserRoleAction.class);
    Root<DelfoiUserRoleAction> root = criteria.from(DelfoiUserRoleAction.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.and(
            criteriaBuilder.equal(root.get(DelfoiUserRoleAction_.delfoi), delfoi),
            criteriaBuilder.equal(root.get(DelfoiUserRoleAction_.userRole), userRole),
            criteriaBuilder.equal(root.get(DelfoiUserRoleAction_.delfoiAction), delfoiAction)
        )
    );
    
    return entityManager.createQuery(criteria).getResultList().size() > 0;
  }

  public List<DelfoiUserRoleAction> listByDelfoi(Delfoi delfoi) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<DelfoiUserRoleAction> criteria = criteriaBuilder.createQuery(DelfoiUserRoleAction.class);
    Root<DelfoiUserRoleAction> root = criteria.from(DelfoiUserRoleAction.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(DelfoiUserRoleAction_.delfoi), delfoi));
    
    return entityManager.createQuery(criteria).getResultList();
  }

  public List<DelfoiUserRoleAction> listByDelfoiAndUserRole(Delfoi delfoi, UserRole role) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<DelfoiUserRoleAction> criteria = criteriaBuilder.createQuery(DelfoiUserRoleAction.class);
    Root<DelfoiUserRoleAction> root = criteria.from(DelfoiUserRoleAction.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.and(
            criteriaBuilder.equal(root.get(DelfoiUserRoleAction_.delfoi), delfoi),
            criteriaBuilder.equal(root.get(DelfoiUserRoleAction_.userRole), role)
        )
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }
  
  public DelfoiUserRoleAction create(Delfoi delfoi, DelfoiAction delfoiAction, UserRole userRole) {
    EntityManager entityManager = getEntityManager(); 

    DelfoiUserRoleAction delfoiUserRoleAction = new DelfoiUserRoleAction();
    
    delfoiUserRoleAction.setDelfoi(delfoi);
    delfoiUserRoleAction.setDelfoiAction(delfoiAction);
    delfoiUserRoleAction.setUserRole(userRole);
    
    entityManager.persist(delfoiUserRoleAction);
    
    return delfoiUserRoleAction;
  }

}
