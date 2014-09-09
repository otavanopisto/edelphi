package fi.internetix.edelphi.domainmodel.base;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
public class AuthSourceSetting {

  public Long getId() {
    return id;
  }
  
  public AuthSource getAuthSource() {
    return authSource;
  }

  public void setAuthSource(AuthSource authSource) {
    this.authSource = authSource;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  @Id 
  @GeneratedValue(strategy=GenerationType.TABLE, generator="AuthSourceSetting")  
  @TableGenerator(name="AuthSourceSetting", initialValue=1, allocationSize=100, table = "hibernate_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_next_hi_value")
  private Long id;

  @ManyToOne
  private AuthSource authSource;

  @NotBlank
  @NotEmpty
  @Column (name = "settingKey", nullable = false)
  private String key;

  @NotBlank
  @NotEmpty
  @Column (nullable = false)
  private String value;

}
