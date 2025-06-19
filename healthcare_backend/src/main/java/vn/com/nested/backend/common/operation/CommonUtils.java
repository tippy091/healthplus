package vn.com.nested.backend.common.operation;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;
import vn.com.nested.backend.common.operation.crypto.RandomPassword;
import vn.com.nested.backend.common.operation.exception.ExportExcelException;
import vn.com.nested.backend.common.operation.slog.SLog;
import vn.com.nested.backend.common.uis.utils.CryptoUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author tippy091
 * @created 02/06/2025
 * @project server
 **/

public class CommonUtils {
    public CommonUtils() {
    }

    public static String getCurrentClassAndMethodNames() {
        StackTraceElement e = Thread.currentThread().getStackTrace()[2];
        String s = e.getClassName();
        String var10000 = s.substring(s.lastIndexOf(46) + 1);
        return var10000 + "." + e.getMethodName();
    }

    public static Boolean nullOrBlank(Object object) {
        return null != object && !StringUtils.isNullOrEmpty(object.toString()) ? Boolean.FALSE : Boolean.TRUE;
    }

    public static String appendLi(String newValue) {
        return "<li>" + newValue + "</li>";
    }

    public static String generatePassword() {
        String easy = "0123456789ACEFGHJKLMNPQRUVWXYabcdefhijkprstuvwx";
        RandomPassword password = new RandomPassword(15, new SecureRandom(), easy);
        return password.nextString();
    }

    public static String getHash(String value, String key) {
        return CryptoUtil.sha256(value + "|" + key);
    }

    public static boolean checkValidInArray(String value, JSONArray jarr, String key) {
        if (!ConvertUtils.checkJSONArray(jarr)) {
            return false;
        } else {
            for(int i = 0; i < jarr.length(); ++i) {
                JSONObject jsonObject = ConvertUtils.getJSONObject(jarr, i);
                if (value.equals(ConvertUtils.getJString(jsonObject, key))) {
                    return true;
                }
            }

            return false;
        }
    }

    public static boolean putJSONObject(JSONObject jsonObject, String key, Object value) {
        try {
            jsonObject.put(key, value);
            return true;
        } catch (JSONException var4) {
            return false;
        }
    }

    public static String genUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static File createFile(JSONArray listData, Map<String, String> mapData, String sheetName, String pathFolder, String fileName) throws IOException {
        Map<String, String> aTitle = new LinkedHashMap(mapData);
        ExcelUtils excelUtils = new ExcelUtils(pathFolder, fileName, aTitle);
        excelUtils.startSheet(sheetName);
        excelUtils.insertTitle();

        for(int i = 0; i < listData.length(); ++i) {
            JSONObject trans = ConvertUtils.getJSONObject(listData, i);
            String[] values = new String[mapData.size()];
            int j = 0;

            for(Iterator var11 = mapData.entrySet().iterator(); var11.hasNext(); ++j) {
                Map.Entry<String, String> entry = (Map.Entry)var11.next();
                values[j] = ConvertUtils.getJString(trans, (String)entry.getKey());
            }

            excelUtils.insertRow(values);
        }

        excelUtils.stop();
        return new File(pathFolder + File.separator, FilenameUtils.getName(excelUtils.getFilePath()));
    }

    public static void makeDirect(String folderPath) {
        File file = new File(folderPath);
        if (!file.exists()) {
            file.mkdirs();
        }

    }

    public static File createFolderPath(String folderPath) {
        File file = new File(folderPath);
        if (!file.exists()) {
            file.mkdirs();
        }

        return file;
    }

    public static String createExcel(Map<String, String> title, String folderPath, String fileName, String sheetName, JSONArray records, SLog sLog) {
        try {
            ExcelUtils fileUtils = new ExcelUtils(folderPath, fileName, title);
            fileUtils.setTitle(title);
            fileUtils.startSheet(sheetName);
            fileUtils.insertTitle();
            fileUtils.makeContent(records);
            fileUtils.stop();
            return folderPath + fileName + ".xlsx";
        } catch (IOException var7) {
            sLog.append(new String[]{"createExcel.exp", ExceptionUtils.getStackTrace(var7)});
            return null;
        }
    }

