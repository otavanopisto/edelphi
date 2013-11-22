package fi.internetix.edelphi.pages.panel.admin.report.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.birt.chart.api.ChartEngine;
import org.eclipse.birt.chart.device.EmptyUpdateNotifier;
import org.eclipse.birt.chart.device.IDeviceRenderer;
import org.eclipse.birt.chart.exception.ChartException;
import org.eclipse.birt.chart.extension.datafeed.BubbleDataPointDefinition;
import org.eclipse.birt.chart.extension.datafeed.BubbleEntry;
import org.eclipse.birt.chart.factory.GeneratedChartState;
import org.eclipse.birt.chart.factory.Generator;
import org.eclipse.birt.chart.model.Chart;
import org.eclipse.birt.chart.model.ChartWithAxes;
import org.eclipse.birt.chart.model.ChartWithoutAxes;
import org.eclipse.birt.chart.model.attribute.Anchor;
import org.eclipse.birt.chart.model.attribute.AxisType;
import org.eclipse.birt.chart.model.attribute.Bounds;
import org.eclipse.birt.chart.model.attribute.ChartDimension;
import org.eclipse.birt.chart.model.attribute.DataPointComponent;
import org.eclipse.birt.chart.model.attribute.IntersectionType;
import org.eclipse.birt.chart.model.attribute.LegendItemType;
import org.eclipse.birt.chart.model.attribute.LineStyle;
import org.eclipse.birt.chart.model.attribute.Position;
import org.eclipse.birt.chart.model.attribute.TickStyle;
import org.eclipse.birt.chart.model.attribute.impl.ColorDefinitionImpl;
import org.eclipse.birt.chart.model.attribute.impl.DataPointImpl;
import org.eclipse.birt.chart.model.component.Axis;
import org.eclipse.birt.chart.model.component.MarkerLine;
import org.eclipse.birt.chart.model.component.Series;
import org.eclipse.birt.chart.model.component.impl.MarkerLineImpl;
import org.eclipse.birt.chart.model.component.impl.SeriesImpl;
import org.eclipse.birt.chart.model.data.BubbleDataSet;
import org.eclipse.birt.chart.model.data.NumberDataSet;
import org.eclipse.birt.chart.model.data.SeriesDefinition;
import org.eclipse.birt.chart.model.data.TextDataSet;
import org.eclipse.birt.chart.model.data.impl.BubbleDataSetImpl;
import org.eclipse.birt.chart.model.data.impl.NumberDataElementImpl;
import org.eclipse.birt.chart.model.data.impl.NumberDataSetImpl;
import org.eclipse.birt.chart.model.data.impl.SeriesDefinitionImpl;
import org.eclipse.birt.chart.model.data.impl.TextDataSetImpl;
import org.eclipse.birt.chart.model.impl.ChartWithAxesImpl;
import org.eclipse.birt.chart.model.impl.ChartWithoutAxesImpl;
import org.eclipse.birt.chart.model.layout.Legend;
import org.eclipse.birt.chart.model.layout.Plot;
import org.eclipse.birt.chart.model.type.BarSeries;
import org.eclipse.birt.chart.model.type.BubbleSeries;
import org.eclipse.birt.chart.model.type.LineSeries;
import org.eclipse.birt.chart.model.type.PieSeries;
import org.eclipse.birt.chart.model.type.impl.BarSeriesImpl;
import org.eclipse.birt.chart.model.type.impl.BubbleSeriesImpl;
import org.eclipse.birt.chart.model.type.impl.LineSeriesImpl;
import org.eclipse.birt.chart.model.type.impl.PieSeriesImpl;
import org.eclipse.birt.chart.plugin.ChartEnginePlugin;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.emf.common.util.EList;

public class ChartModelProvider {

