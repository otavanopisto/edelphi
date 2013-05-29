package fi.internetix.edelphi.domainmodel.base;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.validation.constraints.NotNull;

import fi.internetix.edelphi.domainmodel.users.UserRole;

@Entity
@PrimaryKeyJoinColumn(name="id")
public class SystemUserRole extends UserRole {
  
  public void setType(SystemUserRoleType type) {
    this.type = type;
  }

  public SystemUserRoleType getType() {
    return type;
  }

  @NotNull
  @Column (nullable = false)
  @Enumerated (EnumType.STRING)
  private SystemUserRoleType type;
}
