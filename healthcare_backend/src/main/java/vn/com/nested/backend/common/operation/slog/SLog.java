package vn.com.nested.backend.common.operation.slog;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.com.nested.backend.common.operation.ConvertUtils;
import vn.com.nested.backend.common.operation.HttpUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import vn.com.nested.backend.common.operation.audit.PortalLogWriter;
import vn.com.nested.backend.common.operation.slog.logging.LogMessage;
import vn.com.nested.backend.common.operation.slog.logging.SingletonLogWriter;
import vn.com.nested.backend.logging.JsonLogBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author tippy091
 * @created 17/06/2025
 * @project healthcare_backend
 **/

public class SLog {
    private static final Logger LOGGER = LoggerFactory.getLogger(SLog.class);
    private static final String ALERT_MSG = "alertMsg";
    private final int ZERO_PAD;
    private final String sessionId;
    private final Map<String, Long> timerStartMap;
    private final MemoryLog memoryLog;
    private final int MAX_LENGTH;
    private String VERSION;
    private LogBuilder logBuilder;
    private int logCount;

    public SLog(HttpServletRequest httpServletRequest, String sessionId) {
        this.ZERO_PAD = 5;
        this.timerStartMap = new HashMap();
        this.MAX_LENGTH = 3000;
        this.VERSION = "v0.77.4";
        this.logCount = 0;
        this.logBuilder = new DefaultLogBuilder();
        this.sessionId = sessionId;
        this.memoryLog = new MemoryLog();
        String serverIp = HttpUtil.getServerIpAddr();
        String requestTime = ConvertUtils.getNow("dd/MM/yyyy HH:mm:ss.SSS");
        this.logBuilder.append("ServerIp", serverIp);
        this.logBuilder.append("SessionId", sessionId);
        this.logBuilder.append("RequestDateTime", requestTime);
        JsonObject json = new JsonObject();
        json.add(".trace", new JsonPrimitive(this.getCurrentTrace()));
        if (httpServletRequest != null) {
            String referer = HttpUtil.getReferer(httpServletRequest);
            String fullURL = HttpUtil.getFullURL(httpServletRequest);
            String clientIpAddr = HttpUtil.getClientIpAddr(httpServletRequest);
            String clientOS = HttpUtil.getClientOS(httpServletRequest);
            String clientBrowser = HttpUtil.getClientBrowser(httpServletRequest);
            String userAgent = HttpUtil.getUserAgent(httpServletRequest);
            JsonObject requestJson = HttpUtil.getRequestJson(httpServletRequest);
            JsonObject requestParam = HttpUtil.getRequestParams(httpServletRequest);
            String method = ConvertUtils.toEmpty(httpServletRequest.getMethod());
            this.logBuilder.append("FullURL", fullURL);
            json.addProperty("UserAgent", userAgent);
            json.addProperty("OperatingSystem", clientOS);
            json.addProperty("BrowserName", clientBrowser);
            json.addProperty("IPAddress", clientIpAddr);
            json.addProperty("Referrer", referer);
            json.addProperty("RequestParams", this.trimLog(requestParam.toString()));
            json.addProperty("RequestJson", requestJson.toString());
            json.addProperty("Method", method);
        }

        this.logBuilder.append(ConvertUtils.toEmpty(String.format("%05d", this.logCount)) + "_request", json);
        ++this.logCount;
    }

    public SLog() {
        this(UUID.randomUUID().toString());
    }

    public SLog(String sessionId) {
        this((HttpServletRequest)null, sessionId);
    }

    public void addTimerStart(String name) {
        this.timerStartMap.put(name, System.currentTimeMillis());
    }

    public void addTimerEnd(String name) {
        if (this.timerStartMap.containsKey(name)) {
            long start = (Long)this.timerStartMap.get(name);
            long end = System.currentTimeMillis();
            long duration = end - start;
            this.timerStartMap.remove(name);
            this.logBuilder.append(name + ".duration", duration);
            this.logBuilder.append(name + ".start", start);
        }
    }

    public void setVersion(String ver) {
        this.VERSION = ver;
    }

    private String trimLog(String log) {
        return log.length() > 3000 ? log.substring(0, 3000) + "..." : log;
    }

    private String getRequestHeader(HttpServletRequest request) {
        try {
            return (String) Collections.list(request.getHeaderNames()).stream().map((headerName) -> {
                return headerName + " : " + String.valueOf(Collections.list(request.getHeaders(headerName)));
            }).collect(Collectors.joining(", "));
        } catch (Exception var3) {
            LOGGER.error(ExceptionUtils.getStackTrace(var3));
            return "";
        }
    }

    public SLog append(String... keyValues) {
        try {
            JsonObject json = new JsonObject();
            json.add(".trace", new JsonPrimitive(this.getCurrentTrace()));

            for(int i = 0; i < keyValues.length - 1; i += 2) {
                json.add(keyValues[i], new JsonPrimitive(ConvertUtils.toEmpty(keyValues[i + 1])));
            }

            String logData = this.trimLog(json.toString());
            this.logBuilder.append(ConvertUtils.toEmpty(String.format("%05d", this.logCount)), logData);
            ++this.logCount;
        } catch (Exception var5) {
        }

        return this;
    }

    public SLog add(String key, String value) {
        try {
            this.logBuilder.append(key, value);
        } catch (Exception var4) {
        }

        return this;
    }

