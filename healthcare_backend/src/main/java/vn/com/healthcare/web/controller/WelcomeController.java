package vn.com.healthcare.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import vn.com.nested.backend.common.operation.slog.SLog;
import vn.com.nested.backend.parent.log.SLogInterface;
/**
 * @author tippy091
 * @created 17/06/2025
 * @project healthcare_backend
 **/
public class WelcomeController {

    @GetMapping("/health-check")
    public ResponseEntity<String> checkHealth(@SLogInterface SLog slog) {
        return new ResponseEntity<>("Service is operational", HttpStatus.OK);
    }
}
