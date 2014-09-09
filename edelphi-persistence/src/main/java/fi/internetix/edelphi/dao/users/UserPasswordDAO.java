package fi.internetix.edelphi.dao.users;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.domainmodel.users.UserPassword;
import fi.internetix.edelphi.domainmodel.users.UserPassword_;

public class UserPasswordDAO extends GenericDAO<UserPassword> {

  public UserPassword create(User user, String passwordHash) {
    UserPassword userPassword = new UserPassword();
   
    userPassword.setUser(user);
    userPassword.setPasswordHash(passwordHash);
    
    getEntityManager().persist(userPassword);
    return userPassword;
  }
  
  public UserPassword updatePasswordHash(UserPassword userPassword, String passwordHash) {
    userPassword.setPasswordHash(passwordHash);

    getEntityManager().persist(userPassword);
    return userPassword;
  }
  
  public UserPassword findByUser(User user) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<UserPassword> criteria = criteriaBuilder.createQuery(UserPassword.class);
    Root<UserPassword> root = criteria.from(UserPassword.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(UserPassword_.user), user));
    
    return getSingleResult(entityManager.createQuery(criteria));
  }
  
  
}
