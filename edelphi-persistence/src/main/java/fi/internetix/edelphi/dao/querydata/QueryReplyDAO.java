package fi.internetix.edelphi.dao.querydata;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.edelphi.domainmodel.panels.PanelStamp;
import fi.internetix.edelphi.domainmodel.querydata.QueryReply;
import fi.internetix.edelphi.domainmodel.querydata.QueryReply_;
import fi.internetix.edelphi.domainmodel.resources.Query;
import fi.internetix.edelphi.domainmodel.users.User;

public class QueryReplyDAO extends GenericDAO<QueryReply> {

  public QueryReply create(User user, Query query, PanelStamp panelStamp, User creator) {
    Date now = new Date();
    return create(user, query, panelStamp, Boolean.FALSE, creator, now, creator, now);
  }

  public QueryReply create(User user, Query query, PanelStamp panelStamp, Boolean complete, User creator, Date created, User modifier, Date modified) {
    QueryReply queryReply = new QueryReply();
    queryReply.setStamp(panelStamp);
    queryReply.setArchived(Boolean.FALSE);
    queryReply.setCreated(created);
    queryReply.setCreator(creator);
    queryReply.setLastModified(modified);
    queryReply.setLastModifier(modifier);
    queryReply.setQuery(query);
    queryReply.setUser(user);
    queryReply.setComplete(complete);

    getEntityManager().persist(queryReply);

    return queryReply;
  }

  public QueryReply findByUserAndQueryAndStamp(User user, Query query, PanelStamp panelStamp) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryReply> criteria = criteriaBuilder.createQuery(QueryReply.class);
    Root<QueryReply> root = criteria.from(QueryReply.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(QueryReply_.query), query), 
        criteriaBuilder.equal(root.get(QueryReply_.user), user),
        criteriaBuilder.equal(root.get(QueryReply_.stamp), panelStamp),
        criteriaBuilder.equal(root.get(QueryReply_.archived), Boolean.FALSE)
      )
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }

  public List<QueryReply> listByQueryAndStamp(Query query, PanelStamp panelStamp) {
    return listByQueryAndStampAndArchived(query, panelStamp, Boolean.FALSE);
  }

  public List<QueryReply> listByQueryAndStampAndArchived(Query query, PanelStamp panelStamp, Boolean archived) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryReply> criteria = criteriaBuilder.createQuery(QueryReply.class);
    Root<QueryReply> root = criteria.from(QueryReply.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(QueryReply_.query), query), 
        criteriaBuilder.equal(root.get(QueryReply_.stamp), panelStamp), 
        criteriaBuilder.equal(root.get(QueryReply_.archived), archived)
      )
    );
    return entityManager.createQuery(criteria).getResultList();
  }

  public List<QueryReply> listByQueryAndArchived(Query query, Boolean archived) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryReply> criteria = criteriaBuilder.createQuery(QueryReply.class);
    Root<QueryReply> root = criteria.from(QueryReply.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(QueryReply_.query), query), 
        criteriaBuilder.equal(root.get(QueryReply_.archived), archived)
      )
    );
    return entityManager.createQuery(criteria).getResultList();
  }

  public List<QueryReply> listByUser(User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryReply> criteria = criteriaBuilder.createQuery(QueryReply.class);
    Root<QueryReply> root = criteria.from(QueryReply.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(QueryReply_.user), user),
        criteriaBuilder.equal(root.get(QueryReply_.archived), Boolean.FALSE)
      )
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

  public Long countByQueryAndStamp(Query query, PanelStamp panelStamp) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<QueryReply> root = criteria.from(QueryReply.class);
    criteria.select(criteriaBuilder.count(root));
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(QueryReply_.query), query), 
        criteriaBuilder.equal(root.get(QueryReply_.stamp), panelStamp), 
        criteriaBuilder.equal(root.get(QueryReply_.archived), Boolean.FALSE)
      )
    );
    
    return entityManager.createQuery(criteria).getSingleResult();
  }

  public QueryReply updateUser(QueryReply queryReply, User user, User modifier) {
    queryReply.setUser(user);
    queryReply.setLastModifier(modifier);
    queryReply.setLastModified(new Date());
    getEntityManager().persist(queryReply);
    return queryReply;
  }

  public QueryReply updateLastModified(QueryReply queryReply, User modifier) {
    queryReply.setLastModifier(modifier);
    queryReply.setLastModified(new Date());
    getEntityManager().persist(queryReply);
    return queryReply;
  }
  
  public QueryReply updateComplete(QueryReply queryReply, User user, Boolean complete) {
    queryReply.setComplete(complete);
    queryReply.setLastModifier(user);
    queryReply.setLastModified(new Date());
    getEntityManager().persist(queryReply);
    return queryReply;
  }
  
}
