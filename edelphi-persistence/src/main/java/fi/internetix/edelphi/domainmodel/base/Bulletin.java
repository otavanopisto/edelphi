package fi.internetix.edelphi.domainmodel.base;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import fi.internetix.edelphi.domainmodel.users.User;

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public class Bulletin implements ArchivableEntity, ModificationTrackedEntity {

  public Long getId() {
    return id;
  }
  
  public String getTitle() {
    return title;
  }
  
  public void setTitle(String title) {
    this.title = title;
  }
  
  public String getMessage() {
    return message;
  }
  
  public void setMessage(String message) {
    this.message = message;
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

  public void setArchived(Boolean archived) {
    this.archived = archived;
  }

  public Boolean getArchived() {
    return archived;
  }
  
  @Transient
  public String getSummary() {
    if (StringUtils.isNotBlank(getMessage())) {
      String plainMessage = StringEscapeUtils.unescapeHtml(getMessage().replaceAll("\\<.*?>",""));
      if (StringUtils.isNotBlank(plainMessage)) {
        if (plainMessage.length() >= 255) {
          return plainMessage.substring(0, 252) + "...";
        } else {
          return plainMessage;
        }
      }
    }
    
    return null;
  }
  
  @Id 
  @GeneratedValue(strategy=GenerationType.TABLE, generator="Bulletin")  
  @TableGenerator(name="Bulletin", initialValue=1, allocationSize=100, table = "hibernate_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_next_hi_value")
  private Long id;

  @NotBlank
  @NotEmpty
  @Column (nullable = false)
  private String title;

  @NotBlank
  @NotEmpty
  @Column (nullable = false, length = 1073741824)
  private String message;

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
