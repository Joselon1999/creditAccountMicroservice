
package com.everis.creditaccountmicroservice.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "creditAccountTransaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreditAccountTransaction {
    private String id;
    private String serialNumber;
    private String idCliente;
    private String transferenceType;
    private double transferenceAmount;
    private double totalAmount;
}
