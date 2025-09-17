package org.ssp.notification.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SqsTags {
    @JsonProperty("ses:source-tls-version")
    private List<String> sesSourceTlsVersion = new ArrayList<>();

    @JsonProperty("ses:operation")
    private List<String> sesOperation = new ArrayList<>();

    @JsonProperty("ses:configuration-set")
    private List<String> sesConfigurationSet = new ArrayList<>();

    @JsonProperty("ses:outgoing-tls-version")
    private List<String> sesOutgoingTlsVersion = new ArrayList<>();

    @JsonProperty("ses:source-ip")
    private List<String> sesSourceIp = new ArrayList<>();

    @JsonProperty("ses:from-domain")
    private List<String> sesFromDomain = new ArrayList<>();

    @JsonProperty("ses:caller-identity")
    private List<String> sesCallerIdentity = new ArrayList<>();

    @JsonProperty("campaignBatchId")
    private List<String> campaignBatchId = new ArrayList<>();

    @JsonProperty("ses:outgoing-ip")
    private List<String> sesOutgoingIp = new ArrayList<>();
}
