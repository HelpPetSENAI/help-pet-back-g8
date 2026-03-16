package helppet.com.service;

import helppet.com.dto.request.UserRequestDTO;
import helppet.com.dto.response.UserResponseDTO;
import helppet.com.entity.User;
import helppet.com.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponseDTO createUser(UserRequestDTO dto){
        User user = new User(dto);
        userRepository.save(user);
        return new UserResponseDTO(user);
    }

    public List<UserResponseDTO> listAllUsers(){
        return userRepository.findAll().stream().map(UserResponseDTO::new).toList();
    }

    public UserResponseDTO listUserById(Long id) {
        return new UserResponseDTO(
                userRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Usuário não encontrado"))
        );
    }

    public UserResponseDTO updateUser(UserRequestDTO dto, Long id){
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado");
        }
        User user = new User(dto);
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setPhone(dto.getPhone());
        return new UserResponseDTO(user);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado");
        }
        userRepository.deleteById(id);
    }


}
