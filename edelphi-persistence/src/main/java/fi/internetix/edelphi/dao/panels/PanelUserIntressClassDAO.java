package fi.internetix.edelphi.dao.panels;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.panels.PanelUserIntressClass;
import fi.internetix.edelphi.domainmodel.panels.PanelUserIntressClass_;

public class PanelUserIntressClassDAO extends GenericDAO<PanelUserIntressClass> {

  public PanelUserIntressClass create(Panel panel, String name) {
    PanelUserIntressClass panelUserIntressClass = new PanelUserIntressClass();
    
    panelUserIntressClass.setName(name);
    panelUserIntressClass.setPanel(panel);
    
    getEntityManager().persist(panelUserIntressClass);
    return panelUserIntressClass;
  }
  
  public List<PanelUserIntressClass> listByPanel(Panel panel) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PanelUserIntressClass> criteria = criteriaBuilder.createQuery(PanelUserIntressClass.class);
    Root<PanelUserIntressClass> root = criteria.from(PanelUserIntressClass.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(PanelUserIntressClass_.panel), panel)
    );

    return entityManager.createQuery(criteria).getResultList(); 
  }
  
  public PanelUserIntressClass updateName(PanelUserIntressClass interest, String name) {
    interest.setName(name);
    getEntityManager().persist(interest);
    return interest;
  }
}
