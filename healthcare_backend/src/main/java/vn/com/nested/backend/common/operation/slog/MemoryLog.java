package vn.com.nested.backend.common.operation.slog;

import vn.com.nested.backend.common.operation.DateTimeUtils;

/**
 * @author tippy091
 * @created 17/06/2025
 * @project healthcare_backend
 **/
public class MemoryLog {

    private static final String FORMAT_TIME = "dd/MM/yyyy hh:mm:ss";


    private long free;
    private long total;
    private long max;
    private String startTime;

    public MemoryLog() { this.reset();}

    public void reset() {
        this.free = Runtime.getRuntime().freeMemory();
        this.total = Runtime.getRuntime().totalMemory();
        this.max = Runtime.getRuntime().maxMemory();
        this.startTime = DateTimeUtils.getCurrentDateTime("dd/MM/yyyy hh:mm:ss");
    }

    public String getMemory(){
        long free = Runtime.getRuntime().freeMemory();
        long total = Runtime.getRuntime().totalMemory();
        String endTime = DateTimeUtils.getCurrentDateTime("dd/MM/yyyy hh:mm:ss");
        return String.format("startTime[%s] endTime[%s] MAX[%,d] RAM[%,d] startFree[%, d] endFree[%,d] startTotal[%,d] endTotal[%,d] useFree[%,d] useTotal[%,d]", this.startTime, endTime, this.max, total - free, this.free, free, this.total, this.free - free, total - this.total);
    }
}
