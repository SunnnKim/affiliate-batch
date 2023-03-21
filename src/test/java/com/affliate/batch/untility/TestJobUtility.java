package com.affliate.batch.untility;

import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;

public abstract class TestJobUtility {

    /***
     * uniqueJobParameters 포함되어 있는 JobParametersBuilder 반환
     * @return JobParametersBuilder
     */
    protected JobParametersBuilder getUniqueJobParametersBuilder(){
        return new JobParametersBuilder(new JobLauncherTestUtils().getUniqueJobParameters());
    }
}
