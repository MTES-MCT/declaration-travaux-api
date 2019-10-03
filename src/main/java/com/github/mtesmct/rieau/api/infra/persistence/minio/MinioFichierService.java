package com.github.mtesmct.rieau.api.infra.persistence.minio;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.PostConstruct;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Fichier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.FichierId;
import com.github.mtesmct.rieau.api.domain.services.FichierService;
import com.github.mtesmct.rieau.api.domain.services.FichierServiceException;
import com.github.mtesmct.rieau.api.infra.config.MinioProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.xmlpull.v1.XmlPullParserException;

import io.minio.MinioClient;
import io.minio.ObjectStat;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidArgumentException;
import io.minio.errors.InvalidBucketNameException;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.NoResponseException;
import io.minio.errors.RegionConflictException;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@ConditionalOnProperty(prefix = "minio", name = "enabled", havingValue = "true")
public class MinioFichierService implements FichierService {

    private final MinioProperties properties;
    private MinioClient minioClient;
    private String fileNameHeader = "X-File-Name";
    private String fileNameHttpHeader = "x-amz-meta-x-file-name";
    private String fileSizeHttpHeader = "content-length";

    @Autowired
    public MinioFichierService(MinioProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void initClient() {
        try {
            log.info("Création du client Minio...");
            this.minioClient = new MinioClient(this.properties.getEndpoint(), this.properties.getAccessKey(),
                    this.properties.getSecretKey());
            log.info("Client Minio créé.");
            boolean isExist = minioClient.bucketExists(this.properties.getBucket());
            if (isExist) {
                log.info("Bucket {" + this.properties.getBucket() + "} already exists.");
            } else {
                log.info("Bucket {" + this.properties.getBucket() + "} does not exist. Creating...");
                minioClient.makeBucket(this.properties.getBucket());
                log.info("Bucket {" + this.properties.getBucket() + "} created.");
            }
        } catch (InvalidBucketNameException | RegionConflictException | NoSuchAlgorithmException | IOException
                | InvalidKeyException | NoResponseException | XmlPullParserException | ErrorResponseException
                | InternalException | InsufficientDataException | InvalidResponseException | InvalidPortException
                | InvalidEndpointException e) {
            log.error("Création du client minio impossible", e);
            throw new IllegalArgumentException("Minio client ne peut pas être nul", e);
        }
    }

    @Override
    public Optional<Fichier> findById(FichierId fichierId) throws FichierServiceException {
        Optional<Fichier> fichier = Optional.empty();
        try {
            ObjectStat objectStat = minioClient.statObject(this.properties.getBucket(), fichierId.toString());
            Map<String, List<String>> headers = objectStat.httpHeaders();
            if (headers == null || headers.isEmpty())
                throw new FichierServiceException(
                        "Impossible de récupérer le nom du fichier depuis Minio. Pas de headers associées à l'objet Minio");
            String fileName = this.getHeader(headers, fileNameHttpHeader);
            String fileSize = this.getHeader(headers, fileSizeHttpHeader);
            log.debug("fileName={}", fileName);
            log.debug("fileSize={}", fileSize);
            InputStream stream = minioClient.getObject(this.properties.getBucket(), fichierId.toString());
            fichier = Optional.ofNullable(
                    new Fichier(fichierId, fileName, objectStat.contentType(), stream, Long.valueOf(fileSize)));
        } catch (InvalidKeyException | InvalidBucketNameException | NoSuchAlgorithmException | InsufficientDataException
                | NoResponseException | ErrorResponseException | InternalException | InvalidArgumentException
                | InvalidResponseException | IOException | XmlPullParserException e) {
            throw new FichierServiceException("Fichier impossible à récupérer depuis l'espace de stockage Minio", e);
        }
        return fichier;

    }

    private String getHeader(Map<String, List<String>> headers, String headerName) {
        List<String> fileNameHeaders = headers.get(headerName);
        if (fileNameHeaders == null || fileNameHeaders.isEmpty())
            throw new FichierServiceException("Impossible de récupérer le nom du fichier depuis Minio. Pas de header {"
                    + headerName + "} associée à l'objet Minio");
        String fileName = fileNameHeaders.get(0);
        return fileName;
    }

    @Override
    public void save(Fichier fichier) throws FichierServiceException {
        try {
            Map<String, String> headerMap = new HashMap<String, String>();
            headerMap.put("Content-Type", fichier.mimeType());
            headerMap.put(fileNameHeader, fichier.nom());
            FichierId fichierId = fichier.identity();
            minioClient.putObject(this.properties.getBucket(), fichierId.toString(), fichier.contenu(),
                    fichier.taille(), headerMap, null, fichier.mimeType());
            log.debug("fichierId={}", Objects.toString(fichierId));
            log.debug("fichier={}", Objects.toString(fichier));
            log.debug("fileNameHeader={}", Objects.toString(headerMap.get(fileNameHeader)));
            fichier.fermer();
        } catch (InvalidKeyException | InvalidBucketNameException | NoSuchAlgorithmException | InsufficientDataException
                | NoResponseException | ErrorResponseException | InternalException | InvalidArgumentException
                | InvalidResponseException | IOException | XmlPullParserException e) {
            throw new FichierServiceException("Fichier impossible à sauvegarder dans l'espace de stockage Minio", e);
        }
    }

    @Override
    public void delete(FichierId fichierId) throws FichierServiceException {
        try {
            minioClient.removeObject(this.properties.getBucket(), fichierId.toString());
        } catch (InvalidKeyException | InvalidBucketNameException | NoSuchAlgorithmException | InsufficientDataException
                | NoResponseException | ErrorResponseException | InternalException | InvalidArgumentException
                | InvalidResponseException | IOException | XmlPullParserException e) {
            throw new FichierServiceException("Fichier impossible à supprimer de l'espace de stockage Minio", e);
        }
    }

}