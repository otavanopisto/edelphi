package fi.internetix.edelphi.dao.users;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.edelphi.domainmodel.base.AuthSource;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.domainmodel.users.UserIdentification;
import fi.internetix.edelphi.domainmodel.users.UserIdentification_;

public class UserIdentificationDAO extends GenericDAO<UserIdentification> {

  public UserIdentification create(User user, String externalId, AuthSource authSource) {
    EntityManager entityManager = getEntityManager(); 
    
    UserIdentification userIdentification = new UserIdentification();
    userIdentification.setAuthSource(authSource);
    userIdentification.setExternalId(externalId);
    userIdentification.setUser(user);
    
    entityManager.persist(userIdentification);

    return userIdentification;
  }
  
  public UserIdentification findByExternalId(String externalId, AuthSource authSource) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<UserIdentification> criteria = criteriaBuilder.createQuery(UserIdentification.class);
    Root<UserIdentification> root = criteria.from(UserIdentification.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.and(
            criteriaBuilder.equal(root.get(UserIdentification_.externalId), externalId),
            criteriaBuilder.equal(root.get(UserIdentification_.authSource), authSource)
        )
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }

  public List<UserIdentification> listByUser(User user) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<UserIdentification> criteria = criteriaBuilder.createQuery(UserIdentification.class);
    Root<UserIdentification> root = criteria.from(UserIdentification.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(UserIdentification_.user), user)
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

}
