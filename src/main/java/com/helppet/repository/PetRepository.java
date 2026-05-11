package com.helppet.repository;

import com.helppet.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório para operações de persistência de Pets.
 * 
 * Implementa isolamento de dados por usuário através de userId.
 * Todos os métodos garantem que um usuário só pode acessar seus próprios pets.
 */
@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    /**
     * Busca todos os pets de um usuário específico.
     * 
     * Usado para listar pets do usuário autenticado em GET /api/v1/pets
     * 
     * @param userId ID do usuário proprietário
     * @return Lista de pets do usuário (pode estar vazia)
     */
    List<Pet> findByUserId(Long userId);

    /**
     * Busca um pet específico verificando se pertence ao usuário.
     * 
     * Usado para validar autorização antes de ATUALIZAR ou DELETAR
     * 
     * @param id ID do pet
     * @param userId ID do usuário proprietário
     * @return Optional contendo o pet se existir e pertencer ao usuário
     */
    Optional<Pet> findByIdAndUserId(Long id, Long userId);

    /**
     * Verifica se um pet pertence a um usuário específico.
     * 
     * Usado para validações de autorização sem carregar toda a entidade
     * 
     * @param petId ID do pet
     * @param userId ID do usuário proprietário
     * @return true se o pet existe e pertence ao usuário, false caso contrário
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Pet p WHERE p.id = :petId AND p.userId = :userId")
    boolean petBelongsToUser(@Param("petId") Long petId, @Param("userId") Long userId);
}
