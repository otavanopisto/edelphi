package fi.internetix.edelphi.dao.panels;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.panels.PanelAuth;
import fi.internetix.edelphi.domainmodel.panels.PanelAuth_;

public class PanelAuthDAO extends GenericDAO<PanelAuth> {

  public List<PanelAuth> listByPanel(Panel panel) {
    EntityManager entityManager = getEntityManager();
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PanelAuth> criteria = criteriaBuilder.createQuery(PanelAuth.class);
    Root<PanelAuth> root = criteria.from(PanelAuth.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(PanelAuth_.panel), panel)
    );

    return entityManager.createQuery(criteria).getResultList(); 
  }
  
}
