package com.everis.creditaccountmicroservice.Service;

import com.everis.creditaccountmicroservice.Document.CreditAccountTransaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CreditAccountTransactionService {
    Mono<CreditAccountTransaction> create(CreditAccountTransaction bankAccountTransaction);
    Mono<CreditAccountTransaction> update(CreditAccountTransaction bankAccountTransaction);
    Flux<CreditAccountTransaction> read();
}
