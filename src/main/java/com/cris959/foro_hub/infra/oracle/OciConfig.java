package com.cris959.foro_hub.infra.oracle;

import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider;
import com.oracle.bmc.auth.SimplePrivateKeySupplier;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class OciConfig {

    @Value("${oracle.cloud.user-id}")
    private String userId;

    @Value("${oracle.cloud.tenancy-id}")
    private String tenancyId;

    @Value("${oracle.cloud.fingerprint}")
    private String fingerprint;

    @Value("${oracle.cloud.private-key-path}")
    private String privateKeyPath;

    @Value("${oracle.cloud.region}")
    private String region;

    @Bean
    public ObjectStorage objectStorageClient() throws IOException {
        // Aqui es donde el SDK construye el cliente usando esos strings
        AuthenticationDetailsProvider provider = SimpleAuthenticationDetailsProvider.builder()
                .userId(userId)
                .tenantId(tenancyId)
                .fingerprint(fingerprint) // Si esto llega nulo o vacio, explota
                .privateKeySupplier(new SimplePrivateKeySupplier(privateKeyPath))
                .region(Region.fromRegionId(region))
                .build();

        return ObjectStorageClient.builder().build(provider);
    }
}