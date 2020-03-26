package com.everis.creditaccountmicroservice.Repository;

import com.everis.creditaccountmicroservice.Document.CreditAccountType;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditAccountTypeRepository extends ReactiveMongoRepository<CreditAccountType,String> {
}
