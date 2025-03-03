package com.asiainfo.fsip.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    public static final String yyyyMMdd = "yyyyMMdd";

    public static final String yyyyMM = "yyyyMM";

    public static final String yyyy_MM_dd = "yyyy-MM-dd";

    /**
     * 检验时间字符串是否合法，对比格式化前后字符串
     * eg：普通：202313 --> 能有效转成date 202401，但是不符合预期
     * 期望直接限制输入的 dateStr 就是合法有效的时间，则可以进行两次转换 dateStr-> Date -> formattedDate
     * 进行 dateStr.equals(formattedDate) 判断
     */
    public static boolean isValidFormat(String dateStr,String pattern) {
        try {
            String formattedDate = formatDateStr(dateStr, pattern);
            return dateStr.equals(formattedDate);
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * 将 dateStr 格式化并返回格式化后的时间字符串
     * 进行两次转换 dateStr-> Date -> formattedDate
     */
    public static String formatDateStr(String dateStr,String pattern) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date date = sdf.parse(dateStr);
        return sdf.format(date);
    }

    public static String getLastDayOfMonth(String yearMonth) {
        int year = Integer.parseInt(yearMonth.split("-")[0]);  //年
        int month = Integer.parseInt(yearMonth.split("-")[1]); //月
        Calendar cal = Calendar.getInstance();
        // 设置年份
        cal.set(Calendar.YEAR, year);
        // 设置月份
        cal.set(Calendar.MONTH, month - 1);
        // 获取某月最大天数
        int lastDay = cal.getActualMaximum(Calendar.DATE);
        // 设置日历中月份的最大天数
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        // 格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(cal.getTime());
    }

    public static Date parseDate(String source, String partern) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat(partern);
        return sdf.parse(source);
    }

    public static String formatDate(Date source, String partern){
        SimpleDateFormat sdf = new SimpleDateFormat(partern);
        return sdf.format(source);
    }

    public static String getLastMonthDate(){
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH);
        cal.set(Calendar.MONTH, month - 2);

        return formatDate(cal.getTime(), yyyyMMdd);
    }

    public static String getLastMonth(int m){
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH);
        cal.set(Calendar.MONTH, month - m);

        return formatDate(cal.getTime(), yyyyMM);
    }

    public static String getTargetMonth(int y, int month){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR) - y;
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);

        return formatDate(cal.getTime(), yyyyMM);
    }

    public static int getYear(int year){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, year);

        return cal.get(Calendar.YEAR);
    }

    public static void main(String[] args) {
        int year = getYear(-1);
        System.out.println(year);
    }

}
