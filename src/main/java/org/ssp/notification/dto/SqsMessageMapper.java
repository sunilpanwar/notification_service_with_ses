package org.ssp.notification.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
/*
@JsonPropertyOrder({
        "eventType",
        "mail"
})
*/
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SqsMessageMapper {

    @JsonProperty("eventType")
    private String eventType;

    @JsonProperty("mail")
    private SqsMailMapper mail;

}