  public static Chart createBarChart(String chartCaption, String xLabel, List<String> categoryCaptions, List<Double> values, Double average, Double q1, Double q3) {
    // bart charts are based on charts that contain axes
    ChartWithAxes cwaBar = ChartWithAxesImpl.create();
//    cwaBar.getBlock().setBackground(ColorDefinitionImpl.WHITE());
    cwaBar.getBlock().getOutline().setVisible(true);
    cwaBar.setDimension(ChartDimension.TWO_DIMENSIONAL_WITH_DEPTH_LITERAL);

    // customize the plot
//    Plot p = cwaBar.getPlot();
//    p.getClientArea().setBackground(ColorDefinitionImpl.create(255, 255, 225));
//    p.getOutline().setVisible(false);

    cwaBar.getTitle().getLabel().getCaption().setValue(chartCaption);

    cwaBar.getLegend().setVisible(false);
    
    // customize the X-axis
    Axis xAxisPrimary = cwaBar.getPrimaryBaseAxes()[0];
    xAxisPrimary.setType(AxisType.TEXT_LITERAL);
    xAxisPrimary.getMajorGrid().setTickStyle(TickStyle.BELOW_LITERAL);
    xAxisPrimary.getOrigin().setType(IntersectionType.VALUE_LITERAL);
    if (xLabel != null) {
      xAxisPrimary.getTitle().setVisible(true);
      xAxisPrimary.getTitle().getCaption().getFont().setSize(12);
      xAxisPrimary.getTitle().getCaption().getFont().setBold(false);
      xAxisPrimary.getTitle().getCaption().setValue(xLabel);
    }

    // customize the Y-axis
    Axis yAxisPrimary = cwaBar.getPrimaryOrthogonalAxis(xAxisPrimary);
    yAxisPrimary.getMajorGrid().setTickStyle(TickStyle.LEFT_LITERAL);
    yAxisPrimary.setType(AxisType.LINEAR_LITERAL);
//    yAxisPrimary.getLabel().getCaption().getFont().setRotation(90);

    TextDataSet categoryValues = TextDataSetImpl.create(categoryCaptions);
    NumberDataSet orthoValues1 = NumberDataSetImpl.create(values);
    
    // create the category base series
    Series seCategory = SeriesImpl.create();
    seCategory.setDataSet(categoryValues);

    // create the value orthogonal series
    BarSeries bs1 = (BarSeries) BarSeriesImpl.create();
    bs1.setDataSet(orthoValues1);
    bs1.setRiserOutline(null);
//    bs1.getLabel().setVisible(true);
    bs1.setLabelPosition(Position.INSIDE_LITERAL);
//    bs1.setRiserOutline(ColorDefinitionImpl.BLACK());

    // wrap the base series in the X-axis series definition
    SeriesDefinition xSeries = SeriesDefinitionImpl.create();
    xAxisPrimary.getSeriesDefinitions().add(xSeries);
    xSeries.getSeries().add(seCategory);

    // wrap the orthogonal series in the X-axis series definition
    SeriesDefinition ySeries = SeriesDefinitionImpl.create();
    yAxisPrimary.getSeriesDefinitions().add(ySeries);
    ySeries.getSeriesPalette().update(ColorDefinitionImpl.create(0, 153, 255));
    ySeries.getSeries().add(bs1);

    if (average != null) {
      MarkerLine ml = MarkerLineImpl.create(xAxisPrimary, NumberDataElementImpl.create(average + 0.5d));
      ml.getLineAttributes().setStyle(LineStyle.SOLID_LITERAL);
      ml.getLabel().getCaption().setValue("A");
      ml.setLabelAnchor(Anchor.NORTH_EAST_LITERAL);
    }

    if (q1 != null) {
      MarkerLine ml = MarkerLineImpl.create(xAxisPrimary, NumberDataElementImpl.create(q1 + 0.5d));
      ml.getLineAttributes().setStyle(LineStyle.DASHED_LITERAL);
      ml.getLabel().getCaption().setValue("Q1");
      ml.setLabelAnchor(Anchor.NORTH_EAST_LITERAL);
    }

    if (q3 != null) {
      MarkerLine ml = MarkerLineImpl.create(xAxisPrimary, NumberDataElementImpl.create(q3 + 0.5d));
      ml.getLineAttributes().setStyle(LineStyle.DASHED_LITERAL);
      ml.getLabel().getCaption().setValue("Q3");
      ml.setLabelAnchor(Anchor.NORTH_EAST_LITERAL);
    }
    
    return cwaBar;
  }

  public static Chart createBarChartHorizontal(String chartCaption, String xLabel, List<String> categoryCaptions, List<Double> values, Double average, Double q1, Double q3) {
    ChartWithAxes chart = (ChartWithAxes) createBarChart(chartCaption, xLabel, categoryCaptions, values, average, q1, q3);
    chart.setTransposed(true);
    chart.setReverseCategory(true);
    return chart;
  }
  
