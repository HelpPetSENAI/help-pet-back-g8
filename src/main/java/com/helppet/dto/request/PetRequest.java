package com.helppet.dto.request;

import com.helppet.enums.Size;
import com.helppet.enums.Type;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para requisição de criação/atualização de Pets.
 * 
 * Todos os campos são validados automaticamente pelo Spring Validation.
 * Erros de validação retornam 400 BAD REQUEST com detalhes dos campos inválidos.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PetRequest {
    
    @NotBlank(message = "Nome do pet é obrigatório")
    private String name;
    
    @NotNull(message = "Idade é obrigatória")
    @Min(value = 0, message = "Idade não pode ser negativa")
    private Integer age;
    
    @NotNull(message = "Tipo é obrigatório")
    private Type type;
    
    @NotNull(message = "Tamanho é obrigatório")
    private Size size;
    
    private String race;
}
