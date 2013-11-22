package fi.internetix.edelphi.domainmodel.users;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PersistenceException;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;

import fi.internetix.edelphi.domainmodel.base.ArchivableEntity;
import fi.internetix.edelphi.domainmodel.base.ModificationTrackedEntity;

@Entity
@Indexed
@Cacheable
@Inheritance(strategy=InheritanceType.JOINED)
public class User implements ArchivableEntity, ModificationTrackedEntity {

  /**
   * Returns internal unique id
   * 
   * @return Internal unique id
   */
  public Long getId() {
    return id;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public String getNickname() {
    return nickname;
  }

  public void setDefaultEmail(UserEmail defaultEmail) {
    this.defaultEmail = defaultEmail;
  }

  public UserEmail getDefaultEmail() {
    return defaultEmail;
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

  public void setLastLogin(Date lastLogin) {
    this.lastLogin = lastLogin;
  }

  public Date getLastLogin() {
    return lastLogin;
  }

  public void setArchived(Boolean archived) {
    this.archived = archived;
  }

  public Boolean getArchived() {
    return archived;
  }

  @Transient
  public String getFullName() {
    return getFullName(true, true);
  }

  @Transient
  public String getFullName(boolean lastNameFirst) {
    return getFullName(lastNameFirst, true);
  }

  @Transient
  public String getFullName(boolean lastNameFirst, boolean useMailIfNull) {
    String fn = getFirstName();
    String ln = getLastName();
    String name = fn == null ? ln == null ? null : ln : ln == null ? fn : lastNameFirst ? ln + ", " + fn : fn + " " + ln;
    return name == null && useMailIfNull ? getDefaultEmailAsString() : name; 
  }
  
  @Transient
  public String getDefaultEmailAsString() {
    return getDefaultEmail() == null ? null : getDefaultEmail().getAddress();
  }

  @Transient
  @Field(index = Index.YES, store = Store.NO, analyze = Analyze.NO)
  public String getFullNameSearch() {
    return StringUtils.lowerCase(getFullName(false));
  }
  
  public void addEmail(UserEmail userEmail) {
    if (!userEmail.getUser().equals(this)) {
      throw new PersistenceException("User/UserEmail discrepancy");
    }
    if (!emails.contains(userEmail)) {
      emails.add(userEmail);
    }
    else {
      throw new PersistenceException("UserEmail to add already in User");
    }
  }

  public void removeEmail(UserEmail userEmail) {
    if (emails.contains(userEmail)) {
      emails.remove(userEmail);
    }
    else {
      throw new PersistenceException("UserEmail to remove not in User");
    }
  }
  
  @SuppressWarnings("unused")
  private void setEmails(List<UserEmail> emails) {
    this.emails = emails;
  }

  public List<UserEmail> getEmails() {
    return emails;
  }

  @Id
  @DocumentId
  @GeneratedValue(strategy=GenerationType.TABLE, generator="User")  
  @TableGenerator(name="User", initialValue=1, allocationSize=100, table = "hibernate_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_next_hi_value")
  private Long id;
  
  @Field(index = Index.YES, store = Store.NO)
  private String firstName;

  @Field(index = Index.YES, store = Store.NO)
  private String lastName;
  
  private String nickname;
  
  @ManyToOne
  private UserEmail defaultEmail;

  @OneToMany (fetch = FetchType.LAZY)
  @JoinColumn (name="user_id")
  @IndexedEmbedded
  private List<UserEmail> emails = new ArrayList<UserEmail>();

  @NotNull
  @Column(nullable = false)
  @Field(index = Index.YES, store = Store.NO)
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

  @Temporal (value=TemporalType.TIMESTAMP)
  private Date lastLogin;

}
