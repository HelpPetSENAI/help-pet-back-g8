package com.helppet.service;

import com.helppet.dto.request.UserRequest;
import com.helppet.dto.response.UserResponse;
import com.helppet.entity.User;
import com.helppet.exception.user.UserNotFoundException;
import com.helppet.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Servico de negocios para operacoes de Usuario.
 */
@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Cria um novo usuario com senha hasheada via BCrypt.
     */
    @Transactional
    public UserResponse createUser(UserRequest dto) {
        User user = new User(dto);
        // Hash da senha antes de persistir - NUNCA salvar senha em texto plano
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(user);
        log.info("Usuario criado: {}", user.getEmail());
        return new UserResponse(user);
    }

    /**
     * Atualiza dados de um usuario existente.
     * BUG CORRIGIDO: a versao anterior criava um novo User sem ID e nao persistia.
     */
    @Transactional
    public UserResponse updateUser(Long id, UserRequest dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setCpf(dto.getCpf());

        // Atualiza senha apenas se fornecida
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        userRepository.save(user);
        log.info("Usuario atualizado: id={}", id);
        return new UserResponse(user);
    }

    /**
     * Lista todos os usuarios com paginacao.
     */
    public Page<UserResponse> listAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserResponse::new);
    }

    /**
     * Busca um usuario por ID.
     */
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return new UserResponse(user);
    }

    /**
     * Remove um usuario por ID.
     */
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
        log.info("Usuario removido: id={}", id);
    }

    /**
     * Busca usuario por email (usado no login).
     */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario nao encontrado com email: " + email));
    }
}
