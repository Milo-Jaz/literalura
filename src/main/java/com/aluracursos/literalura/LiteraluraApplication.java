package com.aluracursos.literalura;

import com.aluracursos.literalura.principal.Principal;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication(scanBasePackages = "com.aluracursos.literalura")
public class LiteraluraApplication {

	public static void main(String[] args) {
		// Inicia el contexto de Spring
		ApplicationContext context = SpringApplication.run(LiteraluraApplication.class, args);

		// Obtiene el bean de Principal y llama al método mostrarMenu()
		Principal principalBean = context.getBean(Principal.class);
		principalBean.mostrarMenu();
	}
}



