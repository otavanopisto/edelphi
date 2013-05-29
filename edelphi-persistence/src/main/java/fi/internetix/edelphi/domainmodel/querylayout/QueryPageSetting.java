package fi.internetix.edelphi.domainmodel.querylayout;

import java.lang.Long;
import javax.persistence.*;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
public class QueryPageSetting {

	
	public Long getId() {
		return this.id;
	}
	
	public QueryPageSettingKey getKey() {
    return key;
  }
	
	public void setKey(QueryPageSettingKey key) {
    this.key = key;
  }
	
	public QueryPage getQueryPage() {
    return queryPage;
  }
	
	public void setQueryPage(QueryPage queryPage) {
    this.queryPage = queryPage;
  }
	
	public String getValue() {
    return value;
  }
	
	public void setValue(String value) {
    this.value = value;
  }

	@Id
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "QueryPageSetting")
  @TableGenerator(name = "QueryPageSetting", allocationSize = 1, table = "hibernate_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_next_hi_value")
  private Long id;
	
	@ManyToOne
  private QueryPageSettingKey key;
	
	@ManyToOne
  private QueryPage queryPage;
	
	@NotEmpty
	@Column (length=1073741824)
	private String value;
}
