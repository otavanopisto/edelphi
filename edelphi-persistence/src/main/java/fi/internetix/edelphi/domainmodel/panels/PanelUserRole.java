package fi.internetix.edelphi.domainmodel.panels;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

import fi.internetix.edelphi.domainmodel.users.UserRole;

@Entity
@PrimaryKeyJoinColumn(name="id")
public class PanelUserRole extends UserRole {
}
