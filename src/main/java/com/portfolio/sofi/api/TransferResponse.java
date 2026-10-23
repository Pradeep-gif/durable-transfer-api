package com.portfolio.sofi.api;

import com.portfolio.sofi.domain.TransferStatus;
import com.portfolio.sofi.persistence.Transfer;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransferResponse(UUID id, String sourceAccountId, String destinationAccountId,
                              BigDecimal amount, String currency, TransferStatus status, Instant createdAt) {
    public static TransferResponse from(Transfer transfer) {
        return new TransferResponse(transfer.getId(), transfer.getSourceAccountId(),
                transfer.getDestinationAccountId(), transfer.getAmount(), transfer.getCurrency(),
                transfer.getStatus(), transfer.getCreatedAt());
    }
}
