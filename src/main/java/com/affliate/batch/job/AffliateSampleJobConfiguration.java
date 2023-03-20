package com.affliate.batch.job;

import com.affliate.batch.mapper.AffiliateResultMapper;
import com.affliate.batch.model.AffiliateResultDto;
import com.affliate.batch.step.processor.AffiliteItemProcess;
import com.affliate.batch.step.tasklet.AffiliateUploadFileTasklet;
import com.affliate.batch.type.AffiliateCompanyType;
import com.affliate.batch.type.AffiliateSqlType;
import com.affliate.batch.utility.AffiliateJobListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/***
 * A_COMPANY 제휴사 샘플정산데이터 추출 BatchJob
 */

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AffliateSampleJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final JdbcTemplate jdbcTemplate;
    private final AffiliateUploadFileTasklet affiliateUploadFileTasklet;


    @Bean
    public Job affiliateBatchJob(){
        return jobBuilderFactory.get("affiliateBatchJob")
                .incrementer(new RunIdIncrementer())
                .listener(new AffiliateJobListener())
                .start(makeAffiliateFileStep(null))
                .next(uploadFileToFinalPathStep())
                .build();
    }


    @Bean
    @JobScope
    public Step makeAffiliateFileStep( @Value("#{jobParameters[tempPath]}") String tempPath ){
        log.info(">>>>>>>>>>>>>>> Step started :::: makeAffiliateFileStep ");
        return stepBuilderFactory.get("makeAffiliateFileStep")
                .<AffiliateResultDto, AffiliateResultDto> chunk(100)
                .reader(affiliateJdbcPagingItemReader())
                .processor(affiliateProcessor())
                .writer(fileWriter(tempPath))
                .build();
    }

    @Bean
    public Step uploadFileToFinalPathStep(){

        log.info(">>>>>>>>>>>>>>> Step started :::: uploadFileToFinalPathStep");
        return stepBuilderFactory.get("uploadFileToFinalPathStep")
                .tasklet(affiliateUploadFileTasklet)
                .build();
    }



    @JobScope
    @Bean
    public JdbcPagingItemReader<AffiliateResultDto> affiliateJdbcPagingItemReader()  {

        try {
            log.info(">>>>>>>>>>>>>>> affiliateResultDtoJdbcPagingItemReader started");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("affiliateCode", AffiliateCompanyType.A_COMPANY.getAffiliateCode());

            return new JdbcPagingItemReaderBuilder<AffiliateResultDto>()
                    .name("affiliateResultDtoJdbcPagingItemReader")
                    .pageSize(100)
                    .fetchSize(100)
                    .dataSource(Objects.requireNonNull(jdbcTemplate.getDataSource()))
                    .queryProvider(createAffiliateQuery())
                    .rowMapper(new AffiliateResultMapper())
                    .parameterValues(parameters)
                    .build();

        }catch (Exception e){
            log.info(e.getMessage());
        }

        return null;

    }

    /* ItemProcessor */
    @Bean
    public ItemProcessor<AffiliateResultDto, AffiliateResultDto> affiliateProcessor(){
        return new AffiliteItemProcess();
    }

    /* ItemWriter */
    @StepScope
    public FlatFileItemWriter<AffiliateResultDto> fileWriter( String tempPath ) {
        FlatFileItemWriter<AffiliateResultDto> writer = null;

        try {
            //lineAggregator
            DelimitedLineAggregator<AffiliateResultDto> lineAggreator = new DelimitedLineAggregator<>();
            lineAggreator.setDelimiter(",");
            lineAggreator.setFieldExtractor(affiliateResultDto -> new Object[]{
                    affiliateResultDto.getOrdNo()
                    ,affiliateResultDto.getCustNo()
                    ,affiliateResultDto.getAffiliateCode()
                    ,affiliateResultDto.getAffiliateCustNo()
                    ,affiliateResultDto.getItemTypeCode()
            });

            writer = new FlatFileItemWriterBuilder<AffiliateResultDto>()
                    .name("affiliateFileWriter")
                    .lineAggregator(lineAggreator)
                    .resource(new FileSystemResource(AffiliateCompanyType.A_COMPANY.getTemporaryFilePath(tempPath)))
                    .encoding("UTF-8")
                    .headerCallback(writer1 -> writer1.write("주문번호,고객번호,제휴사코드,제휴고객번호,상품카테고리코드" ))
                    .footerCallback(writer1 -> writer1.write("----------------------\n" ))
                    .build();

            writer.setAppendAllowed(false);
            writer.afterPropertiesSet();


        }catch (Exception e){
            log.debug(">>>>>>>>>> fileWriter() error : " + e.getMessage());
        }
        return writer;
    }

    @Bean
    public PagingQueryProvider createAffiliateQuery() throws Exception {
        log.info(">>>>>> createAffiliateQuery ");

        AffiliateSqlType affiliateSql = AffiliateCompanyType.A_COMPANY.getAffiliateSqlType();
        SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();
        queryProvider.setDataSource(Objects.requireNonNull(jdbcTemplate.getDataSource()));
        queryProvider.setSelectClause(affiliateSql.getSelectClause());
        queryProvider.setFromClause(affiliateSql.getFromClause());
        queryProvider.setWhereClause(affiliateSql.getWhereClause());

        Map<String, Order> sortKey = new HashMap<>();
        sortKey.put("ORD_NO", Order.ASCENDING);

        queryProvider.setSortKeys(sortKey);

        return queryProvider.getObject();

    }
}

