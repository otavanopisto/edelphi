package fi.internetix.edelphi.domainmodel.querydata;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name="id")
public class QueryQuestionBinaryAnswer extends QueryQuestionAnswer {

  public void setData(byte[] data) {
    this.data = data;
  }

  public byte[] getData() {
    return data;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public String getContentType() {
    return contentType;
  }

  @Column (length=1073741824)
  private byte[] data;
  
  private String contentType;
}
