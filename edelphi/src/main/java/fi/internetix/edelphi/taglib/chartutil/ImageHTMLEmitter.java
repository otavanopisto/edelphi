package fi.internetix.edelphi.taglib.chartutil;

import java.io.IOException;

import org.eclipse.birt.core.exception.BirtException;

public interface ImageHTMLEmitter {

  public String generateHTML() throws BirtException, IOException;
  
}
