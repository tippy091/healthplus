package vn.com.healthcare.utils;

import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tippy091
 * @created 17/06/2025
 * @project healthcare_backend
 **/
public class EnumConstants {


    public EnumConstants() {

    }

    public static enum STATUS {
        PENDING,
        ACTIVE,
        INACTIVE;

        private STATUS() {

        }
    }

    public static enum SYS_PERMISSION {
        ROLE_VIEW("view"),
        ROLE_EDIT("edit"),
        ROLE_DELETE("delete"),
        ROLE_ADD("add"),
        ROLE_APPROVAL("approval"),
        ROLE_EXPORT("export");

        private static final Map<String, SYS_PERMISSION> map = new HashMap();
        private final String value;

        SYS_PERMISSION(String value) {
            this.value = value;
        }

        public static SYS_PERMISSION fromValue(String value) {
            return MapUtils.getObject(map, value);
        }

        public String getValue() {
            return this.value;
        }

        static {
            SYS_PERMISSION[] var0 = values();
            int var1 = var0.length;

            for(int var2 = 0; var2 < var1; ++var2) {
                SYS_PERMISSION e = var0[var2];
                map.put(e.getValue(), e);
            }

        }
    }

    public static enum USER_TYPE {
        GLOBAL_ADMIN,
        PATIENT,
        DEPARTMENT_DOCTOR,
        DEPARTMENT_MANAGER,
        NURSE;
        private USER_TYPE() {

        }
    }

    public static enum RESULT {
        SUCCESS,
        FAIL,
        ERROR;

        private RESULT() {

        }
    }
}
