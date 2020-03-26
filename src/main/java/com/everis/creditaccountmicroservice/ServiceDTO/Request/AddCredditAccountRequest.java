package com.everis.creditaccountmicroservice.ServiceDTO.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddCredditAccountRequest {
    private String serialNumber;
    private String idCreditAccountType;
    private String clientId;
    private String dni;
    private double ammount;
    private double limit;
}