  public static Chart createLineChart() {
    // bart charts are based on charts that contain axes
    ChartWithAxes cwaBar = ChartWithAxesImpl.create();
    cwaBar.getBlock().setBackground(ColorDefinitionImpl.WHITE());
    cwaBar.getBlock().getOutline().setVisible(true);
    cwaBar.setDimension(ChartDimension.TWO_DIMENSIONAL_WITH_DEPTH_LITERAL);

    // customize the plot
    Plot p = cwaBar.getPlot();
    p.getClientArea().setBackground(ColorDefinitionImpl.create(255, 255, 225));
    p.getOutline().setVisible(false);

    cwaBar.getTitle().getLabel().getCaption().setValue("Simple Bar Chart");

    // customize the legend
    Legend lg = cwaBar.getLegend();
    lg.getText().getFont().setSize(16);
    lg.getInsets().set(10, 5, 0, 0);
    lg.setAnchor(Anchor.NORTH_LITERAL);

    // customize the X-axis
    Axis xAxisPrimary = cwaBar.getPrimaryBaseAxes()[0];
    xAxisPrimary.setType(AxisType.TEXT_LITERAL);
    xAxisPrimary.getMajorGrid().setTickStyle(TickStyle.BELOW_LITERAL);
    xAxisPrimary.getOrigin().setType(IntersectionType.VALUE_LITERAL);
    xAxisPrimary.getTitle().setVisible(false);

    // customize the Y-axis
    Axis yAxisPrimary = cwaBar.getPrimaryOrthogonalAxis(xAxisPrimary);
    yAxisPrimary.getMajorGrid().setTickStyle(TickStyle.LEFT_LITERAL);
    yAxisPrimary.setType(AxisType.LINEAR_LITERAL);
    yAxisPrimary.getLabel().getCaption().getFont().setRotation(90);

//    Axis[] axaOrthogonal = cwaBar.getOrthogonalAxes(xAxisPrimary, true);
    
    // initialize a collection with the X-series data
    Vector<String> vs = new Vector<String>();
    vs.add("A");
    vs.add("B");
    vs.add("C");
    vs.add("D");
    vs.add("E");
    vs.add("F");

    TextDataSet categoryValues = TextDataSetImpl.create(vs);

    // initialize a collection with the Y-series data
    ArrayList<Double> vn1 = new ArrayList<Double>();
    vn1.add(new Double(25));
    vn1.add(new Double(35));
    vn1.add(new Double(45));
    vn1.add(null);
    vn1.add(null);
    vn1.add(null);

    ArrayList<Double> q1set = new ArrayList<Double>();
    q1set.add(null);
    q1set.add(null);
    q1set.add(new Double(45));
    q1set.add(new Double(43));
    q1set.add(new Double(23));
    q1set.add(new Double(33));

    ArrayList<Double> medianSet = new ArrayList<Double>();
    medianSet.add(null);
    medianSet.add(null);
    medianSet.add(new Double(45));
    medianSet.add(new Double(44));
    medianSet.add(new Double(37));
    medianSet.add(new Double(50));
    
    ArrayList<Double> q3set = new ArrayList<Double>();
    q3set.add(null);
    q3set.add(null);
    q3set.add(new Double(45));
    q3set.add(new Double(45));
    q3set.add(new Double(55));
    q3set.add(new Double(65));
    
    NumberDataSet orthoValues1 = NumberDataSetImpl.create(vn1);
    NumberDataSet orthoValues2 = NumberDataSetImpl.create(q1set);
    NumberDataSet orthoValues3 = NumberDataSetImpl.create(q3set);
    NumberDataSet orthoValues4 = NumberDataSetImpl.create(medianSet);

    // create the category base series
    Series seCategory = SeriesImpl.create();
    seCategory.setDataSet(categoryValues);

    // create the value orthogonal series

    LineSeries ls = (LineSeries) LineSeriesImpl.create();
    ls.setDataSet(orthoValues1);
    ls.getLineAttributes().setColor(ColorDefinitionImpl.GREEN());
    
    LineSeries ls2 = (LineSeries) LineSeriesImpl.create();
    ls2.setDataSet(orthoValues2);
    ls2.getLineAttributes().setColor(ColorDefinitionImpl.RED());
    ls2.getLineAttributes().setStyle(LineStyle.DASHED_LITERAL);

    LineSeries ls3 = (LineSeries) LineSeriesImpl.create();
    ls3.setDataSet(orthoValues3);
    ls3.getLineAttributes().setColor(ColorDefinitionImpl.BLUE());
    ls3.getLineAttributes().setStyle(LineStyle.DASHED_LITERAL);

    LineSeries ls4 = (LineSeries) LineSeriesImpl.create();
    ls4.setDataSet(orthoValues4);
    ls4.getLineAttributes().setColor(ColorDefinitionImpl.ORANGE());
    
//  ls.getMarker().setType(MarkerType.BOX_LITERAL);
    
    // wrap the base series in the X-axis series definition
    SeriesDefinition sdX = SeriesDefinitionImpl.create();
//    sdX.getSeriesPalette().update(0); // set the colors in the palette
    xAxisPrimary.getSeriesDefinitions().add(sdX);
    sdX.getSeries().add(seCategory);

    // wrap the orthogonal series in the X-axis series definition
    SeriesDefinition sdY1 = SeriesDefinitionImpl.create();
//    sdY1.getSeriesPalette().update(1); // set the color in the palette
    yAxisPrimary.getSeriesDefinitions().add(sdY1);
    sdY1.getSeries().add(ls);

    SeriesDefinition sdY2 = SeriesDefinitionImpl.create();
//    sdY2.getSeriesPalette().update(1); // set the color in the palette
    yAxisPrimary.getSeriesDefinitions().add(sdY2);
    sdY2.getSeries().add(ls2);

    SeriesDefinition sdY3 = SeriesDefinitionImpl.create();
//    sdY3.getSeriesPalette().update(1); // set the color in the palette
    yAxisPrimary.getSeriesDefinitions().add(sdY3);
    sdY3.getSeries().add(ls3);

    SeriesDefinition sdY4 = SeriesDefinitionImpl.create();
//    sdY4.getSeriesPalette().update(1); // set the color in the palette
    yAxisPrimary.getSeriesDefinitions().add(sdY4);
    sdY4.getSeries().add(ls4);
    
    return cwaBar;
  }
  
