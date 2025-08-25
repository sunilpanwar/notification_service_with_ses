package org.ssp.notification.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@EnableScheduling
public class InitialMessageAggregatorJob {

    private static final Logger log = LoggerFactory.getLogger(InitialMessageAggregatorJob.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${maintenance-job.queries.aggregate-initial-messages}")
    private String aggregateMessageIdQuery;

    @Value("${maintenance-job.queries.archive-notifications}")
    private String insertToHistoryQuery;

    @Value("${maintenance-job.queries.delete-archived-notifications}")
    private String deleteFromNotificationQuery;

    /**
     * Runs a daily maintenance job that performs two main tasks:
     * 1. Aggregates initial message IDs for email threading into a permanent lookup table.
     * 2. Archives and purges old, processed notifications to keep the main table lean.
     */
    @Scheduled(cron = "${maintenance-job.cron}")
    @Transactional
    public void performDailyMaintenance() {
        log.info("Starting daily maintenance job: Aggregating message IDs and archiving old notifications.");

        try {
            // --- Step 1: Aggregate initial message IDs ---
            log.info("Aggregating initial message IDs for threading lookup...");
            int rowsInserted = jdbcTemplate.update(aggregateMessageIdQuery);
            log.info("Aggregation complete. Inserted {} new initial message lookups.", rowsInserted);

            // --- Step 2: Archive old notifications ---
            log.info("Archiving old notifications...");
            int rowsMoved = jdbcTemplate.update(insertToHistoryQuery);
            log.info("Moved {} rows to notification_history table.", rowsMoved);

            // --- Step 3: Delete archived notifications ---
            // This step only runs if the archival (INSERT) was successful, thanks to @Transactional.
            log.info("Deleting archived notifications from the main table...");
            int rowsDeleted = jdbcTemplate.update(deleteFromNotificationQuery);
            log.info("Deleted {} rows from notification table.", rowsDeleted);

            log.info("Daily maintenance job finished successfully.");
        } catch (Exception e) {
            log.error("Daily maintenance job failed. Transaction will be rolled back.", e);
            // By re-throwing the exception, we ensure the @Transactional manager
            // correctly handles the rollback.
            throw e;
        }
    }
}
