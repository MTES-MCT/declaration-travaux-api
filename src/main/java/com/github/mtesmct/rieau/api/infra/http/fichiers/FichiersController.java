package com.github.mtesmct.rieau.api.infra.http.fichiers;

import java.util.Optional;

import com.github.mtesmct.rieau.api.application.auth.AuthRequiredException;
import com.github.mtesmct.rieau.api.application.auth.UserForbiddenException;
import com.github.mtesmct.rieau.api.application.auth.UserInfoServiceException;
import com.github.mtesmct.rieau.api.application.dossiers.FichierNotFoundException;
import com.github.mtesmct.rieau.api.application.dossiers.UserNotOwnerException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Fichier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.FichierId;
import com.github.mtesmct.rieau.api.infra.application.fichiers.TxLireFichierService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(FichiersController.ROOT_URI)
public class FichiersController {

    public static final String ROOT_URI = "/fichiers";
    @Autowired
    private TxLireFichierService lireFichierService;

    @GetMapping("/{id}")
    public ResponseEntity<Resource> lireFichier(@PathVariable String id) throws FichierNotFoundException,
            UserForbiddenException, AuthRequiredException, UserInfoServiceException, UserNotOwnerException {
        Optional<Fichier> fichier = this.lireFichierService.execute(new FichierId(id));
        if (fichier.isEmpty())
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fichier.get().nom())
                .contentType(MediaType.valueOf(fichier.get().mimeType())).contentLength(fichier.get().taille()).body(new InputStreamResource(fichier.get().contenu()));
    }

}