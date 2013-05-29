package fi.internetix.edelphi.domainmodel.panels;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PersistenceException;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import fi.internetix.edelphi.domainmodel.base.ArchivableEntity;
import fi.internetix.edelphi.domainmodel.base.ModificationTrackedEntity;
import fi.internetix.edelphi.domainmodel.users.User;

@Entity
public class PanelUserGroup implements ArchivableEntity, ModificationTrackedEntity {

  /**
   * Returns internal unique id
   * 
   * @return Internal unique id
   */
  public Long getId() {
    return id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
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

  public void setArchived(Boolean archived) {
    this.archived = archived;
  }

  public Boolean getArchived() {
    return archived;
  }

  public void setUsers(List<User> users) {
    this.users = users;
  }

  public List<User> getUsers() {
    return users;
  }
  
  public void addUser(User user) {
    if (!users.contains(user)) {
      users.add(user);
    }
    else {
      throw new PersistenceException("PanelUserGroup already contains User");
    }
  }

  public void removeUser(User user) {
    if (users.contains(user)) {
      users.remove(user);
    }
    else {
      throw new PersistenceException("PanelUserGroup does not contain User");
    }
  }

  public void setPanel(Panel panel) {
    this.panel = panel;
  }

  public Panel getPanel() {
    return panel;
  }

  public PanelStamp getStamp() {
    return stamp;
  }

  public void setStamp(PanelStamp stamp) {
    this.stamp = stamp;
  }

  @Id 
  @GeneratedValue(strategy=GenerationType.TABLE, generator="PanelUserGroup")  
  @TableGenerator(name="PanelUserGroup", initialValue=1, allocationSize=100, table = "hibernate_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_next_hi_value")
  private Long id;
  
  @NotNull
  @Column(nullable = false)
  private String name;
  
  @ManyToOne 
  private Panel panel;

  @ManyToOne
  private PanelStamp stamp;

  @ManyToMany (fetch = FetchType.LAZY)
  @JoinTable (name="__PanelUserGroupUsers", joinColumns=@JoinColumn(name="panelUserGroup_id"), inverseJoinColumns=@JoinColumn(name="user_id"))
  private List<User> users = new ArrayList<User>();
  
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