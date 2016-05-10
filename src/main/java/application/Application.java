package application;

import application.examples.Customer;
import application.examples.CustomerRepository;
import application.user.PaceUser;
import application.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Spring Boot main application.Application
 */
@SpringBootApplication
public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class);
    }

    @Bean
    public CommandLineRunner demo(CustomerRepository repository) {
        return (args) -> {
            // save a couple of customers
            repository.save(new Customer("Jack", "Bauer"));
            repository.save(new Customer("Chloe", "O'Brian"));
            repository.save(new Customer("Kim", "Bauer"));
            repository.save(new Customer("David", "Palmer"));
            repository.save(new Customer("Michelle", "Dessler"));
            repository.save(new Customer("Allar", "Viinamäe"));

            // fetch all customers
            log.info("Customers found with findAll():");
            log.info("-------------------------------");
            for (Customer customer : repository.findAll()) {
                log.info(customer.toString());
            }
            log.info("");

            // fetch an individual customer by ID
            Customer customer = repository.findOne(2L);
            log.info("Customer found with findOne(2L):");
            log.info("--------------------------------");
            log.info(customer.toString());
            log.info("");

            // fetch customers by last name
            log.info("Customer found with findByLastName('Bauer'):");
            log.info("--------------------------------------------");
            for (Customer bauer : repository.findByLastName("Bauer")) {
                log.info(bauer.toString());
            }
            log.info("");
        };
    }

    @Bean
    public CommandLineRunner user(UserRepository repository) {
        return (args) -> {
            // save a couple of customers
            repository.save(new PaceUser("Allar", "1273703759309879"));

            PaceUser teacherAdmin = new PaceUser();
            teacherAdmin.setAuthResponse("unknown");
            teacherAdmin.setEmail("teacher@mail.ee");
            teacherAdmin.setPassword("teacher");
            teacherAdmin.setRole("teacher");

            repository.save(teacherAdmin);


            // fetch all customers
            log.info("Users found with findAll():");
            log.info("-------------------------------");
            for (PaceUser paceUser : repository.findAll()) {
                log.info(paceUser.toString());
            }
            log.info("");
        };
    }

//    Required to enable cross-origin resource sharing
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/*");
            }
        };
    }
}
