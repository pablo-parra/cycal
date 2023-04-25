package com.tastik.cycal.task;

import com.tastik.cycal.core.domain.report.Report;
import com.tastik.cycal.core.interactors.UseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Profile("remote")
public class CycalScheduler {

    private static final Logger LOG = LoggerFactory.getLogger(CycalScheduler.class);
    private final UseCase<Report> sendReport;

    public CycalScheduler(@Qualifier("SendReport")UseCase<Report> sendReport) {
        LOG.info("SCHEDULED TASK: sendReport");
        this.sendReport = sendReport;
    }

    @Scheduled(cron = "${cron.expression}")
    public void report() {
        LOG.info("Starting report...");
        this.sendReport.execute();
        LOG.info("Report job DONE!");
    }
}
