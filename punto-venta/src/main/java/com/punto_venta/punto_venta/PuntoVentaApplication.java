package com.punto_venta.punto_venta;

import com.punto_venta.model.Categoria;
import com.punto_venta.repository.CategoriaRepository;
import org.slf4j.Logger; // Importa la clase Logger de SLF4J para registrar mensajes de log en la aplicación
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean; // Importa la anotación Bean para definir un bean de Spring que se ejecutará al iniciar la aplicación
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import com.punto_venta.repository.ProductRepository;

@SpringBootApplication(scanBasePackages = "com.punto_venta")
@EntityScan(basePackages = "com.punto_venta.model")
@EnableJpaRepositories(basePackages = "com.punto_venta.repository")
public class PuntoVentaApplication {

	private static final Logger log = LoggerFactory.getLogger(PuntoVentaApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(PuntoVentaApplication.class, args);
	}

	@Bean // Define un bean de Spring que se ejecutará al iniciar la aplicación. En este caso, es un CommandLineRunner que se utiliza para verificar la conexión a la base de datos y contar el número de productos almacenados.
	public CommandLineRunner initDatabase(ProductRepository productRepository, CategoriaRepository categoriaRepository) {
		return args -> {
			log.info("-----------------------------------------");
			log.info("Iniciando verificación de base de datos...");
			try {
				// Verificar si la tabla de categorías está vacía
				if (categoriaRepository.count() == 0) {
					log.info("La tabla de categorías está vacía. Creando categoría inicial...");
					// Usamos el constructor sin ID para que la DB asigne el 1 automáticamente
					Categoria general = new Categoria("GENERAL", "Categoría por defecto para productos");
					categoriaRepository.save(general);
					log.info("Categoría GENERAL creada automáticamente con ID 1.");
				}

				long productCount = productRepository.count();
				log.info("Número de productos en la BD: {}", productCount);
			} catch (Exception e) {
				log.error("Error durante la inicialización: {}", e.getMessage());
			}
			log.info("-----------------------------------------");
		};
	}

}
