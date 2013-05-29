package fi.internetix.edelphi.query;

public enum QueryOptionEditor {
  
  TEXT            (QueryOptionDataType.TEXT),
  MEMO            (QueryOptionDataType.TEXT),
  INTEGER         (QueryOptionDataType.INTEGER),
  FLOAT           (QueryOptionDataType.FLOAT),
  BOOLEAN         (QueryOptionDataType.BOOLEAN),
  SPREADSHEET     (QueryOptionDataType.MAP),
  TIME_SERIE_DATA (QueryOptionDataType.MAP),
  OPTION_SET      (QueryOptionDataType.LIST),
  TIMELINE_TYPE   (QueryOptionDataType.INTEGER),
  SCALE1D_TYPE    (QueryOptionDataType.INTEGER),
  SCALE2D_TYPE    (QueryOptionDataType.INTEGER), 
  FORM_FIELDS     (QueryOptionDataType.JSON_SERIALIZED), 
  HIDDEN          (QueryOptionDataType.TEXT);
  
  private QueryOptionEditor(QueryOptionDataType dataType) {
    this.dataType = dataType;
  }
  
  public QueryOptionDataType getDataType() {
    return dataType;
  }
  
  private QueryOptionDataType dataType;
}