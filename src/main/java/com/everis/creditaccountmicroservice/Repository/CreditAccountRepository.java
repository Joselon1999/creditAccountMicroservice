package com.everis.creditaccountmicroservice.Repository;

import com.everis.creditaccountmicroservice.Document.CreditAccount;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CreditAccountRepository extends ReactiveMongoRepository<CreditAccount,String> {
    Flux<CreditAccount> findAllByClientId(String clientId);
    Mono<CreditAccount> findByClientIdExists(String clientId);
}
