package com.everis.creditaccountmicroservice.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Document(value = "creditAccount")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreditAccount {
    @Id
    private String id;
    @NotBlank(message = "'serialNumber' can't be blank")
    private String serialNumber;
    @NotBlank(message = "'type' can't be blank")
    private CreditAccountType creditAccountType;
    @NotBlank(message = "'bankId' can't be blank")
    private String bankId;
    @NotBlank(message = "'clientId' can't be blank")
    private String clientId;
    @NotBlank(message = "'dni' can't be blank")
    private String dni;
    @NotBlank(message = "'limit' can't be blank")
    private double limit;
    private double ammount;
    @NotBlank(message = "'expireDate' can't be blank")
    private Date expireDate;
    private Date creationDate;
}
