package fi.internetix.edelphi.query;

public class QueryOption {

  public QueryOption(QueryOptionType type, String name, String localeKey, QueryOptionEditor editor, boolean editableWithAnswers) {
    this.name = name;
    this.localeKey = localeKey;
    this.editor = editor;
    this.type = type;
    this.editableWithAnswers = editableWithAnswers;
  }
  
  public String getName() {
    return name;
  }
  
  public String getLocaleKey() {
    return localeKey;
  }
  
  public QueryOptionType getType() {
    return type;
  }
  
  public QueryOptionEditor getEditor() {
    return editor;
  }
  
  public boolean isEditableWithAnswers() {
    return editableWithAnswers;
  }
  
  private String name;
  private String localeKey;
  private QueryOptionType type;
  private QueryOptionEditor editor;
  private boolean editableWithAnswers;
}