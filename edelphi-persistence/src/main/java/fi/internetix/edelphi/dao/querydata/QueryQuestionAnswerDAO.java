package fi.internetix.edelphi.dao.querydata;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionAnswer;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionAnswer_;
import fi.internetix.edelphi.domainmodel.querydata.QueryReply;
import fi.internetix.edelphi.domainmodel.querydata.QueryReply_;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;
import fi.internetix.edelphi.domainmodel.querymeta.QueryField;
import fi.internetix.edelphi.domainmodel.querymeta.QueryField_;

public class QueryQuestionAnswerDAO extends GenericDAO<QueryQuestionAnswer> {

  public QueryQuestionAnswer findByQueryReplyAndQueryField(QueryReply queryReply, QueryField queryField) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionAnswer> criteria = criteriaBuilder.createQuery(QueryQuestionAnswer.class);
    Root<QueryQuestionAnswer> root = criteria.from(QueryQuestionAnswer.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(QueryQuestionAnswer_.queryReply), queryReply),
        criteriaBuilder.equal(root.get(QueryQuestionAnswer_.queryField), queryField)
      )
    );

    return getSingleResult(entityManager.createQuery(criteria)); 
  }
  
  public Long countByQueryPage(QueryPage queryPage) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<QueryQuestionAnswer> root = criteria.from(QueryQuestionAnswer.class);
    Join<QueryQuestionAnswer, QueryField> qfJoin = root.join(QueryQuestionAnswer_.queryField);
    Join<QueryQuestionAnswer, QueryReply> qrJoin = root.join(QueryQuestionAnswer_.queryReply);
    
    criteria.select(criteriaBuilder.count(root));
    criteria.where(
        criteriaBuilder.and(
            criteriaBuilder.equal(qrJoin.get(QueryReply_.archived), Boolean.FALSE),
            criteriaBuilder.equal(qfJoin.get(QueryField_.queryPage), queryPage)
        )
    );

    return entityManager.createQuery(criteria).getSingleResult();
  }

  public Long countByQueryField(QueryField queryField) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<QueryQuestionAnswer> root = criteria.from(QueryQuestionAnswer.class);
    
    criteria.select(criteriaBuilder.count(root));
    criteria.where(
      criteriaBuilder.equal(root.get(QueryQuestionAnswer_.queryField), queryField)
    );
    
    return entityManager.createQuery(criteria).getSingleResult();
  }

  public Long countByQueryFieldAndArchived(QueryField queryField, boolean archived) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<QueryQuestionAnswer> root = criteria.from(QueryQuestionAnswer.class);
    Join<QueryQuestionAnswer, QueryReply> qrJoin = root.join(QueryQuestionAnswer_.queryReply);
    
    criteria.select(criteriaBuilder.count(root));
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(qrJoin.get(QueryReply_.archived), archived),
        criteriaBuilder.equal(root.get(QueryQuestionAnswer_.queryField), queryField)
      )
    );
    
    return entityManager.createQuery(criteria).getSingleResult();
  }

  public Long countByQueryFieldAndReply(QueryField queryField, QueryReply queryReply) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<QueryQuestionAnswer> root = criteria.from(QueryQuestionAnswer.class);
    
    criteria.select(criteriaBuilder.count(root));
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(QueryQuestionAnswer_.queryReply), queryReply),
        criteriaBuilder.equal(root.get(QueryQuestionAnswer_.queryField), queryField)
      )
    );
    
    return entityManager.createQuery(criteria).getSingleResult();
  }
}
