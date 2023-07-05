package com.example.bacourse;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

@SpringBootApplication
@EnableEncryptableProperties
@EntityScan(basePackageClasses = { 
	BacourseApplication.class,
	Jsr310JpaConverters.class 
})
public class BacourseApplication {
	@PostConstruct
	void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	public static void main(String[] args) {
		SpringApplication.run(BacourseApplication.class, args);
	}

}
