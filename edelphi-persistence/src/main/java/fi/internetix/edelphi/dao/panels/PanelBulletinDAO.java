package fi.internetix.edelphi.dao.panels;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.panels.PanelBulletin;
import fi.internetix.edelphi.domainmodel.panels.PanelBulletin_;
import fi.internetix.edelphi.domainmodel.users.User;

public class PanelBulletinDAO extends GenericDAO<PanelBulletin> {
  
  public PanelBulletin create(Panel panel, String title, String message, User creator) {
    
    Date now = new Date();
    
    PanelBulletin panelBulletin = new PanelBulletin();
    panelBulletin.setArchived(Boolean.FALSE);
    panelBulletin.setCreated(now);
    panelBulletin.setCreator(creator);
    panelBulletin.setLastModified(now);
    panelBulletin.setLastModifier(creator);
    panelBulletin.setTitle(title);
    panelBulletin.setMessage(message);
    panelBulletin.setPanel(panel);
    
    getEntityManager().persist(panelBulletin);
    
    return panelBulletin;
  }
  
  public List<PanelBulletin> listByPanelAndArchived(Panel panel, Boolean archived) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PanelBulletin> criteria = criteriaBuilder.createQuery(PanelBulletin.class);
    Root<PanelBulletin> root = criteria.from(PanelBulletin.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(PanelBulletin_.archived), archived),
        criteriaBuilder.equal(root.get(PanelBulletin_.panel), panel)
      )
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

  public PanelBulletin updateTitle(PanelBulletin panelBulletin, String title, User modifier) {
    EntityManager entityManager = getEntityManager();
    
    panelBulletin.setTitle(title);
    panelBulletin.setLastModifier(modifier);
    panelBulletin.setLastModified(new Date());
    
    entityManager.persist(panelBulletin);
    
    return panelBulletin;
  }

  public PanelBulletin updateMessage(PanelBulletin panelBulletin, String message, User modifier) {
    EntityManager entityManager = getEntityManager();
    
    panelBulletin.setMessage(message);
    panelBulletin.setLastModifier(modifier);
    panelBulletin.setLastModified(new Date());
    
    entityManager.persist(panelBulletin);
    
    return panelBulletin;
  }
}
