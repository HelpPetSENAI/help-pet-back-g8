package helppet.com.entity;

import helppet.com.dto.request.UserRequestDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table (name = "tb_user")
public class User {

    @Id
    @GeneratedValue (strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    private String phone;

    public User(UserRequestDTO userRequestDTO) {
        this.name = userRequestDTO.getName();
        this.email = userRequestDTO.getEmail();
        this.password = userRequestDTO.getPassword();
        this.phone = userRequestDTO.getPhone();
    }
}
