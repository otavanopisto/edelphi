package fi.internetix.edelphi.dao.resources;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.edelphi.domainmodel.resources.Folder;
import fi.internetix.edelphi.domainmodel.resources.Query;
import fi.internetix.edelphi.domainmodel.resources.QueryState;
import fi.internetix.edelphi.domainmodel.resources.Query_;
import fi.internetix.edelphi.domainmodel.users.User;

public class QueryDAO extends GenericDAO<Query> {

  public Query create(Folder parentFolder, String name, String urlName, Boolean allowEditReply, String description, QueryState state, Date closes, Integer indexNumber, User creator) {
    Date now = new Date();
    return create(parentFolder, name, urlName, allowEditReply, description, state, closes, indexNumber, creator, now, creator, now);
  }

  public Query create(Folder parentFolder, String name, String urlName, Boolean allowEditReply, String description, QueryState state, Date closes, Integer indexNumber, User creator, Date created, User modifier, Date modified) {
    Query query = new Query();
    query.setArchived(Boolean.FALSE);
    query.setCreated(created);
    query.setCreator(creator);
    query.setLastModified(modified);
    query.setLastModifier(modifier);
    query.setName(name);
    query.setUrlName(urlName);
    query.setParentFolder(parentFolder);
    query.setAllowEditReply(allowEditReply);
    query.setDescription(description);
    query.setState(state);
    query.setCloses(closes);
    query.setIndexNumber(indexNumber);
    
    getEntityManager().persist(query);
    
    return query;
  }
  
  public List<Query> listByFolderAndVisibleAndArchived(Folder folder, Boolean visible, Boolean archived) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Query> criteria = criteriaBuilder.createQuery(Query.class);
    Root<Query> root = criteria.from(Query.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(Query_.archived), archived),
        criteriaBuilder.equal(root.get(Query_.visible), visible),
        criteriaBuilder.equal(root.get(Query_.parentFolder), folder)
      )
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }
  
  public List<Query> listByFolderAndArchived(Folder folder, Boolean archived) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Query> criteria = criteriaBuilder.createQuery(Query.class);
    Root<Query> root = criteria.from(Query.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(Query_.archived), archived),
        criteriaBuilder.equal(root.get(Query_.parentFolder), folder)
      )
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

  public List<Query> listByUrlName(String urlName) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Query> criteria = criteriaBuilder.createQuery(Query.class);
    Root<Query> root = criteria.from(Query.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(Query_.archived), Boolean.FALSE),
        criteriaBuilder.equal(root.get(Query_.urlName), urlName)
      )
    );
    return entityManager.createQuery(criteria).getResultList();
  }
  
  public Long countByFolderAndArchived(Folder folder, Boolean archived) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<Query> root = criteria.from(Query.class);
    criteria.select(criteriaBuilder.count(root));
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(Query_.archived), archived),
        criteriaBuilder.equal(root.get(Query_.parentFolder), folder)
      )
    );
    
    return entityManager.createQuery(criteria).getSingleResult();
  }
  
  public Query updateName(Query query, User modifier, String name, String urlName) {
    Date now = new Date();

    query.setName(name);
    query.setUrlName(urlName);
    query.setLastModified(now);
    query.setLastModifier(modifier);

    getEntityManager().persist(query);
    return query;
  }
  
  public Query updateAllowEditReply(Query query, User modifier, Boolean allowEditReply) {
    Date now = new Date();

    query.setAllowEditReply(allowEditReply);
    query.setLastModified(now);
    query.setLastModifier(modifier);

    getEntityManager().persist(query);
    return query;
  }
  
  public Query updateCloses(Query query, User modifier, Date closes) {
    Date now = new Date();

    query.setCloses(closes);
    query.setLastModified(now);
    query.setLastModifier(modifier);

    getEntityManager().persist(query);
    return query;
  }
  
  public Query updateState(Query query, User modifier, QueryState state) {
    Date now = new Date();

    query.setState(state);
    query.setLastModified(now);
    query.setLastModifier(modifier);

    getEntityManager().persist(query);
    return query;
  }
  
  public Query update(Query query, User modifier, Folder parentFolder, String name, Boolean allowEditReply) {
    Date now = new Date();
    
    EntityManager entityManager = getEntityManager(); 
    
    query.setArchived(Boolean.FALSE);
    query.setLastModified(now);
    query.setLastModifier(modifier);
    query.setName(name);
    query.setParentFolder(parentFolder);
    query.setAllowEditReply(allowEditReply);
    
    entityManager.persist(query);

    return query;
  }
  
}
