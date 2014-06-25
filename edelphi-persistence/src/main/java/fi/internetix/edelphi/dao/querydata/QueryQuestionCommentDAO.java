package fi.internetix.edelphi.dao.querydata;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.edelphi.domainmodel.panels.PanelStamp;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionComment;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionComment_;
import fi.internetix.edelphi.domainmodel.querydata.QueryReply;
import fi.internetix.edelphi.domainmodel.querydata.QueryReply_;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;
import fi.internetix.edelphi.domainmodel.users.User;

public class QueryQuestionCommentDAO extends GenericDAO<QueryQuestionComment> {

  public QueryQuestionComment create(QueryReply queryReply, QueryPage queryPage, QueryQuestionComment parentComment, String comment, Boolean hidden, User creator) {
    Date now = new Date();
    return create(queryReply, queryPage, parentComment, comment, hidden, creator, now, creator, now);
  }

  public QueryQuestionComment create(QueryReply queryReply, QueryPage queryPage, QueryQuestionComment parentComment, String comment, Boolean hidden, User creator, Date created, User modifier, Date modified) {
    QueryQuestionComment questionComment = new QueryQuestionComment();

    questionComment.setComment(comment);
    questionComment.setQueryPage(queryPage);
    questionComment.setQueryReply(queryReply);
    questionComment.setParentComment(parentComment);
    questionComment.setCreator(creator);
    questionComment.setHidden(hidden);
    questionComment.setCreated(created);
    questionComment.setLastModifier(modifier);
    questionComment.setLastModified(modified);
    
    getEntityManager().persist(questionComment);
    return questionComment;
  }
  