    public SLog add(String key, long value) {
        try {
            this.logBuilder.append(key, value);
        } catch (Exception var5) {
        }

        return this;
    }

    public SLog add(String key, int value) {
        try {
            this.logBuilder.append(key, value);
        } catch (Exception var4) {
        }

        return this;
    }

    public SLog add(String key, double value) {
        try {
            this.logBuilder.append(key, value);
        } catch (Exception var5) {
        }

        return this;
    }

    public SLog append(String param, String comment, String exception) {
        try {
            JsonObject json = new JsonObject();
            json.add(".trace", new JsonPrimitive(this.getCurrentTrace()));
            json.add("1_param", new JsonPrimitive(ConvertUtils.toEmpty(param)));
            json.add("2_comment", new JsonPrimitive(ConvertUtils.toEmpty(comment)));
            json.add("3_exception", new JsonPrimitive(ConvertUtils.toEmpty(exception)));
            this.logBuilder.append(ConvertUtils.toEmpty(String.format("%05d", this.logCount)), json);
            ++this.logCount;
        } catch (Exception var5) {
        }

        return this;
    }

    public SLog appendAlert(String param, String exception, String alertMsg) {
        JsonObject json = new JsonObject();
        json.add(".trace", new JsonPrimitive(this.getCurrentTrace()));
        json.add("1_param", new JsonPrimitive(ConvertUtils.toEmpty(param)));
        json.add("2_exception", new JsonPrimitive(ConvertUtils.toEmpty(exception)));
        this.logBuilder.append(ConvertUtils.toEmpty(String.format("%05d", this.logCount)), json);
        ++this.logCount;
        this.logBuilder.append("alertMsg", alertMsg);
        return this;
    }

    public SLog append(String param, String comment, Exception exception) {
        try {
            JsonObject json = new JsonObject();
            json.add(".trace", new JsonPrimitive(this.getCurrentTrace()));
            json.add("1_param", new JsonPrimitive(ConvertUtils.toEmpty(param)));
            json.add("2_comment", new JsonPrimitive(ConvertUtils.toEmpty(comment)));
            json.add("3_exception", new JsonPrimitive(ConvertUtils.toEmpty(ExceptionUtils.getStackTrace(exception))));
            exception.printStackTrace();
            this.logBuilder.append(ConvertUtils.toEmpty(String.format("%05d", this.logCount)), json);
            ++this.logCount;
        } catch (Exception var5) {
        }

        return this;
    }

    private String getCurrentTrace() {
        StackTraceElement e = Thread.currentThread().getStackTrace()[3];
        return e.toString().substring(e.toString().indexOf("(") + 1, e.toString().indexOf(")"));
    }

    public void writeAndClear() {
        this.writeLog("SUCCESS", true);
    }

    public void writeLog(String respCode, boolean isClear) {
        try {
            this.logBuilder.append("memoryLog", this.memoryLog.getMemory());
            String var10001 = this.sessionId;
            LOGGER.info(var10001 + "\n" + this.logBuilder.buildPrettyFormat());
            JsonLogBuilder simpleLogBuilder = new JsonLogBuilder();
            simpleLogBuilder.set("module", System.getProperty("application.name")).set("opVer", this.VERSION);
            simpleLogBuilder.set("respCode", respCode);
            LogMessage simpleLog = simpleLogBuilder.set("data", new vn.com.nested.backend.common.operation.json.JsonObject(this.logBuilder.buildObject())).build();

            try {
                SingletonLogWriter.INSTANCE.write(simpleLog);
            } catch (Exception var12) {
                LOGGER.debug("SingletonLogWriter error.");
            }

            try {
                PortalLogWriter.INSTANCE.write(simpleLog);
            } catch (Exception var11) {
                LOGGER.debug("PortalLogWriter error.");
            }
        } catch (Exception var13) {
            LOGGER.error("Cannot write kafka log.");
        } finally {
            if (isClear) {
                this.logCount = 0;
                this.logBuilder = new DefaultLogBuilder();
                this.logBuilder.append("ServerIp", HttpUtil.getServerIpAddr());
                this.logBuilder.append("SessionId", this.sessionId);
                this.logBuilder.append("RequestDateTime", ConvertUtils.getNow("dd/MM/yyyy HH:mm:ss.SSS"));
                this.memoryLog.reset();
            }

        }

    }

    public void writeAndClear(String respCode) {
        this.writeLog(respCode, true);
    }

    public void writeLog(boolean isClear) {
        this.writeLog("SUCCESS", isClear);
    }

    public void writeTempLogs(boolean isWriteLogs) {
        if (isWriteLogs) {
            String var10001 = this.sessionId;
            LOGGER.info(var10001 + "\n" + this.logBuilder.buildPrettyFormat());
        }

    }

    public void writeAuditAndClear(OPLogWriter writer) {
        this.writeLogAudit(true, writer);
    }

    public void writeLogAudit(boolean isClear, OPLogWriter writer) {
        try {
            JsonLogBuilder simpleLogBuilder = new JsonLogBuilder();
            simpleLogBuilder.set("module", System.getProperty("application.name") + "Audit").set("opVer", this.VERSION);
        } catch (Exception var4) {
            LOGGER.error("Cannot write kafka log.");
        }

    }

    public LogBuilder getLogBuilder() {
        return this.logBuilder;
    }
}
