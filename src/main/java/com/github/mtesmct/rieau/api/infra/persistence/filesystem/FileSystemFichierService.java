package com.github.mtesmct.rieau.api.infra.persistence.filesystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Fichier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.FichierId;
import com.github.mtesmct.rieau.api.domain.services.FichierService;
import com.github.mtesmct.rieau.api.domain.services.FichierServiceException;
import com.github.mtesmct.rieau.api.infra.config.AppProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@ConditionalOnExpression("'${minio.enabled},${app.fichiers-dir}'=='false,target/fichiers'")
@Slf4j
public class FileSystemFichierService implements FichierService {

    private final AppProperties properties;
    private final String attributMimeType = "user.mimetype";
    private final String attributName = "user.name";

    @Autowired
    public FileSystemFichierService(AppProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void initStore() throws FichierServiceException {
        log.info("Initialisation du magasin des fichiers joints...");
        try {
            if (Objects.toString(this.properties.getFichiersDir()).equals("null"))
                throw new FichierServiceException(
                        "Le chemin du dossier de stockage des fichiers joints {app.fichiers-dir} ne peut pas être nul.");
            File file = new File(this.properties.getFichiersDir());
            if (!file.exists()) {
                if (!file.mkdir())
                    throw new FichierServiceException("Le dossier de stockage des fichiers joints {"
                            + this.properties.getFichiersDir() + "} n'existe pas et ne peut pas être créé.");
            }
        } catch (SecurityException | NullPointerException e) {
            throw new FichierServiceException(e);
        }
        log.info("Magasin des fichiers joints créé.");
        log.info("Les fichiers joints seront stockés dans le dossier {" + this.properties.getFichiersDir() + "}.");
    }

    @PreDestroy
    public void cleanStore() {
        log.info("Nettoyage du magasin des fichiers joints...");
        try {
            if (Objects.toString(this.properties.getFichiersDir()).equals("null"))
                throw new FichierServiceException(
                        "Le chemin du dossier de stockage des fichiers joints {app.fichiers-dir} ne peut pas être nul.");
            File file = new File(this.properties.getFichiersDir());
            if (file.exists()) {
                if (!file.delete())
                    throw new FichierServiceException("Le dossier de stockage des fichiers joints {"
                            + this.properties.getFichiersDir() + "} ne peut pas être supprimé.");
            }
        } catch (SecurityException | NullPointerException e) {
            throw new FichierServiceException(e);
        }
        log.info("Magasin des fichiers joints supprimé.");
    }

    private UserDefinedFileAttributeView view(FichierId fichierId) throws FichierServiceException {
        UserDefinedFileAttributeView view;
        try {
            Path path = Paths.get(this.properties.getFichiersDir() + "/" + fichierId.toString());
            FileStore store = Files.getFileStore(path);
            if (!store.supportsFileAttributeView("user"))
                throw new FichierServiceException("Le système de fichiers ne supporte pas la vue d'attribut {user}");
            view = Files.getFileAttributeView(path, UserDefinedFileAttributeView.class);
        } catch (IOException e) {
            throw new FichierServiceException(e);
        }
        return view;
    }

    private String read(FichierId fichierId, String attribut) throws FichierServiceException {
        try {
            int size = this.view(fichierId).size(attribut);
            ByteBuffer buf = ByteBuffer.allocateDirect(size);
            this.view(fichierId).read(attribut, buf);
            buf.flip();
            return Charset.defaultCharset().decode(buf).toString();
        } catch (IOException e) {
            throw new FichierServiceException(e);
        }
    }

    @Override
    public Optional<Fichier> findById(FichierId fichierId) throws FichierServiceException {
        Optional<Fichier> fichier = Optional.empty();
        try {
            File file = new File(this.properties.getFichiersDir(), fichierId.toString());
            if (!file.exists())
                return fichier;
            fichier = Optional.ofNullable(new Fichier(fichierId, this.read(fichierId, attributName),
                    this.read(fichierId, attributMimeType), new FileInputStream(file), file.length()));
        } catch (IOException e) {
            throw new FichierServiceException(e);
        }
        return fichier;
    }

    @Override
    public void save(Fichier fichier) throws FichierServiceException {
        try {
            Path file = Files
                    .createFile(Paths.get(this.properties.getFichiersDir() + "/" + fichier.identity().toString()));
            this.view(fichier.identity()).write(attributName, Charset.defaultCharset().encode(fichier.nom()));
            this.view(fichier.identity()).write(attributMimeType, Charset.defaultCharset().encode(fichier.mimeType()));
            FileOutputStream fos = new FileOutputStream(file.toFile());
            int i;
            while ((i = fichier.contenu().read()) != -1) {
                fos.write(i);
            }
            fichier.fermer();
            fos.close();
        } catch (IOException e) {
            throw new FichierServiceException(e);
        }
    }

    @Override
    public void delete(FichierId fichierId) throws FichierServiceException {
        try {
            Path file = Paths.get(this.properties.getFichiersDir() + "/" + fichierId.toString());
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new FichierServiceException(e);
        }
    }

}