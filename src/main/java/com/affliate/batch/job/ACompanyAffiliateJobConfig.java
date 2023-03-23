package com.affliate.batch.job;

import com.affliate.batch.mapper.AffiliateResultMapper;
import com.affliate.batch.model.AffiliateResultDto;
import com.affliate.batch.step.processor.AffiliteItemProcess;
import com.affliate.batch.step.tasklet.AffiliateUploadFileTasklet;
import com.affliate.batch.type.AffiliateCompanyType;
import com.affliate.batch.type.AffiliateSqlType;
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
public class ACompanyAffiliateJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final JdbcTemplate jdbcTemplate;


    @Bean
    public Job affiliateBatchJob(){
        return jobBuilderFactory.get("affiliateBatchJob")
                .incrementer(new RunIdIncrementer())
                .start(makeAffiliateFileStep())
                .next(uploadFileToFinalPathStep())
                .build();
    }


    @Bean
    @JobScope
    public Step makeAffiliateFileStep() {
        log.info(">>>>>>>>>>>>>>> Step started :::: makeAffiliateFileStep ");
        return stepBuilderFactory.get("makeAffiliateFileStep")
                .<AffiliateResultDto, AffiliateResultDto> chunk(100)
                .reader(affiliateJdbcPagingItemReader())
                .processor(affiliateProcessor())
                .writer(fileWriter(null))
                .build();
    }

    @Bean
    @JobScope
    public Step uploadFileToFinalPathStep(){

        log.info(">>>>>>>>>>>>>>> Step started :::: uploadFileToFinalPathStep");
        return stepBuilderFactory.get("uploadFileToFinalPathStep")
                .tasklet(new AffiliateUploadFileTasklet())
                .build();
    }

    @Bean
    @StepScope
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
            log.error(e.getMessage());
        }

        return null;

    }

    /* ItemProcessor */
    @Bean
    @StepScope
    public ItemProcessor<AffiliateResultDto, AffiliateResultDto> affiliateProcessor(){
        return new AffiliteItemProcess();
    }

    /* ItemWriter */
    @Bean
    @StepScope
    public FlatFileItemWriter<AffiliateResultDto> fileWriter( @Value("#{jobParameters[tempPath]}") String tempPath ) {
        log.info(">>>>>>>>>>>>>>>>>> started :::: fileWriter");
        FlatFileItemWriter<AffiliateResultDto> writer = null;

        try {
            //lineAggregator : 개체를 나타내는 문자열을 만드는 데 사용되는 인터페이스
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
                    .headerCallback(writer1 -> writer1.write("주문번호,고객번호,제휴사코드,제휴고객번호,상품카테고리코드" )) // header설정
                    .footerCallback(writer1 -> writer1.write("----------------------\n" )) // footer 설정
                    .build();

            writer.setAppendAllowed(false); // 이미 존재하는 경우 대상 파일을 추가해야 함을 나타내는 플래그
            writer.afterPropertiesSet();


        }catch (Exception e){
            log.error(">>>>>>>>>> fileWriter() error : " + e.getMessage());
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
        queryProvider.setSortKeys(Map.of("ORD_NO", Order.ASCENDING));

        return queryProvider.getObject();

    }
}