  public QueryQuestionComment findByQueryReplyAndQueryPage(QueryReply queryReply, QueryPage queryPage) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionComment> criteria = criteriaBuilder.createQuery(QueryQuestionComment.class);
    Root<QueryQuestionComment> root = criteria.from(QueryQuestionComment.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(QueryQuestionComment_.queryReply), queryReply),
        criteriaBuilder.equal(root.get(QueryQuestionComment_.queryPage), queryPage),
        criteriaBuilder.equal(root.get(QueryQuestionComment_.archived), Boolean.FALSE)
      )
    );

    return getSingleResult(entityManager.createQuery(criteria)); 
  }

  public QueryQuestionComment findRootCommentByQueryReplyAndQueryPage(QueryReply queryReply, QueryPage queryPage) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionComment> criteria = criteriaBuilder.createQuery(QueryQuestionComment.class);
    Root<QueryQuestionComment> root = criteria.from(QueryQuestionComment.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(QueryQuestionComment_.queryReply), queryReply),
        criteriaBuilder.equal(root.get(QueryQuestionComment_.queryPage), queryPage),
        criteriaBuilder.isNull(root.get(QueryQuestionComment_.parentComment)),
        criteriaBuilder.equal(root.get(QueryQuestionComment_.archived), Boolean.FALSE)
      )
    );

    return getSingleResult(entityManager.createQuery(criteria)); 
  }
  
  /**
   * Lists all nonarchived comments left on the given query page across all the stamps of the panel
   * the query belongs to. You probably want to use {@link #listByQueryPageAndStamp(QueryPage,PanelStamp)}
   * instead.
   * 
   * @param queryPage  the comments' query page
   * 
   * @return  a list of all nonarchived comments left on the given query page across all stamps
   */
  public List<QueryQuestionComment> listByQueryPage(QueryPage queryPage) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionComment> criteria = criteriaBuilder.createQuery(QueryQuestionComment.class);
    Root<QueryQuestionComment> root = criteria.from(QueryQuestionComment.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(QueryQuestionComment_.queryPage), queryPage),
        criteriaBuilder.equal(root.get(QueryQuestionComment_.archived), Boolean.FALSE)
      )
    );

    return entityManager.createQuery(criteria).getResultList(); 
  }

  /**
   * Lists all nonarchived comments left on the given query page in the given panel stamp.
   * 
   * @param queryPage  the comments' query page
   * @param stamp      the panel stamp
   * 
   * @return  a list of all nonarchived comments left on the given query page in the given panel stamp
   */
  public List<QueryQuestionComment> listByQueryPageAndStamp(QueryPage queryPage, PanelStamp stamp) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionComment> criteria = criteriaBuilder.createQuery(QueryQuestionComment.class);
    Root<QueryQuestionComment> root = criteria.from(QueryQuestionComment.class);
    Join<QueryQuestionComment, QueryReply> qqJoin = root.join(QueryQuestionComment_.queryReply);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(qqJoin.get(QueryReply_.stamp), stamp),
        criteriaBuilder.equal(root.get(QueryQuestionComment_.queryPage), queryPage),
        criteriaBuilder.equal(root.get(QueryQuestionComment_.archived), Boolean.FALSE)
      )
    );

    return entityManager.createQuery(criteria).getResultList(); 
  }

  public List<QueryQuestionComment> listByQueryReply(QueryReply queryReply) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionComment> criteria = criteriaBuilder.createQuery(QueryQuestionComment.class);
    Root<QueryQuestionComment> root = criteria.from(QueryQuestionComment.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.and(
            criteriaBuilder.equal(root.get(QueryQuestionComment_.queryReply), queryReply),
            criteriaBuilder.equal(root.get(QueryQuestionComment_.archived), Boolean.FALSE)
        )
    );

    return entityManager.createQuery(criteria).getResultList(); 
  }

  public List<QueryQuestionComment> listRootCommentsByQueryPageAndStamp(QueryPage queryPage, PanelStamp panelStamp) {
    EntityManager entityManager = getEntityManager();
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionComment> criteria = criteriaBuilder.createQuery(QueryQuestionComment.class);
    Root<QueryQuestionComment> root = criteria.from(QueryQuestionComment.class);
    Join<QueryQuestionComment, QueryReply> qqJoin = root.join(QueryQuestionComment_.queryReply);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.and(
            criteriaBuilder.equal(qqJoin.get(QueryReply_.stamp), panelStamp),
            criteriaBuilder.equal(root.get(QueryQuestionComment_.queryPage), queryPage),
            criteriaBuilder.equal(root.get(QueryQuestionComment_.archived), Boolean.FALSE),
            criteriaBuilder.isNull(root.get(QueryQuestionComment_.parentComment))
        )
    );

    return entityManager.createQuery(criteria).getResultList(); 
  }

  public List<QueryQuestionComment> listByParentCommentAndArchived(QueryQuestionComment parentComment, Boolean archived) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionComment> criteria = criteriaBuilder.createQuery(QueryQuestionComment.class);
    Root<QueryQuestionComment> root = criteria.from(QueryQuestionComment.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.and(
            criteriaBuilder.equal(root.get(QueryQuestionComment_.parentComment), parentComment),
            criteriaBuilder.equal(root.get(QueryQuestionComment_.archived), archived)
        )
    );

    return entityManager.createQuery(criteria).getResultList(); 
  }

  /**
   * Lists all non-root comments on page and orders them to map with parentComment.id as key and 
   * list of comments directly below parentComment as value. 
   *  
   * @param queryPage page where to list the comments from
   * @return Map<parentComment.id, List<childComments>>
   */
  public Map<Long, List<QueryQuestionComment>> listTreesByQueryPage(QueryPage queryPage) {
    Map<Long, List<QueryQuestionComment>> result = new HashMap<Long, List<QueryQuestionComment>>();

    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionComment> criteria = criteriaBuilder.createQuery(QueryQuestionComment.class);
    Root<QueryQuestionComment> root = criteria.from(QueryQuestionComment.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.and(
            criteriaBuilder.equal(root.get(QueryQuestionComment_.queryPage), queryPage),
            criteriaBuilder.equal(root.get(QueryQuestionComment_.archived), Boolean.FALSE),
            criteriaBuilder.isNotNull(root.get(QueryQuestionComment_.parentComment))
        )
    );

    List<QueryQuestionComment> allChildren = entityManager.createQuery(criteria).getResultList(); 
    
    for (QueryQuestionComment comment : allChildren) {
      Long parentCommentId = comment.getParentComment().getId();
      List<QueryQuestionComment> children = result.get(parentCommentId);
      
      if (children == null) {
        children = new ArrayList<QueryQuestionComment>();
        result.put(parentCommentId, children);
      }

      children.add(comment);
    }
    
    return result;
  }
  
  public Long countByQueryPage(QueryPage queryPage) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<QueryQuestionComment> root = criteria.from(QueryQuestionComment.class);
    criteria.select(criteriaBuilder.count(root));
    criteria.where(
        criteriaBuilder.and(
            criteriaBuilder.equal(root.get(QueryQuestionComment_.queryPage), queryPage),
            criteriaBuilder.equal(root.get(QueryQuestionComment_.archived), Boolean.FALSE)
        )
    );

    return entityManager.createQuery(criteria).getSingleResult();
  }

  public QueryQuestionComment updateComment(QueryQuestionComment comment, String newComment, User modifier) {
    comment.setLastModified(new Date());
    comment.setLastModifier(modifier);
    comment.setComment(newComment);
    
    getEntityManager().persist(comment);
    return comment;
  }

  public QueryQuestionComment updateComment(QueryQuestionComment comment, String newComment, User modifier, Date modified) {
    comment.setLastModified(modified);
    comment.setLastModifier(modifier);
    comment.setComment(newComment);
    
    getEntityManager().persist(comment);
    return comment;
  }

  public QueryQuestionComment updateHidden(QueryQuestionComment comment, Boolean hidden, User modifier) {
    comment.setLastModified(new Date());
    comment.setLastModifier(modifier);
    comment.setHidden(hidden);
    
    getEntityManager().persist(comment);
    
    return comment;
  }

  public Map<Long, List<QueryQuestionComment>> listTreesByQueryPageAndStamp(QueryPage queryPage, PanelStamp panelStamp) {
    Map<Long, List<QueryQuestionComment>> result = new HashMap<Long, List<QueryQuestionComment>>();

    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionComment> criteria = criteriaBuilder.createQuery(QueryQuestionComment.class);
    Root<QueryQuestionComment> root = criteria.from(QueryQuestionComment.class);
    Join<QueryQuestionComment, QueryReply> qqJoin = root.join(QueryQuestionComment_.queryReply);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.and(
            criteriaBuilder.equal(qqJoin.get(QueryReply_.stamp), panelStamp),
            criteriaBuilder.equal(root.get(QueryQuestionComment_.queryPage), queryPage),
            criteriaBuilder.equal(root.get(QueryQuestionComment_.archived), Boolean.FALSE),
            criteriaBuilder.isNotNull(root.get(QueryQuestionComment_.parentComment))
        )
    );

    List<QueryQuestionComment> allChildren = entityManager.createQuery(criteria).getResultList();

    for (QueryQuestionComment comment : allChildren) {
      Long parentCommentId = comment.getParentComment().getId();
      List<QueryQuestionComment> children = result.get(parentCommentId);

      if (children == null) {
        children = new ArrayList<QueryQuestionComment>();
        result.put(parentCommentId, children);
      }

      children.add(comment);
    }

    return result;
  }

}
