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
import fi.internetix.edelphi.domainmodel.base.Delfoi;
import fi.internetix.edelphi.domainmodel.base.ModificationTrackedEntity;
import fi.internetix.edelphi.domainmodel.resources.Folder;
import fi.internetix.edelphi.domainmodel.users.User;

@Entity
public class Panel implements ArchivableEntity, ModificationTrackedEntity {

  /**
   * Returns internal unique id
   * 
   * @return Internal unique id
   */
  public Long getId() {
    return id;
  }

  public void setRootFolder(Folder rootFolder) {
    this.rootFolder = rootFolder;
  }

  public Folder getRootFolder() {
    return rootFolder;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
  
  @Transient
  public String getUrlName() {
    return rootFolder.getUrlName();
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  public void setDelfoi(Delfoi delfoi) {
    this.delfoi = delfoi;
  }

  public Delfoi getDelfoi() {
    return delfoi;
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

  public void setDefaultPanelUserRole(PanelUserRole defaultPanelUserRole) {
    this.defaultPanelUserRole = defaultPanelUserRole;
  }

  public PanelUserRole getDefaultPanelUserRole() {
    return defaultPanelUserRole;
  }

  public void setAccessLevel(PanelAccessLevel accessLevel) {
    this.accessLevel = accessLevel;
  }

  public PanelAccessLevel getAccessLevel() {
    return accessLevel;
  }

  public void setState(PanelState state) {
    this.state = state;
  }

  public PanelState getState() {
    return state;
  }

  public void setInvitationTemplate(String invitationTemplate) {
    this.invitationTemplate = invitationTemplate;
  }

  public String getInvitationTemplate() {
    return invitationTemplate;
  }

  @Transient
  public String getFullPath() {
    return getRootFolder().getFullPath();
  }

  public PanelStamp getCurrentStamp() {
    return currentStamp;
  }

  public void setCurrentStamp(PanelStamp currentStamp) {
    this.currentStamp = currentStamp;
  }

  @Id 
  @GeneratedValue(strategy=GenerationType.TABLE, generator="Panel")  
  @TableGenerator(name="Panel", initialValue=1, allocationSize=100, table = "hibernate_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_next_hi_value")
  private Long id;
  
  @ManyToOne
  private Delfoi delfoi;
  
  @NotNull
  @Column (nullable = false)
  @NotEmpty
  private String name;
  
  @Column (length=1073741824)
  private String description;
  
  @Column (length=1073741824)
  private String invitationTemplate;  

  @ManyToOne
  private PanelUserRole defaultPanelUserRole;

  @ManyToOne
  private Folder rootFolder;

  @ManyToOne
  private PanelStamp currentStamp;

  @NotNull
  @Column (nullable = false)
  @Enumerated (EnumType.STRING)
  private PanelAccessLevel accessLevel;

  @NotNull
  @Column (nullable = false)
  @Enumerated (EnumType.STRING)
  private PanelState state;

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
