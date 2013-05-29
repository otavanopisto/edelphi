package fi.internetix.edelphi.dao.panels;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.panels.PanelStamp;
import fi.internetix.edelphi.domainmodel.panels.PanelUser;
import fi.internetix.edelphi.domainmodel.panels.PanelUserJoinType;
import fi.internetix.edelphi.domainmodel.panels.PanelUserRole;
import fi.internetix.edelphi.domainmodel.panels.PanelUser_;
import fi.internetix.edelphi.domainmodel.users.User;

public class PanelUserDAO extends GenericDAO<PanelUser> {

  public PanelUser create(Panel panel, User user, PanelUserRole role, PanelUserJoinType joinType, PanelStamp stamp, User creator) {
    Date now = new Date();
    
    PanelUser panelUser = new PanelUser();
    panelUser.setPanel(panel);
    panelUser.setUser(user);
    panelUser.setRole(role);
    panelUser.setJoinType(joinType);
    panelUser.setStamp(stamp);
    panelUser.setCreated(now);
    panelUser.setCreator(creator);
    panelUser.setLastModified(now);
    panelUser.setLastModifier(creator);
    panelUser.setArchived(Boolean.FALSE);
    
    getEntityManager().persist(panelUser);
    return panelUser;
  }

  public PanelUser create(Panel panel, User user, PanelUserRole role, PanelUserJoinType joinType, PanelStamp stamp, User creator, Date created, User modifier, Date modified) {
    PanelUser panelUser = new PanelUser();
    panelUser.setPanel(panel);
    panelUser.setUser(user);
    panelUser.setRole(role);
    panelUser.setJoinType(joinType);
    panelUser.setStamp(stamp);
    panelUser.setCreated(created);
    panelUser.setCreator(creator);
    panelUser.setLastModified(modified);
    panelUser.setLastModifier(modifier);
    panelUser.setArchived(Boolean.FALSE);
    
    getEntityManager().persist(panelUser);
    return panelUser;
  }
  
  public PanelUser findByPanelAndUserAndStamp(Panel panel, User user, PanelStamp stamp) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PanelUser> criteria = criteriaBuilder.createQuery(PanelUser.class);
    Root<PanelUser> root = criteria.from(PanelUser.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(PanelUser_.panel), panel), 
        criteriaBuilder.equal(root.get(PanelUser_.user), user),
        criteriaBuilder.equal(root.get(PanelUser_.stamp), stamp),
        criteriaBuilder.equal(root.get(PanelUser_.archived), Boolean.FALSE)
      )
    );
      
    return getSingleResult(entityManager.createQuery(criteria));
  }
  
  public List<PanelUser> listByPanelAndRoleAndStamp(Panel panel, PanelUserRole role, PanelStamp stamp) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PanelUser> criteria = criteriaBuilder.createQuery(PanelUser.class);
    Root<PanelUser> root = criteria.from(PanelUser.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(PanelUser_.panel), panel), 
        criteriaBuilder.equal(root.get(PanelUser_.role), role),
        criteriaBuilder.equal(root.get(PanelUser_.stamp), stamp),
        criteriaBuilder.equal(root.get(PanelUser_.archived), Boolean.FALSE)
      )
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

  public List<PanelUser> listByPanelAndUserAndStamp(Panel panel, User user, PanelStamp stamp) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PanelUser> criteria = criteriaBuilder.createQuery(PanelUser.class);
    Root<PanelUser> root = criteria.from(PanelUser.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(PanelUser_.panel), panel), 
        criteriaBuilder.equal(root.get(PanelUser_.user), user),
        criteriaBuilder.equal(root.get(PanelUser_.stamp), stamp),
        criteriaBuilder.equal(root.get(PanelUser_.archived), Boolean.FALSE)
      )
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }
  
  public PanelUser updateJoinType(PanelUser panelUser, PanelUserJoinType joinType, User modifier) {
    panelUser.setJoinType(joinType);
    panelUser.setLastModified(new Date());
    panelUser.setLastModifier(modifier);
    
    getEntityManager().persist(panelUser);
    return panelUser;
  }

  public PanelUser updateRole(PanelUser panelUser, PanelUserRole panelUserRole, User modifier) {
    panelUser.setRole(panelUserRole);
    panelUser.setLastModified(new Date());
    panelUser.setLastModifier(modifier);
    
    getEntityManager().persist(panelUser);
    return panelUser;
  }

  public List<PanelUser> listByPanelAndStamp(Panel panel, PanelStamp stamp) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PanelUser> criteria = criteriaBuilder.createQuery(PanelUser.class);
    Root<PanelUser> root = criteria.from(PanelUser.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(PanelUser_.panel), panel), 
        criteriaBuilder.equal(root.get(PanelUser_.stamp), stamp), 
        criteriaBuilder.equal(root.get(PanelUser_.archived), Boolean.FALSE)
      )
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

  public List<PanelUser> listByUser(User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PanelUser> criteria = criteriaBuilder.createQuery(PanelUser.class);
    Root<PanelUser> root = criteria.from(PanelUser.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(PanelUser_.user), user), 
        criteriaBuilder.equal(root.get(PanelUser_.archived), Boolean.FALSE)
      )
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }
    
}
