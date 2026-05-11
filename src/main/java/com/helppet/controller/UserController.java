package com.helppet.controller;

import com.helppet.dto.request.LoginRequest;
import com.helppet.dto.request.UserRequest;
import com.helppet.dto.response.ApiResponse;
import com.helppet.dto.response.LoginResponse;
import com.helppet.dto.response.UserResponse;
import com.helppet.entity.User;
import com.helppet.service.UserService;
import com.helppet.util.JwtUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Controller de Usuarios.
 *
 * Todos os endpoints retornam ApiResponse para formato consistente.
 * Paginacao implementada nos endpoints de listagem.
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody UserRequest request) {
        UserResponse user = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Usuario criado com sucesso", user));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserResponse>>> listUsers(
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        Page<UserResponse> page = userService.listAllUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success("Usuarios listados", page));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success("Usuario encontrado", user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest request) {
        UserResponse user = userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success("Usuario atualizado", user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("Usuario removido com sucesso", null));
    }

    /**
     * Login: valida credenciais e retorna token JWT.
     * A validacao de senha usa BCrypt (nunca comparacao direta de strings).
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest loginRequest) {
        try {
            User user = userService.findByEmail(loginRequest.getEmail());

            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                log.warn("Tentativa de login com senha incorreta para: {}", loginRequest.getEmail());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Credenciais invalidas"));
            }

            String token = jwtUtil.generateToken(user.getEmail(), String.valueOf(user.getId()));
            LoginResponse response = new LoginResponse(token, user.getEmail(), user.getName());

            log.info("Login bem-sucedido para: {}", user.getEmail());
            return ResponseEntity.ok(ApiResponse.success("Login realizado com sucesso", response));

        } catch (Exception e) {
            log.warn("Falha no login para: {}", loginRequest.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Credenciais invalidas"));
        }
    }

    /**
     * Verifica se o token JWT foi aceito pelo Spring Security.
     * Util para o frontend validar se a sessao ainda e valida.
     */
    @GetMapping("/auth-check")
    public ResponseEntity<ApiResponse<String>> authCheck(Authentication authentication) {
        return ResponseEntity.ok(
                ApiResponse.success("Autenticado", authentication.getName())
        );
    }
}
