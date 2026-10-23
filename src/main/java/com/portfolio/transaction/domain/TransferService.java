package com.portfolio.transaction.domain;

import com.portfolio.transaction.api.TransferNotFoundException;
import com.portfolio.transaction.api.TransferRequest;
import com.portfolio.transaction.persistence.Transfer;
import com.portfolio.transaction.persistence.TransferRepository;
import com.portfolio.transaction.workflow.TransferWorkflowStarter;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class TransferService {
    private final TransferRepository repository;
    private final TransferWorkflowStarter workflowStarter;

    public TransferService(TransferRepository repository, TransferWorkflowStarter workflowStarter) {
        this.repository = repository;
        this.workflowStarter = workflowStarter;
    }

    @Transactional
    public Transfer create(String idempotencyKey, TransferRequest request) {
        if (!StringUtils.hasText(idempotencyKey) || idempotencyKey.length() > 100) {
            throw new IllegalArgumentException("Idempotency-Key must contain 1 to 100 characters");
        }
        if (request.sourceAccountId().equals(request.destinationAccountId())) {
            throw new IllegalArgumentException("Source and destination accounts must differ");
        }
        var existing = repository.findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) {
            Transfer original = existing.get();
            if (!original.getSourceAccountId().equals(request.sourceAccountId())
                    || !original.getDestinationAccountId().equals(request.destinationAccountId())
                    || original.getAmount().compareTo(request.amount()) != 0
                    || !original.getCurrency().equals(request.currency())) {
                throw new IdempotencyConflictException(idempotencyKey);
            }
            return original;
        }

        Transfer transfer = repository.save(new Transfer(idempotencyKey, request.sourceAccountId(),
                request.destinationAccountId(), request.amount(), request.currency()));
        workflowStarter.start(transfer.getId());
        return transfer;
    }

    @Transactional(readOnly = true)
    public Transfer get(UUID id) {
        return repository.findById(id).orElseThrow(() -> new TransferNotFoundException(id));
    }
}
