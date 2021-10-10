package ys.cloud.sbot.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class User {

    @Id
    @JsonIgnore
    private String id;

    @NotBlank
    private String username;

    @NotBlank
    @JsonIgnore
    private String password;

    @NotBlank
    @Email
    private String email;

    @Builder.Default()
    @JsonIgnore
    private boolean active = true;
    
    @Builder.Default()
    private List<String> roles = new ArrayList<>();

}
