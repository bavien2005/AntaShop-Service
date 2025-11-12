package org.anta.dto.response;

import org.anta.enums.Role;
import lombok.Data;
@Data
public class UserResponse {

        private Long id ;

        private String name;

        private String password;

        private String email;
        
        private Role role;

        private String phoneNumber;

}
