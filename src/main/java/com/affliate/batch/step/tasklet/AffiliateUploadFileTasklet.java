package com.affliate.batch.step.tasklet;

import com.affliate.batch.type.AffiliateCompanyType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Slf4j
@AllArgsConstructor
@Component
public class AffiliateUploadFileTasklet implements Tasklet {

    /**
     * 제휴사 제외조건들 세팅
     * */

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        log.info(">>>>>>>>>>>> tasklet start :: AffiliateUploadFileTasklet ");

        String tempPath = (String) stepContribution.getStepExecution().getJobExecution().getJobParameters().getString("tempPath");
        String finalPath = (String) stepContribution.getStepExecution().getJobExecution().getJobParameters().getString("finalPath");
        String affiliateName = (String) stepContribution.getStepExecution().getJobExecution().getJobParameters().getString("affiliateName");

        String tempFilePath = Objects.requireNonNull(AffiliateCompanyType.findTypeByAffiliateName(affiliateName)).getTemporaryFilePath(tempPath);
        String finalFilePath = Objects.requireNonNull(AffiliateCompanyType.findTypeByAffiliateName(affiliateName)).getFullFilePathWithDate(finalPath);

        File tempFile = new File(tempFilePath);

        if(tempFile.exists() && tempFile.isFile()){
            Files.createDirectories(Path.of(finalFilePath).getParent());
            Files.copy(Path.of(tempFilePath), Path.of(finalFilePath), StandardCopyOption.REPLACE_EXISTING);

            FileSystemUtils.deleteRecursively(Path.of(tempFilePath));


        }
        log.info(">>>>>>>>>>>> END >>>>>>>>>>>> ");

        return RepeatStatus.FINISHED;
    }

}
