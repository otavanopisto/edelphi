package fi.internetix.edelphi.domainmodel.resources;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name="id")
public class Query extends Resource {
  
  public Query() {
    setType(ResourceType.QUERY);
  }
  
  public QueryState getState() {
    return state;
  }
  
  public void setState(QueryState state) {
    this.state = state;
  }
  
  public Date getCloses() {
    return closes;
  }
  
  public void setCloses(Date closes) {
    this.closes = closes;
  }
  
  public Boolean getAllowEditReply() {
    return allowEditReply;
  }
  
  public void setAllowEditReply(Boolean allowEditReply) {
    this.allowEditReply = allowEditReply;
  }
  
  private Date closes;
  
  private Boolean allowEditReply;

  @Column (nullable = false)
  @Enumerated (EnumType.STRING)
  private QueryState state;
}
