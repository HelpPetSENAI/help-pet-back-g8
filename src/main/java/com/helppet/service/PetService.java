package com.helppet.service;

import com.helppet.dto.request.PetRequest;
import com.helppet.dto.response.PetResponse;
import com.helppet.entity.Pet;
import com.helppet.exception.PetNotFoundException;
import com.helppet.exception.UnauthorizedPetAccessException;
import com.helppet.repository.PetRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Serviço de lógica de negócio para Pets.
 * 
 * Implementa:
 * - Isolamento de dados por usuário
 * - Validações de negócio
 * - Transações distribuídas
 * - Logging estruturado com Correlation ID
 */
@Service
public class PetService {

    private static final Logger logger = LoggerFactory.getLogger(PetService.class);
    
    private final PetRepository petRepository;
    private final RequestContext requestContext;

    public PetService(PetRepository petRepository, RequestContext requestContext) {
        this.petRepository = petRepository;
        this.requestContext = requestContext;
    }

    /**
     * Criar um novo pet para o usuário autenticado.
     * 
     * Fluxo:
     * 1. Recebe PetRequest e userId (do gateway via RequestContext)
     * 2. Cria entidade Pet com userId preenchido
     * 3. Salva no banco de dados
     * 4. Retorna PetResponse
     * 
     * @param dto Dados do pet a criar
     * @param userId ID do usuário proprietário (vem do gateway)
     * @return PetResponse do pet criado
     */
    public PetResponse createPet(PetRequest dto, Long userId) {
        String correlationId = requestContext.getCorrelationId();
        logger.info("[{}] Criando novo pet para usuário {} - nome: {}", 
            correlationId, userId, dto.getName());
        
        Pet pet = new Pet(dto);
        pet.setUserId(userId);
        
        Pet saved = petRepository.save(pet);
        
        logger.info("[{}] Pet criado com sucesso - id: {}, userId: {}", 
            correlationId, saved.getId(), userId);
        
        return new PetResponse(saved);
    }

    /**
     * Atualizar um pet existente.
     * 
     * Valida se o pet pertence ao usuário ANTES de atualizar.
     * Se o pet não existir ou não pertencer ao usuário, lança exceção.
     * 
     * @param id ID do pet a atualizar
     * @param dto Novos dados do pet
     * @param userId ID do usuário autenticado
     * @return PetResponse atualizado
     * @throws PetNotFoundException se pet não existir
     * @throws UnauthorizedPetAccessException se pet não pertencer ao usuário
     */
    @Transactional
    public PetResponse updatePet(Long id, PetRequest dto, Long userId) {
        String correlationId = requestContext.getCorrelationId();
        logger.info("[{}] Atualizando pet {} do usuário {}", correlationId, id, userId);
        
        // Validação: pet existe E pertence ao usuário
        Pet pet = petRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> {
                logger.warn("[{}] Pet {} não encontrado ou não pertence ao usuário {}", 
                    correlationId, id, userId);
                return new PetNotFoundException("Pet não encontrado ou não pertence ao usuário");
            });

        // Atualizar campos
        pet.setName(dto.getName());
        pet.setAge(dto.getAge());
        pet.setType(dto.getType());
        pet.setSize(dto.getSize());
        pet.setRace(dto.getRace());

        Pet saved = petRepository.save(pet);
        
        logger.info("[{}] Pet {} atualizado com sucesso", correlationId, id);
        
        return new PetResponse(saved);
    }

    /**
     * Listar todos os pets do usuário autenticado.
     * 
     * Retorna apenas pets onde userId == usuário autenticado.
     * 
     * @param userId ID do usuário autenticado
     * @return Lista de PetResponse dos pets do usuário
     */
    public List<PetResponse> listUserPets(Long userId) {
        String correlationId = requestContext.getCorrelationId();
        logger.debug("[{}] Listando pets do usuário {}", correlationId, userId);
        
        List<PetResponse> pets = petRepository.findByUserId(userId)
            .stream()
            .map(PetResponse::new)
            .toList();
        
        logger.info("[{}] Encontrados {} pets para usuário {}", 
            correlationId, pets.size(), userId);
        
        return pets;
    }

    /**
     * Obter um pet específico (com validação de propriedade).
     * 
     * Valida que o pet pertence ao usuário antes de retornar.
     * 
     * @param id ID do pet
     * @param userId ID do usuário autenticado
     * @return PetResponse do pet
     * @throws PetNotFoundException se pet não existir ou não pertencer ao usuário
     */
    public PetResponse getPetById(Long id, Long userId) {
        String correlationId = requestContext.getCorrelationId();
        logger.debug("[{}] Buscando pet {} do usuário {}", correlationId, id, userId);
        
        return petRepository.findByIdAndUserId(id, userId)
            .map(PetResponse::new)
            .orElseThrow(() -> {
                logger.warn("[{}] Pet {} não encontrado ou não pertence ao usuário {}", 
                    correlationId, id, userId);
                return new PetNotFoundException("Pet não encontrado ou não pertence ao usuário");
            });
    }

    /**
     * Deletar um pet (com validação de propriedade).
     * 
     * Valida que o pet pertence ao usuário ANTES de deletar.
     * 
     * @param id ID do pet
     * @param userId ID do usuário autenticado
     * @throws UnauthorizedPetAccessException se usuário não for proprietário
     */
    @Transactional
    public void deletePet(Long id, Long userId) {
        String correlationId = requestContext.getCorrelationId();
        logger.info("[{}] Deletando pet {} do usuário {}", correlationId, id, userId);
        
        if (!petRepository.petBelongsToUser(id, userId)) {
            logger.warn("[{}] Usuário {} tentou deletar pet {} que não lhe pertence", 
                correlationId, userId, id);
            throw new UnauthorizedPetAccessException(
                "Você não tem permissão para deletar este pet"
            );
        }
        
        petRepository.deleteById(id);
        logger.info("[{}] Pet {} deletado com sucesso", correlationId, id);
    }
}
