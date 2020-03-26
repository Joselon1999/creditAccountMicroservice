package com.everis.creditaccountmicroservice.Controller;

import com.everis.creditaccountmicroservice.Document.CreditAccountTransaction;
import com.everis.creditaccountmicroservice.Service.CreditAccountTransactionService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class CreditAccountTransactionController {

    @Autowired
    CreditAccountTransactionService creditAccountTransactionService;

    //READ
    @ApiOperation(value = "List all bankAccountTypes")
    @GetMapping(value = "/creditAccountTransactions")
    public ResponseEntity<Flux<CreditAccountTransaction>> listBankAccountType(){
        return ResponseEntity.ok().body(creditAccountTransactionService.read());
    }
}
