package br.com.saduarte.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnsupportesMathOperationException extends RuntimeException {

    public UnsupportesMathOperationException(String message) {
        super(message);
    }
}
