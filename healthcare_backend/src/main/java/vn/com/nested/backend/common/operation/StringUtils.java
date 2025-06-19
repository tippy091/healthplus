package vn.com.nested.backend.common.operation;

import com.google.common.base.Splitter;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;

/**
 * @author tippy091
 * @created 02/06/2025
 * @project server
 **/
public class StringUtils {
    private static final Charset UTF8_CHARSET;
    public static String EMPTY;

    public StringUtils() {
    }

    public static String decodeUTF8(byte[] bytes) {
        return new String(bytes, UTF8_CHARSET);
    }

    public static byte[] encodeUTF8(String string) {
        return string.getBytes(UTF8_CHARSET);
    }

    public static boolean isNullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }

    public static String toEmpty(Object input) {
        return CommonUtils.nullOrBlank(input) ? "" : input.toString();
    }

    public static String appendZero(String value, int maxLength) {
        return maxLength < 0 ? "" : String.format("%0" + maxLength + "d", value);
    }

    public static String encode(String plainText) {
        return Base64.encodeBase64URLSafeString(plainText.getBytes());
    }

    public static String decode(String encodedString) {
        return new String(Base64.decodeBase64(encodedString), StandardCharsets.UTF_8);
    }

    public static String truncate(String input, int maxLength) {
        if (input == null) {
            return "";
        } else {
            return input.length() <= maxLength ? input : input.substring(0, maxLength);
        }
    }

    public static void toHTML(String parentKey, Object obj, StringBuilder builderHTML) {
        if (obj instanceof JSONObject) {
            Iterator keys = ((JSONObject)obj).keys();
            if (parentKey != null) {
                builderHTML.append("<li><strong>").append(parentKey).append("</strong></li>");
            }

            builderHTML.append("<ul>");

            while(keys.hasNext()) {
                String key = (String)keys.next();

                try {
                    toHTML(key, ((JSONObject)obj).get(key), builderHTML);
                } catch (JSONException var6) {
                    ExceptionUtils.printRootCauseStackTrace(var6);
                }
            }

            builderHTML.append("</ul>");
        } else {
            builderHTML.append(HTMLUtils.getLi(parentKey, obj.toString()));
        }

    }

    public static Map<String, String> parseMap(String formattedMap) {
        return Splitter.on(",").withKeyValueSeparator("=").split(formattedMap);
    }

    static {
        UTF8_CHARSET = StandardCharsets.UTF_8;
        EMPTY = "";
    }
}
