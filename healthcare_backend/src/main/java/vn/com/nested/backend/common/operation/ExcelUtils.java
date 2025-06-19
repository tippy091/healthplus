package vn.com.nested.backend.common.operation;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.com.nested.backend.common.operation.slog.SLog;

import java.io.*;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * @author tippy091
 * @created 02/06/2025
 * @project server
 **/

public final class ExcelUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelUtils.class);
    private final int EXCEL_MIN_ROWS = 10000;
    private final int EXCEL_MAX_ROWS = 1000000;
    private final String directName;
    private final String fileName;
    private final String filePath;
    private final String zipFileName;
    private final FileOutputStream outputStream;
    private final SXSSFWorkbook wb_poi_sxss;
    private final CellStyle borderStyle;
    DecimalFormat currencyFormat = new DecimalFormat("#,###,###");
    private String zipFilePath;
    private Sheet ws_poi_sxss;
    private int rowNum = 0;
    private JSONArray data = new JSONArray();
    private Map<String, String> aTitle;

    public ExcelUtils(String directName, String fileName, Map<String, String> aTitle) throws IOException {
        this.directName = directName;
        this.fileName = fileName + ".xlsx";
        this.zipFileName = fileName + ".zip";
        this.filePath = directName + "/" + this.fileName;
        this.zipFilePath = directName + "/" + this.zipFilePath;
        this.aTitle = aTitle;
        this.makeDirect(this.directName);
        this.deleteFile(this.filePath);
        this.deleteFile(this.zipFilePath);
        this.makeFile(this.filePath);
        this.outputStream = new FileOutputStream(this.filePath);
        this.wb_poi_sxss = new SXSSFWorkbook(-1);
        this.borderStyle = this.wb_poi_sxss.createCellStyle();
        this.borderStyle.setBorderBottom(BorderStyle.THIN);
        this.borderStyle.setBorderTop(BorderStyle.THIN);
        this.borderStyle.setBorderLeft(BorderStyle.THIN);
        this.borderStyle.setBorderRight(BorderStyle.THIN);
        this.borderStyle.setWrapText(true);
    }

    public void startSheet1(String sheetName) {
        this.ws_poi_sxss = this.wb_poi_sxss.createSheet(sheetName);
        this.ws_poi_sxss.setColumnWidth(0, 1792);

        for(int i = 1; i < this.aTitle.size(); ++i) {
            this.ws_poi_sxss.setColumnWidth(i, 5120);
        }

        this.rowNum = 0;
    }

    public void startSheetWithSize(String sheetName, Map<Integer, Integer> cols) {
        this.ws_poi_sxss = this.wb_poi_sxss.createSheet(sheetName);
        this.ws_poi_sxss.setColumnWidth(0, 1792);

        for(int i = 1; i < this.aTitle.size(); ++i) {
            if (cols.containsKey(i)) {
                this.ws_poi_sxss.setColumnWidth(i, (Integer)cols.get(i) * 256);
            } else {
                this.ws_poi_sxss.setColumnWidth(i, 5120);
            }
        }

        this.rowNum = 0;
    }

    public void startSheet(String sheetName) {
        this.ws_poi_sxss = this.wb_poi_sxss.createSheet(sheetName);

        for(int i = 0; i < this.aTitle.size(); ++i) {
            this.ws_poi_sxss.setColumnWidth(i, 5120);
        }

        this.rowNum = 0;
    }

    public String getFilePath() {
        return this.filePath + File.separator + this.fileName;
    }

    public void insertRow(String... params) {
        Row row = this.ws_poi_sxss.createRow(this.rowNum);

        for(int i = 0; i < params.length; ++i) {
            Cell cell = row.createCell(i);
            cell.setCellStyle(this.borderStyle);
            cell.setCellValue(params[i]);
        }

        if (this.rowNum % 100 == 0) {
            try {
                ((SXSSFSheet)this.ws_poi_sxss).flushRows(10000);
            } catch (IOException var5) {
                LOGGER.error(CommonUtils.getCurrentClassAndMethodNames() + ".exp={}", ExceptionUtils.getStackTrace(var5));
            }
        }

        ++this.rowNum;
    }

    public void makeContent(JSONArray rawData, Map<Integer, HorizontalAlignment> listAlign) throws FileNotFoundException, IOException {
        this.data = rawData;

        try {
            for(int i = 0; i < this.data.length(); ++i) {
                Row row = this.ws_poi_sxss.createRow(this.rowNum);
                JSONObject dataElement = this.data.getJSONObject(i);
                int j = 0;

                for(Iterator var7 = this.aTitle.keySet().iterator(); var7.hasNext(); ++j) {
                    String key = (String)var7.next();
                    Cell cell = row.createCell(j);
                    cell.setCellStyle(this.borderStyle);
                    String cellData = "";
                    if (dataElement.has(key)) {
                        cellData = dataElement.getString(key);
                    }

                    cell.setCellValue(cellData);
                    if (listAlign.containsKey(j)) {
                        CellUtil.setAlignment(cell, (HorizontalAlignment)listAlign.get(j));
                    }
                }

                if (this.rowNum % 100 == 0) {
                    ((SXSSFSheet)this.ws_poi_sxss).flushRows(10000);
                }

                ++this.rowNum;
            }
        } catch (JSONException var11) {
            LOGGER.error(CommonUtils.getCurrentClassAndMethodNames() + ".exp={}", ExceptionUtils.getStackTrace(var11));
        }

    }

    public void makeContent(JSONArray rawData) throws IOException {
        this.data = rawData;

        for(int i = 0; i < this.data.length(); ++i) {
            Row row = this.ws_poi_sxss.createRow(this.rowNum);
            JSONObject dataElement = ConvertUtils.getJSONObject(this.data, i);
            Map<String, Object> fieldValueMap = new HashMap();
            ConvertUtils.getMapKeyValue((String)null, dataElement, fieldValueMap);
            int j = 0;

            for(Iterator var7 = this.aTitle.keySet().iterator(); var7.hasNext(); ++j) {
                String key = (String)var7.next();
                Cell cell = row.createCell(j);
                cell.setCellStyle(this.borderStyle);
                String cellData = "";
                if (fieldValueMap.get(key) != null) {
                    cellData = String.valueOf(fieldValueMap.get(key));
                }

                cell.setCellValue(cellData);
            }

            if (this.rowNum % 100 == 0) {
                ((SXSSFSheet)this.ws_poi_sxss).flushRows(10000);
            }

            ++this.rowNum;
        }

    }

    public void makeContentWithSum(JSONArray rawData, Map<String, Long> listColsSum, boolean isBold, HSSFColor color, String sumText, int sumTextPosition) {
        this.data = rawData;

        try {
            for(int i = 0; i < this.data.length(); ++i) {
                Row row = this.ws_poi_sxss.createRow(this.rowNum);
                JSONObject dataElement = this.data.getJSONObject(i);
                int j = 0;

                for(Iterator var11 = this.aTitle.keySet().iterator(); var11.hasNext(); ++j) {
                    String key = (String)var11.next();
                    Cell cell = row.createCell(j);
                    cell.setCellStyle(this.borderStyle);
                    String cellData = "";
                    if (dataElement.has(key)) {
                        cellData = dataElement.getString(key);
                        if (listColsSum.containsKey(key)) {
                            long sumCell = (Long)listColsSum.get(key);
                            sumCell += this.getLong(cellData);
                            listColsSum.put(key, sumCell);
                            cellData = this.currencyFormat.format(this.getLong(cellData));
                            CellUtil.setAlignment(cell, HorizontalAlignment.RIGHT);
                        }
                    }

                    cell.setCellValue(cellData);
                }

                if (this.rowNum % 100 == 0) {
                    ((SXSSFSheet)this.ws_poi_sxss).flushRows(10000);
                }

                ++this.rowNum;
            }

            this.makeSumFooter(listColsSum, true, color, sumText, sumTextPosition);
        } catch (Exception var17) {
            LOGGER.error(CommonUtils.getCurrentClassAndMethodNames() + ".exp={}", ExceptionUtils.getStackTrace(var17));
        }

    }

    private void makeSumFooter(Map<String, Long> listColsSum, boolean isBold, HSSFColor color, String sumText, int sumTextPosition) {
        Font font = this.wb_poi_sxss.createFont();
        font.setBold(isBold);
        font.setColor(color.getIndex());
        CellStyle cellStyle = this.wb_poi_sxss.createCellStyle();
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setWrapText(true);
        cellStyle.setFont(font);
        if (listColsSum.size() > 0) {
            int j = 0;
            Row row = this.ws_poi_sxss.createRow(this.rowNum);

            for(Iterator var10 = this.aTitle.keySet().iterator(); var10.hasNext(); ++j) {
                String key = (String)var10.next();
                Cell cell = row.createCell(j);
                cell.setCellStyle(cellStyle);
                CellUtil.setAlignment(cell, HorizontalAlignment.RIGHT);
                if (listColsSum.containsKey(key)) {
                    cell.setCellValue(this.currencyFormat.format(listColsSum.get(key)));
                }

                if (sumTextPosition == j) {
                    cell.setCellValue(sumText);
                }
            }
        }

    }

    private Long getLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException var3) {
            return 0L;
        }
    }

    public void makeFooter(JSONArray rawData, Map<Integer, HorizontalAlignment> listAlign, boolean isBold, HSSFColor color) {
        this.data = rawData;

        try {
            Font font = this.wb_poi_sxss.createFont();
            font.setBold(isBold);
            font.setColor(color.getIndex());
            CellStyle cellStyle = this.wb_poi_sxss.createCellStyle();
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setWrapText(true);
            cellStyle.setFont(font);

            for(int i = 0; i < this.data.length(); ++i) {
                Row row = this.ws_poi_sxss.createRow(this.rowNum);
                JSONObject dataElement = this.data.getJSONObject(i);
                int j = 0;

                for(Iterator var11 = this.aTitle.keySet().iterator(); var11.hasNext(); ++j) {
                    String key = (String)var11.next();
                    Cell cell = row.createCell(j);
                    cell.setCellStyle(cellStyle);
                    String cellData = "";
                    if (dataElement.has(key)) {
                        cellData = dataElement.getString(key);
                    }

                    cell.setCellValue(cellData);
                    if (listAlign.containsKey(j)) {
                        CellUtil.setAlignment(cell, (HorizontalAlignment)listAlign.get(j));
                    }
                }

                if (this.rowNum % 100 == 0) {
                    ((SXSSFSheet)this.ws_poi_sxss).flushRows(10000);
                }

                ++this.rowNum;
            }
        } catch (Exception var15) {
            LOGGER.error(CommonUtils.getCurrentClassAndMethodNames() + ".exp={}", ExceptionUtils.getStackTrace(var15));
        }

    }

    public void makeContent1(JSONArray rawData) throws IOException {
        this.data = rawData;

        try {
            Font font = this.wb_poi_sxss.createFont();
            font.setBold(true);
            CellStyle cellStyle = this.wb_poi_sxss.createCellStyle();
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setWrapText(true);
            cellStyle.setFont(font);

            for(int i = 0; i < this.data.length(); ++i) {
                Row row = this.ws_poi_sxss.createRow(this.rowNum);
                JSONObject dataElement = this.data.getJSONObject(i);
                int j = 0;

                for(Iterator var8 = this.aTitle.keySet().iterator(); var8.hasNext(); ++j) {
                    String key = (String)var8.next();
                    Cell cell = row.createCell(j);
                    cell.setCellStyle(cellStyle);
                    String cellData = "";
                    if (dataElement.has(key)) {
                        cellData = dataElement.getString(key);
                    }

                    cell.setCellValue(cellData);
                }

                if (this.rowNum % 100 == 0) {
                    ((SXSSFSheet)this.ws_poi_sxss).flushRows(10000);
                }

                ++this.rowNum;
            }
        } catch (JSONException var12) {
            LOGGER.error(CommonUtils.getCurrentClassAndMethodNames() + ".exp={}", ExceptionUtils.getStackTrace(var12));
        }

    }

    public void makeContent2(JSONArray rawData) throws IOException {
        this.data = rawData;

        try {
            Font font = this.wb_poi_sxss.createFont();
            CellStyle cellStyle = this.wb_poi_sxss.createCellStyle();
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setWrapText(true);
            cellStyle.setFont(font);

            for(int i = 0; i < this.data.length(); ++i) {
                Row row = this.ws_poi_sxss.getRow(this.rowNum);
                if (row == null) {
                    row = this.ws_poi_sxss.createRow(this.rowNum);
                }

                JSONObject dataElement = this.data.getJSONObject(i);
                int j = 0;

                for(Iterator var8 = this.aTitle.keySet().iterator(); var8.hasNext(); ++j) {
                    String key = (String)var8.next();
                    Cell cell = row.createCell(j);
                    cell.setCellStyle(cellStyle);
                    String cellData = "";
                    if (dataElement.has(key)) {
                        cellData = dataElement.getString(key);
                    }

                    cell.setCellValue(cellData);
                }

                if (this.rowNum % 100 == 0) {
                    ((SXSSFSheet)this.ws_poi_sxss).flushRows(10000);
                }

                ++this.rowNum;
            }
        } catch (JSONException var12) {
            LOGGER.error(CommonUtils.getCurrentClassAndMethodNames() + ".exp={}", ExceptionUtils.getStackTrace(var12));
        }

    }

    public void makeContent3(JSONArray rawData) throws IOException {
        this.data = rawData;

        try {
            Font font = this.wb_poi_sxss.createFont();
            CellStyle cellStyle = this.wb_poi_sxss.createCellStyle();
            cellStyle.setWrapText(true);
            cellStyle.setFont(font);
            cellStyle.setAlignment(HorizontalAlignment.LEFT);

            for(int i = 0; i < this.data.length(); ++i) {
                Row row = this.ws_poi_sxss.createRow(this.rowNum);
                JSONObject dataElement = this.data.getJSONObject(i);
                int j = 0;

                for(Iterator var8 = this.aTitle.keySet().iterator(); var8.hasNext(); ++j) {
                    String key = (String)var8.next();
                    if ("num_trans".equalsIgnoreCase(key)) {
                        this.ws_poi_sxss.addMergedRegion(new CellRangeAddress(this.rowNum, this.rowNum, 2, 5));
                    }

                    Cell cell = row.createCell(j);
                    cell.setCellStyle(cellStyle);
                    String cellData = "";
                    if (dataElement.has(key)) {
                        cellData = dataElement.getString(key);
                    }

                    cell.setCellValue(cellData);
                }

                if (this.rowNum % 100 == 0) {
                    ((SXSSFSheet)this.ws_poi_sxss).flushRows(10000);
                }

                ++this.rowNum;
            }
        } catch (JSONException var12) {
            LOGGER.error(CommonUtils.getCurrentClassAndMethodNames() + ".exp={}", ExceptionUtils.getStackTrace(var12));
        }

    }

    public void makeContent4(JSONArray rawData) throws IOException {
        this.data = rawData;

        try {
            Font font = this.wb_poi_sxss.createFont();
            CellStyle cellStyle = this.wb_poi_sxss.createCellStyle();
            cellStyle.setWrapText(true);
            cellStyle.setFont(font);
            cellStyle.setAlignment(HorizontalAlignment.LEFT);
            cellStyle.setVerticalAlignment(VerticalAlignment.TOP);

            for(int i = 0; i < this.data.length(); ++i) {
                Row row = this.ws_poi_sxss.createRow(this.rowNum);
                JSONObject dataElement = this.data.getJSONObject(i);
                int j = 0;

                for(Iterator var8 = this.aTitle.keySet().iterator(); var8.hasNext(); ++j) {
                    String key = (String)var8.next();
                    if ("num_trans".equalsIgnoreCase(key)) {
                        this.ws_poi_sxss.addMergedRegion(new CellRangeAddress(this.rowNum, this.rowNum, 2, 5));
                    }

                    Cell cell = row.createCell(j);
                    cell.setCellStyle(cellStyle);
                    String cellData = "";
                    if (dataElement.has(key)) {
                        cellData = dataElement.getString(key);
                    }

                    cell.setCellValue(cellData);
                }

                if (this.rowNum % 100 == 0) {
                    ((SXSSFSheet)this.ws_poi_sxss).flushRows(10000);
                }

                ++this.rowNum;
            }
        } catch (JSONException var12) {
            LOGGER.error(CommonUtils.getCurrentClassAndMethodNames() + ".exp={}", ExceptionUtils.getStackTrace(var12));
        }

    }

    public void makeContent5(JSONArray rawData) throws IOException {
        this.data = rawData;

        try {
            Font font = this.wb_poi_sxss.createFont();
            font.setBold(true);
            CellStyle cellStyle = this.wb_poi_sxss.createCellStyle();
            cellStyle.setFont(font);
            cellStyle.setAlignment(HorizontalAlignment.CENTER);

            for(int i = 0; i < this.data.length(); ++i) {
                Row row = this.ws_poi_sxss.createRow(this.rowNum);
                JSONObject dataElement = this.data.getJSONObject(i);
                int j = 0;

                for(Iterator var8 = this.aTitle.keySet().iterator(); var8.hasNext(); ++j) {
                    String key = (String)var8.next();
                    Cell cell = row.createCell(j);
                    cell.setCellStyle(cellStyle);
                    String cellData = "";
                    if (dataElement.has(key)) {
                        cellData = dataElement.getString(key);
                    }

                    cell.setCellValue(cellData);
                }

                if (this.rowNum % 100 == 0) {
                    ((SXSSFSheet)this.ws_poi_sxss).flushRows(10000);
                }

                ++this.rowNum;
            }
        } catch (JSONException var12) {
            LOGGER.error(CommonUtils.getCurrentClassAndMethodNames() + ".exp={}", ExceptionUtils.getStackTrace(var12));
        }

    }

    public void mergeCellLeft(String content, boolean isBold, int rowHeight) {
        int titleLength = this.aTitle.size();
        Row row = this.ws_poi_sxss.createRow(this.rowNum);
        this.ws_poi_sxss.addMergedRegion(new CellRangeAddress(this.rowNum, this.rowNum, 0, titleLength - 1));
        CellStyle cellStyle = this.wb_poi_sxss.createCellStyle();
        cellStyle.setWrapText(true);
        Cell cell = CellUtil.createCell(row, 0, content);
        CellUtil.setAlignment(cell, HorizontalAlignment.LEFT);
        cell.setCellStyle(cellStyle);
        row.setHeightInPoints((float)rowHeight * this.ws_poi_sxss.getDefaultRowHeightInPoints());
        if (isBold) {
            Font font = this.wb_poi_sxss.createFont();
            font.setBold(true);
            CellUtil.setFont(cell, font);
        }

        ++this.rowNum;
    }

    public void mergeCellCenter(String content, boolean isBold, int rowHeight) {
        int titleLength = this.aTitle.size();
        Row row = this.ws_poi_sxss.createRow(this.rowNum);
        this.ws_poi_sxss.addMergedRegion(new CellRangeAddress(this.rowNum, this.rowNum, 0, titleLength - 1));
        CellStyle cellStyle = this.wb_poi_sxss.createCellStyle();
        cellStyle.setWrapText(true);
        Cell cell = CellUtil.createCell(row, 0, content);
        cell.setCellStyle(cellStyle);
        CellUtil.setAlignment(cell, HorizontalAlignment.CENTER);
        row.setHeightInPoints((float)rowHeight * this.ws_poi_sxss.getDefaultRowHeightInPoints());
        if (isBold) {
            Font font = this.wb_poi_sxss.createFont();
            font.setBold(true);
            CellUtil.setFont(cell, font);
        }

        ++this.rowNum;
    }

    public void mergeCellCenter(String content, boolean isBold, int rowHeight, HSSFColor color, int fontSize) {
        int titleLength = this.aTitle.size();
        Row row = this.ws_poi_sxss.createRow(this.rowNum);
        this.ws_poi_sxss.addMergedRegion(new CellRangeAddress(this.rowNum, this.rowNum, 0, titleLength - 1));
        CellStyle cellStyle = this.wb_poi_sxss.createCellStyle();
        cellStyle.setWrapText(true);
        Cell cell = CellUtil.createCell(row, 0, content);
        cell.setCellStyle(cellStyle);
        CellUtil.setAlignment(cell, HorizontalAlignment.CENTER);
        row.setHeightInPoints((float)rowHeight * this.ws_poi_sxss.getDefaultRowHeightInPoints());
        Font font = this.wb_poi_sxss.createFont();
        font.setBold(isBold);
        font.setColor(color.getIndex());
        font.setFontHeight((short)fontSize);
        CellUtil.setFont(cell, font);
        ++this.rowNum;
    }

    public void mergeCellCenter(String content, boolean isBold, boolean isItalic) {
        int titleLength = this.aTitle.size();
        Row row = this.ws_poi_sxss.createRow(this.rowNum);
        this.ws_poi_sxss.addMergedRegion(new CellRangeAddress(this.rowNum, this.rowNum, 0, titleLength - 1));
        Cell cell = CellUtil.createCell(row, 0, content);
        CellUtil.setAlignment(cell, HorizontalAlignment.CENTER);
        Font font;
        if (isBold) {
            font = this.wb_poi_sxss.createFont();
            font.setBold(true);
            CellUtil.setFont(cell, font);
        }

        if (isItalic) {
            font = this.wb_poi_sxss.createFont();
            font.setItalic(true);
            CellUtil.setFont(cell, font);
        }

        row.setHeightInPoints(2.0F * this.ws_poi_sxss.getDefaultRowHeightInPoints());
        ++this.rowNum;
    }

    public void mergeCellCenter1(String content, boolean isBold) {
        int titleLength = this.aTitle.size();
        Row row = this.ws_poi_sxss.createRow(this.rowNum);
        this.ws_poi_sxss.addMergedRegion(new CellRangeAddress(this.rowNum, this.rowNum, 0, titleLength - 1));
        Cell cell = CellUtil.createCell(row, 0, content);
        CellUtil.setAlignment(cell, HorizontalAlignment.CENTER);
        Font font = this.wb_poi_sxss.createFont();
        font.setFontName("Times New Roman");
        if (isBold) {
            font.setBold(true);
        }

        CellUtil.setFont(cell, font);
        ++this.rowNum;
    }

    public void mergeRowLeft(String content, int numOfRow, int colIndex, boolean isBold) {
        Row row = this.ws_poi_sxss.getRow(this.rowNum - numOfRow);
        if (row == null) {
            row = this.ws_poi_sxss.createRow(this.rowNum - numOfRow);
        }

        Cell cell = CellUtil.createCell(row, colIndex, content);
        this.ws_poi_sxss.addMergedRegion(new CellRangeAddress(this.rowNum - numOfRow, this.rowNum - 1, colIndex, colIndex));
        Font font = this.wb_poi_sxss.createFont();
        CellStyle cellStyle = this.wb_poi_sxss.createCellStyle();
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setWrapText(true);
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setFont(font);
        if (isBold) {
            font.setBold(true);
        }

        CellUtil.setFont(cell, font);
        cell.setCellStyle(cellStyle);
    }

    public void insertFooter(String content, boolean isBold) {
        int titleLength = this.aTitle.size();
        Row row = this.ws_poi_sxss.createRow(this.rowNum);
        int position = titleLength >= 3 ? titleLength - 3 : 0;
        this.ws_poi_sxss.addMergedRegion(new CellRangeAddress(this.rowNum, this.rowNum, position, titleLength - 1));
        Cell cell = CellUtil.createCell(row, position, content);
        CellUtil.setAlignment(cell, HorizontalAlignment.CENTER);
        if (isBold) {
            Font font = this.wb_poi_sxss.createFont();
            font.setBold(true);
            CellUtil.setFont(cell, font);
        }

        ++this.rowNum;
    }

    public void insertFooterMerge(String content, boolean isBold, HorizontalAlignment align) {
        int titleLength = this.aTitle.size();
        Row row = this.ws_poi_sxss.createRow(this.rowNum);
        this.ws_poi_sxss.addMergedRegion(new CellRangeAddress(this.rowNum, this.rowNum, 0, titleLength - 1));
        Cell cell = CellUtil.createCell(row, 0, content);
        CellUtil.setAlignment(cell, align);
        if (isBold) {
            Font font = this.wb_poi_sxss.createFont();
            font.setBold(true);
            CellUtil.setFont(cell, font);
        }

        ++this.rowNum;
    }

    public void insertFooterLeft(String content, String content1, String content2) {
        Row row = this.ws_poi_sxss.createRow(this.rowNum);
        int position = 1;
        int distance = 3;
        this.ws_poi_sxss.addMergedRegion(new CellRangeAddress(this.rowNum, this.rowNum, position, distance));
        CellUtil.createCell(row, position, content);
        CellUtil.createCell(row, position + distance, content1);
        CellUtil.createCell(row, position + distance + 1, content2);
        ++this.rowNum;
    }

    public void insertTitle() {
        int j = 0;
        Row row = this.ws_poi_sxss.createRow(this.rowNum);
        Font font = this.wb_poi_sxss.createFont();
        font.setBold(true);
        XSSFCellStyle style = (XSSFCellStyle)this.wb_poi_sxss.createCellStyle();
        style.setFont(font);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setWrapText(true);

        for(Iterator var5 = this.aTitle.keySet().iterator(); var5.hasNext(); ++j) {
            String key = (String)var5.next();
            Cell cell = CellUtil.createCell(row, j, (String)this.aTitle.get(key));
            cell.setCellStyle(style);
        }

        ++this.rowNum;
    }

    public void insertCustomRow(boolean isbold, String... listData) {
        int j = 0;
        Row row = this.ws_poi_sxss.createRow(this.rowNum);
        Font font = this.wb_poi_sxss.createFont();
        font.setBold(isbold);
        XSSFCellStyle style = (XSSFCellStyle)this.wb_poi_sxss.createCellStyle();
        style.setFont(font);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setWrapText(true);
        String[] var7 = listData;
        int var8 = listData.length;

        for(int var9 = 0; var9 < var8; ++var9) {
            String listDatum = var7[var9];
            Cell cell = CellUtil.createCell(row, j, listDatum);
            cell.setCellStyle(style);
            ++j;
        }

        ++this.rowNum;
    }

    public void insertCustomRow(boolean isbold, List listData, Map<Integer, HorizontalAlignment> listAlign, boolean isBorder) {
        int j = 0;
        Row row = this.ws_poi_sxss.createRow(this.rowNum);
        Font font = this.wb_poi_sxss.createFont();
        font.setBold(isbold);
        XSSFCellStyle style = (XSSFCellStyle)this.wb_poi_sxss.createCellStyle();
        style.setFont(font);
        if (isBorder) {
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
        }

        style.setWrapText(true);

        for(int i = 0; i < listData.size(); ++i) {
            Cell cell = CellUtil.createCell(row, j, listData.get(i).toString());
            style.setAlignment((HorizontalAlignment)listAlign.get(i));
            cell.setCellStyle(style);
            ++j;
        }

        ++this.rowNum;
    }

    public void insertRow() {
        ++this.rowNum;
    }

    public void stop() throws IOException {
        this.wb_poi_sxss.write(this.outputStream);
        this.outputStream.close();
        this.wb_poi_sxss.dispose();
    }

    public void makeZip() throws IOException {
        Runtime.getRuntime().exec(new String[]{"sh", "-c", "cd " + this.directName + " && zip -r " + this.zipFileName + " " + this.fileName});
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

    public void setTitle(Map<String, String> aTitle) {
        this.aTitle = aTitle;
    }

    public void makeRowTotalCount(int totalCount) {
        Font font = this.wb_poi_sxss.createFont();
        font.setBold(true);
        XSSFCellStyle style = (XSSFCellStyle)this.wb_poi_sxss.createCellStyle();
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setWrapText(true);
        Row row = this.ws_poi_sxss.createRow(this.rowNum);
        Cell cell = row.createCell(0);
        cell.setCellStyle(this.borderStyle);
        cell.setCellValue("Total Count");
        cell.setCellStyle(style);
        cell = row.createCell(1);
        cell.setCellStyle(this.borderStyle);
        cell.setCellValue((double)totalCount);
        cell.setCellStyle(style);
        ++this.rowNum;
    }

    public void insertTitleV2() {
        int j = 0;
        Row row = this.ws_poi_sxss.createRow(this.rowNum);
        Font font = this.wb_poi_sxss.createFont();
        font.setBold(true);
        XSSFCellStyle style = (XSSFCellStyle)this.wb_poi_sxss.createCellStyle();
        style.setFont(font);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setWrapText(true);

        for(Iterator var5 = this.aTitle.keySet().iterator(); var5.hasNext(); ++j) {
            String key = (String)var5.next();
            this.ws_poi_sxss.setColumnWidth(j, 5120);
            Cell cell = CellUtil.createCell(row, j, (String)this.aTitle.get(key));
            cell.setCellStyle(style);
        }

        ++this.rowNum;
    }

    public void createOceanFile(List<Object> rawData, String sheetName, int maxRow, SLog sLog) {
        if (maxRow < 10000) {
            maxRow = 10000;
        }

        if (maxRow > 1000000) {
            maxRow = 1000000;
        }

        List<List<Object>> partition = Lists.partition(rawData, maxRow);
        sLog.append(new String[]{"Partition", String.valueOf(partition.size())});
        AtomicInteger partitionIdx = new AtomicInteger();
        partition.forEach((dataList) -> {
            this.ws_poi_sxss = this.wb_poi_sxss.createSheet(sheetName + (partitionIdx.get() > 0 ? " " + partitionIdx.get() : ""));
            this.rowNum = 0;
            this.insertTitleV2();
            dataList.forEach((object) -> {
                Row row = this.ws_poi_sxss.createRow(this.rowNum);
                Map dataMap;
                if (object instanceof SearchHit searchHit) {
                    dataMap = searchHit.getSourceAsMap();
                } else {
                    dataMap = (Map)object;
                }

                AtomicInteger cellIdx = new AtomicInteger();
                this.aTitle.keySet().forEach((key) -> {
                    Cell cell = row.createCell(cellIdx.get());
                    cell.setCellStyle(this.borderStyle);
                    String cellData = "";
                    if (dataMap.containsKey(key)) {
                        cellData = String.valueOf(dataMap.get(key));
                    }

                    cell.setCellValue(cellData);
                    cellIdx.getAndIncrement();
                });
                if (this.rowNum % 100 == 0) {
                    try {
                        ((SXSSFSheet)this.ws_poi_sxss).flushRows(100);
                    } catch (IOException var7) {
                        sLog.append("Error", "Error flush", var7);
                    }
                }

                ++this.rowNum;
            });
            partitionIdx.getAndIncrement();
        });
    }
}
