package org.api.doit.exception.handler;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to build a standardized error response for global exceptions.
 * This class is used to format the exception details into a Map structure,
 * which can be returned as a JSON response in case of errors.
 */
public class GlobalExceptionBuilder {

    /**
     * Builds a standardized error response with essential information.
     *
     * @param status the HTTP status code for the error
     * @param errorTitle a short title describing the error
     * @param errorMessage detailed error message to provide more information
     * @param path the request URI where the error occurred
     * @param method the HTTP method used (GET, POST, etc.)
     * @return a map containing the error details
     */
    public static Map<String, Object> build(int status,
                                            String errorTitle,
                                            Object errorMessage,
                                            String path,
                                            String method) {
        Map<String, Object> data = new HashMap<>();

        data.put("status", status); // Status code for the error
        data.put("error", errorTitle); // Title describing the error
        data.put("message", errorMessage); // Detailed message about the error
        data.put("path", path); // Path of the request that caused the error
        data.put("method", method); // HTTP method used in the request
        data.put("timestamp", Instant.now().toString()); // Timestamp of the error

        return data;
    }
}
