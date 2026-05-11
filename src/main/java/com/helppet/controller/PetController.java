package com.helppet.controller;

import com.helppet.dto.request.PetRequest;
import com.helppet.dto.response.ApiResponse;
import com.helppet.dto.response.PetResponse;
import com.helppet.service.PetService;
import com.helppet.service.RequestContext;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gerenciar operações de Pets.
 * 
 * Todos os endpoints estão versionados em /api/v1/pets
 * Todos os endpoints requerem autenticação via API Gateway:
 * - Headers obrigatórios: X-User-Id, X-Request-Id
 * 
 * Responsabilidades:
 * 1. Receber requisições HTTP
 * 2. Extrair userId via RequestContext (populado pelo RequestContextFilter)
 * 3. Chamar PetService com userId para isolamento
 * 4. Retornar ApiResponse padronizada
 * 
 * Tratamento de erros: Centralizado em GlobalExceptionHandler
 */
@RestController
@RequestMapping("/api/v1/pets")
public class PetController {

    private static final Logger logger = LoggerFactory.getLogger(PetController.class);
    
    private final PetService petService;
    private final RequestContext requestContext;

    public PetController(PetService petService, RequestContext requestContext) {
        this.petService = petService;
        this.requestContext = requestContext;
    }

    /**
     * POST /api/v1/pets
     * 
     * Criar um novo pet para o usuário autenticado.
     * 
     * @param petRequest Dados do pet (validados)
     * @return ResponseEntity com ApiResponse contendo o pet criado (status 201)
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PetResponse>> createPet(
            @Valid @RequestBody PetRequest petRequest) {
        
        Long userId = requestContext.getUserId();
        String correlationId = requestContext.getCorrelationId();
        
        logger.info("[{}] POST /api/v1/pets - Criando pet para usuário {}", 
            correlationId, userId);
        
        PetResponse pet = petService.createPet(petRequest, userId);
        
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(new ApiResponse<>(true, "Pet criado com sucesso", pet));
    }

    /**
     * PUT /api/v1/pets/{id}
     * 
     * Atualizar um pet existente (apenas o proprietário pode).
     * 
     * @param id ID do pet a atualizar
     * @param petRequest Novos dados do pet (validados)
     * @return ResponseEntity com ApiResponse contendo o pet atualizado (status 200)
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PetResponse>> updatePet(
            @PathVariable Long id,
            @Valid @RequestBody PetRequest petRequest) {
        
        Long userId = requestContext.getUserId();
        String correlationId = requestContext.getCorrelationId();
        
        logger.info("[{}] PUT /api/v1/pets/{} - Atualizando pet do usuário {}",
            correlationId, id, userId);
        
        PetResponse pet = petService.updatePet(id, petRequest, userId);
        
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Pet atualizado com sucesso", pet)
        );
    }

    /**
     * GET /api/v1/pets
     * 
     * Listar todos os pets do usuário autenticado.
     * 
     * @return ResponseEntity com ApiResponse contendo lista de pets (status 200)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PetResponse>>> listUserPets() {
        Long userId = requestContext.getUserId();
        String correlationId = requestContext.getCorrelationId();
        
        logger.info("[{}] GET /api/v1/pets - Listando pets do usuário {}",
            correlationId, userId);
        
        List<PetResponse> pets = petService.listUserPets(userId);
        
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Pets listados com sucesso", pets)
        );
    }

    /**
     * GET /api/v1/pets/{id}
     * 
     * Obter detalhes de um pet específico (apenas do proprietário).
     * 
     * @param id ID do pet
     * @return ResponseEntity com ApiResponse contendo o pet (status 200)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PetResponse>> getPet(@PathVariable Long id) {
        Long userId = requestContext.getUserId();
        String correlationId = requestContext.getCorrelationId();
        
        logger.info("[{}] GET /api/v1/pets/{} - Buscando pet do usuário {}",
            correlationId, id, userId);
        
        PetResponse pet = petService.getPetById(id, userId);
        
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Pet encontrado", pet)
        );
    }

    /**
     * DELETE /api/v1/pets/{id}
     * 
     * Deletar um pet (apenas do proprietário).
     * 
     * @param id ID do pet a deletar
     * @return ResponseEntity com status 204 NO CONTENT
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePet(@PathVariable Long id) {
        Long userId = requestContext.getUserId();
        String correlationId = requestContext.getCorrelationId();
        
        logger.info("[{}] DELETE /api/v1/pets/{} - Deletando pet do usuário {}",
            correlationId, id, userId);
        
        petService.deletePet(id, userId);
        
        return ResponseEntity.noContent().build();
    }
}
