package vn.com.nested.backend.common.operation.json;

/**
 * @author tippy091
 * @created 02/06/2025
 * @project server
 **/
public class JsonParsingException extends RuntimeException {
    public JsonParsingException(Throwable t) {
        super(t);
    }
}
