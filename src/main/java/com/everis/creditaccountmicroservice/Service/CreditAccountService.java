package com.everis.creditaccountmicroservice.Service;

import com.everis.creditaccountmicroservice.Document.CreditAccount;
import com.everis.creditaccountmicroservice.Document.CreditAccountTransaction;
import com.everis.creditaccountmicroservice.ServiceDTO.Request.AddCredditAccountRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CreditAccountService {
    Mono<CreditAccount> create(AddCredditAccountRequest addCredditAccountRequest);
    Mono<CreditAccount> update(String id,AddCredditAccountRequest addCredditAccountRequest);
    Flux<CreditAccount> readAll(String clientId);
    Mono<CreditAccount> delete(String bankId);
    Mono<CreditAccount> getOne(String id);
    Mono<CreditAccount> isPresent(String clientId);
    Mono<CreditAccount> tranference(String id, CreditAccountTransaction creditAccountTransaction);
}
