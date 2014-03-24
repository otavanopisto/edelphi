/*******************************************************************************
 * Copyright (c) 2006 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package fi.internetix.edelphi.taglib.chartutil;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.birt.chart.model.Chart;
import org.eclipse.birt.core.exception.BirtException;

import fi.internetix.edelphi.pages.panel.admin.report.util.ChartModelProvider;

public class PngImageHTMLEmitter implements ImageHTMLEmitter {

  public PngImageHTMLEmitter(Chart chartModel, int width, int height) {
    super();
    this.chartModel = chartModel;
    this.width = width;
    this.height = height;
  }

  public String generateHTML() throws BirtException {
    byte[] chartData = ChartModelProvider.getChartData(chartModel, "PNG");
    
    StringBuilder dataUrlBuilder = new StringBuilder();
    dataUrlBuilder.append("data:image/png;base64,");
    dataUrlBuilder.append(Base64.encodeBase64String(chartData));
    
    StringBuilder html = new StringBuilder();
    html.append(String.format("<img src=\"%s\" style=\"width: %s; height: %s;\" width=\"%s\" height=\"%s\" border=\"%s\">", dataUrlBuilder.toString(), width, height, width, height,
        0));
    html.append("<br/>");
    return html.toString();
  }

  private Chart chartModel;
  private int width;
  private int height;
}
