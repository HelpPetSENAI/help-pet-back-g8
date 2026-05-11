package com.helppet.repository;


import com.helppet.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório JPA para operações de persistência da entidade User.
 * <p>
 * Esta interface fornece métodos CRUD (Create, Read, Update, Delete) para
 * a entidade User, além de queries customizadas para buscar usuários por
 * e-mail e CPF. Herda funcionalidades do JpaRepository do Spring Data.
 * </p>
 *
 * @author Help Pet Team
 * @version 1.0
 * @since 2026-03-03
 */
@Repository
public interface UserRepository extends JpaRepository <User, Long> {
    
    /**
     * Busca um usuário pelo endereço de e-mail.
     * <p>
     * Método de query derivada que procura um usuário único com o e-mail especificado.
     * </p>
     *
     * @param email endereço de e-mail a ser buscado
     * @return Optional contendo o usuário encontrado ou vazio se não existir
     */
    Optional<User> findByEmail(String email);

    /**
     * Busca um usuário pelo CPF.
     * <p>
     * Método de query derivada que procura um usuário único com o CPF especificado.
     * </p>
     *
     * @param cpf número do CPF a ser buscado
     * @return usuário encontrado ou null se não existir
     */
    User findByCpf(String cpf);
}
