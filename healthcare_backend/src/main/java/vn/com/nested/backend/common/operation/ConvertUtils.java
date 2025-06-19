package vn.com.nested.backend.common.operation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.gson.*;
import org.apache.commons.codec.binary.Base64;
import com.google.gson.reflect.TypeToken;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import com.monitorjbl.xlsx.StreamingReader;
import vn.com.nested.backend.common.operation.CommonUtils;
import vn.com.nested.backend.common.operation.DateTimeUtils;
import vn.com.nested.backend.common.operation.slog.SLog;
import vn.com.nested.backend.common.uis.utils.CryptoUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
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

public class ConvertUtils {
    public static final String EMPTY_STRING = "";
    public static final String ZERO_STRING = "0";
    public static final String DEFAULT_DATE_FORMAT = "yyyyMMdd";
    public static final String DEFAULT_DATETIME_FORMAT = "yyyyMMddHHmmss";
    public static final String DEFAULT_NUMBER_FORMAT = "###,###.##";
    public static final String DEFAULT_SEPARATOR = ",";
    protected static final Logger LOGGER = LoggerFactory.getLogger(ConvertUtils.class);
    private static final String KHONG = "không";
    private static final String MOT = "một";
    private static final String HAI = "hai";
    private static final String BA = "ba";
    private static final String BON = "bốn";
    private static final String NAM = "năm";
    private static final String SAU = "sáu";
    private static final String BAY = "bảy";
    private static final String TAM = "tám";
    private static final String CHIN = "chín";
    private static final String LAM = "lăm";
    private static final String LE = "lẻ";
    private static final String MUOI = "mươi";
    private static final String MUOIF = "mười";
    private static final String MOTS = "mốt";
    private static final String TRAM = "trăm";
    private static final String NGHIN = "nghìn";
    private static final String TRIEU = "triệu";
    private static final String TY = "tỷ";
    private static final String[] NUMBER = new String[]{"không", "một", "hai", "ba", "bốn", "năm", "sáu", "bảy", "tám", "chín"};

    public ConvertUtils() {
    }

    public static String toEmpty(Object input) {
        return CommonUtils.nullOrBlank(input) ? "" : input.toString();
    }

    public static boolean checkJSONArray(JSONArray arr) {
        if (null == arr) {
            return false;
        } else {
            try {
                if (arr.length() > 0) {
                    return true;
                }
            } catch (Exception var2) {
            }

            return false;
        }
    }

    public static String toNull(Object input) {
        return CommonUtils.nullOrBlank(input) ? null : input.toString();
    }

    public static boolean getJBoolean(JSONObject jsonObject, String key) {
        try {
            return !CommonUtils.nullOrBlank(jsonObject) && !CommonUtils.nullOrBlank(key) && jsonObject.has(key) && !CommonUtils.nullOrBlank(jsonObject.getString(key)) ? jsonObject.getBoolean(key) : false;
        } catch (JSONException var3) {
            return false;
        }
    }

    public static Long getJLong(JSONObject jsonObject, String key) {
        try {
            return !CommonUtils.nullOrBlank(jsonObject) && !CommonUtils.nullOrBlank(key) && jsonObject.has(key) && !CommonUtils.nullOrBlank(jsonObject.getString(key)) ? jsonObject.getLong(key) : 0L;
        } catch (JSONException var3) {
            return 0L;
        }
    }

    public static Integer getJInt(JSONObject jsonObject, String key) {
        try {
            return !CommonUtils.nullOrBlank(jsonObject) && !CommonUtils.nullOrBlank(key) && jsonObject.has(key) && !CommonUtils.nullOrBlank(jsonObject.getString(key)) ? jsonObject.getInt(key) : 0;
        } catch (JSONException var3) {
            return 0;
        }
    }

    public static String getJFormatNumber(JSONObject jsonObject, String key) {
        try {
            return !CommonUtils.nullOrBlank(jsonObject) && !CommonUtils.nullOrBlank(key) && jsonObject.has(key) && !CommonUtils.nullOrBlank(jsonObject.getString(key)) ? getNumber(jsonObject.getDouble(key), "###,###.##") : "0";
        } catch (JSONException var3) {
            return "0";
        }
    }

    public static double getJDouble(JSONObject jsonObject, String key) {
        try {
            if (!CommonUtils.nullOrBlank(jsonObject) && !CommonUtils.nullOrBlank(key) && jsonObject.has(key) && !CommonUtils.nullOrBlank(jsonObject.getString(key))) {
                String objectString = jsonObject.getString(key);
                return Double.parseDouble(objectString.replaceAll(",", ""));
            } else {
                return 0.0;
            }
        } catch (JSONException var3) {
            return 0.0;
        }
    }

    public static Long getJLong(JsonObject jsonObject, String key) {
        return !CommonUtils.nullOrBlank(jsonObject) && !CommonUtils.nullOrBlank(key) && jsonObject.has(key) ? jsonObject.get(key).getAsLong() : 0L;
    }

    public static JSONObject convertToJSON(String jStr) {
        try {
            return new JSONObject(jStr);
        } catch (JSONException var2) {
            return null;
        }
    }

    public static JSONArray convertToJArr(String jStr) {
        try {
            return new JSONArray(jStr);
        } catch (JSONException var2) {
            return new JSONArray();
        }
    }

    public static JSONObject convertToJSON(String jStr, SLog sLog) {
        try {
            return new JSONObject(jStr);
        } catch (JSONException var3) {
            if (sLog != null) {
                sLog.append(new String[]{"convertToJSON", "invalid json", "data", jStr});
            }

            return null;
        }
    }

