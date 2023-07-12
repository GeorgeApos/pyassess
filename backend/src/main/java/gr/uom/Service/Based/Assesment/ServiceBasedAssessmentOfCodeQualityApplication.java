package gr.uom.Service.Based.Assesment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:application.properties")
public class ServiceBasedAssessmentOfCodeQualityApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceBasedAssessmentOfCodeQualityApplication.class, args);
	}

}