  public static Chart createTimeSeriesChart(String chartCaption, List<String> categoryCaptions, double minY, double maxY, 
      ChartDataSeries preliminaryValues, ChartDataSeries averageValues, ChartDataSeries q1Values, ChartDataSeries q3Values, ChartDataSeries minValues, ChartDataSeries maxValues) {

    ChartWithAxes cwaBar = ChartWithAxesImpl.create();
    cwaBar.getBlock().setBackground(ColorDefinitionImpl.WHITE());
    cwaBar.getBlock().getOutline().setVisible(true);
    cwaBar.setDimension(ChartDimension.TWO_DIMENSIONAL_WITH_DEPTH_LITERAL);

    // customize the plot
//    Plot p = cwaBar.getPlot();
//    p.getClientArea().setBackground(ColorDefinitionImpl.create(255, 255, 225));
//    p.getOutline().setVisible(false);

    cwaBar.getTitle().getLabel().getCaption().setValue(chartCaption);

    // customize the legend
    Legend lg = cwaBar.getLegend();
    lg.getText().getFont().setSize(12);
    lg.getInsets().set(10, 5, 0, 0);
    lg.setAnchor(Anchor.NORTH_LITERAL);

    // customize the X-axis
    Axis xAxisPrimary = cwaBar.getPrimaryBaseAxes()[0];
    xAxisPrimary.setType(AxisType.TEXT_LITERAL);
    xAxisPrimary.getMajorGrid().setTickStyle(TickStyle.BELOW_LITERAL);
    xAxisPrimary.getOrigin().setType(IntersectionType.VALUE_LITERAL);
    xAxisPrimary.getTitle().setVisible(false);
    
    // customize the Y-axis
    Axis yAxisPrimary = cwaBar.getPrimaryOrthogonalAxis(xAxisPrimary);
    yAxisPrimary.getMajorGrid().setTickStyle(TickStyle.LEFT_LITERAL);
    yAxisPrimary.setType(AxisType.LINEAR_LITERAL);
//    yAxisPrimary.getLabel().getCaption().getFont().setRotation(90);
    yAxisPrimary.getScale().setMin(NumberDataElementImpl.create(minY));
    yAxisPrimary.getScale().setMax(NumberDataElementImpl.create(maxY));
    
    TextDataSet categoryValues = TextDataSetImpl.create(categoryCaptions);

    // create the category base series
    Series seCategory = SeriesImpl.create();
    seCategory.setDataSet(categoryValues);

    // wrap the base series in the X-axis series definition
    SeriesDefinition sdX = SeriesDefinitionImpl.create();
    xAxisPrimary.getSeriesDefinitions().add(sdX);
    sdX.getSeries().add(seCategory);
    
    if (preliminaryValues != null) {
      NumberDataSet orthoValues1 = NumberDataSetImpl.create(preliminaryValues.getData());
      
      LineSeries ls = (LineSeries) LineSeriesImpl.create();
      
      ls.setSeriesIdentifier(preliminaryValues.getCaption());
      ls.setDataSet(orthoValues1);
      ls.getLineAttributes().setColor(ColorDefinitionImpl.GREEN());
//    ls.getMarker().setType(MarkerType.BOX_LITERAL);

      SeriesDefinition sdY1 = SeriesDefinitionImpl.create();
      yAxisPrimary.getSeriesDefinitions().add(sdY1);
      sdY1.getSeries().add(ls);
      sdY1.setZOrder(20);
    }
    
    if (averageValues != null) {
      NumberDataSet orthoValues4 = NumberDataSetImpl.create(averageValues.getData());
      
      LineSeries ls4 = (LineSeries) LineSeriesImpl.create();
      ls4.setSeriesIdentifier(averageValues.getCaption());
      ls4.setDataSet(orthoValues4);
      ls4.getLineAttributes().setColor(ColorDefinitionImpl.ORANGE());

      SeriesDefinition sdY4 = SeriesDefinitionImpl.create();
      yAxisPrimary.getSeriesDefinitions().add(sdY4);
      sdY4.getSeries().add(ls4);
      sdY4.setZOrder(10);
    }

    
    if (q1Values != null) {
      NumberDataSet orthoValues2 = NumberDataSetImpl.create(q1Values.getData());
      
      LineSeries ls2 = (LineSeries) LineSeriesImpl.create();
      ls2.setSeriesIdentifier(q1Values.getCaption());
      ls2.setDataSet(orthoValues2);
      ls2.getLineAttributes().setColor(ColorDefinitionImpl.RED());
      ls2.getLineAttributes().setStyle(LineStyle.DASHED_LITERAL);

      SeriesDefinition sdY2 = SeriesDefinitionImpl.create();
      yAxisPrimary.getSeriesDefinitions().add(sdY2);
      sdY2.getSeries().add(ls2);
    }

    if (q3Values != null) {
      NumberDataSet orthoValues3 = NumberDataSetImpl.create(q3Values.getData());
      
      LineSeries ls3 = (LineSeries) LineSeriesImpl.create();
      ls3.setSeriesIdentifier(q3Values.getCaption());
      ls3.setDataSet(orthoValues3);
      ls3.getLineAttributes().setColor(ColorDefinitionImpl.BLUE());
      ls3.getLineAttributes().setStyle(LineStyle.DASHED_LITERAL);

      SeriesDefinition sdY3 = SeriesDefinitionImpl.create();
      yAxisPrimary.getSeriesDefinitions().add(sdY3);
      sdY3.getSeries().add(ls3);
    }
    
    if (minValues != null) {
      NumberDataSet dataSet = NumberDataSetImpl.create(minValues.getData());
      
      LineSeries lineSeries = (LineSeries) LineSeriesImpl.create();
      lineSeries.setSeriesIdentifier(minValues.getCaption());
      lineSeries.setDataSet(dataSet);
      lineSeries.getLineAttributes().setColor(ColorDefinitionImpl.YELLOW());
      lineSeries.getLineAttributes().setStyle(LineStyle.SOLID_LITERAL);

      SeriesDefinition minSeriesDefinition = SeriesDefinitionImpl.create();
      yAxisPrimary.getSeriesDefinitions().add(minSeriesDefinition);
      minSeriesDefinition.getSeries().add(lineSeries);
    }
    
    if (maxValues != null) {
      NumberDataSet dataSet = NumberDataSetImpl.create(maxValues.getData());
      
      LineSeries lineSeries = (LineSeries) LineSeriesImpl.create();
      lineSeries.setSeriesIdentifier(maxValues.getCaption());
      lineSeries.setDataSet(dataSet);
      lineSeries.getLineAttributes().setColor(ColorDefinitionImpl.GREEN());
      lineSeries.getLineAttributes().setStyle(LineStyle.SOLID_LITERAL);

      SeriesDefinition minSeriesDefinition = SeriesDefinitionImpl.create();
      yAxisPrimary.getSeriesDefinitions().add(minSeriesDefinition);
      minSeriesDefinition.getSeries().add(lineSeries);
    }

    return cwaBar;
  }
  
