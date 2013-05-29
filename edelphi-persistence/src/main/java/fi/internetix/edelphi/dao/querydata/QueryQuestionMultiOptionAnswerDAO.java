package fi.internetix.edelphi.dao.querydata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionMultiOptionAnswer;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionMultiOptionAnswer_;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionOptionAnswer_;
import fi.internetix.edelphi.domainmodel.querydata.QueryReply;
import fi.internetix.edelphi.domainmodel.querydata.QueryReply_;
import fi.internetix.edelphi.domainmodel.querymeta.QueryField;
import fi.internetix.edelphi.domainmodel.querymeta.QueryOptionFieldOption;
import fi.internetix.edelphi.domainmodel.querymeta.QueryOptionFieldOption_;

public class QueryQuestionMultiOptionAnswerDAO extends GenericDAO<QueryQuestionMultiOptionAnswer> {

  public QueryQuestionMultiOptionAnswer create(QueryReply queryReply, QueryField queryField, Set<QueryOptionFieldOption> options) {
    EntityManager entityManager = getEntityManager();
    
    QueryQuestionMultiOptionAnswer queryQuestionOptionAnswer = new QueryQuestionMultiOptionAnswer();
    queryQuestionOptionAnswer.setOptions(options);
    queryQuestionOptionAnswer.setQueryField(queryField);
    queryQuestionOptionAnswer.setQueryReply(queryReply);
    
    entityManager.persist(queryQuestionOptionAnswer);
    
    return queryQuestionOptionAnswer;
  }
  
  public QueryQuestionMultiOptionAnswer findByQueryReplyAndQueryField(QueryReply queryReply, QueryField queryField) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionMultiOptionAnswer> criteria = criteriaBuilder.createQuery(QueryQuestionMultiOptionAnswer.class);
    Root<QueryQuestionMultiOptionAnswer> root = criteria.from(QueryQuestionMultiOptionAnswer.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(QueryQuestionOptionAnswer_.queryField), queryField),
        criteriaBuilder.equal(root.get(QueryQuestionOptionAnswer_.queryReply), queryReply)
      )
    );
    

    return getSingleResult(entityManager.createQuery(criteria));
  }

  public List<QueryQuestionMultiOptionAnswer> listByQueryField(QueryField queryField) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionMultiOptionAnswer> criteria = criteriaBuilder.createQuery(QueryQuestionMultiOptionAnswer.class);
    Root<QueryQuestionMultiOptionAnswer> root = criteria.from(QueryQuestionMultiOptionAnswer.class);
    Join<QueryQuestionMultiOptionAnswer, QueryReply> qrJoin = root.join(QueryQuestionMultiOptionAnswer_.queryReply);

    criteria.select(root);
    
    criteria.where(
      criteriaBuilder.and(
          criteriaBuilder.equal(root.get(QueryQuestionOptionAnswer_.queryField), queryField),
          criteriaBuilder.equal(qrJoin.get(QueryReply_.archived), Boolean.FALSE)
      )
    );
    return entityManager.createQuery(criteria).getResultList();
  }

  public QueryQuestionMultiOptionAnswer updateOptions(QueryQuestionMultiOptionAnswer queryQuestionOptionAnswer, Set<QueryOptionFieldOption> options) {
    queryQuestionOptionAnswer.setOptions(options);
    getEntityManager().persist(queryQuestionOptionAnswer);
    return queryQuestionOptionAnswer;
  }

  public Map<Long, Long> listOptionAnswerCounts(QueryField queryMultiselectField) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Tuple> criteria = criteriaBuilder.createQuery(Tuple.class);
    Root<QueryQuestionMultiOptionAnswer> answerRoot = criteria.from(QueryQuestionMultiOptionAnswer.class);
    
    Join<QueryQuestionMultiOptionAnswer, QueryOptionFieldOption> queryFieldOptionRoot = answerRoot.join(QueryQuestionMultiOptionAnswer_.options);
    criteria.multiselect(
        queryFieldOptionRoot.get(QueryOptionFieldOption_.id), 
        criteriaBuilder.count(answerRoot.get(QueryQuestionMultiOptionAnswer_.id)));
    criteria.where(criteriaBuilder.equal(answerRoot.get(QueryQuestionMultiOptionAnswer_.queryField), queryMultiselectField));
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
