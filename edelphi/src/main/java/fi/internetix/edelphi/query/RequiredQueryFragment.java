package fi.internetix.edelphi.query;

import java.util.HashMap;
import java.util.Map;

public class RequiredQueryFragment {

  public RequiredQueryFragment(String name) {
    this.name = name;
  }
  
  public void addAttribute(String name, String value) {
    attributes.put(name, value);
  }
  
  public String getName() {
    return name;
  }
  
  public Map<String, String> getAttributes() {
    return attributes;
  }
  
  private String name;
  private Map<String, String> attributes = new HashMap<String, String>();
}
