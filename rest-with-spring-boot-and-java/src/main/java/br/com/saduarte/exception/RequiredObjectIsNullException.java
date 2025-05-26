package br.com.saduarte.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RequiredObjectIsNullException extends RuntimeException {

    public RequiredObjectIsNullException() {
        super("Não é permitido persistir uma entidade com valor nulo");
    }

    public RequiredObjectIsNullException(String message) {
        super(message);
    }
}
