package helppet.com.controller;

import helppet.com.dto.request.UserRequestDTO;
import helppet.com.dto.response.UserResponseDTO;
import helppet.com.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity <UserResponseDTO> createUser (UserRequestDTO dto) {
        return ResponseEntity.ok(userService.createUser(dto));
    }

    @GetMapping ("/list")
    public ResponseEntity <Iterable<UserResponseDTO>> listarUsuarios(){
        return ResponseEntity.ok(userService.listAllUsers());
    }

    @GetMapping ("/list/{id}")
    public ResponseEntity <UserResponseDTO> listarUsuarioPorId(Long id){
        return ResponseEntity.ok(userService.listUserById(id));
    }

    @GetMapping ("/{id}")
    public String DeleteUser(Long id){
        userService.deleteUser(id);
        return "Usuario deletado com sucesso";
    }
}
