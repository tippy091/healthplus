package vn.com.nested.backend.common.uis.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author tippy091
 * @created 05/06/2025
 * @project server
 **/

public class ConverterUtils {
    private static final Logger log = LoggerFactory.getLogger(ConverterUtils.class);
    private static final Pattern SELFIE_IMAGE_PATTERN = Pattern.compile("(.+?)KYCServiceNATIONAL_ID([0-9]*?)selfie(.*?)", 256);
    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+", 256);
    private static Logger LOGGER = LoggerFactory.getLogger(ConverterUtils.class);

    public ConverterUtils() {
    }

    public static String serialize(Object object) {
        return serialize(object, (FilterProvider)null);
    }

    public static String serialize(Object object, FilterProvider filterProvider) {
        try {
            ObjectMapper objectMapper = initObjectMapper(filterProvider);
            return objectMapper.writeValueAsString(object);
        } catch (Exception var3) {
            log.error("ConverterUtils-serialize:{}", var3);
            return null;
        }
    }

    public static JsonObject serializeToJson(Object object) {
        return serializeToJson(object, (FilterProvider)null);
    }

    public static JsonObject serializeToJson(Object object, FilterProvider filterProvider) {
        try {
            ObjectMapper objectMapper = initObjectMapper(filterProvider);
            return (JsonObject)(new Gson()).fromJson(objectMapper.writeValueAsString(object), JsonObject.class);
        } catch (Exception var3) {
            log.error("ConverterUtils-serialize:{}", var3);
            return null;
        }
    }

    public static JsonArray serializeToJsonArray(Object object) {
        return serializeToJsonArray(object, (FilterProvider)null);
    }

    public static JsonArray serializeToJsonArray(Object object, FilterProvider filterProvider) {
        try {
            ObjectMapper objectMapper = initObjectMapper(filterProvider);
            return (JsonArray)(new Gson()).fromJson(objectMapper.writeValueAsString(object), JsonArray.class);
        } catch (Exception var3) {
            log.error("ConverterUtils-serialize:{}", var3);
            return null;
        }
    }

    public static ObjectMapper initObjectMapper(FilterProvider filterProvider) {
        SimpleModule objectIdModule = new SimpleModule("ObjectIdModule");
        objectIdModule.addSerializer(ObjectId.class, new ObjectIdSerializer());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(objectIdModule);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        if (filterProvider != null) {
            objectMapper.setFilterProvider(filterProvider);
        }

        return objectMapper;
    }

    public static <T> T deserialize(String json, Class<T> clazz) {
        try {
            ObjectMapper objectMapper = initObjectMapper((FilterProvider)null);
            return objectMapper.readValue(json, clazz);
        } catch (Exception var3) {
            log.error("ConverterUtils-serialize:{}", var3);
            return null;
        }
    }

