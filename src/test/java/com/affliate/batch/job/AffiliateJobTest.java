package com.affliate.batch.job;

import com.affliate.batch.config.BatchTestConfig;
import com.affliate.batch.step.processor.AffiliteItemProcess;
import com.affliate.batch.step.tasklet.AffiliateUploadFileTasklet;
import com.affliate.batch.untility.TestJobUtility;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBatchTest
@SpringBootTest(classes = {
        ACompanyAffiliateJobConfig.class,
        BatchTestConfig.class,
        AffiliateUploadFileTasklet.class,
        AffiliteItemProcess.class})
public class AffiliateJobTest extends TestJobUtility {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Before
    public void beforeJob(){
    }

    @Test
    public void success_case() throws Exception {
        // given
        JobParameters jobParameters = getUniqueJobParametersBuilder()
                .addString("affiliateName", "A_COMPANY")
                .addString("tempPath", "out/test/temp")
                .addString("finalPath", "out/test/final")
                .toJobParameters();

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        // then
        assert(BatchStatus.COMPLETED.equals(jobExecution.getStatus()));
        assert(ExitStatus.COMPLETED.equals(jobExecution.getExitStatus()));

    }


}
