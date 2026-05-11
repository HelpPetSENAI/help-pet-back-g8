package com.helppet.entity;

import com.helppet.dto.request.PetRequest;
import com.helppet.enums.Size;
import com.helppet.enums.Type;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidade JPA que representa um Pet no sistema.
 * <p>
 * Esta classe mapeia a tabela de pets no banco de dados, armazenando
 * informações sobre os animais cadastrados no sistema Help Pet.
 * 
 * IMPORTANTE: Cada pet está associado a um usuário específico (userId).
 * Apenas o proprietário do pet pode realizar operações nele (isolamento de dados).
 * </p>
 *
 * @author Help Pet Team
 * @version 1.0
 * @since 2026-03-03
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pets")
public class Pet {

    /**
     * Identificador único do pet.
     * <p>
     * Chave primária auto-incrementada gerada automaticamente pelo banco de dados.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ID do usuário proprietário do pet.
     * <p>
     * Vem do API Gateway via header X-User-Id.
     * Garante isolamento de dados entre usuários (um usuário só vê seus próprios pets).
     * OBRIGATÓRIO - não pode ser nulo.
     * </p>
     */
    @Column(nullable = false)
    private Long userId;
    
    /**
     * Nome do pet.
     */
    @Column(nullable = false, length = 100)
    private String name;
    
    /**
     * Idade do pet em anos.
     */
    @Column(nullable = false)
    private int age;
    
    /**
     * Tipo/espécie do pet (ex: CACHORRO, GATO, PASSARO, ROEDOR).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;
    
    /**
     * Tamanho/porte do pet (PEQUENO, MEDIO, GRANDE).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Size size;
    
    /**
     * Raça do pet.
     */
    @Column(length = 100)
    private String race;

    /**
     * Data de criação do registro do pet.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Data da última atualização do registro do pet.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Construtor que cria uma entidade Pet a partir de um PetRequest.
     * <p>
     * Converte um objeto de requisição em uma entidade de banco de dados.
     * NOTA: userId deve ser preenchido APÓS este construtor pelo service.
     * </p>
     *
     * @param dto objeto PetRequest contendo os dados do pet
     */
    public Pet(PetRequest dto) {
        this.name = dto.getName();
        this.age = dto.getAge();
        this.type = dto.getType();
        this.size = dto.getSize();
        this.race = dto.getRace();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Hook do JPA - executado ANTES de INSERIR um registro.
     * <p>
     * Garante que createdAt seja sempre preenchido no momento da criação.
     * </p>
     */
    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Hook do JPA - executado ANTES de ATUALIZAR um registro.
     * <p>
     * Atualiza automaticamente a data de última modificação.
     * </p>
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
