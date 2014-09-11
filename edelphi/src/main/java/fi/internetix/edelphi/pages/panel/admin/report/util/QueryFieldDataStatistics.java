package fi.internetix.edelphi.pages.panel.admin.report.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class QueryFieldDataStatistics {

  private final ArrayList<Double> data;
  private Double avg = null;
  private Double min = null;
  private Double max = null;
  private Double stdDev = null;
  private int count = 0;
  private double shift = 0;
  private final Map<Double, String> dataNames;

  public QueryFieldDataStatistics(List<Double> data) {
    this(data, null);
  }

  public QueryFieldDataStatistics(List<Double> data, Map<Double, String> dataNames) {
    this.dataNames = dataNames;
    this.data = new ArrayList<Double>(data);

    while (this.data.contains(null))
      this.data.remove(null);
    
    Collections.sort(this.data);
    
    calculateStatistics();
  }

  public void setShift(double shift) {
    this.shift = shift;
  }
  
  public double getShift() {
    return shift;
  }
  
  private void calculateStatistics() {
    // TODO: Optimize

    double total = 0;
    
    for (Double d : this.data) {
      count++;
      total += d.doubleValue();
      
      if ((getMin() == null) || (d.doubleValue() < getMin().doubleValue()))
        setMin(d);
      
      if ((getMax() == null) || (d.doubleValue() > getMax().doubleValue()))
        setMax(d);
    }
    
    if (getCount() > 0) {
      this.setAvg(total / getCount());
      double avg = getAvg() == null ? 0 : getAvg();
      
      double stddev = 0;
      for (Double d : this.data) {
        stddev += (d - avg) * (d - avg);
      }
      stddev = Math.sqrt((double) 1 / getCount() * stddev);
      setStdDev(new Double(stddev));
    }
  }

  private void setAvg(Double avg) {
    this.avg = avg;
  }

  public Double getAvg() {
    return avg != null ? new Double(avg.doubleValue() + shift) : null;
  }

  private void setMin(Double min) {
    this.min = min;
  }

  public Double getMin() {
    return min != null ? new Double(min.doubleValue() + shift) : null;
  }

  private void setMax(Double max) {
    this.max = max;
  }

  public Double getMax() {
    return max != null ? new Double(max.doubleValue() + shift) : null;
  }
  
  public int getCount() {
    return count;
  }
  
  /**
   * Returns quantile over base value.
   * 
   * @param quantile
   * @param base
   * @return
   */
  public Double getQuantile(int quantile, int base) {
    if (getCount() == 0)
      return null;
    
    if ((quantile > base) || (quantile <= 0) || (base <= 0))
      throw new IllegalArgumentException("Incorrect quantile/base specified.");
    
    double quantileFraq = (double) quantile / base;
    
    int index = (int) Math.round(quantileFraq * (getCount() - 1));
    
    return (double) data.get(index) + shift;
  }
  
  public Double getMedian() {
    return getQuantile(2, 4);
  }
  
  public Double getQ1() {
    return getQuantile(1, 4);
  }
  
  public Double getQ3() {
    return getQuantile(3, 4);
  }

  private void setStdDev(Double stdDev) {
    this.stdDev = stdDev;
  }

  public Double getStdDev() {
    return stdDev;
  }

  public Map<Double, String> getDataNames() {
    if (shift != 0) {
      Map<Double, String> result = new HashMap<Double, String>();
      
      Set<Double> keySet = dataNames.keySet();
      
      for (Double d : keySet) {
        String v = dataNames.get(d);
        
        result.put(new Double(d + shift), v);
      }

      return result;
    } else
      return dataNames;
  }
  
}
