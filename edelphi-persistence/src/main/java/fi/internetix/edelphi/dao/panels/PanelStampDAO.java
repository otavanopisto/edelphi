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
import fi.internetix.edelphi.domainmodel.panels.PanelStamp_;
import fi.internetix.edelphi.domainmodel.users.User;

public class PanelStampDAO extends GenericDAO<PanelStamp> {

  public PanelStamp create(Panel panel, String name, String description, Date stampTime, User creator) {
    Date now = new Date();

    PanelStamp panelStamp = new PanelStamp();
    panelStamp.setPanel(panel);
    panelStamp.setName(name);
    panelStamp.setDescription(description);
    panelStamp.setStampTime(stampTime);
    panelStamp.setCreated(now);
    panelStamp.setCreator(creator);
    panelStamp.setLastModified(now);
    panelStamp.setLastModifier(creator);
    panelStamp.setArchived(Boolean.FALSE);

    getEntityManager().persist(panelStamp);
    return panelStamp;
  }
  
  public List<PanelStamp> listByPanel(Panel panel) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PanelStamp> criteria = criteriaBuilder.createQuery(PanelStamp.class);
    Root<PanelStamp> root = criteria.from(PanelStamp.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(PanelStamp_.panel), panel),
        criteriaBuilder.equal(root.get(PanelStamp_.archived), Boolean.FALSE)
      )
    );

    return entityManager.createQuery(criteria).getResultList();
  }

  public PanelStamp update(PanelStamp panelStamp, String name, String description, Date stampTime, User updater) {
    panelStamp.setName(name);
    panelStamp.setDescription(description);
    panelStamp.setStampTime(stampTime);
    panelStamp.setLastModified(new Date());
    panelStamp.setLastModifier(updater);
    getEntityManager().persist(panelStamp);
    return panelStamp;
  }

}
