package fi.internetix.edelphi.taglib;

import org.apache.taglibs.standard.tag.common.core.ParamSupport;

public class ParamTag extends ParamSupport {

  private static final long serialVersionUID = -898563655662883470L;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
