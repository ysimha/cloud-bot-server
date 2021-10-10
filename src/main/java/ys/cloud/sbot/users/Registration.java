package ys.cloud.sbot.users;


import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Registration {

    @NotBlank
    @Email
    private String username;

    @NotBlank
    private String password;
}
