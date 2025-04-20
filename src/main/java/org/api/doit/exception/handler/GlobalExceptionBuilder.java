package org.api.doit.exception.handler;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class GlobalExceptionBuilder {
    public static Map<String, Object> build(int status,
                                            String errorTitle,
                                            String errorMessage,
                                            String path,
                                            String method) {
        Map<String, Object> data = new HashMap<>();

        data.put("status", status);
        data.put("error", errorTitle);
        data.put("message", errorMessage);
        data.put("path", path);
        data.put("method", method);
        data.put("timestamp", Instant.now().toString());

        return data;
    }
}
