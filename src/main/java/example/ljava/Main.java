package example.ljava;

import java.lang.reflect.InvocationTargetException;

import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.validation.ConstraintViolationException;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpStatus;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import example.ljava.util.Result;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableJpaRepositories("example.ljava.repository")
@Slf4j
class Main {
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
    public HandlerExceptionResolver validationHandlerExceptionResolver() {
        HandlerExceptionResolver resolver = new HandlerExceptionResolver() {
            @Override
            public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response,
                    Object handler, Exception ex) {
                if (!(ex instanceof MethodArgumentNotValidException) && !(ex instanceof ConstraintViolationException)) {
                    return null;
                }
                ModelAndView result = new ModelAndView();
                MappingJackson2JsonView view = new MappingJackson2JsonView();
                result.setStatus(HttpStatus.FORBIDDEN);
                try {
                    result.addAllObjects(BeanUtils.describe(Result.failure(ex.getMessage())));
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    log.error(e.getMessage(), e);
                }

                result.setView(view);
                return result;
            }
        };

        return resolver;
    }
}