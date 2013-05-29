package fi.internetix.edelphi.dao.querylayout;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPageType;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPage_;
import fi.internetix.edelphi.domainmodel.querylayout.QuerySection;
import fi.internetix.edelphi.domainmodel.querylayout.QuerySection_;
import fi.internetix.edelphi.domainmodel.resources.Folder;
import fi.internetix.edelphi.domainmodel.resources.Query;
import fi.internetix.edelphi.domainmodel.resources.Query_;
import fi.internetix.edelphi.domainmodel.users.User;

public class QueryPageDAO extends GenericDAO<QueryPage> {

  public QueryPage create(User creator, QuerySection querySection, QueryPageType pageType, Integer pageNumber, String title, Boolean visible) {
    Date now = new Date();

    QueryPage queryPage = new QueryPage();
    queryPage.setQuerySection(querySection);
    queryPage.setTitle(title);
    queryPage.setPageNumber(pageNumber);
    queryPage.setVisible(visible);
    queryPage.setArchived(Boolean.FALSE);
    queryPage.setCreated(now);
    queryPage.setLastModified(now);
    queryPage.setCreator(creator);
    queryPage.setLastModifier(creator);
    queryPage.setPageType(pageType);

    getEntityManager().persist(queryPage);
    return queryPage;
  }

  public QueryPage findByQueryAndPageNumber(Query query, Integer pageNumber) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryPage> criteria = criteriaBuilder.createQuery(QueryPage.class);

    Root<QueryPage> root = criteria.from(QueryPage.class);
    Join<QueryPage, QuerySection> qsJoin = root.join(QueryPage_.querySection);

    criteria.select(root);

    criteria.where(criteriaBuilder.and(criteriaBuilder.equal(qsJoin.get(QuerySection_.query), query),
        criteriaBuilder.equal(qsJoin.get(QuerySection_.archived), Boolean.FALSE), criteriaBuilder.equal(root.get(QueryPage_.pageNumber), pageNumber),
        criteriaBuilder.equal(root.get(QueryPage_.archived), Boolean.FALSE)));

