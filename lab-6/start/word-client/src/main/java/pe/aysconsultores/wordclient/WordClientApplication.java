package pe.aysconsultores.wordclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class WordClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(WordClientApplication.class, args);
	}

}
