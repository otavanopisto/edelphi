package fi.internetix.edelphi.domainmodel.users;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.TableGenerator;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
public class PasswordReset {

  public Long getId() {
    return id;
  }

  public String getHash() {
    return hash;
  }

  public void setHash(String hash) {
    this.hash = hash;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @Id 
  @GeneratedValue(strategy=GenerationType.TABLE, generator="PasswordReset")  
  @TableGenerator(name="PasswordReset", initialValue=1, allocationSize=100, table = "hibernate_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_next_hi_value")
  private Long id;

  @NotNull
  @Column (nullable = false)
  @NotEmpty
  private String email;

  @NotNull
  @Column (nullable = false)
  @NotEmpty
  private String hash;

}