  public static String getContentType(String renderFormat) {
    if ("SVG".equalsIgnoreCase(renderFormat))
      return "image/svg+xml";

    if ("PDF".equalsIgnoreCase(renderFormat))
      return "application/pdf";
    
    return "image/png";
  }
  
  public static byte[] getChartData(Chart chartModel, String imageFormatExtension) throws BirtException {
    byte[] chartData = null;
    Generator generator = Generator.instance();
    IDeviceRenderer deviceRenderer = null;
    try {
      // FETCH A HANDLE TO THE DEVICE RENDERER
      deviceRenderer = ChartEngine.instance().getRenderer("dv." + imageFormatExtension.toUpperCase());

      deviceRenderer.setProperty(IDeviceRenderer.DPI_RESOLUTION, Integer.valueOf(72)); // dpi

      if ("SVG".equalsIgnoreCase(imageFormatExtension)) {
        deviceRenderer.setProperty("resize.svg", Boolean.TRUE);
      }

      // BUILD THE CHART
      final Bounds originalBounds = chartModel.getBlock().getBounds();

      // we must copy the bounds to avoid that setting it on one object
      // unsets it on its precedent container

      final Bounds bo = originalBounds.copyInstance();

      GeneratedChartState gcs = generator.build(deviceRenderer.getDisplayServer(), chartModel, bo, null, null, null); //externalContext, runtimeContext, styleProcessor);

      // WRITE TO THE IMAGE FILE
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      BufferedOutputStream bos = new BufferedOutputStream(baos);

      deviceRenderer.setProperty(IDeviceRenderer.FILE_IDENTIFIER, bos);
      deviceRenderer.setProperty(IDeviceRenderer.UPDATE_NOTIFIER, new EmptyUpdateNotifier(chartModel, gcs.getChartModel()));

      generator.render(deviceRenderer, gcs);

      // RETURN A STREAM HANDLE TO THE NEWLY CREATED IMAGE
      try {
        chartData = baos.toByteArray();
        bos.close();
      } catch (Exception ioex) {
        throw new ChartException(ChartEnginePlugin.ID, ChartException.GENERATION, ioex);
      }
    }
    catch (BirtException birtException) {
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
    }
    catch (RuntimeException ex) {
      throw new ChartException(ChartEnginePlugin.ID, ChartException.GENERATION, ex);
    }
    finally {
      if (deviceRenderer != null) {
        deviceRenderer.dispose();
      }
    }

    return chartData;
  }
  public static Chart createStackedBarChartHorizontal(String chartCaption, List<String> categoryCaptions, List<List<Double>> stackedSeriess) {
    ChartWithAxes chart = (ChartWithAxes) createStackedBarChart(chartCaption, categoryCaptions, stackedSeriess);
    chart.setTransposed(true);
    chart.setReverseCategory(true);
    return chart;
  }

