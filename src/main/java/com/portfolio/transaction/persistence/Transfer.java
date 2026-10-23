package com.portfolio.transaction.persistence;

import com.portfolio.transaction.domain.TransferStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transfers", uniqueConstraints = @UniqueConstraint(name = "uk_transfer_idempotency_key", columnNames = "idempotency_key"))
public class Transfer {
    @Id
    private UUID id;
    @Column(name = "idempotency_key", nullable = false, length = 100)
    private String idempotencyKey;
    @Column(name = "source_account_id", nullable = false, length = 80)
    private String sourceAccountId;
    @Column(name = "destination_account_id", nullable = false, length = 80)
    private String destinationAccountId;
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;
    @Column(nullable = false, length = 3)
    private String currency;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransferStatus status;
    @Column(nullable = false)
    private Instant createdAt;

    protected Transfer() { }

    public Transfer(String idempotencyKey, String sourceAccountId, String destinationAccountId,
                    BigDecimal amount, String currency) {
        this.id = UUID.randomUUID();
        this.idempotencyKey = idempotencyKey;
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.amount = amount;
        this.currency = currency;
        this.status = TransferStatus.PENDING;
        this.createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public String getSourceAccountId() { return sourceAccountId; }
    public String getDestinationAccountId() { return destinationAccountId; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public TransferStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public void complete() { this.status = TransferStatus.COMPLETED; }
    public void fail() { this.status = TransferStatus.FAILED; }
}
