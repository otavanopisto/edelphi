package fi.internetix.edelphi.dao.users;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.domainmodel.users.UserPicture;
import fi.internetix.edelphi.domainmodel.users.UserPicture_;

public class UserPictureDAO extends GenericDAO<UserPicture> {

  public UserPicture create(User user, String contentType, byte[] data) {
    UserPicture userPicture = new UserPicture();
    userPicture.setUser(user);
    userPicture.setContentType(contentType);
    userPicture.setData(data);
    userPicture.setLastModified(new Date());
    getEntityManager().persist(userPicture);
    return userPicture;
  }
 
  public UserPicture updateData(UserPicture userPicture, String contentType, byte[] data) {
    userPicture.setData(data);
    userPicture.setContentType(contentType);
    userPicture.setLastModified(new Date());
    getEntityManager().persist(userPicture);
    return userPicture;
  }

  public UserPicture updateUser(UserPicture userPicture, User user) {
    userPicture.setUser(user);
    getEntityManager().persist(userPicture);
    return userPicture;
  }

  public UserPicture findByUser(User user) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<UserPicture> criteria = criteriaBuilder.createQuery(UserPicture.class);
    Root<UserPicture> root = criteria.from(UserPicture.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(UserPicture_.user), user));
    
    return getSingleResult(entityManager.createQuery(criteria));
  }

  public Boolean findUserHasPicture(User user) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<UserPicture> root = criteria.from(UserPicture.class);
    criteria.select(criteriaBuilder.count(root));
    criteria.where(criteriaBuilder.equal(root.get(UserPicture_.user), user));

    return entityManager.createQuery(criteria).getSingleResult() == 1;
  }
  
}
