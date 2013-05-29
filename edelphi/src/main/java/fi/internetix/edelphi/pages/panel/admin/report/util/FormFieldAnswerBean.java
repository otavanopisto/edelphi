package fi.internetix.edelphi.pages.panel.admin.report.util;

public class FormFieldAnswerBean {

  public FormFieldAnswerBean(String fieldType, Integer fieldIndex) {
    this(fieldType, fieldIndex, null);
  }

  public FormFieldAnswerBean(String fieldType, Integer fieldIndex, Long replyId) {
    this.fieldType = fieldType;
    this.fieldIndex = fieldIndex;
    this.replyId = replyId;
  }

  public String getFieldType() {
    return fieldType;
  }

  public Integer getFieldIndex() {
    return fieldIndex;
  }

  public Long getReplyId() {
    return replyId;
  }

  private final String fieldType;
  private Integer fieldIndex;
  private Long replyId;

}
