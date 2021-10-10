package ys.cloud.sbot.users.profile;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;
import ys.cloud.sbot.config.Username;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Data
@ToString
@Builder
@Document
public class UserProfile {

    @Id
    private String id ;

    @Builder.Default
    private List<ExchangeAccount> exchangeAccounts = new LinkedList<ExchangeAccount>();
    
    @CreatedDate
    private LocalDateTime createdDate;

    @CreatedBy
    private Username author;
    
    @LastModifiedBy
    private String lastModifiedUser;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}