  public static Chart createStackedBarChart(String chartCaption, List<String> categoryCaptions, List<List<Double>> stackedSeriess) {
    // bart charts are based on charts that contain axes
    ChartWithAxes cwaBar = ChartWithAxesImpl.create();
//    cwaBar.getBlock().setBackground(ColorDefinitionImpl.WHITE());
    cwaBar.getBlock().getOutline().setVisible(true);
    cwaBar.setDimension(ChartDimension.TWO_DIMENSIONAL_WITH_DEPTH_LITERAL);

    // customize the plot
//    Plot p = cwaBar.getPlot();
//    p.getClientArea().setBackground(ColorDefinitionImpl.create(255, 255, 225));
//    p.getOutline().setVisible(false);

    cwaBar.getTitle().getLabel().getCaption().setValue(chartCaption);

//    cwaBar.getLegend().setVisible(false);
    
    // customize the X-axis
    Axis xAxisPrimary = cwaBar.getPrimaryBaseAxes()[0];
    xAxisPrimary.setType(AxisType.TEXT_LITERAL);
    xAxisPrimary.getMajorGrid().setTickStyle(TickStyle.BELOW_LITERAL);
    xAxisPrimary.getOrigin().setType(IntersectionType.VALUE_LITERAL);
    xAxisPrimary.getTitle().setVisible(false);

    // customize the Y-axis
    Axis yAxisPrimary = cwaBar.getPrimaryOrthogonalAxis(xAxisPrimary);
    yAxisPrimary.getMajorGrid().setTickStyle(TickStyle.LEFT_LITERAL);
    yAxisPrimary.setType(AxisType.LINEAR_LITERAL);
//    yAxisPrimary.getLabel().getCaption().getFont().setRotation(90);

    TextDataSet categoryValues = TextDataSetImpl.create(categoryCaptions);
    
    // create the category base series
    Series seCategory = SeriesImpl.create();
    seCategory.setDataSet(categoryValues);

    // wrap the base series in the X-axis series definition
    SeriesDefinition xSeries = SeriesDefinitionImpl.create();
    xAxisPrimary.getSeriesDefinitions().add(xSeries);
    xSeries.getSeries().add(seCategory);

    // wrap the orthogonal series in the X-axis series definition
    SeriesDefinition ySeries = SeriesDefinitionImpl.create();
    yAxisPrimary.getSeriesDefinitions().add(ySeries);
    ySeries.getSeriesPalette().update(ColorDefinitionImpl.create(0, 153, 255));

    // create the value orthogonal series
    
    int seriesPosition = 1;
    for (List<Double> values : stackedSeriess) {
      NumberDataSet orthoValues1 = NumberDataSetImpl.create(values);
      
      BarSeries barSeries = (BarSeries) BarSeriesImpl.create();
      barSeries.setSeriesIdentifier(seriesPosition);
      barSeries.setDataSet(orthoValues1);
      barSeries.setRiserOutline(null);
      barSeries.getLabel().setVisible(true);
      barSeries.setLabelPosition(Position.INSIDE_LITERAL);
  //    bs1.setRiserOutline(ColorDefinitionImpl.BLACK());
      barSeries.setStacked(true);
      
      ySeries.getSeries().add(barSeries);
      seriesPosition++;
    }
    
    return cwaBar;
  }

