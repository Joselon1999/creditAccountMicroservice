package com.everis.creditaccountmicroservice.Controller;

import com.everis.creditaccountmicroservice.Document.CreditAccount;
import com.everis.creditaccountmicroservice.Service.CreditAccountService;
import com.everis.creditaccountmicroservice.ServiceDTO.Request.AddCredditAccountRequest;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api")
public class CreditAccountController {

    @Autowired
    CreditAccountService creditAccountService;

    @ApiOperation(value = "Creates new accounts",
            notes = "Requires a AddcreditAccountRequest Params - Which are the same as  the creditAccount Params" +
                    "excluding the ID")
    @PostMapping(value = "/creditAccounts")
    public Mono<CreditAccount> createClientcreditAccount(@Valid @RequestBody AddCredditAccountRequest addcreditAccountRequest){
        return creditAccountService.create(addcreditAccountRequest);
    }
    /*UPDATE*/
    @ApiOperation(value = "Creates new accounts",
            notes = "Requires creditId and AddcreditAccountRequest Params ")
    @PutMapping(value = "/creditAccounts/{creditId}")
    public Mono<CreditAccount> updateClientcreditAccount(@PathVariable("creditId") String creditId,
                                                     @Valid @RequestBody AddCredditAccountRequest addcreditAccountRequest) {
        return creditAccountService.update(creditId,addcreditAccountRequest);
    }
    /*READ*/
    @ApiOperation(value = "Creates new accounts",
            notes = "Requires Client ID")
    @GetMapping(value = "/creditAccounts/{clientId}")
    public Flux<CreditAccount> listClientcreditAccounts(@PathVariable(value = "clientId") String clientId){
        return creditAccountService.readAll(clientId);
    }
    /*DELETE*/
    @ApiOperation(value = "Deletes a credit accounts",
            notes = "Requires creditAccount ID")
    @DeleteMapping(value = "/creditAccounts/{creditId}")
    public Mono<CreditAccount> deleteClientcreditAccount(@PathVariable(value = "creditId") String creditId){
        return creditAccountService.delete(creditId);

    }
    /*FIND ONE
    @GetMapping(value = "/find/{clientId}")
    public Mono<Client> findOne(@PathVariable(value = "clientId") String clientId){
        return clientService.getOne(clientId);
    }*/
}
