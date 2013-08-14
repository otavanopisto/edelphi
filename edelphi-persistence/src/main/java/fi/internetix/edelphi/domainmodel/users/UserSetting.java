package fi.internetix.edelphi.domainmodel.users;

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

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
public class UserSetting {

  public Long getId() {
    return id;
  }
  
  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public UserSettingKey getKey() {
    return key;
  }

  public void setKey(UserSettingKey key) {
    this.key = key;
  }

  @Id 
  @GeneratedValue(strategy=GenerationType.TABLE, generator="UserSetting")  
  @TableGenerator(name="UserSetting", initialValue=1, allocationSize=100, table = "hibernate_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_next_hi_value")
  private Long id;

  @ManyToOne
  private User user;

  @NotNull
  @Column (name = "settingKey", nullable = false)
  @Enumerated (EnumType.STRING)
  private UserSettingKey key;

  @NotBlank
  @NotEmpty
  @Column (nullable = false)
  private String value;

}
