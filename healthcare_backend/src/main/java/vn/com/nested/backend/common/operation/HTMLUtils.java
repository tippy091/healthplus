package vn.com.nested.backend.common.operation;

/**
 * @author tippy091
 * @created 02/06/2025
 * @project server
 **/
public class HTMLUtils {
    public HTMLUtils() {
    }

    public static String getTR() {
        return "<TR>";
    }

    public static String getTD(String style, String value) {
        return "<TD class=\"" + style + "\"> " + value + " </TD>";
    }

    public static String getTD() {
        return "<TD>";
    }

    public static String getLi(String key, String value) {
        return "<li><strong>" + key + ": </strong>" + value + "</li>";
    }

    public static String getUl(String listLI) {
        return "<ul>" + listLI + "</ul>";
    }
}