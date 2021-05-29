package com.stock.master.exception;

import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;

public class FieldInputException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final List<FieldError> fieldErrors = new ArrayList<>();

    public void addError(String key, String value) {
        FieldError e = new FieldError("", key, value);
        fieldErrors.add(e);
    }

    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }

    public boolean hasErrors() {
        return !fieldErrors.isEmpty();
    }

}
