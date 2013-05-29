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
import javax.validation.constraints.NotNull;

import fi.internetix.edelphi.domainmodel.base.ArchivableEntity;
import fi.internetix.edelphi.domainmodel.base.ModificationTrackedEntity;
import fi.internetix.edelphi.domainmodel.users.User;

@Entity
public class PanelUser implements ArchivableEntity, ModificationTrackedEntity {

  /**
   * Returns internal unique id
   * 
   * @return Internal unique id
   */
  public Long getId() {
    return id;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public User getUser() {
    return user;
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

  public void setJoinType(PanelUserJoinType joinType) {
    this.joinType = joinType;
  }

  public PanelUserJoinType getJoinType() {
    return joinType;
  }

  public PanelStamp getStamp() {
    return stamp;
  }

  public void setStamp(PanelStamp stamp) {
    this.stamp = stamp;
  }

  @Id 
  @GeneratedValue(strategy=GenerationType.TABLE, generator="PanelUser")  
  @TableGenerator(name="PanelUser", initialValue=1, allocationSize=100, table = "hibernate_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_next_hi_value")
  private Long id;
  
  @ManyToOne
  private Panel panel;
  
  @ManyToOne
  private User user;
  
  @ManyToOne
  private PanelUserRole role;

  @NotNull
  @Column (nullable = false)
  @Enumerated (EnumType.STRING)
  private PanelUserJoinType joinType;
  
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
}
