package com.github.mtesmct.rieau.api.infra.persistence.minio;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Fichier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.FichierId;
import com.github.mtesmct.rieau.api.domain.services.FichierService;
import com.github.mtesmct.rieau.api.domain.services.FichierServiceException;
import com.github.mtesmct.rieau.api.infra.config.MinioProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
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
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.NoResponseException;
import io.minio.errors.RegionConflictException;
import lombok.extern.slf4j.Slf4j;

@Component
@Profile("staging")
@Slf4j
@Primary
public class MinioFichierService implements FichierService {

    private final MinioProperties properties;
    private MinioClient minioClient;

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
                | InternalException | InsufficientDataException | InvalidResponseException | InvalidPortException | InvalidEndpointException e) {
            log.error("Création du client minio impossible", e);
            throw new IllegalArgumentException("Minio client ne peut pas être nul", e);
        }
    }

    @Override
    public Optional<Fichier> findById(FichierId fichierId) throws FichierServiceException {
        Optional<Fichier> fichier = Optional.empty();
        try {
            ObjectStat objectStat = minioClient.statObject(this.properties.getBucket(), fichierId.toString());
            List<String> headers = objectStat.httpHeaders().get("X-File-Name");
            if (headers.isEmpty())
                throw new FichierServiceException("Impossible de récupérer le nom du fichier depuis Minio");
            String fileName = headers.get(0);
            InputStream stream = minioClient.getObject(this.properties.getBucket(), fichierId.toString());
            fichier = Optional.ofNullable(new Fichier(fileName, objectStat.contentType(), stream, stream.available()));
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
            minioClient.putObject(this.properties.getBucket(), fichierId.toString(), fichier.content(),
            fichier.size(), headerMap, null, fichier.mimeType());
            log.debug("fichierId={}", fichierId.toString());
            log.debug("fichier.size={}", fichier.size());
            log.debug("fichier.nom={}", fichier.nom());
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