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
    @NotBlank(message = "'serialNumber' can't be blank")
    private String serialNumber;
    @NotBlank(message = "'type' can't be blank")
    private String type;
    @NotBlank(message = "'clientId' can't be blank")
    private String clientId;
}
