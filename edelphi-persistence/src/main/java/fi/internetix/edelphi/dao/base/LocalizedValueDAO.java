package fi.internetix.edelphi.dao.base;

import java.util.List;
import java.util.Locale;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.edelphi.domainmodel.base.LocalizedEntry;
import fi.internetix.edelphi.domainmodel.base.LocalizedValue;
import fi.internetix.edelphi.domainmodel.base.LocalizedValue_;

public class LocalizedValueDAO extends GenericDAO<LocalizedValue> {

  public LocalizedValue create(LocalizedEntry entry, Locale locale, String text) {
    LocalizedValue localizedValue = new LocalizedValue();
    localizedValue.setLocale(locale);
    localizedValue.setEntry(entry);
    localizedValue.setText(text);
    getEntityManager().persist(localizedValue);
    return localizedValue;
  }
  
  public LocalizedValue findByEntryAndLocale(LocalizedEntry entry, Locale locale) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<LocalizedValue> criteria = criteriaBuilder.createQuery(LocalizedValue.class);
    Root<LocalizedValue> root = criteria.from(LocalizedValue.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(LocalizedValue_.entry), entry),
        criteriaBuilder.equal(root.get(LocalizedValue_.locale), locale)
      )
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }
  
  public List<LocalizedValue> listByEntry(LocalizedEntry entry) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<LocalizedValue> criteria = criteriaBuilder.createQuery(LocalizedValue.class);
    Root<LocalizedValue> root = criteria.from(LocalizedValue.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(LocalizedValue_.entry), entry)
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }
  
  public LocalizedValue updateText(LocalizedValue localizedValue, String text) {
    localizedValue.setText(text);
    getEntityManager().persist(localizedValue);
    return localizedValue;
  }
  
}

