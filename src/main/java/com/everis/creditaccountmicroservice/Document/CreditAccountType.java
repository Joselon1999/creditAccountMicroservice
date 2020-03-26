package com.everis.creditaccountmicroservice.Document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "creditAccountType")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditAccountType {
    @Id
    private String id;
    private String name;
}
