package br.com.saduarte.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

    public BadRequestException() {
        super("arquivo não suportado");
    }

    public BadRequestException(String message) {
        super(message);
    }
}
