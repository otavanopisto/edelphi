/*******************************************************************************
 * Copyright (c) 2007 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package fi.internetix.edelphi.taglib;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.taglibs.standard.tag.common.core.ParamParent;
import org.eclipse.birt.chart.model.Chart;
import org.eclipse.birt.chart.model.attribute.Bounds;

import fi.internetix.edelphi.EdelfoiStatusCode;
import fi.internetix.edelphi.dao.panels.PanelStampDAO;
import fi.internetix.edelphi.dao.querylayout.QueryPageDAO;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.panels.PanelStamp;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;
import fi.internetix.edelphi.pages.panel.admin.report.util.QueryReportChartContext;
import fi.internetix.edelphi.pages.panel.admin.report.util.QueryReportPageController;
import fi.internetix.edelphi.pages.panel.admin.report.util.QueryReportPageProvider;
import fi.internetix.edelphi.taglib.chartutil.ChartImageManager;
import fi.internetix.edelphi.taglib.chartutil.ChartWebHelper;
import fi.internetix.edelphi.taglib.chartutil.ImageHTMLEmitter;
import fi.internetix.edelphi.utils.ResourceUtils;
import fi.internetix.smvc.SmvcRuntimeException;

/**
 * 
 * Tag for generating chart image and HTML
 * 
 */
public class QueryPageChartTag extends BodyTagSupport implements ParamParent {

  private static final long serialVersionUID = 8922643273976526624L;

  private double width;

	private double height;

	private String renderURL;

	// TODO: output type
	private String output = "PNG";

	private Long queryPageId;
	
	private Long stampId;

  private Map<String, String> parameters;
	
  public int doEndTag() throws JspException {
    try {
      if (!ChartWebHelper.checkOutputType(output)) {
        throw new SmvcRuntimeException(EdelfoiStatusCode.REPORTING_ERROR, "Unknown output format.");
      }
      
      // TODO: Localization
      Locale locale = Locale.getDefault();

      QueryPageDAO queryPageDAO = new QueryPageDAO();
      PanelStampDAO panelStampDAO = new PanelStampDAO();
      QueryPage queryPage = queryPageDAO.findById(queryPageId);
      PanelStamp panelStamp = null;
      if (stampId == null) {
        Panel panel = ResourceUtils.getResourcePanel(queryPage.getQuerySection().getQuery()); 
        panelStamp = panel.getCurrentStamp();
        stampId = panelStamp.getId();
      }
      else {
        panelStamp = panelStampDAO.findById(stampId);
      }
      QueryReportPageController queryReportPageController = QueryReportPageProvider.getController(queryPage.getPageType());
      QueryReportChartContext queryReportChartContext = new QueryReportChartContext(locale, parameters, panelStamp);
      Chart chartModel = queryReportPageController.constructChart(queryReportChartContext, queryPage);

      if (chartModel != null) {
        // Set size in chart model
        Bounds bounds = chartModel.getBlock().getBounds();
        bounds.setWidth(width);
        bounds.setHeight(height);
      } else {
        throw new SmvcRuntimeException(EdelfoiStatusCode.REPORTING_ERROR, "ChartModel was not found.");
      }

      ChartImageManager imageManager = new ChartImageManager(chartModel, output);

      String elementId = "";
      String imageUrl = this.pageContext.getServletContext().getContextPath() + "/report/viewchart.binary";
      
      String imageMap = imageManager.getImageMap();
      
      pageContext.getOut().println(createEmitter(chartModel, elementId, imageUrl, imageMap).generateHTML());
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return EVAL_PAGE;
  }

  private ImageHTMLEmitter createEmitter(Chart chartModel, String elementId, String imageSrc, String imageMap) {
    ImageHTMLEmitter emitter = new ImageHTMLEmitter(
        this.queryPageId,
        this.stampId,
        elementId,
        this.output, 
        imageSrc,
        chartModel != null ? chartModel.getTitle().getLabel().getCaption().getValue() : "",
        (int) this.width,
        (int) this.height, 
        imageMap,
        parameters);
    return emitter;
  }

  /**
   * @param width
   *          the width to set
   */
  public void setWidth(double width) {
    this.width = width;
  }

  /**
   * @return the width
   */
  public double getWidth() {
    return width;
  }

  /**
   * @param height
   *          the height to set
   */
  public void setHeight(double height) {
    this.height = height;
  }

  /**
   * @return the height
   */
  public double getHeight() {
    return height;
  }

  /**
   * @param renderURL
   *          the renderURL to set
   */
  public void setRenderURL(String renderURL) {
    this.renderURL = renderURL;
  }

  /**
   * @return the renderURL
   */
  public String getRenderURL() {
    return renderURL;
  }

  /**
   * @param output
   *          the output to set
   */
  public void setOutput(String output) {
    this.output = output;
  }

  /**
   * @return the output
   */
  public String getOutput() {
    return output;
  }

  protected ServletContext getServletContext() {
    return this.pageContext.getServletContext();
  }

  public void setQueryPageId(Long queryPageId) {
    this.queryPageId = queryPageId;
  }

  public Long getQueryPageId() {
    return queryPageId;
  }

  @Override
  public int doStartTag() throws JspException {
    parameters = new HashMap<String, String>();
    return EVAL_BODY_INCLUDE;
  }

  @Override
  public void addParameter(String name, String value) {
    name = ResourceUtils.decodeUrlName(name);
    value = ResourceUtils.decodeUrlName(value);

    parameters.put(name, value);
  }

  public Long getStampId() {
    return stampId;
  }

  public void setStampId(Long stampId) {
    this.stampId = stampId;
  }
}
