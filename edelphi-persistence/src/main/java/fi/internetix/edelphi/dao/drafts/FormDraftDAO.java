package fi.internetix.edelphi.dao.drafts;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.edelphi.domainmodel.drafts.FormDraft;
import fi.internetix.edelphi.domainmodel.drafts.FormDraft_;
import fi.internetix.edelphi.domainmodel.users.User;

public class FormDraftDAO extends GenericDAO<FormDraft> {

  public FormDraft create(String url, String draftData, User creator) {
    FormDraft formDraft = new FormDraft();

    Date now = new Date(System.currentTimeMillis());
    
    formDraft.setCreated(now);
    formDraft.setCreator(creator);
    formDraft.setData(draftData);
    formDraft.setModified(now);
    formDraft.setUrl(url);
    
    getEntityManager().persist(formDraft);
    
    return formDraft;
  }
  
  public FormDraft update(FormDraft formDraft, String draftData) {
    formDraft.setData(draftData);
    formDraft.setModified(new Date(System.currentTimeMillis()));
    
    getEntityManager().persist(formDraft);
    return formDraft;
  }

  public FormDraft findByUrlAndUser(String url, User creator) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<FormDraft> criteria = criteriaBuilder.createQuery(FormDraft.class);
    Root<FormDraft> root = criteria.from(FormDraft.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.and(
        criteriaBuilder.equal(root.get(FormDraft_.url), url),
        criteriaBuilder.equal(root.get(FormDraft_.creator), creator)
    ));
    
    return getSingleResult(entityManager.createQuery(criteria));
  }

  public List<FormDraft> listByUrl(String url) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<FormDraft> criteria = criteriaBuilder.createQuery(FormDraft.class);
    Root<FormDraft> root = criteria.from(FormDraft.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(FormDraft_.url), url)
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }
  
}
