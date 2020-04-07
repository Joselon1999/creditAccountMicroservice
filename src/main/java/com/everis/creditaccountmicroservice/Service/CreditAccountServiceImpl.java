package com.everis.creditaccountmicroservice.Service;

import com.everis.creditaccountmicroservice.Document.CreditAccount;
import com.everis.creditaccountmicroservice.Document.CreditAccountTransaction;
import com.everis.creditaccountmicroservice.Document.CreditAccountType;
import com.everis.creditaccountmicroservice.Repository.CreditAccountRepository;
import com.everis.creditaccountmicroservice.Repository.CreditAccountTransactionRepository;
import com.everis.creditaccountmicroservice.Repository.CreditAccountTypeRepository;
import com.everis.creditaccountmicroservice.ServiceDTO.Request.AddCredditAccountRequest;
import com.everis.creditaccountmicroservice.ServiceDTO.Request.CreditPaymentRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class CreditAccountServiceImpl implements CreditAccountService {

    @Autowired
    CreditAccountRepository creditAccountRepository;
    @Autowired
    CreditAccountTypeRepository creditAccountTypeRepository;
    @Autowired
    CreditAccountTransactionRepository creditAccountTransactionRepository;

    private Boolean exist(String id) {

        //MAP OUTSIDE ON COLLECTION CLIENTS
        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("clientId", id);
        ResponseEntity<Boolean> responseEntity = new RestTemplate().
                getForEntity("http://localhost:8001/api/clients/exist/{clientId}",
                        Boolean.class, uriVariables);
        Boolean isPresent = responseEntity.getBody();
        return isPresent;
    }


    @Override
    public Mono<CreditAccount> create(AddCredditAccountRequest addCredditAccountRequest) {

        Mono<CreditAccountType> bankAccountTypeMono =
                creditAccountTypeRepository.findById(addCredditAccountRequest.getIdCreditAccountType())
                        .switchIfEmpty(Mono.error(new Exception("TIPO DE CUENTA BANCARIA ERRONEO")));

        CreditAccount creditAccount = new CreditAccount();
        BeanUtils.copyProperties(addCredditAccountRequest, creditAccount);
        creditAccount.setClientId(addCredditAccountRequest.getClientId());
        creditAccount.setSerialNumber(addCredditAccountRequest.getSerialNumber());
        creditAccount.setDni(addCredditAccountRequest.getDni());
        creditAccount.setLimit(addCredditAccountRequest.getLimit());
        creditAccount.setAmmount(addCredditAccountRequest.getAmmount());
        creditAccount.setExpireDate(addCredditAccountRequest.getExpireDate());
        creditAccount.setCreationDate(new Date());
        creditAccount.setCreditAccountType(CreditAccountType.builder()
                .id(bankAccountTypeMono.map(creditAccountType -> {
                    return creditAccountType.getId();
                }).block())
                .name(bankAccountTypeMono.map(creditAccountType -> {
                    return creditAccountType.getName();
                }).block())
                .build());

        //TODO pasar esto a funcional y .filter
        if (creditAccount.getExpireDate().compareTo(new Date()) < 0) {
            return Mono.error(new Exception("FECHA IMPOSIBLE DE SELECCIONAR"));
        }
        if (validateDebt(creditAccount.getClientId()).block()){
            return Mono.error(new Exception("CLIENTE TIENE DEUDA PENDIENTE"));
        }
        if (exist(creditAccount.getClientId())) {
            return creditAccountRepository.save(creditAccount);
        } else {
            return Mono.error(new Exception("CLIENTE NO EXISTE"));
        }
    }

    @Override
    public Mono<CreditAccount> update(String id, AddCredditAccountRequest addCredditAccountRequest) {
        return creditAccountRepository.findById(id).flatMap(client -> {
            client.setSerialNumber(addCredditAccountRequest.getSerialNumber());
            //client.setType(addCredditAccountRequest.getType());       ->  No lo pongo porque no se deberia cambiar
            client.setDni(addCredditAccountRequest.getDni());
            client.setLimit(addCredditAccountRequest.getLimit());
            client.setClientId(addCredditAccountRequest.getClientId());
            client.setExpireDate(addCredditAccountRequest.getExpireDate());
            if (client.getExpireDate().compareTo(new Date()) < 0) {
                return Mono.error(new Exception("FECHA IMPOSIBLE DE SELECCIONAR"));
            }
            return creditAccountRepository.save(client);
        });

    }

    @Override
    public Flux<CreditAccount> readAll(String clientId) {
        if (exist(clientId)) {
            return creditAccountRepository.findAllByClientId(clientId);
        } else {
            return Flux.error(new Exception("ID CLIENTE INCORRECTO"));
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

    @Override
    public Mono<CreditAccount> tranference(String id, CreditAccountTransaction creditAccountTransaction) {
        try {
        return creditAccountRepository.findById(id)
                .filter(bankAccount -> bankAccount.getAmmount() + creditAccountTransaction.getTransferenceAmount() >= 0)
                .switchIfEmpty(Mono.error(new Exception("Monto a retirar supera a monto actual")))
                .flatMap(bankAccount -> {
                    bankAccount.setAmmount(bankAccount.getAmmount() + creditAccountTransaction.getTransferenceAmount());
                    CreditAccountTransaction newTransaction = new CreditAccountTransaction();
                    newTransaction.setIdCliente(bankAccount.getClientId());
                    newTransaction.setSerialNumber(bankAccount.getSerialNumber());
                    newTransaction.setTransferenceType("TRANSFERENCE");
                    newTransaction.setTransferenceAmount(creditAccountTransaction.getTransferenceAmount());
                    newTransaction.setTotalAmount(bankAccount.getAmmount());
                    creditAccountTransactionRepository.save(newTransaction).subscribe();
                    System.out.println("MONTO INGRESADO:    " + newTransaction.getTransferenceAmount());
                    System.out.println("MONTO TOTAL ACTUAL: " + newTransaction.getTotalAmount());
                    return creditAccountRepository.save(bankAccount);
                });
    }catch (Exception e){
        return Mono.error(e);
    }
    }

    @Override
    public Mono<CreditAccount> reciveTranference(CreditPaymentRequest creditPaymentRequest) {
        try {
            return creditAccountRepository.findById(creditPaymentRequest.getIdCreditAccount())
                    .filter(bankAccount -> bankAccount.getAmmount() - creditPaymentRequest.getAmmount() >= 0)
                    .switchIfEmpty(Mono.error(new Exception("Monto a retirar supera a monto actual")))
                    .flatMap(bankAccount -> {
                        bankAccount.setAmmount(bankAccount.getAmmount() - creditPaymentRequest.getAmmount());
                        CreditAccountTransaction newTransaction = new CreditAccountTransaction();
                        newTransaction.setIdCliente(bankAccount.getClientId());
                        newTransaction.setSerialNumber(bankAccount.getSerialNumber());
                        newTransaction.setTransferenceType("TRANSFERENCE FROM BANK ACCOUNT");
                        newTransaction.setTransferenceAmount(creditPaymentRequest.getAmmount());
                        newTransaction.setTotalAmount(bankAccount.getAmmount());
                        creditAccountTransactionRepository.save(newTransaction).subscribe();
                        System.out.println("MONTO INGRESADO:    " + newTransaction.getTransferenceAmount());
                        System.out.println("MONTO TOTAL ACTUAL: " + newTransaction.getTotalAmount());
                        return creditAccountRepository.save(bankAccount);
                    });
        }catch (Exception e){
            return Mono.error(e);
        }
    }

    @Override
    public Mono<Boolean> validateDebt(String clientId) {

        return creditAccountRepository.findAllByClientId(clientId)
                .filter(creditAccount -> creditAccount.getAmmount()>0)
                .filter(creditAccount -> creditAccount.getExpireDate().compareTo(new Date())<0)
                .count()
                .map(aLong -> {
                    System.out.println(aLong);
                    return aLong > 0;
                });
    }

    @Override
    public Flux<CreditAccount> readAllByBankInTime(String bankId, int days) {
        long DAY_IN_MS = 1000 * 60 * 60 * 24;
        Date begin = new Date(System.currentTimeMillis() - (days * DAY_IN_MS));
        System.out.println("DIAS: "+days);
        return creditAccountRepository.findByBankIdAndCreationDateBetween(bankId,begin,new Date());
    }

}