    return getSingleResult(entityManager.createQuery(criteria));
  }

  public List<QueryPage> listByQuerySection(QuerySection querySection) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryPage> criteria = criteriaBuilder.createQuery(QueryPage.class);
    Root<QueryPage> root = criteria.from(QueryPage.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.and(criteriaBuilder.equal(root.get(QueryPage_.querySection), querySection),
        criteriaBuilder.equal(root.get(QueryPage_.archived), Boolean.FALSE)));

    return entityManager.createQuery(criteria).getResultList();
  }

  public List<QueryPage> listByQuery(Query query) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryPage> criteria = criteriaBuilder.createQuery(QueryPage.class);

    Root<QueryPage> root = criteria.from(QueryPage.class);
    Join<QueryPage, QuerySection> qsJoin = root.join(QueryPage_.querySection);

    // TODO: Query archived...

    criteria.select(root);
    criteria.where(criteriaBuilder.and(criteriaBuilder.equal(qsJoin.get(QuerySection_.query), query),
        criteriaBuilder.equal(qsJoin.get(QuerySection_.archived), Boolean.FALSE), criteriaBuilder.equal(root.get(QueryPage_.archived), Boolean.FALSE)));

    return entityManager.createQuery(criteria).getResultList();
  }

  public Long countByQuery(Query query) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);

    Root<QueryPage> root = criteria.from(QueryPage.class);
    Join<QueryPage, QuerySection> qsJoin = root.join(QueryPage_.querySection);

    // TODO: Query archived...

    criteria.select(criteriaBuilder.count(root));
    criteria.where(criteriaBuilder.and(criteriaBuilder.equal(qsJoin.get(QuerySection_.query), query),
        criteriaBuilder.equal(qsJoin.get(QuerySection_.archived), Boolean.FALSE), criteriaBuilder.equal(root.get(QueryPage_.archived), Boolean.FALSE)));

    return entityManager.createQuery(criteria).getSingleResult();
  }

  public Long countByQueryAndVisible(Query query, Boolean visible) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);

    Root<QueryPage> root = criteria.from(QueryPage.class);
    Join<QueryPage, QuerySection> qsJoin = root.join(QueryPage_.querySection);

    // TODO: Query archived...

    criteria.select(criteriaBuilder.count(root));
    criteria.where(criteriaBuilder.and(criteriaBuilder.equal(qsJoin.get(QuerySection_.query), query),
        criteriaBuilder.equal(qsJoin.get(QuerySection_.visible), visible), criteriaBuilder.equal(qsJoin.get(QuerySection_.archived), Boolean.FALSE),
        criteriaBuilder.equal(root.get(QueryPage_.visible), visible), criteriaBuilder.equal(root.get(QueryPage_.archived), Boolean.FALSE)));

    return entityManager.createQuery(criteria).getSingleResult();
  }

  public QueryPage updateLastModified(QueryPage queryPage, Date lastModified, User lastModifier) {
    queryPage.setLastModifier(lastModifier);
    queryPage.setLastModified(lastModified);
    getEntityManager().persist(queryPage);
    return queryPage;
  }

  public QueryPage updatePageNumber(QueryPage queryPage, Integer pageNumber, User modifier) {
    Date now = new Date();

    queryPage.setPageNumber(pageNumber);
    queryPage.setLastModified(now);
    queryPage.setLastModifier(modifier);

    getEntityManager().persist(queryPage);
    return queryPage;
  }

  public QueryPage updateTitle(QueryPage queryPage, String title, User modifier) {
    Date now = new Date();

    queryPage.setTitle(title);
    queryPage.setLastModified(now);
    queryPage.setLastModifier(modifier);

    getEntityManager().persist(queryPage);
    return queryPage;
  }

  public QueryPage updateVisible(QueryPage queryPage, Boolean visible, User modifier) {
    Date now = new Date();

    queryPage.setVisible(visible);
    queryPage.setLastModified(now);
    queryPage.setLastModifier(modifier);

    getEntityManager().persist(queryPage);
    return queryPage;
  }

  public Integer findMaxPageNumber(Query query) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Integer> criteria = criteriaBuilder.createQuery(Integer.class);

    Root<QueryPage> root = criteria.from(QueryPage.class);
    Join<QueryPage, QuerySection> qsJoin = root.join(QueryPage_.querySection);

    criteria.select(criteriaBuilder.max(root.get(QueryPage_.pageNumber)));
    criteria.where(criteriaBuilder.and(criteriaBuilder.equal(qsJoin.get(QuerySection_.query), query),
        criteriaBuilder.equal(qsJoin.get(QuerySection_.archived), Boolean.FALSE), criteriaBuilder.equal(root.get(QueryPage_.archived), Boolean.FALSE)));

    return entityManager.createQuery(criteria).getSingleResult();
  }

  public QueryPage updateSection(QueryPage queryPage, QuerySection querySection, User modifier) {
    queryPage.setQuerySection(querySection);
    queryPage.setLastModified(new Date());
    queryPage.setLastModifier(modifier);

    getEntityManager().persist(queryPage);
    return queryPage;
  }

  public List<QueryPage> listByType(QueryPageType pageType) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryPage> criteria = criteriaBuilder.createQuery(QueryPage.class);

    Root<QueryPage> root = criteria.from(QueryPage.class);

    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(QueryPage_.pageType), pageType));

    return entityManager.createQuery(criteria).getResultList();
  }

  public List<QueryPage> listByQueryAndType(Query query, QueryPageType pageType) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryPage> criteria = criteriaBuilder.createQuery(QueryPage.class);

    Root<QueryPage> root = criteria.from(QueryPage.class);
    Join<QueryPage, QuerySection> qsJoin = root.join(QueryPage_.querySection);

    criteria.select(root);
    criteria.where(criteriaBuilder.and(criteriaBuilder.equal(qsJoin.get(QuerySection_.query), query),
        criteriaBuilder.equal(qsJoin.get(QuerySection_.archived), Boolean.FALSE), criteriaBuilder.equal(root.get(QueryPage_.pageType), pageType),
        criteriaBuilder.equal(root.get(QueryPage_.archived), Boolean.FALSE)));

    return entityManager.createQuery(criteria).getResultList();
  }

  public List<QueryPage> listByQueryParentFolderAndPageType(Folder parentFolder, QueryPageType pageType) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryPage> criteria = criteriaBuilder.createQuery(QueryPage.class);

    Root<QueryPage> root = criteria.from(QueryPage.class);
    Join<QueryPage, QuerySection> qsJoin = root.join(QueryPage_.querySection);
    Join<QuerySection, Query> qJoin = qsJoin.join(QuerySection_.query);

    criteria.select(root);
    criteria.where(criteriaBuilder.and(criteriaBuilder.equal(qJoin.get(Query_.parentFolder), parentFolder),
        criteriaBuilder.equal(qJoin.get(Query_.archived), Boolean.FALSE), criteriaBuilder.equal(qsJoin.get(QuerySection_.archived), Boolean.FALSE),
        criteriaBuilder.equal(root.get(QueryPage_.pageType), pageType), criteriaBuilder.equal(root.get(QueryPage_.archived), Boolean.FALSE)));

    return entityManager.createQuery(criteria).getResultList();
  }

}
