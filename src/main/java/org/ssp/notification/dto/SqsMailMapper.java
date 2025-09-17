package org.ssp.notification.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
/*@JsonPropertyOrder({
        "timestamp",
        "source",
        "messageId"
})*/
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SqsMailMapper {

    @JsonProperty("timestamp")
    private String timestamp;

    @JsonProperty("source")
    private String source;

    @JsonProperty("messageId")
    private String messageId;

    @JsonProperty("destination")
    private List<String> destination;

    @JsonProperty("tags")
    private SqsTags tags;

}