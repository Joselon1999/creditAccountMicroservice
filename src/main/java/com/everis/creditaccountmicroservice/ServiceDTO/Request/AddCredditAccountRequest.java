package com.everis.creditaccountmicroservice.ServiceDTO.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddCredditAccountRequest {
    private String serialNumber;
    private String idCreditAccountType;
    private String clientId;
    private String dni;
    private String bankId;
    private double ammount;
    private double limit;
    private double monto;
    private Date expireDate;
}
