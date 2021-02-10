package example.ljava;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

@SpringBootApplication
@EnableJpaRepositories("example.ljava.repository")
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQLDialect");
        vendorAdapter.setGenerateDdl(false);
        vendorAdapter.setShowSql(true);

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(dataSource);
        factory.setJpaVendorAdapter(vendorAdapter);
        return factory;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory);
        return txManager;
    }

    @Bean("validatorFactoryBean")
    public LocalValidatorFactoryBean localValidatorFactoryBean() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        // validator.setProviderClass(HibernateValidator.class);
        try {
            validator.setProviderClass(Class.forName("org.hibernate.validator.HibernateValidator"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return validator;
    }

    @Bean
    @DependsOn({ "validatorFactoryBean" })
    public MethodValidationPostProcessor methodValidationPostProcessor(LocalValidatorFactoryBean validatorFactory) {
        MethodValidationPostProcessor validation = new MethodValidationPostProcessor();
        validation.setValidator(validatorFactory);
        return validation;
    }

    @Bean
    public ErrorPageRegistrar messagePageCustomizer() {
        return registry -> registry.addErrorPages(new ErrorPage("/message"));
    }

}