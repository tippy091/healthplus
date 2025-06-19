package vn.com.nested.backend.common.operation;

import com.google.gson.JsonObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author tippy091
 * @created 02/06/2025
 * @project server
 **/

public class DateTimeUtils {
    private static final Map<String, String> DATE_FORMAT_REGEXPS = new HashMap<String, String>() {
        {
            this.put("^\\d{8}$", "yyyyMMdd");
            this.put("^\\d{1,2}-\\d{1,2}-\\d{4}$", "dd-MM-yyyy");
            this.put("^\\d{4}-\\d{1,2}-\\d{1,2}$", "yyyy-MM-dd");
            this.put("^\\d{1,2}/\\d{1,2}/\\d{4}$", "MM/dd/yyyy");
            this.put("^\\d{4}/\\d{1,2}/\\d{1,2}$", "yyyy/MM/dd");
            this.put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}$", "dd MMM yyyy");
            this.put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}$", "dd MMMM yyyy");
            this.put("^\\d{12}$", "yyyyMMddHHmm");
            this.put("^\\d{8}\\s\\d{4}$", "yyyyMMdd HHmm");
            this.put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}$", "dd-MM-yyyy HH:mm");
            this.put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}$", "yyyy-MM-dd HH:mm");
            this.put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}$", "MM/dd/yyyy HH:mm");
            this.put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}$", "yyyy/MM/dd HH:mm");
            this.put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}$", "dd MMM yyyy HH:mm");
            this.put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}$", "dd MMMM yyyy HH:mm");
            this.put("^\\d{14}$", "yyyyMMddHHmmss");
            this.put("^\\d{8}\\s\\d{6}$", "yyyyMMdd HHmmss");
            this.put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd-MM-yyyy HH:mm:ss");
            this.put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$", "yyyy-MM-dd HH:mm:ss");
            this.put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}.\\d{9}$", "yyyy-MM-dd HH:mm:ss.SSSSSSSSS");
            this.put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}.\\d{3}$", "yyyy-MM-dd HH:mm:ss.SSS");
            this.put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}.\\d{2}$", "yyyy-MM-dd HH:mm:ss.SS");
            this.put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}.\\d{1}$", "yyyy-MM-dd HH:mm:ss.S");
            this.put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "MM/dd/yyyy HH:mm:ss");
            this.put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$", "yyyy/MM/dd HH:mm:ss");
            this.put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMM yyyy HH:mm:ss");
            this.put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMMM yyyy HH:mm:ss");
        }
    };

    public DateTimeUtils() {
    }

    public static String getDate(Date date, String format) {
        if (CommonUtils.nullOrBlank(date)) {
            return "";
        } else {
            DateFormat dateFormat = CommonUtils.nullOrBlank(format) ? new SimpleDateFormat("yyyy-MM-dd") : new SimpleDateFormat(format);
            return dateFormat.format(date);
        }
    }

    public static String getDate(LocalDate date, String format) {
        if (CommonUtils.nullOrBlank(date)) {
            return "";
        } else {
            DateTimeFormatter dateFormat = CommonUtils.nullOrBlank(format) ? DateTimeFormatter.ofPattern("yyyy-MM-dd") : DateTimeFormatter.ofPattern(format);
            return date.format(dateFormat);
        }
    }

    public static String getDate(LocalDateTime date, String format) {
        if (CommonUtils.nullOrBlank(date)) {
            return "";
        } else {
            DateTimeFormatter dateFormat = CommonUtils.nullOrBlank(format) ? DateTimeFormatter.ofPattern("yyyy-MM-dd") : DateTimeFormatter.ofPattern(format);
            return date.format(dateFormat);
        }
    }

    public static String changeDateFormat(String date, String fromFormat, String toFormat) {
        String DATETTIME_WITH_MILIS = "yyyy-MM-dd HH:mm:ss.S";
        String DATETTIME_WITHOUT_MILIS = "yyyy-MM-dd HH:mm:ss";
        Date rawDate;
        if (!"yyyy-MM-dd HH:mm:ss.S".equals(fromFormat) && !"yyyy-MM-dd HH:mm:ss".equals(fromFormat)) {
            rawDate = toDate(date, fromFormat);
        } else {
            rawDate = toDate(date, "yyyy-MM-dd HH:mm:ss.S");
            if (CommonUtils.nullOrBlank(rawDate)) {
                rawDate = toDate(date, "yyyy-MM-dd HH:mm:ss");
            }
        }

        String result = getDate(rawDate, toFormat);
        return CommonUtils.nullOrBlank(result) ? StringUtils.toEmpty(date) : result;
    }

    public static String changeDateFormat(String date, String toFormat) {
        String fromFormat = determineDateFormat(date);
        String DATETTIME_WITH_MILIS = "yyyy-MM-dd HH:mm:ss.S";
        String DATETTIME_WITHOUT_MILIS = "yyyy-MM-dd HH:mm:ss";
        Date rawDate;
        if (!"yyyy-MM-dd HH:mm:ss.S".equals(fromFormat) && !"yyyy-MM-dd HH:mm:ss".equals(fromFormat)) {
            rawDate = toDate(date, fromFormat);
        } else {
            rawDate = toDate(date, "yyyy-MM-dd HH:mm:ss.S");
            if (CommonUtils.nullOrBlank(rawDate)) {
                rawDate = toDate(date, "yyyy-MM-dd HH:mm:ss");
            }
        }

        String result = getDate(rawDate, toFormat);
        return CommonUtils.nullOrBlank(result) ? StringUtils.toEmpty(date) : result;
    }

    public static Date toDate(String date, String format) {
        if (CommonUtils.nullOrBlank(date)) {
            return null;
        } else {
            try {
                DateFormat dateFormat = CommonUtils.nullOrBlank(format) ? new SimpleDateFormat("yyyy-MM-dd") : new SimpleDateFormat(format);
                return dateFormat.parse(date);
            } catch (Exception var3) {
                return null;
            }
        }
    }

    public static LocalDate toLocalDate(String date, String format) {
        if (CommonUtils.nullOrBlank(date)) {
            return null;
        } else {
            try {
                DateTimeFormatter dateFormat = CommonUtils.nullOrBlank(format) ? DateTimeFormatter.ofPattern("yyyy-MM-dd") : DateTimeFormatter.ofPattern(format);
                dateFormat = dateFormat.withLocale(Locale.getDefault());
                return LocalDate.parse(date, dateFormat);
            } catch (Exception var3) {
                return null;
            }
        }
    }

    public static LocalDateTime toLocalDateTime(String date, String format) {
        if (CommonUtils.nullOrBlank(date)) {
            return null;
        } else {
            try {
                DateTimeFormatter dateFormat = CommonUtils.nullOrBlank(format) ? DateTimeFormatter.ofPattern("yyyy-MM-dd") : DateTimeFormatter.ofPattern(format);
                dateFormat = dateFormat.withLocale(Locale.getDefault());
                return LocalDateTime.parse(date, dateFormat);
            } catch (Exception var3) {
                return null;
            }
        }
    }

    public static Date toDate(LocalDate localDate) {
        try {
            return java.sql.Date.valueOf(localDate);
        } catch (Exception var2) {
            return null;
        }
    }

    public static String convertDate(String datetime, String currentFormat, String newFormat) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(currentFormat);
            LocalDate tmpDate = LocalDate.parse(datetime, formatter);
            return DateTimeUtil.getDateAsString(tmpDate, newFormat);
        } catch (Exception var5) {
            return null;
        }
    }

    public static String convertDateTime(String datetime, String currentFormat, String newFormat) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(currentFormat);
            LocalDateTime tmpDate = LocalDateTime.parse(datetime, formatter);
            DateTimeFormatter newFormatter = DateTimeFormatter.ofPattern(newFormat);
            return newFormatter.format(tmpDate);
        } catch (Exception var6) {
            return null;
        }
    }

    public static LocalDate toLocalDate(Date date) {
        try {
            return (new java.sql.Date(date.getTime())).toLocalDate();
        } catch (Exception var2) {
            return null;
        }
    }

    public static LocalDate convertToLocalDate(String datetime, String currentFormat) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(currentFormat);
            return LocalDate.parse(datetime, formatter);
        } catch (Exception var3) {
            return null;
        }
    }

    public static String determineDateFormat(String dateString) {
        Iterator var1 = DATE_FORMAT_REGEXPS.keySet().iterator();

        String regexp;
        do {
            if (!var1.hasNext()) {
                return null;
            }

            regexp = (String)var1.next();
        } while(!dateString.toLowerCase().matches(regexp));

        return (String)DATE_FORMAT_REGEXPS.get(regexp);
    }

    public static List<String> getYMBetween(LocalDate dateFrom, LocalDate dateTo) {
        List<String> result = new ArrayList();
        String ymTo = getDate(dateTo, "yyyyMM");

        for(LocalDate iterDate = dateFrom; getDate(iterDate, "yyyyMM").compareTo(ymTo) <= 0; iterDate = iterDate.plusMonths(1L)) {
            result.add(getDate(iterDate, "yyyyMM"));
        }

        return result;
    }

    public static String getCurrentDateTime(String formatDateTime) {
        if ("timestamp".equalsIgnoreCase(formatDateTime)) {
            return String.valueOf(System.currentTimeMillis());
        } else {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatDateTime);
            return simpleDateFormat.format(new Date());
        }
    }

    public static String changeDateFormat(long timestamp, String format) {
        return DateTimeUtil.toLocalDateTimeAsString(new Date(timestamp), format);
    }

    public static String convertObjectIdTodate(String objectId, String dateFormat) {
        long l = Long.parseLong(objectId.substring(0, 8), 16) * 1000L;
        Date date = new Date(l);
        return getDate(date, dateFormat);
    }

    public static Date getDay(int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(1);
        int month = calendar.get(2);
        int day = calendar.get(5);
        calendar.set(year, month, day, hour, minute, second);
        return calendar.getTime();
    }

    public static String getDateTime(JsonObject data, String key) {
        JsonObject kycUpdatedObj = ConvertUtils.getGsonObject(data, key);
        return getDateTime(kycUpdatedObj);
    }

    public static String getDateTime(JsonObject dateObject) {
        try {
            long mDate = ConvertUtils.getGsonLong(dateObject, "$numberLong", (String)null);
            if (mDate > 0L) {
                return changeDateFormat(mDate, "yyyy-MM-dd HH:mm:ss");
            }
        } catch (Throwable var3) {
        }

        return "";
    }

    public static long convertToMillis(String datetime, String currentFormat) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(currentFormat);
            LocalDateTime tmpDate = LocalDateTime.parse(datetime, formatter);
            return ZonedDateTime.of(tmpDate, ZoneId.systemDefault()).toInstant().toEpochMilli();
        } catch (Exception var4) {
            return 0L;
        }
    }
}
