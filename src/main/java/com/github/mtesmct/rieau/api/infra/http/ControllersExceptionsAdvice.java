package com.github.mtesmct.rieau.api.infra.http;

import com.github.mtesmct.rieau.api.application.auth.AuthRequiredException;
import com.github.mtesmct.rieau.api.application.auth.UserForbiddenException;
import com.github.mtesmct.rieau.api.application.dossiers.CerfaImportException;
import com.github.mtesmct.rieau.api.application.dossiers.DossierImportException;
import com.github.mtesmct.rieau.api.application.dossiers.DossierNotFoundException;
import com.github.mtesmct.rieau.api.application.dossiers.FichierNotFoundException;
import com.github.mtesmct.rieau.api.application.dossiers.UserNotOwnerException;
import com.github.mtesmct.rieau.api.infra.InfraPackageScan;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = InfraPackageScan.class)
public class ControllersExceptionsAdvice {

  @ExceptionHandler({ AuthRequiredException.class })
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public AuthRequiredException handleAuthRequiredException(AuthRequiredException e) {
    return e;
  }

  @ExceptionHandler({ UserForbiddenException.class })
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public UserForbiddenException handleUserForbiddenException(UserForbiddenException e) {
    return e;
  }

  @ExceptionHandler({ UserNotOwnerException.class })
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public UserNotOwnerException handleUserNotOwnerException(UserNotOwnerException e) {
    return e;
  }

  @ExceptionHandler({ FichierNotFoundException.class })
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public FichierNotFoundException handleFichierNotFoundException(FichierNotFoundException e) {
    return e;
  }

  @ExceptionHandler({ DossierNotFoundException.class })
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public DossierNotFoundException handleDossierNotFoundException(DossierNotFoundException e) {
    return e;
  }

  @ExceptionHandler({ DossierImportException.class })
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public DossierImportException handleDossierImportException(DossierImportException e) {
    return e;
  }

  @ExceptionHandler({ CerfaImportException.class })
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public CerfaImportException handleCerfaImportException(CerfaImportException e) {
    return e;
  }

}