    public static JsonObject toGSon(String jSTr) {
        try {
            return JsonParser.parseString(jSTr).getAsJsonObject();
        } catch (Exception var2) {
            return new JsonObject();
        }
    }

    public static JsonArray toGSonArray(String jSTr) {
        try {
            return JsonParser.parseString(jSTr).getAsJsonArray();
        } catch (Exception var2) {
            return new JsonArray();
        }
    }

    public static String getJString(JSONObject jsonObject, String key) {
        try {
            return !CommonUtils.nullOrBlank(jsonObject) && !CommonUtils.nullOrBlank(key) && jsonObject.has(key) ? jsonObject.getString(key) : "";
        } catch (Exception var3) {
            return "";
        }
    }

    public static String getJNString(JSONObject jsonObject, String key) {
        try {
            if (!CommonUtils.nullOrBlank(jsonObject) && !CommonUtils.nullOrBlank(key) && jsonObject.has(key)) {
                String value = jsonObject.getString(key);
                return StringUtils.isBlank(value) ? null : value;
            } else {
                return null;
            }
        } catch (JSONException var3) {
            return null;
        }
    }

    public static String getJNString(JsonObject jsonObject, String key) {
        try {
            if (!CommonUtils.nullOrBlank(jsonObject) && !CommonUtils.nullOrBlank(key) && jsonObject.has(key)) {
                String value = jsonObject.get(key).getAsString();
                return StringUtils.isBlank(value) ? null : value;
            } else {
                return null;
            }
        } catch (JsonIOException var3) {
            return null;
        }
    }

    public static String getJDateString(JSONObject jsonObject, String key, String currentFormat, String newFormat) {
        try {
            if (!CommonUtils.nullOrBlank(jsonObject) && !CommonUtils.nullOrBlank(key) && jsonObject.has(key)) {
                String value = jsonObject.getString(key);
                return DateTimeUtils.changeDateFormat(value, currentFormat, newFormat);
            } else {
                return null;
            }
        } catch (JSONException var5) {
            return null;
        }
    }

    public static String getJDateString(JSONObject jsonObject, String key, String newFormat) {
        try {
            if (!CommonUtils.nullOrBlank(jsonObject) && !CommonUtils.nullOrBlank(key) && jsonObject.has(key)) {
                String value = jsonObject.getString(key);
                return DateTimeUtils.changeDateFormat(value, newFormat);
            } else {
                return null;
            }
        } catch (JSONException var4) {
            return null;
        }
    }

    public static String getJString(JsonObject jsonObject, String key) {
        try {
            return !CommonUtils.nullOrBlank(jsonObject) && !CommonUtils.nullOrBlank(key) && jsonObject.has(key) ? jsonObject.get(key).getAsString() : "";
        } catch (Exception var3) {
            return "";
        }
    }

    public static String getJString(JSONArray arr, int index) {
        try {
            return !checkJSONArray(arr) ? "" : arr.getString(index);
        } catch (Exception var3) {
            return "";
        }
    }

    public static Object getJObject(JSONObject jsonObject, String key) {
        try {
            return !CommonUtils.nullOrBlank(jsonObject) && !CommonUtils.nullOrBlank(key) && jsonObject.has(key) ? jsonObject.get(key) : null;
        } catch (Exception var3) {
            return null;
        }
    }

    public static JsonObject getGsonObject(JsonObject jsonObject, String key) {
        try {
            return jsonObject.has(key) ? jsonObject.get(key).getAsJsonObject() : new JsonObject();
        } catch (Exception var3) {
            return new JsonObject();
        }
    }

    public static String getGsonString(JsonObject jsonObject, String key) {
        try {
            return jsonObject.has(key) ? jsonObject.get(key).getAsString() : "";
        } catch (Exception var3) {
            return "";
        }
    }

    public static String getGsonDateString(JsonObject jsonObject, String key, String newFormat) {
        String value = jsonObject.has(key) ? jsonObject.get(key).getAsString() : "";
        return DateTimeUtils.changeDateFormat(value, newFormat);
    }

    public static Long getGsonLong(JsonObject jsonObject, String key, String newFormat) {
        String value = jsonObject.has(key) ? jsonObject.get(key).getAsString() : "";
        return toLong(value);
    }

    public static Long toLong(String number) {
        return CommonUtils.nullOrBlank(number) ? 0L : Long.parseLong(number.replace(",", ""));
    }

    public static Double toDouble(String number) {
        return CommonUtils.nullOrBlank(number) ? 0.0 : Double.parseDouble(number.replace(",", ""));
    }

    public static String getNumber(Number number, String format) {
        if (CommonUtils.nullOrBlank(number)) {
            return "";
        } else {
            Locale locale = Locale.of("vi", "VN");
            DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance(locale);
            DecimalFormat decimalFormat = CommonUtils.nullOrBlank(format) ? new DecimalFormat("###,###.##", decimalFormatSymbols) : new DecimalFormat(format, decimalFormatSymbols);
            return decimalFormat.format(number);
        }
    }

    public static String getNumber(double number, String format) {
        if (CommonUtils.nullOrBlank(number)) {
            return "";
        } else {
            Locale locale = Locale.of("vi", "VN");
            DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance(locale);
            DecimalFormat decimalFormat = CommonUtils.nullOrBlank(format) ? new DecimalFormat("###,###.##", decimalFormatSymbols) : new DecimalFormat(format, decimalFormatSymbols);
            return decimalFormat.format(number);
        }
    }

