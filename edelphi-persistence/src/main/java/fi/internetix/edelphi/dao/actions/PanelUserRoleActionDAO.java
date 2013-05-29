package fi.internetix.edelphi.dao.actions;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiAction;
import fi.internetix.edelphi.domainmodel.actions.PanelUserRoleAction;
import fi.internetix.edelphi.domainmodel.actions.PanelUserRoleAction_;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.users.UserRole;

public class PanelUserRoleActionDAO extends GenericDAO<PanelUserRoleAction> {
  
  public PanelUserRoleAction create(Panel panel, DelfoiAction delfoiAction, UserRole userRole) {
    EntityManager entityManager = getEntityManager(); 

    PanelUserRoleAction panelUserRoleAction = new PanelUserRoleAction();
    
    panelUserRoleAction.setDelfoiAction(delfoiAction);
    panelUserRoleAction.setPanel(panel);
    panelUserRoleAction.setUserRole(userRole);
    
    entityManager.persist(panelUserRoleAction);
    
    return panelUserRoleAction;
  }

  public boolean hasPanelActionAccess(Panel panel, UserRole userRole, DelfoiAction delfoiAction) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PanelUserRoleAction> criteria = criteriaBuilder.createQuery(PanelUserRoleAction.class);
    Root<PanelUserRoleAction> root = criteria.from(PanelUserRoleAction.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.and(
            criteriaBuilder.equal(root.get(PanelUserRoleAction_.panel), panel),
            criteriaBuilder.equal(root.get(PanelUserRoleAction_.userRole), userRole),
            criteriaBuilder.equal(root.get(PanelUserRoleAction_.delfoiAction), delfoiAction)
        )
    );
    
    return entityManager.createQuery(criteria).getResultList().size() > 0;
  }

  public List<PanelUserRoleAction> listByPanel(Panel panel) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PanelUserRoleAction> criteria = criteriaBuilder.createQuery(PanelUserRoleAction.class);
    Root<PanelUserRoleAction> root = criteria.from(PanelUserRoleAction.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(PanelUserRoleAction_.panel), panel));
    
    return entityManager.createQuery(criteria).getResultList();
  }
  
  public List<PanelUserRoleAction> listByPanelAndUserRole(Panel panel, UserRole role) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PanelUserRoleAction> criteria = criteriaBuilder.createQuery(PanelUserRoleAction.class);
    Root<PanelUserRoleAction> root = criteria.from(PanelUserRoleAction.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.and(
            criteriaBuilder.equal(root.get(PanelUserRoleAction_.panel), panel),
            criteriaBuilder.equal(root.get(PanelUserRoleAction_.userRole), role)
        )
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

	public List<PanelUserRoleAction> listByPanelAndDelfoiAction(Panel panel, DelfoiAction delfoiAction) {
		EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PanelUserRoleAction> criteria = criteriaBuilder.createQuery(PanelUserRoleAction.class);
    Root<PanelUserRoleAction> root = criteria.from(PanelUserRoleAction.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.and(
            criteriaBuilder.equal(root.get(PanelUserRoleAction_.panel), panel),
            criteriaBuilder.equal(root.get(PanelUserRoleAction_.delfoiAction), delfoiAction)
        )
    );
    
    return entityManager.createQuery(criteria).getResultList();
	}
  
}
