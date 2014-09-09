package fi.internetix.edelphi.domainmodel.querylayout;

import java.lang.Long;
import javax.persistence.*;

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public class QueryPageTemplateSetting {

	
	public Long getId() {
		return this.id;
	}
	
	public QueryPageSettingKey getKey() {
    return key;
  }
	
	public void setKey(QueryPageSettingKey key) {
    this.key = key;
  }
	
	public QueryPageTemplate getQueryPageTemplate() {
    return queryPageTemplate;
  }
	
	public void setQueryPageTemplate(QueryPageTemplate queryPageTemplate) {
    this.queryPageTemplate = queryPageTemplate;
  }

	@Id
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "QueryPageTemplateSetting")
  @TableGenerator(name = "QueryPageTemplateSetting", allocationSize = 1, table = "hibernate_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_next_hi_value")
  private Long id;
	
	@ManyToOne
  private QueryPageSettingKey key;
	
	@ManyToOne
  private QueryPageTemplate queryPageTemplate;
}
