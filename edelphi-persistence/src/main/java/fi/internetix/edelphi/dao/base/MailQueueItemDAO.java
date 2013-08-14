package fi.internetix.edelphi.dao.base;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.edelphi.domainmodel.base.EmailMessage;
import fi.internetix.edelphi.domainmodel.base.MailQueueItem;
import fi.internetix.edelphi.domainmodel.base.MailQueueItemState;
import fi.internetix.edelphi.domainmodel.base.MailQueueItem_;
import fi.internetix.edelphi.domainmodel.users.User;

public class MailQueueItemDAO extends GenericDAO<MailQueueItem> {

  public MailQueueItem create(MailQueueItemState state, EmailMessage emailMessage, User creator) {
    Date now = new Date();
    
    MailQueueItem mailQueueItem = new MailQueueItem();
    mailQueueItem.setState(state);
    mailQueueItem.setEmailMessage(emailMessage);
    mailQueueItem.setCreated(now);
    mailQueueItem.setCreator(creator);
    mailQueueItem.setLastModified(now);
    mailQueueItem.setLastModifier(creator);
    mailQueueItem.setArchived(Boolean.FALSE);
    
    getEntityManager().persist(mailQueueItem);
    return mailQueueItem;
  }
  
  public List<MailQueueItem> listByStateAndArchived(MailQueueItemState state, Boolean archived) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<MailQueueItem> criteria = criteriaBuilder.createQuery(MailQueueItem.class);
    Root<MailQueueItem> root = criteria.from(MailQueueItem.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(MailQueueItem_.state), state),
        criteriaBuilder.equal(root.get(MailQueueItem_.archived), archived)
      )
    );

    return entityManager.createQuery(criteria).getResultList();
  }

  public MailQueueItem updateState(MailQueueItem mailQueueItem, MailQueueItemState state, User modifier) {
    mailQueueItem.setState(state);
    mailQueueItem.setLastModifier(modifier);
    mailQueueItem.setLastModified(new Date());
    
    getEntityManager().persist(mailQueueItem);
    return mailQueueItem;
    
  }
  
}
