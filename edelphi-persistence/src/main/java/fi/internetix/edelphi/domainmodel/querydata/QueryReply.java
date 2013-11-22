package fi.internetix.edelphi.domainmodel.querydata;

import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import fi.internetix.edelphi.domainmodel.base.ArchivableEntity;
import fi.internetix.edelphi.domainmodel.base.ModificationTrackedEntity;
import fi.internetix.edelphi.domainmodel.panels.PanelStamp;
import fi.internetix.edelphi.domainmodel.resources.Query;
import fi.internetix.edelphi.domainmodel.users.User;

@Entity
@Cacheable
public class QueryReply implements ArchivableEntity, ModificationTrackedEntity {

  /**
   * Returns internal unique id
   * 
   * @return Internal unique id
   */
  public Long getId() {
    return id;
  }

  public void setQuery(Query query) {
    this.query = query;
  }

  public Query getQuery() {
    return query;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public User getUser() {
    return user;
  }

  public void setArchived(Boolean archived) {
    this.archived = archived;
  }

  public Boolean getArchived() {
    return archived;
  }

  public void setComplete(Boolean complete) {
    this.complete = complete;
  }

  public Boolean getComplete() {
    return complete;
  }

  public void setCreator(User creator) {
    this.creator = creator;
  }

  public User getCreator() {
    return creator;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public Date getCreated() {
    return created;
  }

  public void setLastModifier(User lastModifier) {
    this.lastModifier = lastModifier;
  }

  public User getLastModifier() {
    return lastModifier;
  }

  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }

  public Date getLastModified() {
    return lastModified;
  }

  public PanelStamp getStamp() {
    return stamp;
  }

  public void setStamp(PanelStamp stamp) {
    this.stamp = stamp;
  }

  @Id 
  @GeneratedValue(strategy=GenerationType.TABLE, generator="QueryReply")  
  @TableGenerator(name="QueryReply", initialValue=1, allocationSize=100, table = "hibernate_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_next_hi_value")
  private Long id;
  
  @ManyToOne
  private Query query;
  
  @ManyToOne
  private User user;
  
  @ManyToOne
  private PanelStamp stamp;
  
  @NotNull
  @Column(nullable = false)
  private Boolean archived = Boolean.FALSE;
  
  @ManyToOne 
  private User creator;
  
  @NotNull
  @Column (updatable=false, nullable=false)
  @Temporal (value=TemporalType.TIMESTAMP)
  private Date created;
  
  @ManyToOne  
  private User lastModifier;
  
  @NotNull
  @Column (nullable=false)
  @Temporal (value=TemporalType.TIMESTAMP)
  private Date lastModified;

  @NotNull
  @Column(nullable = false)
  private Boolean complete = Boolean.FALSE;
}
