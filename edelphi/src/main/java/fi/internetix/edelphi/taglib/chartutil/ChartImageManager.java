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

package fi.internetix.edelphi.taglib.chartutil;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Locale;

import javax.servlet.ServletContext;

import org.eclipse.birt.chart.api.ChartEngine;
import org.eclipse.birt.chart.device.EmptyUpdateNotifier;
import org.eclipse.birt.chart.device.IDeviceRenderer;
import org.eclipse.birt.chart.device.IImageMapEmitter;
import org.eclipse.birt.chart.exception.ChartException;
import org.eclipse.birt.chart.factory.GeneratedChartState;
import org.eclipse.birt.chart.factory.Generator;
import org.eclipse.birt.chart.factory.IDataRowExpressionEvaluator;
import org.eclipse.birt.chart.factory.RunTimeContext;
import org.eclipse.birt.chart.integrate.SimpleActionEvaluator;
import org.eclipse.birt.chart.integrate.SimpleActionRenderer;
import org.eclipse.birt.chart.model.Chart;
import org.eclipse.birt.chart.model.attribute.Bounds;
import org.eclipse.birt.chart.plugin.ChartEnginePlugin;
import org.eclipse.birt.chart.script.IExternalContext;
import org.eclipse.birt.chart.style.IStyleProcessor;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.PlatformConfig;
import org.mozilla.javascript.Scriptable;

import com.ibm.icu.util.ULocale;

/**
 * Class for managing image resources, including generating and disposing.
 * 
 */
public class ChartImageManager {

  private final Chart chartModel;

  private String imageFormatExtension = null;

  private final IDataRowExpressionEvaluator evaluator;

  private final IStyleProcessor styleProcessor;

  private RunTimeContext runtimeContext = null;

  private IExternalContext externalContext = null;

  private String imageMap = null;

  private int dpi = 72;

  public ChartImageManager(Chart chartModel, String outputFormat) throws Exception {
    this(chartModel, outputFormat, null, null, null, null);
  }

  public ChartImageManager(Chart chartModel, String outputFormat, IDataRowExpressionEvaluator evaluator, RunTimeContext rtc,
      IExternalContext externalContext, IStyleProcessor styleProc) throws Exception {
    this.chartModel = chartModel;
    this.imageFormatExtension = outputFormat;
    this.evaluator = evaluator;
    this.styleProcessor = styleProc;

    if (externalContext == null) {
      this.externalContext = new IExternalContext() {

        private static final long serialVersionUID = 4666361117214885689L;

        public Object getObject() {
          // TODO Auto-generated method stub
          return null;
        }

        public Scriptable getScriptable() {
          // TODO Auto-generated method stub
          return null;
        }
      };
    } else {
      this.externalContext = externalContext;
    }

    if (rtc == null) {
      this.runtimeContext = new RunTimeContext();
      this.runtimeContext.setULocale(ULocale.getDefault());
    } else {
      this.runtimeContext = rtc;
    }

    generateStream();
  }

  public String getImageContentType() {
    if ("PNG".equalsIgnoreCase(imageFormatExtension))
      return "image/png";

    if ("SVG".equalsIgnoreCase(imageFormatExtension))
      return "image/svg+xml";
    
    return null;
  }
  
  public InputStream generateStream() throws BirtException {
    InputStream fis = null;
    Generator generator = Generator.instance();
    IDeviceRenderer deviceRenderer = null;
    try {
      if (evaluator == null) {
        // If chart has runtime dataset, do not create sample data
        if (!ChartWebHelper.isChartInRuntime(chartModel)) {
          chartModel.createSampleRuntimeSeries();
        }
      } else {
        generator.bindData(evaluator, new SimpleActionEvaluator(), chartModel, runtimeContext);
      }

      runtimeContext.setActionRenderer(new SimpleActionRenderer(evaluator));

      // FETCH A HANDLE TO THE DEVICE RENDERER
      deviceRenderer = ChartEngine.instance().getRenderer("dv." + imageFormatExtension.toUpperCase(Locale.US));

      deviceRenderer.setProperty(IDeviceRenderer.DPI_RESOLUTION, Integer.valueOf(dpi));

      if ("SVG".equalsIgnoreCase(imageFormatExtension)) {
        deviceRenderer.setProperty("resize.svg", Boolean.TRUE);
      }

      // BUILD THE CHART
      final Bounds originalBounds = chartModel.getBlock().getBounds();

      // we must copy the bounds to avoid that setting it on one object
      // unsets it on its precedent container

      final Bounds bo = originalBounds.copyInstance();

      GeneratedChartState gcs = generator.build(deviceRenderer.getDisplayServer(), chartModel, bo, externalContext, runtimeContext, styleProcessor);

      // WRITE TO THE IMAGE FILE
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      BufferedOutputStream bos = new BufferedOutputStream(baos);

      deviceRenderer.setProperty(IDeviceRenderer.FILE_IDENTIFIER, bos);
      deviceRenderer.setProperty(IDeviceRenderer.UPDATE_NOTIFIER, new EmptyUpdateNotifier(chartModel, gcs.getChartModel()));

      generator.render(deviceRenderer, gcs);

      // cleanup the dataRow evaluator.
      // rowAdapter.close( );

      // RETURN A STREAM HANDLE TO THE NEWLY CREATED IMAGE
      try {
        fis = new ByteArrayInputStream(baos.toByteArray());
        bos.close();
      } catch (Exception ioex) {
        throw new ChartException(ChartEnginePlugin.ID, ChartException.GENERATION, ioex);
      }

      if (!"SVG".equalsIgnoreCase(imageFormatExtension) && deviceRenderer instanceof IImageMapEmitter) {
        imageMap = ((IImageMapEmitter) deviceRenderer).getImageMap();
      }

    } catch (BirtException birtException) {
      Throwable ex = birtException;
      while (ex.getCause() != null) {
        ex = ex.getCause();
      }

      if (ex instanceof ChartException && ((ChartException) ex).getType() == ChartException.ZERO_DATASET) {
        // if the Data set has zero lines, just
        // returns null gracefully.
        return null;
      }

      if (ex instanceof ChartException && ((ChartException) ex).getType() == ChartException.ALL_NULL_DATASET) {
        // if the Data set contains all null values, just
        // returns null gracefully and render nothing.
        return null;
      }

      if ((ex instanceof ChartException && ((ChartException) ex).getType() == ChartException.INVALID_IMAGE_SIZE)) {
        // if the image size is invalid, this may caused by
        // Display=None, lets ignore it.
        return null;
      }

      throw birtException;
    } catch (RuntimeException ex) {
      throw new ChartException(ChartEnginePlugin.ID, ChartException.GENERATION, ex);
    } finally {
      if (deviceRenderer != null) {
        deviceRenderer.dispose();
      }
    }

    return fis;
  }

  public String getImageMap() {
    return imageMap;
  }

  public synchronized static void init(ServletContext context) {
    // Initialize chart engine in standalone mode
    PlatformConfig config = new PlatformConfig();
    config.setProperty("STANDALONE", "true"); //$NON-NLS-2$
    ChartEngine.instance(config);
  }

  /**
   * Trim the end separator
   * 
   * @param path
   * @return
   */
  protected static String trimSep(String path) {
    path = trimString(path);
    if (path.endsWith(File.separator)) {
      path = path.substring(0, path.length() - 1);
    }

    return path;
  }

  /**
   * Returns trim string, not null
   * 
   * @param str
   * @return
   */
  private static String trimString(String str) {
    return str != null ? str.trim() : "";
  }

}
