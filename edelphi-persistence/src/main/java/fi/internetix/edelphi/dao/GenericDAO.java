package fi.internetix.edelphi.dao;

import java.lang.reflect.ParameterizedType;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import fi.internetix.edelphi.domainmodel.base.ArchivableEntity;
import fi.internetix.edelphi.domainmodel.base.ModificationTrackedEntity;
import fi.internetix.edelphi.domainmodel.users.User;

public class GenericDAO<T> {
  
  @SuppressWarnings("unchecked")
  public T findById(Long id) {
    EntityManager entityManager = getEntityManager();
    return (T) entityManager.find(getGenericTypeClass(), id);
  }

  @SuppressWarnings("unchecked")
  public List<T> listAll() {
    EntityManager entityManager = getEntityManager();
    Class<?> genericTypeClass = getGenericTypeClass();
    Query query = entityManager.createQuery("select o from " + genericTypeClass.getName() + " o");
    return query.getResultList();
  }

  @SuppressWarnings("unchecked")
  public List<T> listAll(int firstResult, int maxResults) {
    EntityManager entityManager = getEntityManager();
    Class<?> genericTypeClass = getGenericTypeClass();
    Query query = entityManager.createQuery("select o from " + genericTypeClass.getName() + " o");
    query.setFirstResult(firstResult);
    query.setMaxResults(maxResults);
    return query.getResultList();
  }
  
  public void archive(ArchivableEntity entity) {
    archive(entity, null);
  }
  
  public void archive(ArchivableEntity entity, User user) {
    EntityManager entityManager = getEntityManager();
    entity.setArchived(Boolean.TRUE);
    if (entity instanceof ModificationTrackedEntity && user != null) {
      ((ModificationTrackedEntity) entity).setLastModified(new Date());
      ((ModificationTrackedEntity) entity).setLastModifier(user);
    }
    entityManager.persist(entity);
}

  public void unarchive(ArchivableEntity entity) {
    unarchive(entity, null);
  }
  
  public void unarchive(ArchivableEntity entity, User user) {
    EntityManager entityManager = getEntityManager();
    entity.setArchived(Boolean.FALSE);
    if (entity instanceof ModificationTrackedEntity && user != null) {
      ((ModificationTrackedEntity) entity).setLastModified(new Date());
      ((ModificationTrackedEntity) entity).setLastModifier(user);
    }
    entityManager.persist(entity);
  }

  public Integer count() {
    EntityManager entityManager = getEntityManager();
    Class<?> genericTypeClass = getGenericTypeClass();
    Query query = entityManager.createQuery("select count(o) from " + genericTypeClass.getName() + " o");
    return (Integer) query.getSingleResult();
  }

  public void delete(T e) {
    getEntityManager().remove(e);
  }
  
  public void flush() {
    getEntityManager().flush();
  }
  
  protected T getSingleResult(Query query) {
    @SuppressWarnings("unchecked")
    List<T> list = query.getResultList();
    
    if (list.size() == 0)
      return null;
    
    if (list.size() == 1)
      return list.get(0);
    
    throw new NonUniqueResultException("SingleResult query returned " + list.size() + " elements");
  }

  protected EntityManager getEntityManager() {
    return THREAD_LOCAL.get();
  }
  
  public static void setEntityManager(EntityManager entityManager) {
    if (entityManager == null)
      THREAD_LOCAL.remove();
    else
      THREAD_LOCAL.set(entityManager);
  }
  
  private Class<?> getGenericTypeClass() {
    ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
    return (Class<?>) parameterizedType.getActualTypeArguments()[0];
  }
  
  private static final ThreadLocal<EntityManager> THREAD_LOCAL = new ThreadLocal<EntityManager>();
}
