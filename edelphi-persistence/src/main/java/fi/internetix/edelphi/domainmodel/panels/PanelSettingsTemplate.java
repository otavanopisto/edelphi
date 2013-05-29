package fi.internetix.edelphi.domainmodel.panels;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import fi.internetix.edelphi.domainmodel.base.ArchivableEntity;

@Entity
public class PanelSettingsTemplate implements ArchivableEntity {

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

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
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

  @Override
  public Boolean getArchived() {
    return archived;
  }

  @Override
  public void setArchived(Boolean archived) {
    this.archived = archived;
  }

  @Id 
  @GeneratedValue(strategy=GenerationType.TABLE, generator="PanelSettingsTemplate")  
  @TableGenerator(name="PanelSettingsTemplate", initialValue=1, allocationSize=100, table = "hibernate_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_next_hi_value")
  private Long id;
  
  @NotNull
  @Column (nullable = false)
  @NotEmpty
  private String name;
  
  @Column (length=1073741824)
  private String description;
  
  @ManyToOne
  private PanelUserRole defaultPanelUserRole;

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
}
