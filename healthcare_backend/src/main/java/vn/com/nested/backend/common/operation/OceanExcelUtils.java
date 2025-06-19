package vn.com.nested.backend.common.operation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.bson.types.ObjectId;
import org.codehaus.jettison.json.JSONObject;
import org.elasticsearch.search.SearchHit;
import vn.com.nested.backend.common.operation.exception.ExportExcelException;
import vn.com.nested.backend.common.operation.slog.SLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author tippy091
 * @created 02/06/2025
 * @project server
 **/

public final class OceanExcelUtils {
    private final int EXCEL_MIN_ROWS = 10000;
    private final int EXCEL_MAX_ROWS = 1000000;
    private final String folderPath;
    private final String fileName;
    private final String zipFileName;
    private final String sheetName;
    private final String filePath;
    private final FileOutputStream outputStream;
    private final SXSSFWorkbook currentWorkbook;
    private final CellStyle headerStyle;
    private final CellStyle bodyStyle;
    private final SLog sLog;
    private final Map<String, String> titleMap;
    private int maxRow;
    private SXSSFSheet currentSheet;
    private int rowNum = 0;
    private int partitionIdx = 0;
    private Map<String, JSONObject> formatMap = new HashMap();

    public OceanExcelUtils(String folderPath, String fileName, String sheetName, Map<String, String> titleMap, int dataSize, int maxRow, SLog sLog) throws Exception {
        if (dataSize == 0) {
            throw new ExportExcelException("Export not support with empty data.");
        } else {
            this.folderPath = folderPath;
            this.sheetName = sheetName;
            this.fileName = fileName + ".xlsx";
            this.zipFileName = fileName + ".zip";
            this.filePath = folderPath + "/" + this.fileName;
            this.titleMap = titleMap;
            this.sLog = sLog;
            this.makeDirect(this.folderPath);
            this.deleteFile(this.filePath);
            this.makeFile(this.filePath);
            this.outputStream = new FileOutputStream(this.filePath);
            this.currentWorkbook = new SXSSFWorkbook(-1);
            this.currentWorkbook.setCompressTempFiles(true);
            this.maxRow = Math.max(maxRow, 10000);
            if (maxRow > 1000000) {
                this.maxRow = 1000000;
            }

            this.headerStyle = this.currentWorkbook.createCellStyle();
            this.bodyStyle = this.currentWorkbook.createCellStyle();
            this.createStyle();
        }
    }

    public OceanExcelUtils(String folderPath, String fileName, String sheetName, Map<String, String> titleMap, int dataSize, int maxRow, Map<String, JSONObject> formatMap, SLog sLog) throws Exception {
        if (dataSize == 0) {
            throw new ExportExcelException("Export not support with empty data.");
        } else {
            this.folderPath = folderPath;
            this.sheetName = sheetName;
            this.fileName = fileName + ".xlsx";
            this.zipFileName = fileName + ".zip";
            this.filePath = folderPath + "/" + this.fileName;
            this.titleMap = titleMap;
            this.sLog = sLog;
            this.formatMap = formatMap;
            this.makeDirect(this.folderPath);
            this.deleteFile(this.filePath);
            this.makeFile(this.filePath);
            this.outputStream = new FileOutputStream(this.filePath);
            this.currentWorkbook = new SXSSFWorkbook(-1);
            this.maxRow = Math.max(maxRow, 10000);
            if (maxRow > 1000000) {
                this.maxRow = 1000000;
            }

            this.headerStyle = this.currentWorkbook.createCellStyle();
            this.bodyStyle = this.currentWorkbook.createCellStyle();
            this.createStyle();
        }
    }

    public void stop() throws IOException {
        this.currentWorkbook.write(this.outputStream);
        this.outputStream.close();
        this.currentWorkbook.dispose();
    }

    private void createStyle() {
        Font font = this.currentWorkbook.createFont();
        font.setBold(true);
        this.headerStyle.setFont(font);
        this.headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        this.headerStyle.setAlignment(HorizontalAlignment.CENTER);
        this.headerStyle.setBorderBottom(BorderStyle.THIN);
        this.headerStyle.setBorderTop(BorderStyle.THIN);
        this.headerStyle.setBorderLeft(BorderStyle.THIN);
        this.headerStyle.setBorderRight(BorderStyle.THIN);
        this.headerStyle.setWrapText(true);
        this.bodyStyle.setBorderBottom(BorderStyle.THIN);
        this.bodyStyle.setBorderTop(BorderStyle.THIN);
        this.bodyStyle.setBorderLeft(BorderStyle.THIN);
        this.bodyStyle.setBorderRight(BorderStyle.THIN);
        this.bodyStyle.setWrapText(true);
    }

    public void makeDirect(String folderPath) {
        File file = new File(folderPath);
        if (!file.exists()) {
            file.mkdirs();
        }

    }