    public static String getNumber(Number number, String format, String zeroChar) {
        if (CommonUtils.nullOrBlank(number)) {
            return "";
        } else if ("0".equals(toEmpty(number).substring(0, 1))) {
            return zeroChar;
        } else {
            DecimalFormat decimalFormat = CommonUtils.nullOrBlank(format) ? new DecimalFormat("###,###.##") : new DecimalFormat(format);
            return decimalFormat.format(number);
        }
    }

    public static String getNow(String format) {
        LocalDateTime sysDate = LocalDateTime.now();
        return DateTimeUtils.getDate(sysDate, format);
    }

    public static String changeDateFormatWithOutICT(String date, String fromFormat, String toFormat) {
        if (date.contains(" ICT")) {
            date = date.replace(" ICT", "");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(fromFormat);
        DateTimeFormatter newFormater = DateTimeFormatter.ofPattern(toFormat);
        LocalDateTime tmpDate = LocalDateTime.parse(date, formatter);
        ZonedDateTime zonedtime = tmpDate.atZone(ZoneId.of("UTC+7"));
        ZonedDateTime converted = zonedtime.withZoneSameInstant(ZoneId.of("UTC"));
        String result = converted.toLocalDateTime().format(newFormater);
        return CommonUtils.nullOrBlank(result) ? "" : result;
    }

    public static String getLastDateOfMonth(Date date, String format) {
        String yearStr = DateTimeUtils.getDate(date, "yyyy");
        String monthStr = DateTimeUtils.getDate(date, "MM");
        String dayStr = DateTimeUtils.getDate(date, "dd");
        int year = Integer.parseInt(yearStr);
        int month = Integer.parseInt(monthStr) - 1;
        int day = Integer.parseInt(dayStr);
        Calendar cal = new GregorianCalendar(year, month, day);
        int daysInMonth = cal.getActualMaximum(5);
        return DateTimeUtils.getDate(DateTimeUtils.toDate(yearStr + monthStr + daysInMonth, "yyyyMMdd"), format);
    }

    public static String getLastDateOfMonth(LocalDate date, String format) {
        String yearStr = DateTimeUtils.getDate(date, "yyyy");
        String monthStr = DateTimeUtils.getDate(date, "MM");
        String dayStr = DateTimeUtils.getDate(date, "dd");
        int year = Integer.parseInt(yearStr);
        int month = Integer.parseInt(monthStr) - 1;
        int day = Integer.parseInt(dayStr);
        Calendar cal = new GregorianCalendar(year, month, day);
        int daysInMonth = cal.getActualMaximum(5);
        return DateTimeUtils.getDate(DateTimeUtils.toDate(yearStr + monthStr + daysInMonth, "yyyyMMdd"), format);
    }

    public static JSONArray toJSONArray(JsonArray gjsonArray) {
        JSONArray jsonArray = new JSONArray();
        Iterator var2 = gjsonArray.iterator();

        while(var2.hasNext()) {
            JsonElement gjsonElement = (JsonElement)var2.next();
            JSONObject jsonObject = new JSONObject();
            Iterator var5 = gjsonElement.getAsJsonObject().entrySet().iterator();

            while(var5.hasNext()) {
                Map.Entry<String, JsonElement> entry = (Map.Entry)var5.next();
                String key = (String)entry.getKey();
                JsonElement value = (JsonElement)entry.getValue();
                if (value.isJsonPrimitive()) {
                    JsonPrimitive primValue = value.getAsJsonPrimitive();

                    try {
                        if (primValue.isNumber()) {
                            jsonObject.put(key, primValue.getAsNumber());
                        } else if (primValue.isString()) {
                            String valueStr = primValue.getAsString();
                            if (valueStr.equals("null")) {
                                jsonObject.put(key, "");
                            } else {
                                jsonObject.put(key, valueStr);
                            }
                        }
                    } catch (JSONException var11) {
                        LOGGER.warn("Exception: {}, Key: {}, Value: {}", new Object[]{var11, key, value});
                    }
                }
            }

            jsonArray.put(jsonObject);
        }

        return jsonArray;
    }

    public static JSONArray mergeJSONArray(JSONArray a1, JSONArray a2) {
        if (a2 == null) {
            return a1;
        } else if (CommonUtils.nullOrBlank(a1.toString()) && CommonUtils.nullOrBlank(a2.toString())) {
            return new JSONArray();
        } else if (!CommonUtils.nullOrBlank(a1.toString()) && a1.length() != 0) {
            if (!CommonUtils.nullOrBlank(a2.toString()) && a2.length() != 0) {
                String var10000 = a1.toString().substring(1, a1.toString().length() - 1);
                String mergeResult = "[" + var10000 + "," + a2.toString().substring(1, a2.toString().length() - 1) + "]";

                try {
                    return new JSONArray(mergeResult);
                } catch (JSONException var4) {
                    return new JSONArray();
                }
            } else {
                return a1;
            }
        } else {
            return a2;
        }
    }

    private static Object lowerKeys(Object obj) {
        if (obj == null) {
            return null;
        } else {
            if (obj instanceof Map) {
                Map<String, Object> map = (Map)obj;
                Map<String, Object> tmpMap = new LinkedHashMap();
                Iterator var3 = map.entrySet().iterator();

                while(var3.hasNext()) {
                    Map.Entry<String, Object> entry = (Map.Entry)var3.next();
                    tmpMap.put(((String)entry.getKey()).toLowerCase(), lowerKeys(entry.getValue()));
                }

                map.clear();
                map.putAll(tmpMap);
            } else if (obj instanceof List) {
                List<Object> list = (List)obj;
                list.replaceAll(ConvertUtils::lowerKeys);
            }

            return obj;
        }
    }

    public static String arrayToString(Object... params) {
        StringBuilder sb = new StringBuilder("[");
        Object[] var2 = params;
        int var3 = params.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Object o = var2[var4];
            sb.append(toEmpty(o));
            sb.append(",");
        }

        if (sb.length() > 1) {
            sb.deleteCharAt(sb.length() - 1);
        }

        sb.append("]");
        return sb.toString();
    }

