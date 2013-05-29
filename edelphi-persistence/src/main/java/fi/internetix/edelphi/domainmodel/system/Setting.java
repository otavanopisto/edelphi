package fi.internetix.edelphi.domainmodel.system;

import java.lang.Long;
import javax.persistence.*;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Entity implementation class for Entity: Setting
 */
@Entity
public class Setting {

	
	public Long getId() {
		return this.id;
	}
	
	public SettingKey getKey() {
    return key;
  }
	
	public void setKey(SettingKey key) {
    this.key = key;
  }
	
	public String getValue() {
    return value;
  }
	
	public void setValue(String value) {
    this.value = value;
  }

	@Id
  @GeneratedValue(strategy=GenerationType.TABLE, generator="Setting")  
  @TableGenerator(name="Setting", initialValue=1, allocationSize=100)
	private Long id;
	
	@ManyToOne
  private SettingKey key;
	
	@NotEmpty
	private String value;
}
