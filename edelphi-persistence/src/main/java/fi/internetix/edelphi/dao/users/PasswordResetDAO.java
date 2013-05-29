package fi.internetix.edelphi.dao.users;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.edelphi.domainmodel.users.PasswordReset;
import fi.internetix.edelphi.domainmodel.users.PasswordReset_;

public class PasswordResetDAO extends GenericDAO<PasswordReset> {
  
  public PasswordReset create(String email, String hash) {
    PasswordReset passwordReset = new PasswordReset();
    passwordReset.setEmail(email);
    passwordReset.setHash(hash);
    getEntityManager().persist(passwordReset);
    return passwordReset;
  }

  public PasswordReset findByEmail(String email) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PasswordReset> criteria = criteriaBuilder.createQuery(PasswordReset.class);
    Root<PasswordReset> root = criteria.from(PasswordReset.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(PasswordReset_.email), email)
    );

    return getSingleResult(entityManager.createQuery(criteria)); 
  }

  public PasswordReset findByEmailAndHash(String email, String hash) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PasswordReset> criteria = criteriaBuilder.createQuery(PasswordReset.class);
    Root<PasswordReset> root = criteria.from(PasswordReset.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(PasswordReset_.email), email),
        criteriaBuilder.equal(root.get(PasswordReset_.hash), hash)
      )
    );

    return getSingleResult(entityManager.createQuery(criteria)); 
  }
  
  public PasswordReset updateHash(PasswordReset passwordReset, String hash) {
    passwordReset.setHash(hash);
    getEntityManager().persist(passwordReset);
    return passwordReset;
  }

}