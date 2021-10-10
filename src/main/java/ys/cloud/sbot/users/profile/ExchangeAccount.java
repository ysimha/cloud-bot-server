package ys.cloud.sbot.users.profile;


import lombok.*;

import javax.validation.constraints.NotBlank;

@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExchangeAccount {

    @NotBlank
    private String exchange ;
    @NotBlank
    private String publicKey ;
    @NotBlank
    private String secret ;
    
}
