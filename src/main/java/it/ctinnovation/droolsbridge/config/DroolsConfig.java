package it.ctinnovation.droolsbridge.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.STSAssumeRoleSessionCredentialsProvider;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.sns.AmazonSNS;
import org.apache.commons.lang3.StringUtils;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.io.ResourceFactory;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@ComponentScan("it.ctinnovation.droolsbridge.service")
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

    @Bean("inConsumer")
    public AWSCredentialsProvider awsSnsInConsumerCredential(){
        AWSSecurityTokenService stsClient = AWSSecurityTokenServiceClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .build();
        return new STSAssumeRoleSessionCredentialsProvider
                .Builder(inConsumerRole, session)
                .withStsClient(stsClient)
                .build();
    }

    @Bean("inProducer")
    public AWSCredentialsProvider awsSnsInProducerCredential(){
        AWSSecurityTokenService stsClient = AWSSecurityTokenServiceClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .build();
        return new STSAssumeRoleSessionCredentialsProvider
                .Builder(inProducerRole, session)
                .withStsClient(stsClient)
                .build();
    }


//    @Bean
//    @Primary
//    public AWSCredentialsProvider awsCredentialsProvider() {
//        log.info("Assuming role {}",assumeRoleARN);
//        if (StringUtils.isNotEmpty(assumeRoleARN)) {
//            AWSSecurityTokenService stsClient = AWSSecurityTokenServiceClientBuilder.standard()
//                    .withClientConfiguration(clientConfiguration())
//                    .withCredentials(awsCredentialsProvider)
//                    .build();
//
//            return new STSAssumeRoleSessionCredentialsProvider
//                    .Builder(assumeRoleARN, "test")
//                    .withStsClient(stsClient)
//                    .build();
//        }
//        return awsCredentialsProvider;
//    }
}
