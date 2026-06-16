package com.punto_venta.punto_venta;

import org.slf4j.Logger; // Importa la clase Logger de SLF4J para registrar mensajes de log en la aplicación
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean; // Importa la anotación Bean para definir un bean de Spring que se ejecutará al iniciar la aplicación
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import com.punto_venta.repository.ProductRepository;

@SpringBootApplication // Anotación que indica que esta clase es la clase principal de una aplicación Spring Boot. Incluye @Configuration, @EnableAutoConfiguration y @ComponentScan por defecto.
@ComponentScan("com.punto_venta")
@EntityScan("com.punto_venta.model")
@EnableJpaRepositories("com.punto_venta.repository")
public class PuntoVentaApplication {

	private static final Logger log = LoggerFactory.getLogger(PuntoVentaApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(PuntoVentaApplication.class, args);
	}

	@Bean // Define un bean de Spring que se ejecutará al iniciar la aplicación. En este caso, es un CommandLineRunner que se utiliza para verificar la conexión a la base de datos y contar el número de productos almacenados.
	public CommandLineRunner testConnection(ProductRepository repository) {
		return args -> {
			log.info("-----------------------------------------");
			log.info("Verificando conexión a la base de datos...");
			try {
				long count = repository.count();
				log.info("¡Conexión exitosa!");
				log.info("Número de productos en la BD: {}", count);
			} catch (Exception e) {
				log.error("Error al conectar a la base de datos: {}", e.getMessage());
			}
			log.info("-----------------------------------------");
		};
	}

}
