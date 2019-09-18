package com.github.mtesmct.rieau.api.infra.persistence.minio;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Fichier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.FichierId;
import com.github.mtesmct.rieau.api.domain.services.FichierService;
import com.github.mtesmct.rieau.api.domain.services.FichierServiceException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.xmlpull.v1.XmlPullParserException;

import io.minio.MinioClient;
import io.minio.ObjectStat;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidArgumentException;
import io.minio.errors.InvalidBucketNameException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.MinioException;
import io.minio.errors.NoResponseException;
import lombok.extern.slf4j.Slf4j;

@Component
@Profile("staging")
@Slf4j
public class MinioFichierService implements FichierService {

    @Value("${minio.endpoint}")
    private String endpoint;
    @Value("${minio.port}")
    private int port;
    @Value("${minio.access.key}")
    private String accessKey;
    @Value("${minio.secret.key}")
    private String secretKey;
    @Value("${minio.bucket}")
    private String bucket;
    @Value("${minio.buffer}")
    private int buffer;
    private MinioClient minioClient;

    public MinioFichierService() {
        try {
            log.info("Création du client Minio...");
            this.minioClient = new MinioClient(this.endpoint, this.port, this.accessKey, this.secretKey);
            log.info("Client Minio créé.");
            boolean isExist = minioClient.bucketExists(this.bucket);
            if (isExist) {
                log.info("Bucket " + this.bucket + "already exists.");
            } else {
                log.info("Bucket " + this.bucket + "does not exist. Creating...");
                minioClient.makeBucket(this.bucket);
                log.info("Bucket " + this.bucket + " created.");
            }
        } catch (MinioException | InvalidKeyException | NoSuchAlgorithmException | IOException
                | XmlPullParserException e) {
            log.error("Création du client minio impossible", e);
            throw new IllegalArgumentException("Minio client ne peut pas être nul", e);
        }
    }

    @Override
    public Optional<Fichier> findById(FichierId fichierId) throws FichierServiceException {
        Optional<Fichier> fichier = Optional.empty();
        try {
            ObjectStat objectStat = minioClient.statObject(this.bucket, fichierId.toString());
            List<String> headers = objectStat.httpHeaders().get("X-File-Name");
            if (headers.isEmpty())
                throw new FichierServiceException("Impossible de récupérer le nom du fichier depuis Minio");
            String fileName = headers.get(0);
            InputStream stream = minioClient.getObject(this.bucket, fichierId.toString());
            fichier = Optional.ofNullable(new Fichier(fileName, objectStat.contentType(), stream));
            stream.close();
        } catch (InvalidKeyException | InvalidBucketNameException | NoSuchAlgorithmException | InsufficientDataException
                | NoResponseException | ErrorResponseException | InternalException | InvalidArgumentException
                | InvalidResponseException | IOException | XmlPullParserException e) {
            throw new FichierServiceException("Fichier impossible à récupérer depuis l'espace de stockage Minio", e);
        }
        return fichier;

    }

    @Override
    public void save(FichierId fichierId, Fichier fichier) throws FichierServiceException {
        try {
            Map<String, String> headerMap = new HashMap<String, String>();
            headerMap.put("Content-Type", fichier.mimeType());
            headerMap.put("X-File-Name", fichier.nom());
            minioClient.putObject(this.bucket, fichierId.toString(), fichier.content(), Long.valueOf(fichier.content().available()), headerMap,
                    null, fichier.mimeType());
        } catch (InvalidKeyException | InvalidBucketNameException | NoSuchAlgorithmException | InsufficientDataException
                | NoResponseException | ErrorResponseException | InternalException | InvalidArgumentException
                | InvalidResponseException | IOException | XmlPullParserException e) {
            throw new FichierServiceException("Fichier impossible à sauvegarder dans l'espace de stockage Minio", e);
        }
    }

    @Override
    public void delete(FichierId fichierId) throws FichierServiceException {
        try {
            minioClient.removeObject(this.bucket, fichierId.toString());
        } catch (InvalidKeyException | InvalidBucketNameException | NoSuchAlgorithmException | InsufficientDataException
                | NoResponseException | ErrorResponseException | InternalException | InvalidArgumentException
                | InvalidResponseException | IOException | XmlPullParserException e) {
            throw new FichierServiceException("Fichier impossible à supprimer de l'espace de stockage Minio", e);
        }
    }

}