    public static <T> T gsonDeserialize(String json, Class<T> clazz) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(json, clazz);
        } catch (Exception var3) {
            log.error("ConverterUtils-serialize:{}", var3);
            return null;
        }
    }

    public static JsonObject getGsonObject(JsonObject object, String key) {
        try {
            return object.get(key).getAsJsonObject();
        } catch (Exception var3) {
            return null;
        }
    }

    public static JsonArray getGsonArray(JsonObject object, String key) {
        try {
            return object.get(key).getAsJsonArray();
        } catch (Exception var3) {
            return null;
        }
    }

    public static LocalDateTime convertToLocalDateTime(String datetime, String currentFormat) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(currentFormat);
            return LocalDateTime.parse(datetime, formatter);
        } catch (Exception var3) {
            return null;
        }
    }

    public static String convertToDateTime(String datetime, String currentFormat, String newFormat) {
        LocalDateTime tmpDate = convertToLocalDateTime(datetime, currentFormat);
        DateTimeFormatter newFormatter = DateTimeFormatter.ofPattern(newFormat);
        return newFormatter.format(tmpDate);
    }

    public static String convertToDate(String datetime, String currentFormat, String newFormat) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(currentFormat);
            LocalDate localDate = LocalDate.parse(datetime, formatter);
            DateTimeFormatter newFormatter = DateTimeFormatter.ofPattern(newFormat);
            return newFormatter.format(localDate);
        } catch (Exception var6) {
            return null;
        }
    }

    public static String trim(String value) {
        return value != null && !value.isEmpty() ? value.trim() : "";
    }

    public static String parseOP(String pathInfo) {
        if (pathInfo != null && !pathInfo.isEmpty()) {
            String op = "";

            try {
                String[] arr = pathInfo.split("\\/");
                if (arr != null && arr.length >= 1) {
                    op = arr[arr.length - 1];
                }
            } catch (Exception var3) {
            }

            return op;
        } else {
            return "";
        }
    }

    public static boolean isBlank(JsonObject jsonObject) {
        return jsonObject == null || jsonObject.size() == 0;
    }

    public static JsonArray toJsonArray(String text) {
        JsonArray ret = null;

        try {
            ret = JsonParser.parseString(text).getAsJsonArray();
        } catch (Exception var3) {
            LOGGER.error(var3.getMessage(), var3);
        }

        return ret;
    }

    public static JsonObject toJsonObject(String text) {
        JsonObject ret = null;

        try {
            if (StringUtils.isEmpty(text)) {
                return ret;
            }

            ret = JsonParser.parseString(text).getAsJsonObject();
        } catch (Exception var3) {
            LOGGER.error(var3.getMessage(), var3);
        }

        return ret;
    }

    public static String getString(JsonObject data, String key) {
        if (data != null && data.has(key)) {
            return data.get(key).isJsonNull() ? null : data.get(key).getAsString();
        } else {
            return null;
        }
    }

    public static boolean getBoolean(JsonObject data, String key) {
        if (data != null && data.has(key)) {
            return data.get(key).isJsonNull() ? false : data.get(key).getAsBoolean();
        } else {
            return false;
        }
    }

    public static String getString(Document data, String key) {
        return data != null && data.containsKey(key) ? data.getString(key) : null;
    }

    public static long getLong(JsonObject data, String key) {
        if (data != null && data.has(key)) {
            return data.get(key).isJsonNull() ? 0L : NumberUtils.toLong(data.get(key).getAsString().replaceAll(",", ""), 0L);
        } else {
            return 0L;
        }
    }

    public static long getLong(JsonObject data, String key, long defaultValue) {
        if (data != null && data.has(key)) {
            return data.get(key).isJsonNull() ? defaultValue : NumberUtils.toLong(data.get(key).getAsString().replaceAll(",", ""), defaultValue);
        } else {
            return defaultValue;
        }
    }

    public static int getInt(JsonObject data, String key) {
        if (data != null && data.has(key)) {
            return data.get(key).isJsonNull() ? 0 : NumberUtils.toInt(data.get(key).getAsString().replaceAll(",", ""), 0);
        } else {
            return 0;
        }
    }

    public static double getDouble(JsonObject data, String key) {
        if (data != null && data.has(key)) {
            return data.get(key).isJsonNull() ? 0.0 : NumberUtils.toDouble(data.get(key).getAsString().replaceAll(",", ""), 0.0);
        } else {
            return 0.0;
        }
    }

    public static String formatMoney(double value) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setGroupingUsed(true);
        return numberFormat.format(value);
    }

    public static String formatMoney(String value) {
        try {
            double doubleValue = Double.parseDouble(value);
            return formatMoney(doubleValue);
        } catch (NumberFormatException var3) {
            return "0";
        }
    }

    public static BigDecimal getBigDecimal(JsonObject data, String key) {
        if (data != null && data.has(key)) {
            if (data.get(key).isJsonNull()) {
                return BigDecimal.ZERO;
            } else {
                double tmp = NumberUtils.toDouble(data.get(key).getAsString().replaceAll(",", ""), 0.0);
                return NumberUtils.toScaledBigDecimal(tmp);
            }
        } else {
            return BigDecimal.ZERO;
        }
    }

    public static Map<String, String> toMapString(JsonObject data) {
        Gson gson = new Gson();
        Map map = (Map)gson.fromJson(data.toString(), Map.class);
        return map;
    }

    public static List<Document> toListDocument(String jsonData) {
        try {
            Gson gson = new Gson();
            Type listType = (new TypeToken<List<Document>>() {
            }).getType();
            return (List)gson.fromJson(jsonData, listType);
        } catch (Exception var3) {
            log.error("ConverterUtils-serialize:{}", var3);
            return new ArrayList();
        }
    }

    public static String getDataFromNestedKey(String key, Map<String, Object> dataMap) {
        String[] nestedKeys = key.split("\\.");
        if (nestedKeys.length <= 1) {
            return "";
        } else {
            int lastIndex = nestedKeys.length - 1;
            Map<String, Object> lastDataMap = dataMap;

            for(int i = 0; i < nestedKeys.length; ++i) {
                String nestedKey = nestedKeys[i];
                if (i == lastIndex) {
                    return Objects.toString(((Map)lastDataMap).get(nestedKey), "");
                }

                Object nestedDataMap = ((Map)lastDataMap).get(nestedKey);
                if (nestedDataMap instanceof LinkedHashMap) {
                    lastDataMap = (LinkedHashMap)nestedDataMap;
                }

                if (nestedDataMap instanceof HashMap) {
                    lastDataMap = (HashMap)nestedDataMap;
                }
            }

            return "";
        }
    }

    public static String[] convertSet2Arr(Set<String> setOfString) {
        if (setOfString == null) {
            return new String[0];
        } else {
            String[] arrayOfString = (String[])setOfString.stream().toArray((x$0) -> {
                return new String[x$0];
            });
            return arrayOfString;
        }
    }

    public static Map<String, String> flatJson(JsonElement jsonObject) {
        Map<String, String> result = new HashMap();
        flatJson(jsonObject, "", result);
        return result;
    }

    private static void flatJson(JsonElement jsonElement, String parentKey, Map<String, String> flatMap) {
        if (jsonElement instanceof JsonObject jsonObject) {
            Iterator var5 = jsonObject.keySet().iterator();

            while(var5.hasNext()) {
                String key = (String)var5.next();
                JsonElement childElement = jsonObject.get(key);
                if (StringUtils.isEmpty(parentKey)) {
                    if (childElement instanceof JsonPrimitive) {
                        JsonPrimitive jsonPrimitive = (JsonPrimitive)childElement;
                        flatMap.put(key, jsonPrimitive.getAsString());
                    } else if (childElement instanceof JsonNull) {
                        flatMap.put(key, (String) null);
                    } else {
                        flatJson(childElement, key, flatMap);
                    }
                } else {
                    String newParentKey = String.join(".", parentKey, key);
                    if (childElement instanceof JsonPrimitive) {
                        JsonPrimitive jsonPrimitive = (JsonPrimitive)childElement;
                        flatMap.put(newParentKey, jsonPrimitive.getAsString());
                    } else {
                        flatJson(childElement, newParentKey, flatMap);
                    }
                }
            }
        } else if (jsonElement instanceof JsonPrimitive jsonPrimitive) {
            flatMap.put(parentKey, jsonPrimitive.getAsString());
        }

    }

    static class ObjectIdSerializer extends JsonSerializer<ObjectId> {
        ObjectIdSerializer() {
        }

        public void serialize(ObjectId value, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException {
            jsonGenerator.writeString(value.toString());
        }
    }

    public static class ZonedDateTimeToDateConverter implements Converter<ZonedDateTime, Date> {
        public static final ZonedDateTimeToDateConverter INSTANCE = new ZonedDateTimeToDateConverter();

        private ZonedDateTimeToDateConverter() {
        }

        public Date convert(ZonedDateTime source) {
            return source == null ? null : Date.from(source.toInstant());
        }
    }

    public static class DateToZonedDateTimeConverter implements Converter<Date, ZonedDateTime> {
        public static final DateToZonedDateTimeConverter INSTANCE = new DateToZonedDateTimeConverter();

        private DateToZonedDateTimeConverter() {
        }

        public ZonedDateTime convert(Date source) {
            return source == null ? null : ZonedDateTime.ofInstant(source.toInstant(), ZoneId.systemDefault());
        }
    }
}
