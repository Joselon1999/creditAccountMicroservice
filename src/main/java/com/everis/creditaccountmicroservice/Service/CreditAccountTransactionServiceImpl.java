package com.everis.creditaccountmicroservice.Service;

import com.everis.creditaccountmicroservice.Document.CreditAccountTransaction;
import com.everis.creditaccountmicroservice.Repository.CreditAccountTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CreditAccountTransactionServiceImpl implements CreditAccountTransactionService {

    @Autowired
    CreditAccountTransactionRepository creditAccountTransactionRepository;
    @Override
    public Mono<CreditAccountTransaction> create(CreditAccountTransaction creditAccountTransaction) {
        return creditAccountTransactionRepository.save(creditAccountTransaction);
    }

    @Override
    public Mono<CreditAccountTransaction> update(CreditAccountTransaction creditAccountTransaction) {
        CreditAccountTransaction newTransaction = new CreditAccountTransaction();
            newTransaction.setIdCliente(creditAccountTransaction.getIdCliente());
            newTransaction.setSerialNumber(creditAccountTransaction.getSerialNumber());
            newTransaction.setTransferenceType("CREATING BUSINESS ACCOUNT");
            newTransaction.setTransferenceAmount(creditAccountTransaction.getTransferenceAmount());
            newTransaction.setTotalAmount(creditAccountTransaction.getTotalAmount());
            return creditAccountTransactionRepository.save(newTransaction);

    }

    @Override
    public Flux<CreditAccountTransaction> read() {
        return creditAccountTransactionRepository.findAll();
    }
}
