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
import fi.internetix.edelphi.domainmodel.panels.PanelUserGroup;
import fi.internetix.edelphi.domainmodel.panels.PanelUserGroup_;
import fi.internetix.edelphi.domainmodel.users.User;

public class PanelUserGroupDAO extends GenericDAO<PanelUserGroup> {

  public PanelUserGroup create(Panel panel, String name, List<User> users, PanelStamp stamp, User creator) {
    Date now = new Date();

    PanelUserGroup panelUserGroup = new PanelUserGroup();
    panelUserGroup.setPanel(panel);
    panelUserGroup.setName(name);
    panelUserGroup.setUsers(users);
    panelUserGroup.setStamp(stamp);
    panelUserGroup.setCreated(now);
    panelUserGroup.setCreator(creator);
    panelUserGroup.setLastModified(now);
    panelUserGroup.setLastModifier(creator);
    panelUserGroup.setArchived(Boolean.FALSE);

    getEntityManager().persist(panelUserGroup);
    return panelUserGroup;
  }

  public PanelUserGroup create(Panel panel, String name, List<User> users, PanelStamp stamp, User creator, Date created, User modifier, Date modified) {
    PanelUserGroup panelUserGroup = new PanelUserGroup();
    panelUserGroup.setPanel(panel);
    panelUserGroup.setName(name);
    panelUserGroup.setUsers(users);
    panelUserGroup.setStamp(stamp);
    panelUserGroup.setCreated(created);
    panelUserGroup.setCreator(creator);
    panelUserGroup.setLastModified(modified);
    panelUserGroup.setLastModifier(modifier);
    panelUserGroup.setArchived(Boolean.FALSE);

    getEntityManager().persist(panelUserGroup);
    return panelUserGroup;
  }

  public List<PanelUserGroup> listByPanelAndStamp(Panel panel, PanelStamp stamp) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PanelUserGroup> criteria = criteriaBuilder.createQuery(PanelUserGroup.class);
    Root<PanelUserGroup> root = criteria.from(PanelUserGroup.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(PanelUserGroup_.panel), panel), 
        criteriaBuilder.equal(root.get(PanelUserGroup_.stamp), stamp), 
        criteriaBuilder.equal(root.get(PanelUserGroup_.archived), Boolean.FALSE)
      )
    );

    return entityManager.createQuery(criteria).getResultList();
  }

  public PanelUserGroup update(PanelUserGroup panelUserGroup, String name, List<User> users, User updater) {
    panelUserGroup.setName(name);
    panelUserGroup.setUsers(users);
    panelUserGroup.setLastModified(new Date());
    panelUserGroup.setLastModifier(updater);
    getEntityManager().persist(panelUserGroup);
    return panelUserGroup;
  }
  
}
