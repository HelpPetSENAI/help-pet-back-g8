package com.helppet.dto.response;

import com.helppet.entity.Pet;
import com.helppet.enums.Size;
import com.helppet.enums.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO para resposta de Pet.
 * 
 * Contém todos os campos públicos de um Pet.
 * NOTA: userId é incluído para rastreamento, mas o cliente sabe que é dele
 * (extraído do header X-User-Id que enviou na requisição).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PetResponse {
    
    private Long id;
    private Long userId;
    private String name;
    private Integer age;
    private Type type;
    private Size size;
    private String race;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Construtor que converte uma entidade Pet em PetResponse.
     */
    public PetResponse(Pet pet) {
        this.id = pet.getId();
        this.userId = pet.getUserId();
        this.name = pet.getName();
        this.age = pet.getAge();
        this.type = pet.getType();
        this.size = pet.getSize();
        this.race = pet.getRace();
        this.createdAt = pet.getCreatedAt();
        this.updatedAt = pet.getUpdatedAt();
    }
}
