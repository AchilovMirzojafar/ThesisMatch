package org.hhu.thesismatch.web;

import java.io.IOException;

import org.hhu.thesismatch.application.applicationservice.BetreuerNichtGefundenException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Übersetzt fachliche Ausnahmen in passende HTTP-Statuscodes.
 */
@ControllerAdvice
public class WebExceptionHandler {

    @ExceptionHandler(BetreuerNichtGefundenException.class)
    public void handleNotFound(BetreuerNichtGefundenException e, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value(),
                "Profil ist als Betreuer nicht gefunden. Bitte wenden Sie sich an den Administrator.");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public void handleBadRequest(IllegalArgumentException e, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }
}
