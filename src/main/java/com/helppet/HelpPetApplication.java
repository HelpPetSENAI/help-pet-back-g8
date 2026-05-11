package com.helppet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principal da aplicação Help Pet.
 * <p>
 * Esta classe é responsável por inicializar e executar a aplicação Spring Boot.
 * O Help Pet é um sistema de gerenciamento de pets e usuários, fornecendo
 * funcionalidades para cadastro, consulta, atualização e exclusão de informações.
 * </p>
 *
 * @author Help Pet Team
 * @version 1.0
 * @since 2026-03-03
 */
@SpringBootApplication
public class HelpPetApplication {

	/**
	 * Método principal que inicia a aplicação Spring Boot.
	 * <p>
	 * Este método é o ponto de entrada da aplicação, responsável por
	 * inicializar o contexto do Spring e iniciar o servidor web embutido.
	 * </p>
	 *
	 * @param args argumentos de linha de comando passados para a aplicação
	 */
	public static void main(String[] args) {
		SpringApplication.run(HelpPetApplication.class, args);
	}

}
