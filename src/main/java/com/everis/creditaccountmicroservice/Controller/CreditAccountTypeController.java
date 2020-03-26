package com.everis.creditaccountmicroservice.Controller;

import com.everis.creditaccountmicroservice.Document.CreditAccountType;
import com.everis.creditaccountmicroservice.Service.CreditAccountTypeService;
import com.everis.creditaccountmicroservice.ServiceDTO.Request.CreditAccountTypeRequest;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping(value = "/api")
public class CreditAccountTypeController {

    @Autowired
    CreditAccountTypeService creditAccountTypeService;

    //CREATE
    @ApiOperation(value = "Creates new creditAccountTypes")
    @PostMapping(value = "/creditAccountTypes")
    public Mono<CreditAccountType> createCreditAccountType(@Valid @RequestBody CreditAccountType creditAccountType){
        return creditAccountTypeService.create(creditAccountType);
    }
    //UPDATE
    @ApiOperation(value = "Updates creditAccountTypes",
            notes = "Requires the creditAccountType ID and all CreditAccountType Request Params - Which are same CreditAccountType Params" +
                    "excluding the ID")
    @PutMapping(value = "/creditAccountTypes/{id}")
    public Mono<ResponseEntity<CreditAccountType>> updateCreditAccountType(@PathVariable("id") String id, @Valid @RequestBody CreditAccountTypeRequest creditAccountTypeRequest) {
        return creditAccountTypeService.update(id,creditAccountTypeRequest)
                .map(creditAccountType -> ResponseEntity.created(URI.create("/creditAccountTypes".concat(creditAccountType.getId())))
                        .contentType(MediaType.APPLICATION_JSON).body(creditAccountType))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    //READ
    @ApiOperation(value = "List all creditAccountTypes")
    @GetMapping(value = "/creditAccountTypes")
    public ResponseEntity<Flux<CreditAccountType>> listCreditAccountType(){
        return ResponseEntity.ok().body(creditAccountTypeService.readAll());
    }
    //DELETE
    @ApiOperation(value = "Deletes a creditAccountType",
            notes = "Requires the creditAccountType ID")
    @DeleteMapping(value = "/creditAccountTypes/{creditAccountTypeId}")
    public Mono<CreditAccountType> deleteCreditAccountType(@PathVariable(value = "creditAccountTypeId") String creditAccountTypeId){
        return creditAccountTypeService.delete(creditAccountTypeId);
    }
    //FIND ONE
    @ApiOperation(value = "List one creditAccountType",
            notes = "Requires the creditAccountType ID")
    @GetMapping(value = "/creditAccountType/{creditAccountTypeId}")
    public Mono<CreditAccountType> findOneCreditAccountType(@PathVariable(value = "creditAccountTypeId") String creditAccountTypeId){
        return creditAccountTypeService.getOne(creditAccountTypeId);
    }
}
