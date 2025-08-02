package org.ssp.notification.config;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Component
@ConfigurationProperties(prefix = "aws")
@Data
public class AwsConfig {

    private String accessKeyId;
    private String secretAccessKey;
    private String region;
    private String sqslUrl;

    @Bean
    SqsAsyncClient sqsAsyncClient()
    {
        return SqsAsyncClient.builder().region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                .build();
    }

    @Bean
    public SqsTemplate sqsTemplate(SqsAsyncClient sqsAsyncClient)
    {
        return SqsTemplate.builder().sqsAsyncClient(sqsAsyncClient).build();
    }
}
