package com.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateUtil {
    public static final int MINUTE = 60;
    public static final int HOUR = 3600;
    public static final int DAY = 86400;
    public static final int ONE_DAY_TIME = 1;
    public static final TimeZone TZ_SHANGHAI = TimeZone.getTimeZone("Asia/Shanghai");

    public DateUtil() {
    }

    public static String getDate(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(TZ_SHANGHAI);
        String var = sdf.format(date);
        return var;
    }

    public static String GetDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TZ_SHANGHAI);
        String sDate = sdf.format(new Date());
        return sDate;
    }

    public static String GetDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TZ_SHANGHAI);
        String sDate = sdf.format(new Date());
        return sDate;
    }

    public static String GetTimeFormat(String strFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(strFormat);
        sdf.setTimeZone(TZ_SHANGHAI);
        String sDate = sdf.format(new Date());
        return sDate;
    }

    public String SetDateFormat(String myDate, String strFormat)
            throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(strFormat);
        sdf.setTimeZone(TZ_SHANGHAI);
        String sDate = sdf.format(sdf.parse(myDate));
        return sDate;
    }

    public String FormatDateTime(String strDateTime, String strFormat) {
        String sDateTime = strDateTime;

        try {
            Calendar Cal = parseDateTime(strDateTime);
            SimpleDateFormat sdf = new SimpleDateFormat(strFormat);
            sdf.setTimeZone(TZ_SHANGHAI);
            sDateTime = sdf.format(Cal.getTime());
        } catch (Exception var6) {
            ;
        }

        return sDateTime;
    }

    public static Calendar parseDateTime(String baseDate) {
        Calendar cal = null;
        cal = new GregorianCalendar();
        int yy = Integer.parseInt(baseDate.substring(0, 4));
        int mm = Integer.parseInt(baseDate.substring(5, 7)) - 1;
        int dd = Integer.parseInt(baseDate.substring(8, 10));
        int hh = 0;
        int mi = 0;
        int ss = 0;
        if (baseDate.length() > 12) {
            hh = Integer.parseInt(baseDate.substring(11, 13));
            mi = Integer.parseInt(baseDate.substring(14, 16));
            ss = Integer.parseInt(baseDate.substring(17, 19));
        }

        cal.set(yy, mm, dd, hh, mi, ss);
        return cal;
    }

    public int getDay(String strDate) {
        Calendar cal = parseDateTime(strDate);
        return cal.get(5);
    }

    public int getMonth(String strDate) {
        Calendar cal = parseDateTime(strDate);
        return cal.get(2) + 1;
    }

    public int getWeekDay(String strDate) {
        Calendar cal = parseDateTime(strDate);
        return cal.get(7);
    }

    public String getWeekDayName(String strDate) {
        String[] mName = new String[]{"日", "一", "二", "三", "四", "五", "六"};
        int iWeek = this.getWeekDay(strDate);
        --iWeek;
        return "星期" + mName[iWeek];
    }

    public int getYear(String strDate) {
        Calendar cal = parseDateTime(strDate);
        return cal.get(1) + 1900;
    }

    public String DateAdd(String strDate, int iCount, int iType) {
        Calendar Cal = parseDateTime(strDate);
        int pType = 0;
        if (iType == 0) {
            pType = 1;
        } else if (iType == 1) {
            pType = 2;
        } else if (iType == 2) {
            pType = 5;
        } else if (iType == 3) {
            pType = 10;
        } else if (iType == 4) {
            pType = 12;
        } else if (iType == 5) {
            pType = 13;
        }

        Cal.add(pType, iCount);
        SimpleDateFormat sdf = null;
        if (iType <= 2) {
            sdf = new SimpleDateFormat("yyyy-MM-dd");
        } else {
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }

        sdf.setTimeZone(TZ_SHANGHAI);
        String sDate = sdf.format(Cal.getTime());
        return sDate;
    }

    public String DateAdd(String strOption, int iDays, String strStartDate) {
        if (!strOption.equals("d")) {
            ;
        }

        return strStartDate;
    }

    public int DateDiff(String strDateBegin, String strDateEnd, int iType) {
        Calendar calBegin = parseDateTime(strDateBegin);
        Calendar calEnd = parseDateTime(strDateEnd);
        long lBegin = calBegin.getTimeInMillis();
        long lEnd = calEnd.getTimeInMillis();
        int ss = (int) ((lBegin - lEnd) / 1000L);
        int min = ss / 60;
        int hour = min / 60;
        int day = hour / 24;
        if (iType == 0) {
            return hour;
        } else if (iType == 1) {
            return min;
        } else {
            return iType == 2 ? day : -1;
        }
    }

    public boolean isLeapYear(int yearNum) {
        boolean isLeep = false;
        if (yearNum % 4 == 0 && yearNum % 100 != 0) {
            isLeep = true;
        } else if (yearNum % 400 == 0) {
            isLeep = true;
        } else {
            isLeep = false;
        }

        return isLeep;
    }

    public int getWeekNumOfYear() {
        Calendar calendar = Calendar.getInstance();
        int iWeekNum = calendar.get(3);
        return iWeekNum;
    }

    public int getWeekNumOfYearDay(String strDate) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.setTimeZone(TZ_SHANGHAI);
        Date curDate = format.parse(strDate);
        calendar.setTime(curDate);
        int iWeekNum = calendar.get(3);
        return iWeekNum;
    }

    public String getYearWeekFirstDay(int yearNum, int weekNum)
            throws ParseException {
        Calendar cal = Calendar.getInstance();
        int month = cal.get(2) + 1;
        int temp = yearNum;
        if (weekNum == 1 && month != 1) {
            cal.set(1, yearNum + 1);
            temp = yearNum;
        }

        int dayofweek = cal.get(7);
        int dayofyear = cal.get(6);
        if (dayofweek > dayofyear) {
            cal.set(1, yearNum);
            temp = yearNum - 1;
        }

        cal.set(3, weekNum);
        cal.set(7, 1);
        String tempYear = Integer.toString(temp);
        String tempMonth = Integer.toString(cal.get(2) + 1);
        String tempDay = Integer.toString(cal.get(5));
        String tempDate = tempYear + "-" + tempMonth + "-" + tempDay;
        return this.SetDateFormat(tempDate, "yyyy-MM-dd");
    }

    public String getYearWeekEndDay(int yearNum, int weekNum)
            throws ParseException {
        Calendar cal = Calendar.getInstance();
        int month = cal.get(2) + 1;
        if (weekNum == 1 && month != 1) {
            ++yearNum;
        }

        cal.set(1, yearNum);
        cal.set(3, weekNum);
        cal.set(7, 7);
        String tempYear = Integer.toString(yearNum);
        String tempMonth = Integer.toString(cal.get(2) + 1);
        String tempDay = Integer.toString(cal.get(5));
        String tempDate = tempYear + "-" + tempMonth + "-" + tempDay;
        return this.SetDateFormat(tempDate, "yyyy-MM-dd");
    }

    public String getYearMonthFirstDay(int yearNum, int monthNum)
            throws ParseException {
        String tempYear = Integer.toString(yearNum);
        String tempMonth = Integer.toString(monthNum);
        String tempDay = "1";
        String tempDate = tempYear + "-" + tempMonth + "-" + tempDay;
        return this.SetDateFormat(tempDate, "yyyy-MM-dd");
    }

    public String getYearMonthEndDay(int yearNum, int monthNum)
            throws ParseException {
        String tempYear = Integer.toString(yearNum);
        String tempMonth = Integer.toString(monthNum);
        String tempDay = "31";
        if (tempMonth.equals("1") || tempMonth.equals("3")
                || tempMonth.equals("5") || tempMonth.equals("7")
                || tempMonth.equals("8") || tempMonth.equals("10")
                || tempMonth.equals("12")) {
            tempDay = "31";
        }

        if (tempMonth.equals("4") || tempMonth.equals("6")
                || tempMonth.equals("9") || tempMonth.equals("11")) {
            tempDay = "30";
        }

        if (tempMonth.equals("2")) {
            if (this.isLeapYear(yearNum)) {
                tempDay = "29";
            } else {
                tempDay = "28";
            }
        }

        String tempDate = tempYear + "-" + tempMonth + "-" + tempDay;
        return this.SetDateFormat(tempDate, "yyyy-MM-dd");
    }

    public static String getPreTime(String sj1, String jj) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TZ_SHANGHAI);
        String mydate1 = "";

        try {
            Date date1 = format.parse(sj1);
            long Time = date1.getTime() / 1000L
                    + (long) (Integer.parseInt(jj) * 60);
            date1.setTime(Time * 1000L);
            mydate1 = format.format(date1);
        } catch (Exception var7) {
            var7.printStackTrace();
        }

        return mydate1;
    }

    public static String getShortDateTime(String dateStr, String pattern)
            throws ParseException {
        return getShortDateTime(dateStr, pattern, new Date());
    }

    public static String getShortDateTime(String dateStr, String pattern,
                                          Date date) throws ParseException {
        String reStr = null;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date dateSrcDate = sdf.parse(dateStr);
        Calendar cln = Calendar.getInstance();
        cln.setTime(date);
        long d = date.getTime() - dateSrcDate.getTime();
        long minue = d / 1000L / 60L;
        String qian = "前";
        boolean b = true;
        if (minue < 0L) {
            minue = -minue;
            qian = "后";
            b = false;
        }

        if (minue < 3L) {
            reStr = "刚刚";
        } else if (minue <= 59L) {
            reStr = minue + "分钟" + qian;
        } else {
            long hour = minue / 60L;
            if (hour <= 23L) {
                reStr = hour + "小时" + qian;
            } else {
                long day = hour / 24L;
                if (day == 1L) {
                    if (b) {
                        reStr = "昨天";
                    } else {
                        reStr = "明天";
                    }
                } else if (2L <= day && day <= 3L) {
                    reStr = day + "天" + qian;
                } else {
                    Calendar srcCln = Calendar.getInstance();
                    srcCln.setTime(dateSrcDate);
                    String format = "MM-dd HH:mm";
                    if (srcCln.get(1) != cln.get(1)) {
                        format = "yyyy-MM-dd HH:mm";
                    }

                    SimpleDateFormat sdf2 = new SimpleDateFormat(format);
                    reStr = sdf2.format(dateSrcDate);
                }
            }
        }

        return reStr;
    }

    public static String nextSomeDay(DateFormat df, String date, int nextDay) throws ParseException{
        String result = "";
        Date today = df.parse(date);
        today = new Date(today.getTime() + nextDay * ONE_DAY_TIME);
        result = df.format(today);
        return result;
    }
}