    public static String getUriQuery(Map<String, String> requestParams) {
        StringBuilder serializedParams = new StringBuilder("");
        Iterator var2 = requestParams.entrySet().iterator();

        while(var2.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry)var2.next();
            String key = toEmpty(entry.getKey());
            if (!CommonUtils.nullOrBlank(key)) {
                serializedParams.append(key).append("=").append(toEmpty(requestParams.get(key))).append("&");
            }
        }

        if (!CommonUtils.nullOrBlank(serializedParams.toString())) {
            serializedParams = new StringBuilder(serializedParams.substring(0, serializedParams.length() - 1));
        }

        return serializedParams.toString();
    }

    public static Map<String, String> getParams(String serializedParams) {
        if (CommonUtils.nullOrBlank(serializedParams)) {
            return null;
        } else {
            Map<String, String> results = new HashMap();
            List<Object> params = getSepList(serializedParams, "&");
            Iterator var3 = params.iterator();

            while(var3.hasNext()) {
                Object o = var3.next();
                String param = (String)o;
                results.put(param.substring(0, param.indexOf("=")), param.substring(param.indexOf("=") + 1));
            }

            return results;
        }
    }

    public static String getEncryptedUri(String Uri, String serializedParams) {
        if (CommonUtils.nullOrBlank(serializedParams)) {
            return "";
        } else {
            String encryptedParams;
            try {
                encryptedParams = Base64.encodeBase64URLSafeString(serializedParams.getBytes());
            } catch (Exception var4) {
                return "";
            }

            return Uri + "/?token=" + encryptedParams;
        }
    }

    public static Map<String, String> getDecryptedToken(String token) {
        try {
            return getParams(new String(Base64.decodeBase64(token), StandardCharsets.UTF_8));
        } catch (Exception var2) {
            LOGGER.error("{}", var2.getMessage());
            return null;
        }
    }

    public static JSONObject getJSONObject(JSONObject obj, String key) {
        String resultString = getJString(obj, key);
        JSONObject result = new JSONObject();

        try {
            result = new JSONObject(resultString);
        } catch (JSONException var5) {
        }

        return result;
    }

    public static JSONArray getJSONArray(JSONObject obj, String key) {
        String resultString = getJString(obj, key);
        JSONArray result = new JSONArray();

        try {
            result = new JSONArray(resultString);
        } catch (JSONException var5) {
        }

        return result;
    }

    public static String getJStringFromJSONObject(JSONObject obj, String key) {
        String resultString = getJString(obj, key);

        try {
            JSONObject result = new JSONObject(resultString);
            resultString = result.toString();
        } catch (JSONException var4) {
        }

        return resultString;
    }

    public static JSONArray getJArray(JSONObject obj, String key) {
        try {
            return obj.getJSONArray(key);
        } catch (JSONException var3) {
            return null;
        }
    }

    public static JSONObject getJSONObject(JSONArray array, int index) {
        JSONObject result = new JSONObject();

        try {
            result = array.getJSONObject(index);
        } catch (JSONException var4) {
        }

        return result;
    }

    public static JSONObject getJSONObject(JSONArray array, String key, String value) {
        JSONObject result = new JSONObject();
        if (!checkJSONArray(array)) {
            return result;
        } else {
            for(int i = 0; i < array.length(); ++i) {
                JSONObject object = getJSONObject(array, i);
                if (getJString(object, key).equalsIgnoreCase(value)) {
                    return object;
                }
            }

            return result;
        }
    }

    public static JSONObject toJSONObject(String jsonObject) {
        try {
            return new JSONObject(jsonObject);
        } catch (JSONException var2) {
            return new JSONObject();
        }
    }

    public static JSONArray toJSONArray(String jsonArray) {
        try {
            return new JSONArray(jsonArray);
        } catch (JSONException var2) {
            return new JSONArray();
        }
    }

    public static String replaceTextByConfigList(String input, String jsonArray, String mapKey, String mapValue) {
        String result = input;
        if (input == null) {
            return null;
        } else {
            if (!CommonUtils.nullOrBlank(jsonArray)) {
                try {
                    JSONArray resultMapping = new JSONArray(jsonArray);

                    for(int i = 0; i < resultMapping.length(); ++i) {
                        JSONObject map = getJSONObject(resultMapping, i);
                        if (result.equals(getJString(map, mapKey))) {
                            result = getJString(map, mapValue);
                            break;
                        }
                    }
                } catch (JSONException var8) {
                }
            }

            return result;
        }
    }

    public static List<Object> getSepList(String input, String separator) {
        List<Object> result = new ArrayList();
        if (CommonUtils.nullOrBlank(input)) {
            result.add(input);
        }

        result = Arrays.asList(input.split("\\s*" + (CommonUtils.nullOrBlank(separator) ? "," : separator) + "\\s*"));
        return result;
    }

    public static String amountToVietnamese(Number number) {
        String numberStr = toEmpty(number);
        ArrayList<String> vnNumber = readNum(numberStr);
        StringBuilder result = new StringBuilder("");
        Iterator var4 = vnNumber.iterator();

        while(var4.hasNext()) {
            String vnDigit = (String)var4.next();
            result.append(vnDigit).append(" ");
        }

        return result.toString();
    }

    public static ArrayList<String> readNum(String a) {
        ArrayList<String> kq = new ArrayList();

        for(ArrayList<String> List_Num = Split(a, 3); List_Num.size() != 0; List_Num.remove(0)) {
            ArrayList nghin;
            switch (List_Num.size() % 3) {
                case 0:
                    nghin = read_3num((String)List_Num.get(0));
                    if (!nghin.isEmpty()) {
                        kq.addAll(nghin);
                        kq.add("triệu");
                    }
                    break;
                case 1:
                    kq.addAll(read_3num((String)List_Num.get(0)));
                    break;
                case 2:
                    nghin = read_3num((String)List_Num.get(0));
                    if (!nghin.isEmpty()) {
                        kq.addAll(nghin);
                        kq.add("nghìn");
                    }
            }

            if (List_Num.size() == List_Num.size() / 3 * 3 + 1 && List_Num.size() != 1) {
                kq.add("tỷ");
            }
        }

        return kq;
    }

    public static ArrayList<String> read_3num(String a) {
        ArrayList<String> kq = new ArrayList();
        int num = -1;

        try {
            num = Integer.parseInt(a);
        } catch (Exception var10) {
        }

        if (num == 0) {
            return kq;
        } else {
            int hang_tram = -1;

            try {
                hang_tram = Integer.parseInt(a.substring(0, 1));
            } catch (Exception var9) {
            }

            int hang_chuc = -1;

            try {
                hang_chuc = Integer.parseInt(a.substring(1, 2));
            } catch (Exception var8) {
            }

            int hang_dv = -1;

            try {
                hang_dv = Integer.parseInt(a.substring(2, 3));
            } catch (Exception var7) {
            }

            if (hang_tram != -1) {
                kq.add(NUMBER[hang_tram]);
                kq.add("trăm");
            }

            switch (hang_chuc) {
                case -1:
                    break;
                case 0:
                    if (hang_dv != 0) {
                        kq.add("lẻ");
                    }
                    break;
                case 1:
                    kq.add("mười");
                    break;
                default:
                    kq.add(NUMBER[hang_chuc]);
                    kq.add("mươi");
            }

            switch (hang_dv) {
                case -1:
                    break;
                case 0:
                    if (kq.isEmpty()) {
                        kq.add(NUMBER[hang_dv]);
                    }
                    break;
                case 1:
                    if (hang_chuc != 0 && hang_chuc != 1 && hang_chuc != -1) {
                        kq.add("mốt");
                    } else {
                        kq.add(NUMBER[hang_dv]);
                    }
                    break;
                case 2:
                case 3:
                case 4:
                default:
                    kq.add(NUMBER[hang_dv]);
                    break;
                case 5:
                    if (hang_chuc != 0 && hang_chuc != -1) {
                        kq.add("lăm");
                    } else {
                        kq.add(NUMBER[hang_dv]);
                    }
            }

            return kq;
        }
    }

    public static ArrayList<String> Split(String str, int chunkSize) {
        int du = str.length() % chunkSize;
        if (du != 0) {
            StringBuilder strBuilder = new StringBuilder(str);

            for(int i = 0; i < chunkSize - du; ++i) {
                strBuilder.insert(0, "#");
            }

            str = strBuilder.toString();
        }

        return splitStringEvery(str, chunkSize);
    }

    public static ArrayList<String> splitStringEvery(String s, int interval) {
        int arrayLength = (int)Math.ceil((double)s.length() / (double)interval);
        String[] result = new String[arrayLength];
        int j = 0;
        int lastIndex = result.length - 1;

        for(int i = 0; i < lastIndex; ++i) {
            result[i] = s.substring(j, j + interval);
            j += interval;
        }

        result[lastIndex] = s.substring(j);
        return new ArrayList(Arrays.asList(result));
    }

    public static String getRequestParam(Map<String, String> requestParams, String param, String defaultValue) {
        try {
            return ((String)requestParams.get(param)).trim().length() > 0 ? ((String)requestParams.get(param)).trim() : defaultValue;
        } catch (Exception var4) {
            return defaultValue;
        }
    }

    public static String[] splitDateRange(String dateRange) {
        if (!vn.com.nested.backend.common.operation.StringUtils.isNullOrEmpty(dateRange)) {
            String[] split = dateRange.split(" - ");
            if (split.length != 2) {
                return null;
            } else {
                split[0] = DateTimeUtils.convertDate(split[0], "dd/MM/yyyy", "yyyy-MM-dd");
                split[1] = DateTimeUtils.convertDate(split[1], "dd/MM/yyyy", "yyyy-MM-dd");
                return split;
            }
        } else {
            return null;
        }
    }

    public static String[] splitDateTimeRange(String dateRange) {
        String[] arrDate = dateRange.split(" - ");
        if (arrDate.length != 2) {
            return null;
        } else {
            arrDate[0] = DateTimeUtils.convertDateTime(arrDate[0], "dd/MM/yyyy HH:mm:ss", "yyyy-MM-dd HH:mm:ss");
            arrDate[1] = DateTimeUtils.convertDateTime(arrDate[1], "dd/MM/yyyy HH:mm:ss", "yyyy-MM-dd HH:mm:ss");
            return arrDate;
        }
    }

    public static int getLength(JSONArray jsonArray) {
        return checkJSONArray(jsonArray) ? jsonArray.length() : 0;
    }

    public static String getHash(String value, String key) {
        return CryptoUtil.sha256(value + "|" + key);
    }

    public static String getEncryptedUri(String Uri, String serializedParams, String sessionId) {
        if (vn.com.nested.backend.common.operation.StringUtils.isNullOrEmpty(serializedParams)) {
            return "";
        } else {
            String encryptedParams;
            try {
                encryptedParams = vn.com.nested.backend.common.operation.StringUtils.encode(serializedParams);
            } catch (Exception var5) {
                LOGGER.error("{}", var5.getMessage());
                return "";
            }

            String hashParams = getHash(encryptedParams, sessionId);
            return Uri + "/?token=" + encryptedParams + "&hash=" + hashParams;
        }
    }

    public static String getTokenFromKeyAndValue(String... keyAndValue) {
        if (!CommonUtils.nullOrBlank(keyAndValue) && keyAndValue.length % 2 == 0) {
            StringBuilder result = new StringBuilder();

            for(int i = 0; i < keyAndValue.length; i += 2) {
                result.append(keyAndValue[i]).append("=").append(keyAndValue[i + 1]).append("&");
            }

            if (!CommonUtils.nullOrBlank(result.toString())) {
                result = new StringBuilder(result.substring(0, result.length() - 1));
            }

            return vn.com.nested.backend.common.operation.StringUtils.encode(result.toString());
        } else {
            return "";
        }
    }

    public static Map<String, String> getDecryptedToken(String token, String hash, String sessionId) {
        String hashToken = getHash(token, sessionId);
        if (!hashToken.equals(hash)) {
            LOGGER.error("{}", "Token differences from Hash");
            return null;
        } else {
            try {
                Map<String, String> requestParams = getParams(vn.com.nested.backend.common.operation.StringUtils.decode(token));
                return requestParams;
            } catch (Exception var6) {
                LOGGER.error("{}", var6.getMessage());
                return null;
            }
        }
    }

    public static Map<String, String> getDecryptedToken(Map<String, String> httpRequestParams, Map<String, Object> model, boolean isCheckHash, String sessionId, SLog sLog) {
        String token = getRequestParam(httpRequestParams, "token", "");
        String hash = getRequestParam(httpRequestParams, "hash", "");
        Map<String, String> requestParams = getParams(vn.com.nested.backend.common.operation.StringUtils.decode(token));
        if (model != null) {
            model.putAll(requestParams);
            model.put("token", token);
            model.put("hash", hash);
        }

        if (isCheckHash && !hash.equalsIgnoreCase(CommonUtils.getHash(token, sessionId))) {
            sLog.append(new String[]{"parseData", "Token differences from Hash"});
            return new HashMap();
        } else {
            return requestParams;
        }
    }

    public static int toInt(String value) {
        if (vn.com.nested.backend.common.operation.StringUtils.isNullOrEmpty(value)) {
            throw new IllegalArgumentException("value is null or empty");
        } else {
            return Integer.parseInt(value);
        }
    }

    public static Map<String, JSONObject> convertToMap(JSONObject data) {
        Map<String, JSONObject> map = new LinkedHashMap();
        Iterator keys = data.keys();

        while(keys.hasNext()) {
            String key = (String)keys.next();
            JSONObject obj = getJSONObject(data, key);
            map.put(key, obj);
        }

        return map;
    }

    public static Map<String, String> toMapString(String data) {
        JSONObject obj = toJSONObject(data);
        return toMapString(obj);
    }

    public static Map<String, String> toMapString(JSONObject data) {
        Map<String, String> map = new LinkedHashMap();
        Iterator<?> keys = data.keys();

        while(keys.hasNext()) {
            String key = (String)keys.next();
            String value = getJString(data, key);
            map.put(key, value);
        }

        return map;
    }

    public static JSONArray toJArray(String value) {
        try {
            return new JSONArray(value);
        } catch (JSONException var2) {
            return new JSONArray();
        }
    }

    public static int removeJSONObject(JSONArray listObj, String jsonKey, String jsonValue) {
        try {
            if (listObj != null && listObj.length() > 0) {
                for(int i = 0; i < listObj.length(); ++i) {
                    JSONObject obj = listObj.getJSONObject(i);
                    if (obj.getString(jsonKey).equals(jsonValue)) {
                        listObj.remove(obj);
                    }
                }

                return listObj.length();
            } else {
                return 0;
            }
        } catch (JSONException var5) {
            return -1;
        }
    }

    public static boolean scaleImage(String url, String destFolderPath, int size, String formatName) {
        try {
            if (vn.com.nested.backend.common.operation.StringUtils.isNullOrEmpty(formatName)) {
                formatName = "jpg";
            }

            URL urlInput = URI.create(url).toURL();
            BufferedImage bufferedImage = ImageIO.read(urlInput);
            File destinationDir = CommonUtils.createFolderPath(destFolderPath + "." + formatName);
            Thumbnails.of(new BufferedImage[]{bufferedImage}).width(size).outputQuality(0.8).asBufferedImage();
            ImageIO.write(bufferedImage, formatName, destinationDir);
            return true;
        } catch (Exception var7) {
            var7.printStackTrace();
            return false;
        }
    }

    public static JSONArray excelToJArr(String path, int sheetPosition, int startRow, JSONArray jFormat) throws IOException {
        switch (FilenameUtils.getExtension(path)) {
            case "xls":
            case "XLS":
                return xlsToJArr(path, sheetPosition, startRow, jFormat);
            case "xlsx":
            case "XLSX":
                InputStream is = new FileInputStream(path);
                return excelToJArr((InputStream)is, sheetPosition, startRow, jFormat);
            default:
                throw new RuntimeException(path + ".readfile not support extension: " + FilenameUtils.getExtension(path));
        }
    }

    public static JSONArray xlsToJArr(String path, int sheetPosition, int startRow, JSONArray jFormat) throws IOException {
        JSONArray data = new JSONArray();
        FileInputStream excelFile = new FileInputStream(path);
        HSSFWorkbook myWorkBook = new HSSFWorkbook(new POIFSFileSystem(excelFile));
        HSSFSheet mySheet = myWorkBook.getSheetAt(sheetPosition);
        Iterator<Row> iterator = mySheet.iterator();
        int countRow = 0;

        while(iterator.hasNext()) {
            ++countRow;
            Row row = (Row)iterator.next();
            if (countRow >= startRow) {
                convertRow(row, jFormat, data);
            }
        }

        return data;
    }

    public static String toString(Map<String, String> elements) {
        try {
            String s = (new ObjectMapper()).writeValueAsString(elements);
            Gson gson = new Gson();
            Type gsonType = (new TypeToken<HashMap>() {
            }).getType();
            return gson.toJson(elements, gsonType);
        } catch (Exception var4) {
            throw new NullPointerException();
        }
    }

    public static JSONArray excelToJArr(InputStream is, int sheetPosition, int startRow, JSONArray jFormat) throws IOException {
        JSONArray data = new JSONArray();
        int countRow = 0;
        Workbook workbook = StreamingReader.builder().rowCacheSize(1000).bufferSize(4096).open(is);

        try {
            Sheet sheet = workbook.getSheetAt(sheetPosition);
            Iterator var8 = sheet.iterator();

            while(var8.hasNext()) {
                Row row = (Row)var8.next();
                ++countRow;
                if (countRow >= startRow) {
                    convertRow(row, jFormat, data);
                }
            }
        } catch (Throwable var11) {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (Throwable var10) {
                    var11.addSuppressed(var10);
                }
            }

            throw var11;
        }

        if (workbook != null) {
            workbook.close();
        }

        return data;
    }

    private static void convertRow(Row row, JSONArray jFormat, JSONArray output) {
        DataFormatter dataFormatter = new DataFormatter();
        JSONObject jRow = new JSONObject();
        String inputFormat;
        if (jFormat != null) {
            for(int i = 0; i < jFormat.length(); ++i) {
                JSONObject childCons = getJSONObject(jFormat, i);
                int pos = getJInt(childCons, "position");
                inputFormat = getJString(childCons, "inputFormat");
                String outputType = getJString(childCons, "outputType");
                String outputFormat = getJString(childCons, "outputFormat");
                String cellData = dataFormatter.formatCellValue(row.getCell(pos));
                if (outputType.equals("Date")) {
                    cellData = getCellData(row.getCell(pos), inputFormat, outputFormat);
                }

                String cellName = getJString(childCons, "name");
                CommonUtils.putJSONObject(jRow, cellName, cellData);
            }
        } else {
            Iterator var13 = row.iterator();

            while(var13.hasNext()) {
                Cell cell = (Cell)var13.next();
                if (cell != null) {
                    CellReference cellRef = new CellReference(row.getRowNum(), cell.getColumnIndex());
                    inputFormat = cellRef.formatAsString().replaceAll("[^a-zA-Z]", "");
                    CommonUtils.putJSONObject(jRow, inputFormat, dataFormatter.formatCellValue(cell));
                }
            }
        }

        output.put(jRow);
    }

    private static String getCellData(Cell cell, String inputFormat, String outputFormat) {
        DataFormatter dataFormatter = new DataFormatter();
        if (null == cell) {
            return "";
        } else {
            switch (cell.getCellType()) {
                case STRING:
                    return DateTimeUtils.convertDateTime(dataFormatter.formatCellValue(cell), inputFormat, outputFormat);
                case NUMERIC:
                    Date dateSend = cell.getDateCellValue();
                    Format simpleFormatter = new SimpleDateFormat(outputFormat);
                    return simpleFormatter.format(dateSend);
                default:
                    return "";
            }
        }
    }

    private static String getCellData(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getRichStringCellValue().getString();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }

                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return String.valueOf(cell.getCellFormula());
            default:
                return "";
        }
    }

    public static void getMapKeyValue(String parentKey, Object obj, Map<String, Object> result) {
        String currentKeyPrefix = (vn.com.nested.backend.common.operation.StringUtils.isNullOrEmpty(parentKey) ? "" : parentKey + ".");

        if (obj instanceof Map<?, ?>) {
            Map<?, ?> map = (Map<?, ?>) obj;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                String key = String.valueOf(entry.getKey());
                getMapKeyValue(currentKeyPrefix + key, entry.getValue(), result);
            }
        } else if (obj instanceof List<?>) {
            List<?> list = (List<?>) obj;
            for (int i = 0; i < list.size(); i++) {
                getMapKeyValue((vn.com.nested.backend.common.operation.StringUtils.isNullOrEmpty(parentKey) ? "" : parentKey) + "[" + i + "]", list.get(i), result);
            }
        } else if (obj instanceof com.google.gson.JsonObject) {
            com.google.gson.JsonObject jsonObj = (com.google.gson.JsonObject) obj;
            for (Map.Entry<String, com.google.gson.JsonElement> entry : jsonObj.entrySet()) {
                getMapKeyValue(currentKeyPrefix + entry.getKey(), entry.getValue(), result);
            }
        } else if (obj instanceof JSONObject) {
            JSONObject jsonObj = (JSONObject) obj;
            Iterator<String> keys = jsonObj.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                try {
                    getMapKeyValue(currentKeyPrefix + key, jsonObj.get(key), result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else if (obj instanceof JSONArray) {
            JSONArray arr = (JSONArray) obj;
            for (int i = 0; i < arr.length(); i++) {
                getMapKeyValue((vn.com.nested.backend.common.operation.StringUtils.isNullOrEmpty(parentKey) ? "" : parentKey) + "[" + i + "]", getJSONObject(arr, i), result);
            }
        } else if (obj instanceof com.google.gson.JsonElement jsonElement) {
            // Xử lý JsonElement từ Gson
            if (jsonElement.isJsonPrimitive()) {
                result.put(parentKey, jsonElement.getAsString());
            } else if (jsonElement.isJsonObject()) {
                getMapKeyValue(parentKey, jsonElement.getAsJsonObject(), result);
            } else if (jsonElement.isJsonArray()) {
                int index = 0;
                for (com.google.gson.JsonElement element : jsonElement.getAsJsonArray()) {
                    getMapKeyValue(parentKey + "[" + index + "]", element, result);
                    index++;
                }
            } else if (jsonElement.isJsonNull()) {
                result.put(parentKey, null);
            }
        } else {
            // Trường hợp cuối cùng: là giá trị primitive (String, Integer, Boolean, ...)
            result.put(parentKey, obj);
        }
    }

    public static JSONObject fileToObject(MultipartFile file) {
        String fileId = CommonUtils.genUUID();
        JSONObject obj = new JSONObject();
        CommonUtils.putJSONObject(obj, "id", fileId);
        CommonUtils.putJSONObject(obj, "name", file.getOriginalFilename());
        CommonUtils.putJSONObject(obj, "size", file.getSize());
        CommonUtils.putJSONObject(obj, "file", file);
        return obj;
    }

    public static RangeQueryBuilder genDateRangeQuery(String dateRange, String keyword, boolean isEpochMillis) {
        String[] splitDateRange = splitDateRange(dateRange);
        if (splitDateRange != null) {
            String fromDate = splitDateRange[0] + " 00:00:00";
            String toDate = splitDateRange[1] + " 23:59:59";
            if (isEpochMillis) {
                fromDate = fromDate + ".0";
                toDate = toDate + ".0";
            }

            if (!Strings.isNullOrEmpty(fromDate) || !Strings.isNullOrEmpty(toDate)) {
                RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(keyword);
                if (!Strings.isNullOrEmpty(fromDate)) {
                    rangeQuery.gte(fromDate);
                }

                if (!Strings.isNullOrEmpty(toDate)) {
                    rangeQuery.lte(toDate);
                }

                return rangeQuery;
            }
        }

        return null;
    }

    public static String getMongoKey(JsonObject mongoRawData) {
        JsonObject iod = mongoRawData.has("_id") ? mongoRawData.get("_id").getAsJsonObject() : new JsonObject();
        String key = getJString(iod, "$oid");
        return key;
    }

    public static JsonElement convertKeyToLowercase(JsonElement jsonElement) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonObject result = new JsonObject();
        Iterator var3 = jsonObject.entrySet().iterator();

        while(var3.hasNext()) {
            Map.Entry<String, JsonElement> entry = (Map.Entry)var3.next();
            result.add(((String)entry.getKey()).toLowerCase(), (JsonElement)entry.getValue());
        }

        return (new JsonParser()).parse(result.toString());
    }

    public static long getNumberLong(JsonObject dateObject, String key) {
        try {
            JsonObject object = getGsonObject(dateObject, key);
            return getGsonLong(object, "$numberLong", (String)null);
        } catch (Throwable var3) {
            return 0L;
        }
    }

    public static JSONArray esResToJSONArray(SearchResponse searchResponse) {
        if (searchResponse == null) {
            return new JSONArray();
        } else if (searchResponse.getHits().getHits().length == 0) {
            return new JSONArray();
        } else {
            JSONArray result = new JSONArray();

            try {
                for(int i = 0; i < searchResponse.getHits().getHits().length; ++i) {
                    JSONObject row = new JSONObject(searchResponse.getHits().getAt(i).getSourceAsString());
                    result.put(row);
                }

                return result;
            } catch (JSONException var4) {
                return new JSONArray();
            }
        }
    }

    public static JSONObject esResToJSONObject(SearchResponse searchResponse) {
        if (searchResponse != null) {
            if (searchResponse.getHits().getHits().length == 0) {
                return null;
            } else if (searchResponse.getHits().getHits().length > 1) {
                return null;
            } else {
                try {
                    return new JSONObject(searchResponse.getHits().getAt(0).getSourceAsString());
                } catch (JSONException var2) {
                    return null;
                }
            }
        } else {
            return null;
        }
    }

    public static List<SearchHit> esResToSearchHits(SearchResponse searchResponse) {
        if (searchResponse != null) {
            return (List)(searchResponse.getHits().getHits().length == 0 ? new ArrayList() : Arrays.asList(searchResponse.getHits().getHits()));
        } else {
            return new ArrayList();
        }
    }
}
