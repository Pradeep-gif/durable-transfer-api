package com.portfolio.sofi.workflow;

import com.portfolio.sofi.persistence.TransferRepository;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransferActivityImpl implements TransferActivities {
    private final TransferRepository repository;

    public TransferActivityImpl(TransferRepository repository) { this.repository = repository; }

    @Override
    @Transactional
    public void execute(String transferId) {
        var transfer = repository.findById(UUID.fromString(transferId))
                .orElseThrow(() -> new IllegalStateException("Transfer record missing: " + transferId));
        // Demo activity: replace with a provider call and ledger posting in a real system.
        transfer.complete();
    }
}
