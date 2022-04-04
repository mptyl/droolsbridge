package it.ctinnovation.droolsbridge.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.STSAssumeRoleSessionCredentialsProvider;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import it.ctinnovation.droolsbridge.props.DroolsConfigProps;
import org.kie.api.KieServices;
import org.kie.api.builder.*;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.io.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.SpringHandlerInstantiator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.Executor;

@Configuration
@ComponentScan("it.ctinnovation.droolsbridge.service")
@ConfigurationProperties(prefix = "application")
@EnableConfigurationProperties({DroolsConfigProps.class})
@EnableWebMvc
public class DroolsConfig {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(DroolsConfig.class);
    // Region
    @Value("${cloud.aws.region.static}")
    private String region;

    // Session
    @Value("${sqs.session}")
    private String session;

    // In
    @Value("${cloud.aws.roles.in.producer.key}")
    private String inProducerKey;

    @Value("${cloud.aws.roles.in.producer.role}")
    private String inProducerRole;

    @Value("${cloud.aws.roles.in.consumer.key}")
    private String inConsumerKey;

    @Value("${cloud.aws.roles.in.consumer.role}")
    private String inConsumerRole;

    // Out
    @Value("${cloud.aws.roles.out.producer.key}")
    private String outProducerKey;

    @Value("${cloud.aws.roles.out.producer.role}")
    private String outProducerRole;

    @Value("${cloud.aws.roles.out.consumer.key}")
    private String outConsumerKey;

    @Value("${cloud.aws.roles.out.consumer.role}")
    private String outConsumerRole;

    // Access e secret key
    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;


    public static final String drlFile = "TAXI_FARE_RULE.drl";

    @Bean
    public KieContainer kieContainer() {
        KieServices kieServices = KieServices.Factory.get();

        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        kieFileSystem.write(ResourceFactory.newClassPathResource(drlFile));
        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();
        KieModule kieModule = kieBuilder.getKieModule();

        return kieServices.newKieContainer(kieModule.getReleaseId());

    }

    @Bean
    public AWSSecurityTokenService stsClient() {
        return AWSSecurityTokenServiceClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .build();
    }

    @Bean("inConsumer")
    @Autowired
    public AWSCredentialsProvider awsSnsInConsumerCredential(AWSSecurityTokenService stsClient){
        return new STSAssumeRoleSessionCredentialsProvider
                .Builder(inConsumerRole, session)
                .withStsClient(stsClient)
                .build();
    }

    @Bean("inProducer")
    @Autowired
    public AWSCredentialsProvider awsSnsInProducerCredential(AWSSecurityTokenService stsClient){
        return new STSAssumeRoleSessionCredentialsProvider
                .Builder(inProducerRole, session)
                .withStsClient(stsClient)
                .build();
    }

    @Bean("outProducer")
    @Autowired
    public AWSCredentialsProvider awsSnsOutProducerCredential(AWSSecurityTokenService stsClient){
        return new STSAssumeRoleSessionCredentialsProvider
                .Builder(outProducerRole, session)
                .withStsClient(stsClient)
                .build();
    }

    @Bean
    @Autowired
    AmazonSQSClient inConsumerClient(AWSCredentialsProvider inConsumer){
        return (AmazonSQSClient) AmazonSQSClientBuilder.standard()
                .withCredentials(inConsumer)
                .withRegion(region)
                .build();
    }

    @Bean
    @Autowired
    AmazonSQSClient inProducerClient(AWSCredentialsProvider inProducer){
        return (AmazonSQSClient) AmazonSQSClientBuilder.standard()
                .withCredentials(inProducer)
                .withRegion(region)
                .build();
    }

    @Bean
    @Autowired
    AmazonSQSClient outProducerClient(AWSCredentialsProvider outProducer){
        return (AmazonSQSClient) AmazonSQSClientBuilder.standard()
                .withCredentials(outProducer)
                .withRegion(region)
                .build();
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String timeZoneCode;
    private ZoneId zoneId;
    private Instant testCurrDate;

    @PostConstruct
    public void init() {
        try {
            zoneId = ZoneId.of(timeZoneCode);
            logger.info("The active Time Zone id is " + getZoneId().toString());
        } catch (Exception e) {
            logger.warn("Configuration error: unknown Time Zone Id : '" + timeZoneCode + "' please config a code from IANA Time Zone Database. - Using UTC from now on.");
            zoneId = ZoneId.of("UTC");
        }
        if (testCurrDate != null) {
            logger.info("Test mode active: the Real Time Clock is stopped. The current Tile is frozen on " + testCurrDate);
        }
    }

    /**
     * Allows for creating Jackson (JsonSerializer, JsonDeserializer, KeyDeserializer, TypeResolverBuilder, TypeIdResolver) beans with
     * autowiring against a Spring ApplicationContext.
     *
     * @param context
     * @return
     */
    @Bean
    public HandlerInstantiator handlerInstantiator(ApplicationContext context) {
        return new SpringHandlerInstantiator(context.getAutowireCapableBeanFactory());
    }

    @Bean
    public ObjectMapper objectMapper(HandlerInstantiator handlerInstantiator) {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.handlerInstantiator(handlerInstantiator);
        return builder.build();
    }

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setThreadNamePrefix("Async-");
        threadPoolTaskExecutor.setCorePoolSize(3);
        threadPoolTaskExecutor.setMaxPoolSize(3);
        threadPoolTaskExecutor.setQueueCapacity(600);
        threadPoolTaskExecutor.afterPropertiesSet();
        logger.info("ThreadPoolTaskExecutor set");
        return threadPoolTaskExecutor;
    }

    @Bean
    public KieContainer kieContainer(DroolsConfigProps conf) {
        ReleaseId releaseId = KieServices.get().newReleaseId(conf.getRuleGroupID(), conf.getRuleArtifactId(), conf.getRuleVersion());
        return KieServices.get().newKieContainer(releaseId);
    }

    @Bean
    public KieScanner kieScanner(KieContainer kContainer) {
        return KieServices.get().newKieScanner(kContainer);
    }

    public void setTestCurrDate(String strTestCurrDate) {
        this.setTestCurrDate(Instant.parse(strTestCurrDate));
    }

    public Instant getTestCurrDate() {
        return testCurrDate;
    }

    public void setTimeZoneCode(String timeZoneCode) {
        this.timeZoneCode = timeZoneCode;
    }

    public ZoneId getZoneId() {
        return zoneId;
    }

    public void setTestCurrDate(Instant testCurrDate) {
        this.testCurrDate = testCurrDate;
    }

}
