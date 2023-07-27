package now.eyak;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EYakApplication {

	public static void main(String[] args) {
		SpringApplication.run(EYakApplication.class, args);
	}
}
