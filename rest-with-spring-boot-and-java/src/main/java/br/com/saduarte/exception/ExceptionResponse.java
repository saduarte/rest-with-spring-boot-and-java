package br.com.saduarte.exception;

import java.util.Date;

public record ExceptionResponse(Date timesTamp, String message, String details) {}
