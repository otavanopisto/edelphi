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

import java.io.IOException;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.birt.chart.model.Chart;
import org.eclipse.birt.core.exception.BirtException;

import fi.internetix.edelphi.pages.panel.admin.report.util.ChartModelProvider;

public class SvgImageHTMLEmitter implements ImageHTMLEmitter {

  public SvgImageHTMLEmitter(Chart chartModel, int width, int height) {
    super();
    this.chartModel = chartModel;
    this.width = width;
    this.height = height;
  }

  public String generateHTML() throws IOException, BirtException {
    byte[] chartData = ChartModelProvider.getChartData(chartModel, "SVG");

    StringBuilder dataUrlBuilder = new StringBuilder();
    dataUrlBuilder.append("data:image/svg+xml;base64,");
    dataUrlBuilder.append(Base64.encodeBase64String(chartData));
    
    StringBuilder html = new StringBuilder();
    html.append("<object type=\"image/svg+xml\"")
      .append(" data=\"").append(dataUrlBuilder.toString()).append('"')
      .append(" width=\"").append(width).append('"')
      .append(" height=\"").append(height).append('"')
      .append(" style=\"display: block\"")
      .append("></object>");
    return html.toString();
  }

  private Chart chartModel;
  private int width;
  private int height;
}
