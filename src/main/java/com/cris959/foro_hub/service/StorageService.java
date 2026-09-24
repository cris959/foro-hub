package com.cris959.foro_hub.service;

import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.requests.GetObjectRequest;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.responses.GetObjectResponse;
import com.oracle.bmc.objectstorage.responses.PutObjectResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
public class StorageService {


    private final ObjectStorage objectStorageClient;

    // Estos valores es mejor tenerlos en application.properties
    @Value("${oracle.cloud.bucket.name}")
    private String bucketName;

    @Value("${oracle.cloud.namespace}")
    private String namespace;

    public StorageService(ObjectStorage objectStorageClient) {
        this.objectStorageClient = objectStorageClient;
    }

    public String subirImagen(MultipartFile archivo) {
        try {
            // 1. Generar un nombre unico para que no se pisen archivos
            String nombreUnico = UUID.randomUUID().toString() + "_" + archivo.getOriginalFilename();

            // 2. Construir la solicitud de subida
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucketName(bucketName)
                    .namespaceName(namespace)
                    .objectName(nombreUnico)
                    .contentType(archivo.getContentType())
                    .contentLength(archivo.getSize())
                    .putObjectBody(archivo.getInputStream())
                    .build();

            // 3. Ejecutar la subida a OCI
            PutObjectResponse response = objectStorageClient.putObject(request);

            // 4. Retornar el nombre del objeto (para guardarlo en la DB de Foro Hub)
            return nombreUnico;

        } catch (Exception e) {
            // ESTO ES CLAVE: Imprimi el error en la consola de IntelliJ
            System.err.println("ERROR EN ORACLE CLOUD STORAGE:");
            if (e instanceof com.oracle.bmc.model.BmcException) {
                System.err.println("Status Code: " + ((com.oracle.bmc.model.BmcException)e).getStatusCode());
                System.err.println("Service Code: " + ((com.oracle.bmc.model.BmcException)e).getServiceCode());
            }
            e.printStackTrace();
            throw new RuntimeException("Error de conexión con Oracle Cloud Storage", e);
        }
    }

    public byte[] obtenerImagen(String nombreArchivo) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .namespaceName(namespace)
                    .bucketName(bucketName)
                    .objectName(nombreArchivo)
                    .build();

            GetObjectResponse getObjectResponse = objectStorageClient.getObject(getObjectRequest);

            try (InputStream inputStream = getObjectResponse.getInputStream()) {
                return inputStream.readAllBytes();
            }
        } catch (Exception e) {
            System.err.println("Error al recuperar objeto de OCI: " + e.getMessage());
            throw new RuntimeException("Error al recuperar la imagen de Oracle Cloud: " + e.getMessage());
        }
    }
}