  public static Chart createBubbleChart(String chartCaption, String xLabel, List<String> xTickLabels, String yLabel, List<String> yTickLabels, int xAxisLabelRotation, int yAxisLabelRotation, Double[][] values) {
    // TODO: Tick labels
    // TODO: y serie colors
    
    ChartWithAxes bubbleChart = ChartWithAxesImpl.create();
    bubbleChart.setType("Bubble Chart");
    bubbleChart.setSubType("Standard Bubble Chart");

    // Plot
    bubbleChart.getBlock().setBackground(ColorDefinitionImpl.WHITE());
    bubbleChart.getBlock().getOutline().setVisible(true);
    Plot p = bubbleChart.getPlot();
    p.getClientArea().setBackground(ColorDefinitionImpl.create(255, 255, 225));

    // Title
    bubbleChart.getTitle().getLabel().getCaption().setValue(chartCaption);

    // Legend
    Legend lg = bubbleChart.getLegend();
    lg.setVisible(false);
    lg.setItemType(LegendItemType.SERIES_LITERAL);

    // X-Axis

    Map<Double, String> xTickLabelMap = new HashMap<Double, String>();
    for (int i = 0, l = xTickLabels.size(); i < l; i++) {
      xTickLabelMap.put(new Double(i), xTickLabels.get(i));
    }
    
    Axis xAxisPrimary = bubbleChart.getPrimaryBaseAxes()[0];
    xAxisPrimary.setFormatSpecifier(new EnumFormatSpecifierImpl(xTickLabelMap));
    xAxisPrimary.setType(AxisType.LINEAR_LITERAL);
    xAxisPrimary.getMajorGrid().setTickStyle(TickStyle.BELOW_LITERAL);
    xAxisPrimary.getOrigin().setType(IntersectionType.MIN_LITERAL);
    if (xAxisLabelRotation > 0)
      xAxisPrimary.getLabel().getCaption().getFont().setRotation(xAxisLabelRotation);
    if (xLabel != null) {
      xAxisPrimary.getTitle().setVisible(true);
      xAxisPrimary.getTitle().getCaption().getFont().setSize(12);
      xAxisPrimary.getTitle().getCaption().getFont().setBold(false);
      xAxisPrimary.getTitle().getCaption().setValue(xLabel);
    }

    // Y-Axis

    Map<Double, String> yTickLabelMap = new HashMap<Double, String>();
    for (int i = 0, l = yTickLabels.size(); i < l; i++) {
      yTickLabelMap.put(new Double(i), yTickLabels.get(i));
    }
    
    Axis yAxisPrimary = bubbleChart.getPrimaryOrthogonalAxis(xAxisPrimary);
    yAxisPrimary.setFormatSpecifier(new EnumFormatSpecifierImpl(yTickLabelMap));
    yAxisPrimary.setType(AxisType.LINEAR_LITERAL);
    yAxisPrimary.getMajorGrid().setTickStyle(TickStyle.LEFT_LITERAL);
    if (yAxisLabelRotation > 0)
      yAxisPrimary.getLabel().getCaption().getFont().setRotation(yAxisLabelRotation);
    if (yLabel != null) {
      yAxisPrimary.getTitle().setVisible(true);
      yAxisPrimary.getTitle().getCaption().getFont().setSize(12);
      yAxisPrimary.getTitle().getCaption().getFont().setBold(false);
      yAxisPrimary.getTitle().getCaption().setValue(yLabel);
    }
    
    Map<Integer, BubbleEntry[]> bubbleEntriesMap = new HashMap<Integer, BubbleEntry[]>();
    double[] xValues = new double[values.length];

    for (int i = 0, l = xValues.length; i < l; i++) {
      xValues[i] = i;
    }

    for (int x = 0; x < values.length; x++) {
      Double[] yValues = values[x];
      for (int y = 0; y < yValues.length; y++) {
        BubbleEntry[] bubbleEntries = bubbleEntriesMap.get(y);
        if (bubbleEntries == null) {
          bubbleEntries = new BubbleEntry[values.length];
          bubbleEntriesMap.put(y, bubbleEntries);
        }
        bubbleEntries[x] = new BubbleEntry(y, yValues[y] == null ? 0 : Double.valueOf(yValues[y]), y);
      }
    }

    BubbleDataSet[] bubbleDataSets = new BubbleDataSet[bubbleEntriesMap.size()];
    for (int y : bubbleEntriesMap.keySet()) {
      bubbleDataSets[y] = BubbleDataSetImpl.create(bubbleEntriesMap.get(y));
    }

    NumberDataSet categoryValues = NumberDataSetImpl.create(xValues);
    
    // X-Series
    Series seCategory = SeriesImpl.create();
    seCategory.setDataSet(categoryValues);

    SeriesDefinition seriesDefinitionX = SeriesDefinitionImpl.create();
    xAxisPrimary.getSeriesDefinitions().add(seriesDefinitionX);
    seriesDefinitionX.getSeries().add(seCategory);
    
    // Y-Series
    SeriesDefinition seriesDefinitionY = SeriesDefinitionImpl.create();
    yAxisPrimary.getSeriesDefinitions().add(seriesDefinitionY);
    for (BubbleDataSet bubbleDataSet : bubbleDataSets) {
      BubbleSeries bubbleSeries = (BubbleSeries) BubbleSeriesImpl.create();
      bubbleSeries.setDataSet(bubbleDataSet);
      bubbleSeries.getLabel().setVisible(true);
      seriesDefinitionY.getSeries().add(bubbleSeries);
      DataPointImpl dataPoint = (DataPointImpl) bubbleSeries.getDataPoint();
      EList<DataPointComponent> components = dataPoint.getComponents();
      for (DataPointComponent component : components) {
        component.setOrthogonalType(BubbleDataPointDefinition.TYPE_SIZE);
      }
    }

    return bubbleChart;
  }

