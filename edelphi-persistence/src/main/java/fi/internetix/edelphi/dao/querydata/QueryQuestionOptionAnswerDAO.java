package fi.internetix.edelphi.dao.querydata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionOptionAnswer;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionOptionAnswer_;
import fi.internetix.edelphi.domainmodel.querydata.QueryReply;
import fi.internetix.edelphi.domainmodel.querymeta.QueryField;
import fi.internetix.edelphi.domainmodel.querymeta.QueryOptionFieldOption;
import fi.internetix.edelphi.domainmodel.querymeta.QueryOptionFieldOption_;

public class QueryQuestionOptionAnswerDAO extends GenericDAO<QueryQuestionOptionAnswer> {

  public QueryQuestionOptionAnswer create(QueryReply queryReply, QueryField queryField, QueryOptionFieldOption option) {
    QueryQuestionOptionAnswer queryQuestionOptionAnswer = new QueryQuestionOptionAnswer();
    queryQuestionOptionAnswer.setOption(option);
    queryQuestionOptionAnswer.setQueryField(queryField);
    queryQuestionOptionAnswer.setQueryReply(queryReply);
    getEntityManager().persist(queryQuestionOptionAnswer);
    return queryQuestionOptionAnswer;
  }
  
  public QueryQuestionOptionAnswer findByQueryReplyAndQueryField(QueryReply queryReply, QueryField queryField) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionOptionAnswer> criteria = criteriaBuilder.createQuery(QueryQuestionOptionAnswer.class);
    Root<QueryQuestionOptionAnswer> root = criteria.from(QueryQuestionOptionAnswer.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(QueryQuestionOptionAnswer_.queryField), queryField),
        criteriaBuilder.equal(root.get(QueryQuestionOptionAnswer_.queryReply), queryReply)
      )
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }

  public Long countByQueryOptionFieldOption(QueryOptionFieldOption queryOptionFieldOption) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<QueryQuestionOptionAnswer> root = criteria.from(QueryQuestionOptionAnswer.class);
    
    criteria.select(criteriaBuilder.count(root));
    criteria.where(
      criteriaBuilder.equal(root.get(QueryQuestionOptionAnswer_.option), queryOptionFieldOption)
    );
    
    return entityManager.createQuery(criteria).getSingleResult();
  }

  public List<QueryQuestionOptionAnswer> listByQueryReplyAndQueryField(QueryReply queryReply, QueryField queryField) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionOptionAnswer> criteria = criteriaBuilder.createQuery(QueryQuestionOptionAnswer.class);
    Root<QueryQuestionOptionAnswer> root = criteria.from(QueryQuestionOptionAnswer.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(QueryQuestionOptionAnswer_.queryField), queryField),
        criteriaBuilder.equal(root.get(QueryQuestionOptionAnswer_.queryReply), queryReply)
      )
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }
  
  public List<QueryQuestionOptionAnswer> listByQueryField(QueryField queryField) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionOptionAnswer> criteria = criteriaBuilder.createQuery(QueryQuestionOptionAnswer.class);
    Root<QueryQuestionOptionAnswer> root = criteria.from(QueryQuestionOptionAnswer.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(QueryQuestionOptionAnswer_.queryField), queryField)
    );
    return entityManager.createQuery(criteria).getResultList();
  }

  public QueryQuestionOptionAnswer updateOption(QueryQuestionOptionAnswer queryQuestionOptionAnswer, QueryOptionFieldOption option) {
    queryQuestionOptionAnswer.setOption(option);
    getEntityManager().persist(queryQuestionOptionAnswer);
    return queryQuestionOptionAnswer;
  }

  /**
   * 
   * @param queryField
   * @return map with queryfieldoption.id as key, count of the option answers as value 
   */
  public Map<Long, Long> listOptionAnswerCounts(QueryField queryField) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Tuple> criteria = criteriaBuilder.createQuery(Tuple.class);
    Root<QueryQuestionOptionAnswer> answerRoot = criteria.from(QueryQuestionOptionAnswer.class);
    Join<QueryQuestionOptionAnswer, QueryOptionFieldOption> queryFieldOptionRoot = answerRoot.join(QueryQuestionOptionAnswer_.option);
    criteria.multiselect(
        queryFieldOptionRoot.get(QueryOptionFieldOption_.id), 
        criteriaBuilder.count(answerRoot.get(QueryQuestionOptionAnswer_.id)));
    criteria.where(criteriaBuilder.equal(answerRoot.get(QueryQuestionOptionAnswer_.queryField), queryField));
    criteria.groupBy(queryFieldOptionRoot.get(QueryOptionFieldOption_.id));

    TypedQuery<Tuple> q = entityManager.createQuery(criteria);
    List<Tuple> resultList = q.getResultList();

    Map<Long, Long> resultMap = new HashMap<Long, Long>();

    for (Tuple optionAnswer : resultList) {
      resultMap.put(optionAnswer.get(0, Long.class), optionAnswer.get(1, Long.class));
    }
    
    return resultMap;
  }
  
}
