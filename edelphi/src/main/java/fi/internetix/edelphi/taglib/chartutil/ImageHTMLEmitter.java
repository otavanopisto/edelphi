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

import java.util.Map;
import java.util.Set;

import fi.internetix.edelphi.pages.panel.admin.report.util.QueryReportChartContext;


/**
 * HTML emitter for image object
 */

public class ImageHTMLEmitter {

  private String id; //$NON-NLS-1$
  private String ext = "PNG"; //$NON-NLS-1$
  private String src; //$NON-NLS-1$
  private String alt; //$NON-NLS-1$
  private String imageMap;

  private int width;
  private int height;

  private StringBuffer html = new StringBuffer();
  private Long queryPageId;
  private Long stampId;
  private Map<String, String> parameters;

  public ImageHTMLEmitter(Long queryPageId, Long stampId, String id, String output, String src, String alt, int width, int height, String imageMap, Map<String, String> parameters) {
    this.queryPageId = queryPageId;
    this.stampId = stampId;
    this.id = id;
    this.src = src;
    this.ext = output;
    this.alt = alt;
    this.width = width;
    this.height = height;
    this.imageMap = imageMap;
    this.parameters = parameters;
  }

  public String generateHTML() {
    if (isSVG()) {
      addSVG();
    } else if (isPDF()) {
      addPDF();
    } else {
      addImage();
    }
    return html.toString();
  }

  private String getImageSrc() {
    StringBuffer url = new StringBuffer();
    
    url.append(this.src);
    url.append("?queryPageId=").append(queryPageId.longValue());
    url.append("&stampId=").append(stampId.longValue());
    url.append("&render=").append(this.ext);
    url.append("&width=").append(this.width);
    url.append("&height=").append(this.height);
    
    Set<String> keySet = parameters.keySet();
    
    for (String key : keySet) {
      String value = parameters.get(key);
      
      url.append('&').append(QueryReportChartContext.CHART_PARAMETER_PREFIX).append(key).append('=').append(value);
    }
    
    return url.toString();
  }
  
  private void addSVG() {
  	addSVGObject();
  }

  private void addSVGObject() {
    String imageSrc = getImageSrc();
    
    html.append("<object type=\"image/svg+xml\"")
      .append(" data=\"").append(imageSrc).append('"')
      .append(" id=\"").append(id).append('"')
      .append(" width=\"").append(width).append('"')
      .append(" height=\"").append(height).append('"')
      .append(" style=\"display: block\"")
      .append("></object>");
  }

//  private void addSVGEmbed() {
//    String imageSrc = getImageSrc();
//    
//    html.append("<embed id=\"" //$NON-NLS-1$
//        + id + "\" type=\"image/svg+xml\" src=\"" + imageSrc + "\" alt=\"" //$NON-NLS-1$ //$NON-NLS-2$
//        + alt + "\" style=\" width: " + width + "px; height: " + height //$NON-NLS-1$ //$NON-NLS-2$
//        + "px;\">"); //$NON-NLS-1$
//    html.append("\n</embed>");
//  }

  private void addPDF() {
    addIFrame();
  }

  private void addImage() {
    addImageDiv();
    // addIFrame( );
  }

  private void addImageDiv() {
    String imageSrc = getImageSrc();
    
//    html.append("<div>\n ");
    
    if (imageMap != null) {
      html.append("<map name=\"" + id + "\">");
      html.append(imageMap);
      html.append("</map>");
    }

    html.append(
        String.format(
            "<img id=\"%s\" src=\"%s\" alt=\"%s\" style=\"width: %s; height: %s;\" width=\"%s\" height=\"%s\" border=\"%s\"", 
            id, imageSrc, alt, width, height, width, height, 0
        )
    );
    
//    html.append("<img " +
//        "id=\"" + id + "\" " +
//        "src=\"" + imageSrc + "\" " +
//        "alt=\"" + alt + "\" " +
//        "style=\" width: " + width + "; height: " + height + ";\" " + 
//        "border=\"0\"");
    
    if (imageMap != null) {
      html.append(" usemap=\"#" + id + "\"");
    }
    html.append(">");
    
    html.append("<br/>");
    
//    html.append("\n</div>");
  }

  private void addIFrame() {
    html.append("<iframe ");
    
    if (id != null)
      html.append("id=\"" + id + "\" ");
    
    html.append("src=\"" + getImageSrc() + "\" width=" + width + " height="
        + height + " scrolling=\"no\" frameborder=\"0\"></iframe>\n");
  }

//  private void addAutoFresh() {
//    int timeInterval = 5000;
//    // Print js code for auto-refresh
//    final String strObj = "window.frames['" + id + "']"; //$NON-NLS-1$ //$NON-NLS-2$
//    final String strFunc = "refreshIFrame_" + id + "( )"; //$NON-NLS-1$ //$NON-NLS-2$
//    html.append("<script language=\"javascript\">\n"); //$NON-NLS-1$
//    html.append(strObj + ".onload = " + strFunc + ";\n"); //$NON-NLS-1$ //$NON-NLS-2$
//    html.append("function " + strFunc + "\n{\n  "); //$NON-NLS-1$ //$NON-NLS-2$
//    html.append("window.setInterval(\"" //$NON-NLS-1$
//        + strObj + ".location.reload()\"," + timeInterval //$NON-NLS-1$
//        + ");\n}\n"); //$NON-NLS-1$
//    html.append("</script>"); //$NON-NLS-1$
//  }

  public boolean isSVG() {
    return "SVG".equalsIgnoreCase(ext); //$NON-NLS-1$
  }

  public boolean isPDF() {
    return "PDF".equalsIgnoreCase(ext); //$NON-NLS-1$
  }

}
