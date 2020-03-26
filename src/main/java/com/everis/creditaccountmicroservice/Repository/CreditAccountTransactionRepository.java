package com.everis.creditaccountmicroservice.Repository;

import com.everis.creditaccountmicroservice.Document.CreditAccountTransaction;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditAccountTransactionRepository extends ReactiveMongoRepository<CreditAccountTransaction,String> {
}
