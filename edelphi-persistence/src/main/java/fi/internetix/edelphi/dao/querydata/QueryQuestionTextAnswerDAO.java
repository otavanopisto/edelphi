package fi.internetix.edelphi.dao.querydata;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionTextAnswer;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionTextAnswer_;
import fi.internetix.edelphi.domainmodel.querydata.QueryReply;
import fi.internetix.edelphi.domainmodel.querymeta.QueryField;

public class QueryQuestionTextAnswerDAO extends GenericDAO<QueryQuestionTextAnswer> {

  public QueryQuestionTextAnswer create(QueryReply queryReply, QueryField queryField, String data) {
    QueryQuestionTextAnswer queryQuestionTextAnswer = new QueryQuestionTextAnswer();
    queryQuestionTextAnswer.setData(data);
    queryQuestionTextAnswer.setQueryField(queryField);
    queryQuestionTextAnswer.setQueryReply(queryReply);
    getEntityManager().persist(queryQuestionTextAnswer);
    return queryQuestionTextAnswer;
  }
  
  public QueryQuestionTextAnswer updateData(QueryQuestionTextAnswer queryQuestionTextAnswer, String data) {
    queryQuestionTextAnswer.setData(data);
    getEntityManager().persist(queryQuestionTextAnswer);
    return queryQuestionTextAnswer;
  }
  
  public QueryQuestionTextAnswer findByQueryReplyAndQueryField(QueryReply queryReply, QueryField queryField) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionTextAnswer> criteria = criteriaBuilder.createQuery(QueryQuestionTextAnswer.class);
    Root<QueryQuestionTextAnswer> root = criteria.from(QueryQuestionTextAnswer.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(QueryQuestionTextAnswer_.queryField), queryField),
        criteriaBuilder.equal(root.get(QueryQuestionTextAnswer_.queryReply), queryReply)
      )
    );

    return getSingleResult(entityManager.createQuery(criteria));
  }
  
  public List<QueryQuestionTextAnswer> listByQueryField(QueryField queryField) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionTextAnswer> criteria = criteriaBuilder.createQuery(QueryQuestionTextAnswer.class);
    Root<QueryQuestionTextAnswer> root = criteria.from(QueryQuestionTextAnswer.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(QueryQuestionTextAnswer_.queryField), queryField)
    );

    return entityManager.createQuery(criteria).getResultList();
  }

}