    public void makeFile(String filePath) throws IOException {
        File f = new File(filePath);
        if (f.exists() && !f.delete()) {
            System.out.println("Don't have permission to makeFile.");
        }

        FileWriter out = new FileWriter(f);

        try {
            out.write("");
        } catch (Throwable var7) {
            try {
                out.close();
            } catch (Throwable var6) {
                var7.addSuppressed(var6);
            }

            throw var7;
        }

        out.close();
    }

    public void deleteFile(String filePath) {
        File f = new File(filePath);
        if (f.exists() && !f.delete()) {
            System.out.println("Don't have permission to deleteFile.");
        }

    }

    public void generateHeader() {
        int j = 0;
        Row row = this.currentSheet.createRow(this.rowNum);

        for(Iterator var3 = this.titleMap.keySet().iterator(); var3.hasNext(); ++j) {
            String key = (String)var3.next();
            this.currentSheet.setColumnWidth(j, 5120);
            CellUtil.createCell(row, j, (String)this.titleMap.get(key), this.headerStyle);
        }

        ++this.rowNum;
    }

    public void createOceanFile(List<Object> rawData) {
        List<List<Object>> partition = Lists.partition(rawData, this.maxRow);
        this.sLog.append(new String[]{"Partition", String.valueOf(partition.size())});
        AtomicInteger partitionIdx = new AtomicInteger();
        partition.forEach((dataList) -> {
            String var10002 = this.sheetName;
            this.currentSheet = this.currentWorkbook.createSheet(var10002 + (partitionIdx.get() > 0 ? " " + partitionIdx.get() : ""));
            this.rowNum = 0;
            this.generateHeader();
            dataList.forEach(this::generateRow);
            partitionIdx.getAndIncrement();
        });
    }

    private void generateRow(Object object) {
        if (object != null) {
            try {
                Row row = this.currentSheet.createRow(this.rowNum);
                Map dataMap;
                if (object instanceof SearchHit) {
                    SearchHit searchHit = (SearchHit)object;
                    dataMap = searchHit.getSourceAsMap();
                } else if (object instanceof JSONObject) {
                    JSONObject obj = (JSONObject)object;
                    dataMap = (Map)(new ObjectMapper()).readValue(obj.toString(), Map.class);
                } else {
                    dataMap = (Map)object;
                }

                AtomicInteger cellIdx = new AtomicInteger();
                this.titleMap.keySet().forEach((key) -> {
                    String cellData = "";
                    if (dataMap.containsKey(key)) {
                        cellData = this.formatCellDateIfNeeded(dataMap.get(key), key, this.formatMap);
                    } else if (org.apache.commons.lang3.StringUtils.isNotBlank(key)) {
                        cellData = this.getDataFromNestedKey(key, dataMap, this.formatMap);
                    }

                    if (org.apache.commons.lang3.StringUtils.isBlank(cellData)) {
                        cellData = this.tryToGetDefaultValue(key, this.formatMap);
                    }

                    CellUtil.createCell(row, cellIdx.get(), cellData, this.bodyStyle);
                    cellIdx.getAndIncrement();
                });
                if (this.rowNum % 100 == 0) {
                    try {
                        this.currentSheet.flushRows(100);
                    } catch (IOException var6) {
                        this.sLog.append("Error", "Error flush", var6);
                    }
                }

                ++this.rowNum;
            } catch (Exception var7) {
                this.sLog.append("error", "Exception encountered generating row.", var7);
            }
        }

    }

    private String tryToGetDefaultValue(String key, Map<String, JSONObject> formatMap) {
        String cellData = "";
        if (formatMap != null && formatMap.containsKey(key)) {
            JSONObject format = (JSONObject)formatMap.get(key);
            if (("mappingData".equalsIgnoreCase(ConvertUtils.getJString(format, "formatType")) || "mappingDataRegex".equalsIgnoreCase(ConvertUtils.getJString(format, "formatType"))) && format.has("defaultData")) {
                cellData = ConvertUtils.getJString(format, "defaultData");
            }
        }

        return cellData;
    }

    private String getDataFromNestedKey(String key, Map<String, Object> dataMap, Map<String, JSONObject> formatMap) {
        String[] nestedKeys = key.split("\\.");
        if (nestedKeys.length <= 1) {
            return "";
        } else {
            int lastIndex = nestedKeys.length - 1;
            Map<String, Object> lastDataMap = dataMap;

            for(int i = 0; i < nestedKeys.length; ++i) {
                String nestedKey = nestedKeys[i];
                if (i == lastIndex) {
                    return this.formatCellDateIfNeeded(((Map)lastDataMap).get(nestedKey), key, formatMap);
                }

                Object nestedDataMap = ((Map)lastDataMap).get(nestedKey);
                if (ObjectUtils.isEmpty(nestedDataMap)) {
                    return "";
                }

                if (nestedDataMap instanceof LinkedHashMap) {
                    lastDataMap = (LinkedHashMap)nestedDataMap;
                }
            }

            return "";
        }
    }

