package fi.internetix.edelphi.domainmodel.panels;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import fi.internetix.edelphi.domainmodel.base.ArchivableEntity;
import fi.internetix.edelphi.domainmodel.base.EmailMessage;
import fi.internetix.edelphi.domainmodel.base.ModificationTrackedEntity;
import fi.internetix.edelphi.domainmodel.resources.Query;
import fi.internetix.edelphi.domainmodel.users.User;

@Entity
public class PanelInvitation implements ArchivableEntity, ModificationTrackedEntity {

  /**
   * Returns internal unique id
   * 
   * @return Internal unique id
   */
  public Long getId() {
    return id;
  }

  public void setRole(PanelUserRole role) {
    this.role = role;
  }

  public PanelUserRole getRole() {
    return role;
  }

  public void setPanel(Panel panel) {
    this.panel = panel;
  }

  public Panel getPanel() {
    return panel;
  }

  public void setQuery(Query query) {
    this.query = query;
  }

  public Query getQuery() {
    return query;
  }

  public void setArchived(Boolean archived) {
    this.archived = archived;
  }

  public Boolean getArchived() {
    return archived;
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

  public void setEmail(String email) {
    this.email = email;
  }

  public String getEmail() {
    return email;
  }

  @Transient
  public String getObfuscatedEmail() {
    int i = email == null ? 0 : email.indexOf('@');
    if (email == null || i <= 2) {
      return email;
    }
    return (i < 9 ? email.substring(0, 3) : email.substring(0, 8)) + "..." + email.substring(i);
  }

  public void setEmailMessage(EmailMessage emailMessage) {
    this.emailMessage = emailMessage;
  }

  public EmailMessage getEmailMessage() {
    return emailMessage;
  }

  public void setHash(String hash) {
    this.hash = hash;
  }

  public String getHash() {
    return hash;
  }

  public void setState(PanelInvitationState state) {
    this.state = state;
  }

  public PanelInvitationState getState() {
    return state;
  }

  @Id 
  @GeneratedValue(strategy=GenerationType.TABLE, generator="PanelInvitation")  
  @TableGenerator(name="PanelInvitation", initialValue=1, allocationSize=100, table = "hibernate_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_next_hi_value")
  private Long id;
  
  @ManyToOne
  private Panel panel;

  @ManyToOne
  private Query query;

  @NotNull
  @Column (nullable = false)
  @NotEmpty
  private String email;

  @NotNull
  @Column (nullable = false)
  @NotEmpty
  private String hash;

  @ManyToOne
  private PanelUserRole role;

  @NotNull
  @Column (nullable = false)
  @Enumerated (EnumType.STRING)
  private PanelInvitationState state;

  @ManyToOne
  private EmailMessage emailMessage;
  
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
  private Boolean archived = Boolean.FALSE;
}
