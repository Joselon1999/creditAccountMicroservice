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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
        creditAccount.setAmmount(0);
        creditAccount.setCreditAccountType(CreditAccountType.builder()
                .id(bankAccountTypeMono.map(creditAccountType -> {
                    return creditAccountType.getId();
                }).block())
                .name(bankAccountTypeMono.map(creditAccountType -> {
                    return creditAccountType.getName();
                }).block())
                .build());
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
        return creditAccountRepository.findById(id)
                .filter(bankAccount ->bankAccount.getAmmount()+creditAccountTransaction.getTransferenceAmount()>=0)
                .switchIfEmpty(Mono.error(new Exception("Monto a retirar supera a monto actual")))
                .flatMap(bankAccount -> {
                    bankAccount.setAmmount(bankAccount.getAmmount()+creditAccountTransaction.getTransferenceAmount());
                    CreditAccountTransaction newTransaction = new CreditAccountTransaction();
                    newTransaction.setIdCliente(creditAccountTransaction.getIdCliente());
                    newTransaction.setSerialNumber(creditAccountTransaction.getSerialNumber());
                    newTransaction.setTransferenceType("TRANSFERENCE");
                    newTransaction.setTransferenceAmount(creditAccountTransaction.getTransferenceAmount());
                    newTransaction.setTotalAmount(bankAccount.getAmmount());
                    creditAccountTransactionRepository.save(newTransaction).subscribe();
                    System.out.println("MONTO INGRESADO:    "+newTransaction.getTransferenceAmount());
                    System.out.println("MONTO TOTAL ACTUAL: "+newTransaction.getTotalAmount());
                    return creditAccountRepository.save(bankAccount);
                });
    }
    @Override
    public Mono<CreditAccount> reciveTranference(CreditPaymentRequest creditPaymentRequest) {
        return creditAccountRepository.findById(creditPaymentRequest.getIdCreditAccount())
                .filter(bankAccount ->bankAccount.getAmmount()-creditPaymentRequest.getAmmount()>=0)
                .switchIfEmpty(Mono.error(new Exception("Monto a retirar supera a monto actual")))
                .flatMap(bankAccount -> {
                    bankAccount.setAmmount(bankAccount.getAmmount()-creditPaymentRequest.getAmmount());
                    CreditAccountTransaction newTransaction = new CreditAccountTransaction();
                    newTransaction.setIdCliente(bankAccount.getClientId());
                    newTransaction.setSerialNumber(bankAccount.getSerialNumber());
                    newTransaction.setTransferenceType("TRANSFERENCE FROM BANK ACCOUNT");
                    newTransaction.setTransferenceAmount(creditPaymentRequest.getAmmount());
                    newTransaction.setTotalAmount(bankAccount.getAmmount());
                    creditAccountTransactionRepository.save(newTransaction).subscribe();
                    System.out.println("MONTO INGRESADO:    "+newTransaction.getTransferenceAmount());
                    System.out.println("MONTO TOTAL ACTUAL: "+newTransaction.getTotalAmount());
                    return creditAccountRepository.save(bankAccount);
                });
    }
}
