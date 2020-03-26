package com.everis.creditaccountmicroservice.Service;

import com.everis.creditaccountmicroservice.Document.CreditAccountType;
import com.everis.creditaccountmicroservice.Repository.CreditAccountTypeRepository;
import com.everis.creditaccountmicroservice.ServiceDTO.Request.CreditAccountTypeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
public class CreditAccountTypeServiceImpl implements CreditAccountTypeService{

    @Autowired
    CreditAccountTypeRepository creditAccountTypeRepository;

    @Override
    public Mono<CreditAccountType> create(CreditAccountType creditAccountType) {
        return creditAccountTypeRepository.save(creditAccountType);
    }

    @Override
    public Mono<CreditAccountType> update(String id, CreditAccountTypeRequest creditAccountTypeRequest) {
        return creditAccountTypeRepository.findById(id).flatMap(creditAccount -> {
            creditAccount.setName(creditAccountTypeRequest.getName());
            return creditAccountTypeRepository.save(creditAccount);
        });
    }

    @Override
    public Flux<CreditAccountType> readAll() {
        return creditAccountTypeRepository.findAll();
    }

    @Override
    public Mono<CreditAccountType> delete(String id) {
        return getOne(id).switchIfEmpty(Mono.empty()).filter(Objects::nonNull)
                .flatMap(bankAccountType -> creditAccountTypeRepository.delete(bankAccountType).then(Mono.just(bankAccountType)));
    }

    @Override
    public Mono<CreditAccountType> getOne(String id) {
        return creditAccountTypeRepository.findById(id);
    }
}