    public static String createExcelToDie(Map<String, String> title, String folderPath, String fileName, String sheetName, List<Object> records, int maxRow, boolean isZip, SLog sLog) throws ExportExcelException {
        try {
            OceanExcelUtils fileUtils = new OceanExcelUtils(folderPath, fileName, sheetName, title, records.size(), maxRow, sLog);
            return createOceanFileInternal(folderPath, fileName, records, isZip, fileUtils);
        } catch (ExportExcelException var9) {
            sLog.append(new String[]{"createExcel.exp", ExceptionUtils.getStackTrace(var9)});
            throw var9;
        } catch (Exception var10) {
            sLog.append(new String[]{"createExcel.exp", ExceptionUtils.getStackTrace(var10)});
            return null;
        }
    }

    public static String createExcelToDie(Map<String, String> title, Map<String, JSONObject> formatMap, String folderPath, String fileName, String sheetName, List<Object> records, int maxRow, boolean isZip, SLog sLog) throws ExportExcelException {
        try {
            OceanExcelUtils fileUtils = new OceanExcelUtils(folderPath, fileName, sheetName, title, records.size(), maxRow, formatMap, sLog);
            return createOceanFileInternal(folderPath, fileName, records, isZip, fileUtils);
        } catch (ExportExcelException var10) {
            sLog.append(new String[]{"createExcel.exp", ExceptionUtils.getStackTrace(var10)});
            throw var10;
        } catch (Exception var11) {
            sLog.append(new String[]{"createExcel.exp", ExceptionUtils.getStackTrace(var11)});
            return null;
        }
    }

    private static String createOceanFileInternal(String folderPath, String fileName, List<Object> records, boolean isZip, OceanExcelUtils fileUtils) throws IOException {
        fileUtils.createOceanFile(records);
        fileUtils.stop();
        if (!isZip) {
            return folderPath + fileName + ".xlsx";
        } else {
            fileUtils.makeZip();
            return folderPath + fileName + ".zip";
        }
    }

    public static JSONObject getFileInfo(String filePath, String createdBy, JSONObject fileJS) throws Exception {
        MultipartFile multipartFile = (MultipartFile)ConvertUtils.getJObject(fileJS, "file");
        String fullFilePath = filePath + "/" + ConvertUtils.getJString(fileJS, "id") + "_" + ConvertUtils.getJString(fileJS, "name");
        File fileDir = new File(filePath);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

        String var10002 = filePath + "/";
        String var10003 = ConvertUtils.getJString(fileJS, "id");
        File file = new File(var10002, var10003 + "_" + ConvertUtils.getJString(fileJS, "name"));
        JSONObject fileInfo = new JSONObject();
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(multipartFile.getBytes());
        fos.close();
        fileInfo.put("filePath", fullFilePath);
        var10002 = ConvertUtils.getJString(fileJS, "id");
        fileInfo.put("fileName", var10002 + "_" + ConvertUtils.getJString(fileJS, "name"));
        fileInfo.put("orgFileName", ConvertUtils.getJString(fileJS, "name"));
        fileInfo.put("createdBy", createdBy);
        fileInfo.put("createdDate", ConvertUtils.getNow("yyyy-MM-dd HH:mm:ss"));
        return fileInfo;
    }

    public static <V> List<V> lookupMulti(String regularExpression, Map<String, V> map) {
        Matcher matcher = Pattern.compile(regularExpression).matcher("");
        Iterator<String> iterator = map.keySet().iterator();
        List<V> values = new ArrayList();

        while(iterator.hasNext()) {
            String key = (String)iterator.next();
            if (matcher.reset(key).matches()) {
                values.add(map.get(key));
            }
        }

        return values;
    }

    public static <V> Object lookupOne(String regularExpression, Map<String, V> map) {
        Matcher matcher = Pattern.compile(regularExpression).matcher("");
        Iterator var3 = map.keySet().iterator();

        String key;
        do {
            if (!var3.hasNext()) {
                return new Object();
            }

            key = (String)var3.next();
        } while(!matcher.reset(key).matches());

        return key;
    }
}

