package fi.internetix.edelphi.dao.querydata;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionNumericAnswer;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionNumericAnswer_;
import fi.internetix.edelphi.domainmodel.querydata.QueryReply;
import fi.internetix.edelphi.domainmodel.querydata.QueryReply_;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;
import fi.internetix.edelphi.domainmodel.querymeta.QueryField;
import fi.internetix.edelphi.domainmodel.querymeta.QueryField_;

public class QueryQuestionNumericAnswerDAO extends GenericDAO<QueryQuestionNumericAnswer> {

  public QueryQuestionNumericAnswer create(QueryReply queryReply, QueryField queryField, Double data) {
    QueryQuestionNumericAnswer queryQuestionNumericAnswer = new QueryQuestionNumericAnswer();
    queryQuestionNumericAnswer.setData(data);
    queryQuestionNumericAnswer.setQueryField(queryField);
    queryQuestionNumericAnswer.setQueryReply(queryReply);
    getEntityManager().persist(queryQuestionNumericAnswer);
    return queryQuestionNumericAnswer;
  }
  
  public QueryQuestionNumericAnswer updateData(QueryQuestionNumericAnswer queryQuestionNumericAnswer, Double data) {
    queryQuestionNumericAnswer.setData(data);
    getEntityManager().persist(queryQuestionNumericAnswer);
    return queryQuestionNumericAnswer;
  }
  
  public QueryQuestionNumericAnswer findByQueryReplyAndQueryField(QueryReply queryReply, QueryField queryField) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionNumericAnswer> criteria = criteriaBuilder.createQuery(QueryQuestionNumericAnswer.class);
    Root<QueryQuestionNumericAnswer> root = criteria.from(QueryQuestionNumericAnswer.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(  
        criteriaBuilder.equal(root.get(QueryQuestionNumericAnswer_.queryReply), queryReply),
        criteriaBuilder.equal(root.get(QueryQuestionNumericAnswer_.queryField), queryField)
      )
    );

    return getSingleResult(entityManager.createQuery(criteria));
  }

  public List<QueryQuestionNumericAnswer> listByQueryField(QueryField queryField) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionNumericAnswer> criteria = criteriaBuilder.createQuery(QueryQuestionNumericAnswer.class);
    Root<QueryQuestionNumericAnswer> root = criteria.from(QueryQuestionNumericAnswer.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(QueryQuestionNumericAnswer_.queryField), queryField)
    );

    return entityManager.createQuery(criteria).getResultList();
  }
  
  public List<QueryQuestionNumericAnswer> listByQueryReplyAndQueryPageOrderByData(QueryReply queryReply, QueryPage queryPage) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionNumericAnswer> criteria = criteriaBuilder.createQuery(QueryQuestionNumericAnswer.class);
    Root<QueryQuestionNumericAnswer> root = criteria.from(QueryQuestionNumericAnswer.class);
    Join<QueryQuestionNumericAnswer, QueryField> qfJoin = root.join(QueryQuestionNumericAnswer_.queryField);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(QueryQuestionNumericAnswer_.queryReply), queryReply),
        criteriaBuilder.equal(qfJoin.get(QueryField_.queryPage), queryPage)
      )
    );
    
    criteria.orderBy(criteriaBuilder.asc(root.get(QueryQuestionNumericAnswer_.data)));

    return entityManager.createQuery(criteria).getResultList();
  }
  
  public Long countByQueryFieldQueryReplyAndData(QueryField queryField, QueryReply queryReply, Double data) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<QueryQuestionNumericAnswer> root = criteria.from(QueryQuestionNumericAnswer.class);
    Join<QueryQuestionNumericAnswer, QueryReply> replyJoin = root.join(QueryQuestionNumericAnswer_.queryReply);
    
    criteria.select(criteriaBuilder.count(root));
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(QueryQuestionNumericAnswer_.queryField), queryField),
        criteriaBuilder.equal(root.get(QueryQuestionNumericAnswer_.queryReply), queryReply),
        criteriaBuilder.equal(root.get(QueryQuestionNumericAnswer_.data), data),
        criteriaBuilder.equal(replyJoin.get(QueryReply_.archived), Boolean.FALSE)
      )
    );
    
    return entityManager.createQuery(criteria).getSingleResult();
  }
  
  // TOOD: This is not a DAO method
  public List<Double> listAnswers(QueryField queryField) {
    List<QueryQuestionNumericAnswer> listByQueryField = listByQueryField(queryField);
    List<Double> results = new ArrayList<Double>();
    
    for (QueryQuestionNumericAnswer answer : listByQueryField) {
      results.add(answer.getData());
    }
    
    return results;
  }
  
}
