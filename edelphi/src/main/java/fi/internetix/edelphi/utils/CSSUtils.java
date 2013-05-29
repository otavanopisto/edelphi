package fi.internetix.edelphi.utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.w3c.css.sac.InputSource;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSStyleSheet;

import com.steadystate.css.dom.CSSRuleListImpl;
import com.steadystate.css.dom.CSSStyleDeclarationImpl;
import com.steadystate.css.dom.CSSStyleRuleImpl;
import com.steadystate.css.dom.CSSStyleSheetImpl;
import com.steadystate.css.dom.CSSValueImpl;
import com.steadystate.css.dom.Property;
import com.steadystate.css.parser.CSSOMParser;

public class CSSUtils {

  public static CSSStyleSheet parseStylesheet(File cssFile) throws IOException {
    FileReader reader = new FileReader(cssFile);
    try {
      return parseStylesheet(new InputSource(reader));
    } finally {
      reader.close();
    }
  }

  public static CSSStyleSheet parseStylesheet(String cssFile) throws IOException {
    StringReader reader = new StringReader(cssFile);
    try {
      return parseStylesheet(new InputSource(reader));
    } finally {
      reader.close();
    }
  }

  public static CSSStyleSheet parseStylesheet(InputSource inputSource) throws IOException {
    CSSOMParser parser = new CSSOMParser();
    return parser.parseStyleSheet(inputSource, null, null);
  }

  public static CSSStyleSheet mergeStylesheets(CSSStyleSheet styleSheet1, CSSStyleSheet styleSheet2) {
    CSSStyleSheetImpl newStyleSheet = new CSSStyleSheetImpl();
    
    CSSRuleListImpl newRules = (CSSRuleListImpl) newStyleSheet.getCssRules();
    CSSRuleList rules1 = styleSheet1.getCssRules();
    CSSRuleList rules2 = styleSheet2.getCssRules();
    
    for (int i = 0, l = rules1.getLength(); i < l; i++) {
      CSSRule cssRule1 = rules1.item(i);
      if (cssRule1.getType() == CSSRule.STYLE_RULE) {
        CSSStyleRule cssStyleRule1 = (CSSStyleRule) cssRule1;
        
        CSSRule cssRule2 = getStyleRule(styleSheet2, cssStyleRule1.getSelectorText());
        if (cssRule2 != null) {
          newRules.add(mergeStyleRules(cssStyleRule1, (CSSStyleRule) cssRule2));
        } else {
          newRules.add(cssRule1);
        }
      } else {
        newRules.add(cssRule1);
      }
    }
    
    for (int i = 0, l = rules2.getLength(); i < l; i++) {
      CSSRule cssRule2 = rules2.item(i);
      if (cssRule2.getType() == CSSRule.STYLE_RULE) {
        CSSStyleRule cssStyleRule2 = (CSSStyleRule) cssRule2;
        if (getStyleRule(newStyleSheet, cssStyleRule2.getSelectorText()) == null) {
          newRules.add(cssRule2);
        }
      }
    }
    
    return newStyleSheet;
  }
  
  public static CSSStyleRule getStyleRule(CSSStyleSheet styleSheet, String selector) {
    CSSRuleList ruleList = styleSheet.getCssRules();
    for (int i = 0, l = ruleList.getLength(); i < l; i++) {
      CSSRule rule = ruleList.item(i);
      if (rule.getType() == CSSRule.STYLE_RULE) {
        CSSStyleRule cssStyleRule = (CSSStyleRule) rule;
        if (selector.equals(cssStyleRule.getSelectorText()))
          return cssStyleRule;
      }
    }
    
    return null;
  }

  public static CSSStyleRule mergeStyleRules(CSSStyleRule cssRule1, CSSStyleRule cssRule2) {
    CSSStyleDeclarationImpl style1Declaration = (CSSStyleDeclarationImpl) cssRule1.getStyle();
    CSSStyleDeclarationImpl style2Declaration = (CSSStyleDeclarationImpl) cssRule2.getStyle();

    if (style1Declaration == null && style2Declaration == null) {
      return null;
    }

    CSSStyleDeclarationImpl newStyleDeclaration = new CSSStyleDeclarationImpl();
    CSSStyleRuleImpl newStyle = new CSSStyleRuleImpl();
    
    if (style1Declaration == null) {
      newStyle.setCssText(cssRule2.getCssText());
      return newStyle;
    }

    if (style2Declaration == null) {
      newStyle.setCssText(cssRule1.getCssText());
      return newStyle;
    }
    
    newStyle.setSelectorText(cssRule1.getSelectorText());

    List<Property> style1Properties = style1Declaration.getProperties();
    List<Property> style2Properties = style2Declaration.getProperties();
    
    for (int i = 0; i < style1Properties.size(); ++i) {
      Property property = style1Properties.get(i);
      if (property.isImportant() || style2Declaration.getPropertyDeclaration(property.getName()) == null) {
        newStyleDeclaration.addProperty(new Property(property.getName(), property.getValue(), property.isImportant()));
      }
    }
    
    for (int i = 0; i < style2Properties.size(); ++i) {
      Property property = style2Properties.get(i);
      if (newStyleDeclaration.getPropertyDeclaration(property.getName()) == null) {
        newStyleDeclaration.addProperty(new Property(property.getName(), property.getValue(), property.isImportant()));
      }
    }
    
    newStyle.setStyle(newStyleDeclaration);

    return newStyle;
  }
  
  public static void addProperty(CSSStyleSheetImpl styleSheet, String selectorText, String property, String value) {
    if (!StringUtils.isBlank(value)) {
      CSSRuleListImpl rules = (CSSRuleListImpl) styleSheet.getCssRules();
      CSSStyleRuleImpl rule = (CSSStyleRuleImpl) CSSUtils.getStyleRule(styleSheet, selectorText);
      CSSValueImpl cssValue = new CSSValueImpl();
      cssValue.setCssText(value);
      if (rule == null) {
        rule = new CSSStyleRuleImpl();
        CSSStyleDeclarationImpl styleDeclaration = new CSSStyleDeclarationImpl();
        rule.setSelectorText(selectorText);
        rule.setStyle(styleDeclaration);
        styleDeclaration.addProperty(new Property(property, cssValue, false));
        rules.add(rule);
      } else {
        ((CSSStyleDeclarationImpl) rule.getStyle()).addProperty(new Property(property, cssValue, false));
      }
    }
  }

  public static String getStylesheetAsString(CSSStyleSheet styleSheet) {
    CSSRuleList cssRuleList = styleSheet.getCssRules();

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < cssRuleList.getLength(); i++) {
      CSSRule cssRule = cssRuleList.item(i);
      sb.append(cssRule.getCssText()).append("\r\n");
    }
    return sb.toString();
  }

	public static CSSStyleSheet loadStylesheet(String src) throws IOException {
		return new CSSOMParser().parseStyleSheet(new InputSource(src.toString()), null, src.toString());
	}
	
  public static String downloadCSS(String src, boolean includeImports) throws IOException {
		CSSStyleSheet styleSheet = CSSUtils.loadStylesheet(src);
		CSSStyleSheetImpl styleSheetImpl = (CSSStyleSheetImpl) styleSheet;
		
		if (includeImports)
		  styleSheetImpl.importImports(true);
		
		return getStylesheetAsString(styleSheet);
  }


}
