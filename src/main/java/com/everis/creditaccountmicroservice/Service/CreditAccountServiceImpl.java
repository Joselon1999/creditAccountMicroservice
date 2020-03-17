package com.everis.creditaccountmicroservice.Service;

import com.everis.creditaccountmicroservice.Document.CreditAccount;
import com.everis.creditaccountmicroservice.Repository.CreditAccountRepository;
import com.everis.creditaccountmicroservice.ServiceDTO.Request.AddCredditAccountRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class CreditAccountServiceImpl implements CreditAccountService{

    @Autowired
    CreditAccountRepository creditAccountRepository;

    private Boolean exist(String id){

        Mono<Boolean> temp = Mono.just(true);

        //MAP INSIDE COLLECTION BANK ACCOUNT
        /*Mono<Boolean> inBankAccount = bankAccountService.isPresent(id);
        if (inBankAccount.equals(temp)){
            System.out.println("Existe  ");
        }else {
            System.out.println("No existe  ");
        }*/

        //MAP OUTSIDE ON COLLECTION CLIENTS
        Map<String,String> uriVariables= new HashMap<>();
        uriVariables.put("clientId",id);
        ResponseEntity<Boolean> responseEntity =new RestTemplate().
                getForEntity("http://localhost:8001/api/clients/exist/{clientId}",
                        Boolean.class,uriVariables);
        Boolean isPresent = responseEntity.getBody();
        return isPresent;
    }

    @Override
    public Mono<CreditAccount> create(AddCredditAccountRequest addCredditAccountRequest) {
        CreditAccount creditAccount = new CreditAccount();
        BeanUtils.copyProperties(addCredditAccountRequest,creditAccount);
        creditAccount.setClientId(addCredditAccountRequest.getClientId());
        creditAccount.setSerialNumber(addCredditAccountRequest.getSerialNumber());
        creditAccount.setType(addCredditAccountRequest.getType());
        if (exist(creditAccount.getClientId())){
            return creditAccountRepository.save(creditAccount);
        }else {
            return Mono.empty();
        }
    }

    @Override
    public Mono<CreditAccount> update(String id, AddCredditAccountRequest addCredditAccountRequest) {
        return creditAccountRepository.findById(id).flatMap(client -> {
            client.setSerialNumber(addCredditAccountRequest.getSerialNumber());
            client.setType(addCredditAccountRequest.getType());
            client.setClientId(addCredditAccountRequest.getClientId());
            return creditAccountRepository.save(client);
        });

    }

    @Override
    public Flux<CreditAccount> readAll(String clientId) {
        if (exist(clientId)) {
            return creditAccountRepository.findAllByClientId(clientId);
        }else{
            return null;
        }
    }

    @Override
    public Mono<CreditAccount> delete(String bankId) {
        return getOne(bankId).switchIfEmpty(Mono.empty()).filter(Objects::nonNull)
                .flatMap(bankAccount -> creditAccountRepository.delete(bankAccount).then(Mono.just(bankAccount)));
    }

    @Override
    public Mono<CreditAccount> getOne(String id) {
        return creditAccountRepository.findById(id);
    }

    @Override
    public Mono<CreditAccount> isPresent(String clientId) {
        return creditAccountRepository.findByClientIdExists(clientId);
    }
}
