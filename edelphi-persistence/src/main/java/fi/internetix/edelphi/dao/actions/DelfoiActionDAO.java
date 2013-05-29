package fi.internetix.edelphi.dao.actions;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiAction;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.actions.DelfoiAction_;

public class DelfoiActionDAO extends GenericDAO<DelfoiAction> {

  public DelfoiAction findByActionName(String actionName) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<DelfoiAction> criteria = criteriaBuilder.createQuery(DelfoiAction.class);
    Root<DelfoiAction> root = criteria.from(DelfoiAction.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(DelfoiAction_.actionName), actionName));
    
    return getSingleResult(entityManager.createQuery(criteria));
  }
  
  public List<DelfoiAction> listByScope(DelfoiActionScope scope) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<DelfoiAction> criteria = criteriaBuilder.createQuery(DelfoiAction.class);
    Root<DelfoiAction> root = criteria.from(DelfoiAction.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(DelfoiAction_.scope), scope));
    
    return entityManager.createQuery(criteria).getResultList();
  }

  public DelfoiAction create(String actionName, DelfoiActionScope scope) {
    EntityManager entityManager = getEntityManager(); 

    DelfoiAction delfoiAction = new DelfoiAction();
    delfoiAction.setActionName(actionName);
    delfoiAction.setScope(scope);
    
    entityManager.persist(delfoiAction);
    
    return delfoiAction;
  }

}
