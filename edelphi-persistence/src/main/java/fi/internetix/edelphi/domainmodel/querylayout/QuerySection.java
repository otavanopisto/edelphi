package fi.internetix.edelphi.domainmodel.querylayout;

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

import org.hibernate.validator.constraints.NotEmpty;

import fi.internetix.edelphi.domainmodel.base.ArchivableEntity;
import fi.internetix.edelphi.domainmodel.base.ModificationTrackedEntity;
import fi.internetix.edelphi.domainmodel.resources.Query;
import fi.internetix.edelphi.domainmodel.users.User;

@Entity
@Cacheable
public class QuerySection implements ArchivableEntity, ModificationTrackedEntity{
  
  public Long getId() {
    return id;
  }
  
  public String getTitle() {
    return title;
  }
  
  public void setTitle(String title) {
    this.title = title;
  }
  
  public Query getQuery() {
    return query;
  }
  
  public void setQuery(Query query) {
    this.query = query;
  }
  
  public Boolean getVisible() {
    return visible;
  }
  
  public void setVisible(Boolean visible) {
    this.visible = visible;
  }
  
  public Boolean getCommentable() {
    return commentable;
  }
  
  public void setCommentable(Boolean commentable) {
    this.commentable = commentable;
  }
  
  public Boolean getViewDiscussions() {
    return viewDiscussions;
  }
  
  public void setViewDiscussions(Boolean viewDiscussions) {
    this.viewDiscussions = viewDiscussions;
  }
  
  public void setCreated(Date created) {
    this.created = created;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreator(User creator) {
    this.creator = creator;
  }

  public User getCreator() {
    return creator;
  }

  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }

  public Date getLastModified() {
    return lastModified;
  }

  public void setLastModifier(User lastModifier) {
    this.lastModifier = lastModifier;
  }

  public User getLastModifier() {
    return lastModifier;
  }
  
  @Override
  public Boolean getArchived() {
    return archived;
  }

  @Override
  public void setArchived(Boolean archived) {
    this.archived = archived;
  }

  public Integer getSectionNumber() {
    return sectionNumber;
  }

  public void setSectionNumber(Integer sectionNumber) {
    this.sectionNumber = sectionNumber;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "QuerySection")
  @TableGenerator(name = "QuerySection", allocationSize = 1, table = "hibernate_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_next_hi_value")
  private Long id;
  
  @NotNull
  @NotEmpty
  @Column (nullable=false)
  private String title;
  
  @ManyToOne
  private Query query;
  
  @NotNull
  @Column(nullable = false)
  private Integer sectionNumber;
  
  @NotNull
  @Column (nullable=false)
  private Boolean visible;
  
  @NotNull
  @Column (nullable=false)
  private Boolean commentable;
  
  @NotNull
  @Column (nullable=false)
  private Boolean viewDiscussions;
  
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
}
