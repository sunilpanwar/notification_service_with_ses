package org.ssp.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationIdDto {
    private Long id;
    private String messageId;
    private String status;
    private String status_details;

}
