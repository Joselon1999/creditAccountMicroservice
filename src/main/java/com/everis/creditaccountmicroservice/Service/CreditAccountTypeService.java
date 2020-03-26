package com.everis.creditaccountmicroservice.Service;

import com.everis.creditaccountmicroservice.Document.CreditAccountType;
import com.everis.creditaccountmicroservice.ServiceDTO.Request.CreditAccountTypeRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CreditAccountTypeService {

    Mono<CreditAccountType> create(CreditAccountType creditAccountType);
    Mono<CreditAccountType> update(String id, CreditAccountTypeRequest creditAccountTypeRequest);
    Flux<CreditAccountType> readAll();
    Mono<CreditAccountType> delete(String id);
    Mono<CreditAccountType> getOne(String id);
}
