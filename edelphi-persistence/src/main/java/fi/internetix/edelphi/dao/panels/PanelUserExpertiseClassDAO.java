package fi.internetix.edelphi.dao.panels;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.panels.PanelUserExpertiseClass;
import fi.internetix.edelphi.domainmodel.panels.PanelUserExpertiseClass_;

public class PanelUserExpertiseClassDAO extends GenericDAO<PanelUserExpertiseClass> {

  public PanelUserExpertiseClass create(Panel panel, String name) {
    PanelUserExpertiseClass panelUserExpertiseClass = new PanelUserExpertiseClass();
    
    panelUserExpertiseClass.setName(name);
    panelUserExpertiseClass.setPanel(panel);
    
    getEntityManager().persist(panelUserExpertiseClass);
    return panelUserExpertiseClass;
  }
  
  public List<PanelUserExpertiseClass> listByPanel(Panel panel) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PanelUserExpertiseClass> criteria = criteriaBuilder.createQuery(PanelUserExpertiseClass.class);
    Root<PanelUserExpertiseClass> root = criteria.from(PanelUserExpertiseClass.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(PanelUserExpertiseClass_.panel), panel)
    );

    return entityManager.createQuery(criteria).getResultList(); 
  }
  
  public PanelUserExpertiseClass updateName(PanelUserExpertiseClass expertise, String name) {
    expertise.setName(name);
    getEntityManager().persist(expertise);
    return expertise;
  }
  
}
