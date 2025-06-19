package vn.com.nested.backend.common.operation;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author tippy091
 * @created 02/06/2025
 * @project server
 **/

public class DateTimeUtil {
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss.SSS";

    private DateTimeUtil() {
    }

    public static String getCurrentDateAsString() {
        return LocalDate.now().toString();
    }

    public static String getCurrentTimeAsString() {
        return LocalTime.now().toString();
    }

    public static String getCurrentDateTimeAsString() {
        return LocalDateTime.now().toString();
    }

    public static String getCurrentDateTimeAsString(String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return formatter.format(LocalDateTime.now());
    }

    public static String getCurrentDateTimeAsString(String format, long day) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        LocalDateTime newDateTime = LocalDateTime.now().plusDays(day);
        return formatter.format(newDateTime);
    }

    public static String getDateTimeAsString(LocalDateTime localDateTime, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return formatter.format(localDateTime);
    }

    public static String getDateAsString(LocalDate localDate, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return formatter.format(localDate);
    }

    public static LocalDateTime toLocalDateTime(Date date) {
        ZoneId defaultZoneId = ZoneId.systemDefault();
        Instant instant = date.toInstant();
        return instant.atZone(defaultZoneId).toLocalDateTime();
    }

    public static String toLocalDateTimeAsString(Date date, String format) {
        LocalDateTime localDateTime = toLocalDateTime(date);
        return getDateTimeAsString(localDateTime, format);
    }

    public static LocalDate toLocalDate(Date date) {
        ZoneId defaultZoneId = ZoneId.systemDefault();
        Instant instant = date.toInstant();
        return instant.atZone(defaultZoneId).toLocalDate();
    }

    public static String toLocalDateAsString(Date date, String format) {
        LocalDate localDate = toLocalDate(date);
        return getDateAsString(localDate, format);
    }
}
