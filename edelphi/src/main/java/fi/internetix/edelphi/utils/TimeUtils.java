package fi.internetix.edelphi.utils;

import org.apache.commons.lang.time.DateUtils;

public class TimeUtils {

  public static String printTimeRemaining(long time, String monthsText, String monthText, String weeksText, String weekText, String daysText, String dayText, String hoursText, String hourText, String minutesText, String minuteText) {
    //long left = time - System.currentTimeMillis();

    long left = time;
    long months = Math.round(left / MS_IN_MONTH);
    left -= months * MS_IN_MONTH;
    long weeks = Math.round(left / MS_IN_WEEK);
    left -= weeks * MS_IN_WEEK;
    long days = Math.round(left / DateUtils.MILLIS_PER_DAY);
    left -= days * DateUtils.MILLIS_PER_DAY;
    long hours = Math.round(left / DateUtils.MILLIS_PER_HOUR);
    left -= hours * DateUtils.MILLIS_PER_HOUR;
    long minutes = Math.round(left / DateUtils.MILLIS_PER_MINUTE);
    left -= minutes * DateUtils.MILLIS_PER_MINUTE;

    StringBuilder resultBuilder = new StringBuilder();

    if (months > 0) {
      resultBuilder.append(months);
      resultBuilder.append(' ');
      resultBuilder.append(months == 1 ? monthText : monthsText);
    }

    if (weeks > 0) {
      if (resultBuilder.length() > 0)
        resultBuilder.append(' ');
      resultBuilder.append(weeks);
      resultBuilder.append(' ');
      resultBuilder.append(weeks == 1 ? weekText : weeksText);
    }

    if (days > 0) {
      if (resultBuilder.length() > 0)
        resultBuilder.append(' ');
      resultBuilder.append(days);
      resultBuilder.append(' ');
      resultBuilder.append(days == 1 ? dayText : daysText);
    }

    if (hours > 0) {
      if (resultBuilder.length() > 0)
        resultBuilder.append(' ');
      resultBuilder.append(hours);
      resultBuilder.append(' ');
      resultBuilder.append(hours == 1 ? hourText : hoursText);
    }

    if (minutes > 0) {
      if (resultBuilder.length() > 0)
        resultBuilder.append(' ');
      resultBuilder.append(minutes);
      resultBuilder.append(' ');
      resultBuilder.append(minutes == 1 ? minuteText : minutesText);
    }

    return resultBuilder.toString();
  }

  private static long MS_IN_WEEK = DateUtils.MILLIS_PER_DAY * 7;
  private static long MS_IN_MONTH = DateUtils.MILLIS_PER_DAY * 30;
}