  public static Chart createPieChart(String chartCaption, List<String> captions, List<Double> values) {
    ChartWithoutAxes chart = ChartWithoutAxesImpl.create( );
    chart.setDimension(ChartDimension.TWO_DIMENSIONAL_WITH_DEPTH_LITERAL);

    // Plot
//    chart.setSeriesThickness(25);
//    chart.getBlock().setBackground(ColorDefinitionImpl.WHITE());

//    Plot p = chart.getPlot();
//    p.getClientArea().setBackground(null);
//    p.getClientArea().getOutline().setVisible(true);
//    p.getOutline().setVisible(true);

    // Legend
//    Legend lg = chart.getLegend();
//    lg.getText().getFont().setSize(16);
//    lg.setBackground(null);
//    lg.getOutline().setVisible(true);

    // Title
    chart.getTitle().getLabel().getCaption().setValue(chartCaption);
//    chart.getTitle( ).getOutline( ).setVisible( true );

    // Data Set
    TextDataSet categoryValues = TextDataSetImpl.create(captions);
    NumberDataSet seriesOneValues = NumberDataSetImpl.create(values);

    // Base Series
    Series seCategory = (Series) SeriesImpl.create();
    seCategory.setDataSet(categoryValues);

    SeriesDefinition seriesDef = SeriesDefinitionImpl.create();
    chart.getSeriesDefinitions().add(seriesDef);
//    sd.getSeriesPalette().update(0);
    seriesDef.getSeries().add(seCategory);

    // Orthogonal Series
    PieSeries sePie = (PieSeries) PieSeriesImpl.create( );
    sePie.setDataSet(seriesOneValues);

    SeriesDefinition seriesDef2 = SeriesDefinitionImpl.create();
    seriesDef.getSeriesDefinitions().add(seriesDef2);
    seriesDef2.getSeries().add(sePie);

    return chart;
  }

}

