package com.github.mtesmct.rieau.api.infra.http;

import com.github.mtesmct.rieau.api.application.auth.AuthRequiredException;
import com.github.mtesmct.rieau.api.application.auth.UserForbiddenException;
import com.github.mtesmct.rieau.api.application.dossiers.CerfaImportException;
import com.github.mtesmct.rieau.api.application.dossiers.DossierImportException;
import com.github.mtesmct.rieau.api.application.dossiers.DossierNotFoundException;
import com.github.mtesmct.rieau.api.application.dossiers.FichierNotFoundException;
import com.github.mtesmct.rieau.api.application.dossiers.UserNotOwnerException;
import com.github.mtesmct.rieau.api.infra.InfraPackageScan;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice(basePackageClasses = InfraPackageScan.class)
public class ControllersExceptionsAdvice extends ResponseEntityExceptionHandler {

  @ExceptionHandler({ AuthRequiredException.class })
  public ResponseEntity<Object> handleAuthRequiredException(Exception ex, WebRequest request) {
    return new ResponseEntity<Object>(ex.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler({ UserForbiddenException.class })
  public ResponseEntity<Object> handleUserForbiddenException(Exception ex, WebRequest request) {
    return new ResponseEntity<Object>(ex.getMessage(), new HttpHeaders(), HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler({ UserNotOwnerException.class })
  public ResponseEntity<Object> handleUserNotOwnerException(Exception ex, WebRequest request) {
    return new ResponseEntity<Object>(ex.getMessage(), new HttpHeaders(), HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler({ FichierNotFoundException.class })
  public ResponseEntity<Object> handleFichierNotFoundException(Exception ex, WebRequest request) {
    return new ResponseEntity<Object>(ex.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler({ DossierNotFoundException.class })
  public ResponseEntity<Object> handleDossierNotFoundException(Exception ex, WebRequest request) {
    return new ResponseEntity<Object>(ex.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler({ DossierImportException.class })
  public ResponseEntity<Object> handleDossierImportException(Exception ex, WebRequest request) {
    if (ex.getCause() !=null && ex.getCause() instanceof FichierNotFoundException)
      return new ResponseEntity<Object>(ex.getCause().getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND);
    return new ResponseEntity<Object>(ex.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler({ CerfaImportException.class })
  public ResponseEntity<Object> handleCerfaImportException(Exception ex, WebRequest request) {
    return new ResponseEntity<Object>(ex.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler({ Exception.class })
  public ResponseEntity<Object> handleAll(Exception ex, WebRequest request) {
    return new ResponseEntity<Object>(ex.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

}