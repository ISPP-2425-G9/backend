package com.caronte.caronte.util.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class ResponseThrow {

    public static void check(boolean condition, HttpStatusCode status, String reason) {
        if(!condition) {
            throw new ResponseStatusException(status, reason);
        }
    }

    public static void checkOrNotFound(boolean condition, String reason) {
        check(condition, HttpStatus.NOT_FOUND, reason);
    }

    public static void checkOrBadRequest(boolean condition, String reason) {
        check(condition, HttpStatus.BAD_REQUEST, reason);
    }

    public static void checkOrForbidden(boolean condition, String reason) {
        check(condition, HttpStatus.FORBIDDEN, reason);
    }

    public static void checkOrForbidden(boolean condition) {
        check(condition, HttpStatus.FORBIDDEN,  "You can't access this data");
    }
   
}
