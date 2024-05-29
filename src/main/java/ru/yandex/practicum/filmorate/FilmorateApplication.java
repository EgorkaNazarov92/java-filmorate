package ru.yandex.practicum.filmorate;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FilmorateApplication {

	private static final Logger log =
			(ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

	public static void main(String[] args) {
		log.info("Starting Filmorate service");
		SpringApplication.run(FilmorateApplication.class, args);
	}

}
