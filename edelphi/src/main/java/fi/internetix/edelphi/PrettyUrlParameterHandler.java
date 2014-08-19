package fi.internetix.edelphi;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import fi.internetix.smvc.dispatcher.ParameterHandler;

public class PrettyUrlParameterHandler implements ParameterHandler {
  
  public PrettyUrlParameterHandler(HttpServletRequest request) {
    this.request = request;
  }
  
  public void addParameter(String name, String value) {
    Set<String> values = parameters.get(name);
    if (values == null) {
      values = new HashSet<String>();
      parameters.put(name, values);
    }

    values.add(value);
  }

  public String getParameter(String name) {
    Set<String> values = parameters.get(name);
    if ((values == null) || (values.size() < 1)) {
      return request.getParameter(name);
    }
    return values.iterator().next();
  }
  
  @Override
  public String[] getParameters(String name) {
    return request.getParameterValues(name);
  }
  
  private HashMap<String, Set<String>> parameters = new HashMap<String, Set<String>>();
  private HttpServletRequest request;
}
