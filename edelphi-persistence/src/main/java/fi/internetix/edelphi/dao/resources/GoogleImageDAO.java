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
import fi.internetix.edelphi.domainmodel.resources.GoogleImage;
import fi.internetix.edelphi.domainmodel.resources.GoogleImage_;
import fi.internetix.edelphi.domainmodel.users.User;

public class GoogleImageDAO extends GenericDAO<GoogleImage> {

  public GoogleImage create(String name, String urlName, Folder parentFolder, String resourceId, Integer indexNumber, Date lastSynchronized, User creator, Date created, User lastModifier, Date lastModified) {
    GoogleImage googleImage = new GoogleImage();
    googleImage.setCreator(creator);
    googleImage.setCreated(created);
    googleImage.setLastModifier(lastModifier);
    googleImage.setLastModified(lastModified);
    googleImage.setName(name);
    googleImage.setUrlName(urlName);
    googleImage.setParentFolder(parentFolder);
    googleImage.setResourceId(resourceId);
    googleImage.setIndexNumber(indexNumber);
    googleImage.setLastSynchronized(lastSynchronized);
    getEntityManager().persist(googleImage);
    return googleImage;
  }

  public List<GoogleImage> listByArchivedOrderByLastSynchronizedAsc(Boolean archived, int firstResult, int maxResults) {
		EntityManager entityManager = getEntityManager(); 
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<GoogleImage> criteria = criteriaBuilder.createQuery(GoogleImage.class);
    Root<GoogleImage> root = criteria.from(GoogleImage.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(GoogleImage_.archived), archived)
    );
    
    criteria.orderBy(criteriaBuilder.asc(root.get(GoogleImage_.lastSynchronized)));
    
    TypedQuery<GoogleImage> query = entityManager.createQuery(criteria);
    query.setFirstResult(firstResult);
    query.setMaxResults(maxResults);
    
    return query.getResultList();
	}
  
  public GoogleImage updateName(GoogleImage googleImage, String name, String urlName, User modifier) {
    googleImage.setName(name);
    googleImage.setUrlName(urlName);
    googleImage.setLastModifier(modifier);
    googleImage.setLastModified(new Date());
    getEntityManager().persist(googleImage);
    return googleImage;
  }
  
  /**
   * Updates GoogleImage name. This method does not update lastModifier and lastModified fields and thus should 
   * be called only by system operations (e.g. scheduler)
   * 
   * @param googleImage GoogleImage entity
   * @param name new resource name
   * @param urlName new resource urlName
   * @return Updated GoogleImage
   */
  public GoogleImage updateName(GoogleImage googleImage, String name, String urlName) {
    googleImage.setName(name);
    googleImage.setUrlName(urlName);
    getEntityManager().persist(googleImage);
    return googleImage;
  }

	public GoogleImage updateLastModified(GoogleImage googleImage, Date lastModified) {
		googleImage.setLastModified(lastModified);
    getEntityManager().persist(googleImage);
    return googleImage;
	}
	
  public GoogleImage updateParentFolder(GoogleImage googleImage, Folder parentFolder, User modifier) {
    googleImage.setParentFolder(parentFolder);
    googleImage.setLastModifier(modifier);
    googleImage.setLastModified(new Date());
    getEntityManager().persist(googleImage);
    return googleImage;
  }

	public GoogleImage updateLastSynchronized(GoogleImage googleImage, Date lastSynchronized) {
    googleImage.setLastSynchronized(lastSynchronized);
    getEntityManager().persist(googleImage);
    return googleImage;
  }
}
