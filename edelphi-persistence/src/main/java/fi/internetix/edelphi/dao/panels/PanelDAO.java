package fi.internetix.edelphi.dao.panels;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.edelphi.domainmodel.base.Delfoi;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.panels.PanelAccessLevel;
import fi.internetix.edelphi.domainmodel.panels.PanelStamp;
import fi.internetix.edelphi.domainmodel.panels.PanelState;
import fi.internetix.edelphi.domainmodel.panels.PanelUser;
import fi.internetix.edelphi.domainmodel.panels.PanelUserRole;
import fi.internetix.edelphi.domainmodel.panels.PanelUser_;
import fi.internetix.edelphi.domainmodel.panels.Panel_;
import fi.internetix.edelphi.domainmodel.resources.Folder;
import fi.internetix.edelphi.domainmodel.users.User;

public class PanelDAO extends GenericDAO<Panel> {

  public Panel create(Delfoi delfoi, String name, String description, Folder rootFolder, 
      PanelState state, PanelAccessLevel accessLevel, PanelUserRole defaultPanelUserRole, User creator) {
    Date now = new Date();

    Panel panel = new Panel();
    panel.setArchived(Boolean.FALSE);
    panel.setCreated(now);
    panel.setLastModified(now);
    panel.setDelfoi(delfoi);
    panel.setDescription(description);
    panel.setName(name);
    panel.setRootFolder(rootFolder);
    panel.setState(state);
    panel.setAccessLevel(accessLevel);
    panel.setDefaultPanelUserRole(defaultPanelUserRole);
    panel.setLastModifier(creator);
    panel.setCreator(creator);

    getEntityManager().persist(panel);

    return panel;
  }
  
  public Panel findByRootFolder(Folder rootFolder) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Panel> criteria = criteriaBuilder.createQuery(Panel.class);
    Root<Panel> root = criteria.from(Panel.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(Panel_.rootFolder), rootFolder));
    
    return getSingleResult(entityManager.createQuery(criteria));
  }
  
  public List<Panel> listByDelfoi(Delfoi delfoi) {
    EntityManager entityManager = getEntityManager();
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Panel> criteria = criteriaBuilder.createQuery(Panel.class);
    Root<Panel> root = criteria.from(Panel.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(Panel_.delfoi), delfoi),
        criteriaBuilder.equal(root.get(Panel_.archived), Boolean.FALSE)
      )
    );

    return entityManager.createQuery(criteria).getResultList(); 
  }

  public List<Panel> listByDelfoiAndAccessLevelAndState(Delfoi delfoi, PanelAccessLevel accessLevel, PanelState state) {
    EntityManager entityManager = getEntityManager();
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Panel> criteria = criteriaBuilder.createQuery(Panel.class);
    Root<Panel> root = criteria.from(Panel.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(Panel_.delfoi), delfoi),
        criteriaBuilder.equal(root.get(Panel_.accessLevel), accessLevel),
        criteriaBuilder.equal(root.get(Panel_.state), state),
        criteriaBuilder.equal(root.get(Panel_.archived), Boolean.FALSE)
      )
    );

    return entityManager.createQuery(criteria).getResultList(); 
  }
  
  public List<Panel> listByDelfoiAndUser(Delfoi delfoi, User user) {
    EntityManager entityManager = getEntityManager();
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    
    CriteriaQuery<Panel> criteria = criteriaBuilder.createQuery(Panel.class);

    Root<PanelUser> puRoot = criteria.from(PanelUser.class);
    Join<PanelUser, Panel> pRoot = puRoot.join(PanelUser_.panel);
    criteria.select(pRoot);
    criteria.where(
      criteriaBuilder.equal(puRoot.get(PanelUser_.stamp), pRoot.get(Panel_.currentStamp)),
      criteriaBuilder.equal(puRoot.get(PanelUser_.archived), Boolean.FALSE),
      criteriaBuilder.equal(puRoot.get(PanelUser_.user), user),
      criteriaBuilder.equal(pRoot.get(Panel_.delfoi), delfoi),
      criteriaBuilder.equal(pRoot.get(Panel_.archived), Boolean.FALSE)
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }
  
  public Panel update(Panel panel, String name, String description, PanelAccessLevel accessLevel, PanelState state, User modifier) {
    EntityManager entityManager = getEntityManager();

    panel.setName(name);
    panel.setDescription(description);
    panel.setAccessLevel(accessLevel);
    panel.setState(state);
    panel.setLastModified(new Date());
    panel.setLastModifier(modifier);
    
    entityManager.persist(panel);
    
    return panel;
  }
  
  public Panel updateInvitationTemplate(Panel panel, String invitationTemplate, User modifier) {
    EntityManager entityManager = getEntityManager();

    panel.setInvitationTemplate(invitationTemplate);
    panel.setLastModified(new Date());
    panel.setLastModifier(modifier);
    
    entityManager.persist(panel);

    return panel;
  }
  
  public Panel updateCurrentStamp(Panel panel, PanelStamp currentStamp, User modifier) {
    EntityManager entityManager = getEntityManager();

    panel.setCurrentStamp(currentStamp);
    panel.setLastModified(new Date());
    panel.setLastModifier(modifier);
    
    entityManager.persist(panel);

    return panel;
  }

}
