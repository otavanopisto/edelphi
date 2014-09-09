package fi.internetix.edelphi.pages.panel.admin.report.util;

public class TextFieldAnswerBean extends FormFieldAnswerBean {

  public TextFieldAnswerBean(String fieldType, String caption, String value, Integer fieldIndex, Long replyId) {
    super(fieldType, fieldIndex, replyId);
    this.caption = caption;
    this.value = value;
  }

  public String getCaption() {
    return caption;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  private String caption;
  private String value;

}
