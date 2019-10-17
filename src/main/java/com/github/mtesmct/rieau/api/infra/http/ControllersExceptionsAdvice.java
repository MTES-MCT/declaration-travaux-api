package com.github.mtesmct.rieau.api.infra.http;

import com.github.mtesmct.rieau.api.application.auth.AuthRequiredException;
import com.github.mtesmct.rieau.api.application.auth.UserForbiddenException;
import com.github.mtesmct.rieau.api.application.auth.UserInfoServiceException;
import com.github.mtesmct.rieau.api.application.dossiers.CerfaImportException;
import com.github.mtesmct.rieau.api.application.dossiers.DossierImportException;
import com.github.mtesmct.rieau.api.application.dossiers.DossierNotFoundException;
import com.github.mtesmct.rieau.api.application.dossiers.FichierNotFoundException;
import com.github.mtesmct.rieau.api.application.dossiers.UserNotOwnerException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.DeposantForbiddenException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.MairieForbiddenException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.StatutForbiddenException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeStatutNotFoundException;
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
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public UserForbiddenException handleUserForbiddenException(UserForbiddenException e) {
    return e;
  }

  @ExceptionHandler({ UserNotOwnerException.class })
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public UserNotOwnerException handleUserNotOwnerException(UserNotOwnerException e) {
    return e;
  }

  @ExceptionHandler({ UserInfoServiceException.class })
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public UserInfoServiceException handleUserInfoServiceException(UserInfoServiceException e) {
    return e;
  }

  @ExceptionHandler({ DeposantForbiddenException.class })
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public DeposantForbiddenException handleDeposantForbiddenException(DeposantForbiddenException e) {
    return e;
  }

  @ExceptionHandler({ MairieForbiddenException.class })
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public MairieForbiddenException handleMairieForbiddenException(MairieForbiddenException e) {
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

  @ExceptionHandler({ TypeStatutNotFoundException.class })
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public TypeStatutNotFoundException handleStatutNotFoundException(TypeStatutNotFoundException e) {
    return e;
  }

  @ExceptionHandler({ StatutForbiddenException.class })
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public StatutForbiddenException handleStatutForbiddenException(StatutForbiddenException e) {
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