    private String formatCellDateIfNeeded(Object rawData, String key, Map<String, JSONObject> formatMap) {
        if (MapUtils.isNotEmpty(formatMap) && formatMap.containsKey(key)) {
            JSONObject format = (JSONObject)formatMap.get(key);
            JSONObject data;
            if ("mappingData".equalsIgnoreCase(ConvertUtils.getJString(format, "formatType"))) {
                data = ConvertUtils.getJSONObject(format, "data");
                return ConvertUtils.getJString(data, String.valueOf(rawData));
            }

            String pattern;
            if ("mappingDataRegex".equalsIgnoreCase(ConvertUtils.getJString(format, "formatType"))) {
                data = ConvertUtils.getJSONObject(format, "data");
                Iterator<String> keys = data.keys();

                do {
                    if (!keys.hasNext()) {
                        return Objects.toString(rawData, "");
                    }

                    pattern = (String)keys.next();
                } while(!String.valueOf(rawData).matches(pattern));

                return ConvertUtils.getJString(data, pattern);
            }

            String toFormat;
            if (rawData instanceof LinkedHashMap) {
                switch (ConvertUtils.getJString(format, "formatType")) {
                    case "numberLong":
                        return Objects.toString(((LinkedHashMap)rawData).get("$numberLong"), "");
                    case "numberLongDate":
                        toFormat = Objects.toString(((LinkedHashMap)rawData).get("$numberLong"), "");
                        return this.convertNumberLongDateToFormattedDateString(toFormat, ConvertUtils.getJString(format, "formatDate"));
                    case "isoDate":
                        toFormat = Objects.toString(((LinkedHashMap)rawData).get("$date"), "");
                        return this.convertNumberLongDateToFormattedDateString(toFormat, ConvertUtils.getJString(format, "formatDate"));
                    default:
                        return Objects.toString(rawData, "");
                }
            }

            if (rawData instanceof String) {
                switch (ConvertUtils.getJString(format, "formatType")) {
                    case "customDate":
                        toFormat = ConvertUtils.getJString(format, "fromDate");
                        toFormat = ConvertUtils.getJString(format, "formatDate");
                        return DateTimeUtils.changeDateFormat((String)rawData, toFormat, toFormat);
                    case "numberLongDate":
                        toFormat = ConvertUtils.getJString(format, "formatDate");
                        return this.convertNumberLongDateToFormattedDateString((String)rawData, toFormat);
                    case "long":
                        toFormat = ConvertUtils.getJString(format, "formatString");
                        return ConvertUtils.getNumber(ConvertUtils.toLong((String)rawData), toFormat);
                    case "int":
                        toFormat = ConvertUtils.getJString(format, "formatString");
                        return ConvertUtils.getNumber((double)ConvertUtils.toInt((String)rawData), toFormat);
                    case "mongoId":
                        toFormat = ConvertUtils.getJString(format, "formatDate");
                        ObjectId oid = new ObjectId((String)rawData);
                        return DateTimeUtils.getDate(oid.getDate(), toFormat);
                    default:
                        return Objects.toString(rawData, "");
                }
            }

            if (rawData instanceof Integer) {
                if (ConvertUtils.getJString(format, "formatType").equals("int")) {
                    toFormat = ConvertUtils.getJString(format, "formatString");
                    return ConvertUtils.getNumber((Integer)rawData, toFormat);
                }

                return Objects.toString(rawData, "");
            }

            if (rawData instanceof Long) {
                if (ConvertUtils.getJString(format, "formatType").equals("long")) {
                    toFormat = ConvertUtils.getJString(format, "formatString");
                    return ConvertUtils.getNumber((Long)rawData, toFormat);
                }

                return Objects.toString(rawData, "");
            }

            if (rawData instanceof Double) {
                toFormat = ConvertUtils.getJString(format, "formatString");
                return ConvertUtils.getNumber((Double)rawData, toFormat);
            }
        }

        return Objects.toString(rawData, "");
    }

    private String convertNumberLongDateToFormattedDateString(String numberAsString, String dateFormat) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(numberAsString)) {
            return "";
        } else {
            long dateAsLong = Long.parseLong(numberAsString);
            Timestamp timestamp = new Timestamp(dateAsLong);
            return DateTimeUtils.getDate(timestamp.toLocalDateTime(), dateFormat);
        }
    }

    public void createOceanFile(Object object) {
        if (this.rowNum == 0) {
            String var10002 = this.sheetName;
            this.currentSheet = this.currentWorkbook.createSheet(var10002 + (this.partitionIdx > 0 ? " " + this.partitionIdx : ""));
            this.generateHeader();
        }

        this.generateRow(object);
        if (this.rowNum == this.maxRow) {
            this.rowNum = 0;
            ++this.partitionIdx;
        }

    }

    public void makeZip() throws IOException {
        Runtime.getRuntime().exec(new String[]{"sh", "-c", "cd " + this.folderPath + " && zip -r " + this.zipFileName + " " + this.fileName});
    }
}
