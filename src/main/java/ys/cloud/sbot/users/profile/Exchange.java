package ys.cloud.sbot.users.profile;

import lombok.*;
import org.springframework.data.annotation.Id;

@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exchange {

    @Id
    private String id;
    private String name;
}
