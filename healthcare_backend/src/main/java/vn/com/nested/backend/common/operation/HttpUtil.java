package vn.com.nested.backend.common.operation;

import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.codehaus.jettison.json.JSONObject;

import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * @author tippy091
 * @created 02/06/2025
 * @project server
 **/
public class HttpUtil {
    public HttpUtil() {
    }

    public static String getClientIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }

    public static String getServerIpAddr() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception var1) {
            return "unknown";
        }
    }

    public static String getClientOS(HttpServletRequest request) {
        String browserDetails = request.getHeader("User-Agent");
        String lowerCaseBrowser = browserDetails.toLowerCase();
        if (lowerCaseBrowser.contains("windows")) {
            return "Windows";
        } else if (lowerCaseBrowser.contains("mac")) {
            return "Mac";
        } else if (lowerCaseBrowser.contains("x11")) {
            return "Unix";
        } else if (lowerCaseBrowser.contains("android")) {
            return "Android";
        } else {
            return lowerCaseBrowser.contains("iphone") ? "IPhone" : "UnKnown, More-Info: " + browserDetails;
        }
    }

    public static String getClientBrowser(HttpServletRequest request) {
        String browserDetails = request.getHeader("User-Agent");
        String user = browserDetails.toLowerCase();
        String browser = "";
        String var10000;
        if (user.contains("msie")) {
            String substring = browserDetails.substring(browserDetails.indexOf("MSIE")).split(";")[0];
            var10000 = substring.split(" ")[0].replace("MSIE", "IE");
            browser = var10000 + "-" + substring.split(" ")[1];
        } else if (browserDetails.indexOf("Version") > 0) {
            String[] arrBrowserDetails = browserDetails.substring(browserDetails.indexOf("Version")).split(" ");
            if (user.contains("safari") && user.contains("version")) {
                var10000 = browserDetails.substring(browserDetails.indexOf("Safari")).split(" ")[0].split("/")[0];
                browser = var10000 + "-" + arrBrowserDetails[0].split("/")[1];
            } else if (user.contains("opera")) {
                var10000 = browserDetails.substring(browserDetails.indexOf("Opera")).split(" ")[0].split("/")[0];
                browser = var10000 + "-" + arrBrowserDetails[0].split("/")[1];
            }
        } else if (user.contains("opr")) {
            browser = browserDetails.substring(browserDetails.indexOf("OPR")).split(" ")[0].replace("/", "-").replace("OPR", "Opera");
        } else if (user.contains("chrome")) {
            browser = browserDetails.substring(browserDetails.indexOf("Chrome")).split(" ")[0].replace("/", "-");
        } else if (!user.contains("mozilla/7.0") && !user.contains("netscape6") && !user.contains("mozilla/4.7") && !user.contains("mozilla/4.78") && !user.contains("mozilla/4.08") && !user.contains("mozilla/3")) {
            if (user.contains("firefox")) {
                browser = browserDetails.substring(browserDetails.indexOf("Firefox")).split(" ")[0].replace("/", "-");
            } else if (user.contains("rv")) {
                browser = "IE";
            } else {
                browser = "UnKnown, More-Info: " + browserDetails;
            }
        } else {
            browser = "Netscape-?";
        }

        return browser;
    }

    public static String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }

    public static String getReferer(HttpServletRequest request) {
        return request.getHeader("referer");
    }

    public static String getFullURL(HttpServletRequest request) {
        StringBuffer requestURL = request.getRequestURL();
        String queryString = request.getQueryString();
        return queryString == null ? requestURL.toString() : requestURL.append('?').append(queryString).toString();
    }

    public static JsonObject getRequestJson(HttpServletRequest request) {
        String jsonBody = (String)request.getAttribute("JSON_REQUEST_BODY");
        if (jsonBody == null) {
            try {
                String body = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
                JSONObject jRequest = ConvertUtils.toJSONObject(body);
                Iterator keyIt = jRequest.keys();

                while(keyIt.hasNext()) {
                    String key = (String)keyIt.next();
                    if (key.contains("password")) {
                        CommonUtils.putJSONObject(jRequest, key, "******");
                    }
                }

                return ConvertUtils.toGSon(jRequest.toString());
            } catch (Exception var6) {
            }
        }

        return new JsonObject();
    }

    public static JsonObject getRequestParams(HttpServletRequest request) {
        try {
            Map parameters = (Map) Collections.list(request.getParameterNames()).stream().collect(Collectors.toMap(Function.identity(), (p) -> {
                return markingIncentive(p, Arrays.asList(request.getParameterValues(p)));
            }));
            return ConvertUtils.toGSon((new JSONObject(parameters)).toString());
        } catch (Exception var2) {
            return new JsonObject();
        }
    }

    private static List<Object> markingIncentive(String key, List<Object> value) {
        return (List)(key.contains("password") ? new ArrayList() : value);
    }
}
