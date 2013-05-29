package fi.internetix.edelphi.dao.resources;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.edelphi.domainmodel.resources.Folder;
import fi.internetix.edelphi.domainmodel.resources.GoogleDocument;
import fi.internetix.edelphi.domainmodel.resources.GoogleDocument_;
import fi.internetix.edelphi.domainmodel.users.User;

public class GoogleDocumentDAO extends GenericDAO<GoogleDocument> {

  public GoogleDocument create(String name, String urlName, Folder parentFolder, String resourceId, Integer indexNumber, Date lastSynchronized, User creator, Date created, User lastModifier, Date lastModified) {
    GoogleDocument googleDocument = new GoogleDocument();
    googleDocument.setCreator(creator);
    googleDocument.setCreated(created);
    googleDocument.setLastModifier(lastModifier);
    googleDocument.setLastModified(lastModified);
    googleDocument.setName(name);
    googleDocument.setUrlName(urlName);
    googleDocument.setParentFolder(parentFolder);
    googleDocument.setResourceId(resourceId);
    googleDocument.setIndexNumber(indexNumber);
    googleDocument.setLastSynchronized(lastSynchronized);
    getEntityManager().persist(googleDocument);
    return googleDocument;
  }

	public List<GoogleDocument> listByArchivedOrderByLastSynchronizedAsc(Boolean archived, int firstResult, int maxResults) {
		EntityManager entityManager = getEntityManager(); 
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<GoogleDocument> criteria = criteriaBuilder.createQuery(GoogleDocument.class);
    Root<GoogleDocument> root = criteria.from(GoogleDocument.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(GoogleDocument_.archived), archived)
    );
    criteria.orderBy(criteriaBuilder.asc(root.get(GoogleDocument_.lastSynchronized)));
    
    TypedQuery<GoogleDocument> query = entityManager.createQuery(criteria);
    query.setFirstResult(firstResult);
    query.setMaxResults(maxResults);
    
    return query.getResultList();
	}
  
  public GoogleDocument updateName(GoogleDocument googleDocument, String name, String urlName, User modifier) {
    googleDocument.setName(name);
    googleDocument.setUrlName(urlName);
    googleDocument.setLastModifier(modifier);
    googleDocument.setLastModified(new Date());
    getEntityManager().persist(googleDocument);
    return googleDocument;
  }
  
  /**
   * Updates GoogleDocument name. This method does not update lastModifier and lastModified fields and thus should 
   * be called only by system operations (e.g. scheduler)
   * 
   * @param googleDocument GoogleDocument entity
   * @param name new resource name
   * @param urlName new resource urlName
   * @return Updated GoogleDocument
   */
  public GoogleDocument updateName(GoogleDocument googleDocument, String name, String urlName) {
    googleDocument.setName(name);
    googleDocument.setUrlName(urlName);
    getEntityManager().persist(googleDocument);
    return googleDocument;
  }

	public GoogleDocument updateLastModified(GoogleDocument googleDocument, Date lastModified) {
		googleDocument.setLastModified(lastModified);
    getEntityManager().persist(googleDocument);
    return googleDocument;
	}

  public GoogleDocument updateParentFolder(GoogleDocument googleDocument, Folder parentFolder, User modifier) {
    googleDocument.setParentFolder(parentFolder);
    googleDocument.setLastModifier(modifier);
    googleDocument.setLastModified(new Date());
    getEntityManager().persist(googleDocument);
    return googleDocument;
  }
  
  public GoogleDocument updateLastSynchronized(GoogleDocument googleDocument, Date lastSynchronized) {
    googleDocument.setLastSynchronized(lastSynchronized);
    getEntityManager().persist(googleDocument);
    return googleDocument;
  }
}
