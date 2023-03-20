package com.affliate.batch.utility;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AffiliateJobListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info(">>>>>>>>>> before job");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info(">>>>>>>>>> after job");
    }
}
