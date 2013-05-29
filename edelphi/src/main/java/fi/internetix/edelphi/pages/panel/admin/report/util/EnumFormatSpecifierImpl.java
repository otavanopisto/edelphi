package fi.internetix.edelphi.pages.panel.admin.report.util;

import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.birt.chart.model.attribute.impl.NumberFormatSpecifierImpl;

import com.ibm.icu.util.ULocale;

public class EnumFormatSpecifierImpl extends NumberFormatSpecifierImpl {

  public EnumFormatSpecifierImpl(Map<Double, String> names) {
    this.names = names;
  }
  
  @Override
  public String format(double dValue, Locale lo) {
    return getValue(dValue, lo);
  }
  
  @Override
  public String format(double dValue, ULocale lo) {
    return getValue(dValue, lo.toLocale());
  }
  
  @Override
  public String format(Number number, ULocale lo) {
    if (number == null)
      return super.format(number, lo);
    
    return getValue(number.doubleValue(), lo.toLocale());
  }
  
  private String getValue(double value, Locale lo) {
    String name = names.get(value);
    if (StringUtils.isBlank(name)) 
      name = "";
    
    return name;
  }
  
  private Map<Double, String> names;
}
