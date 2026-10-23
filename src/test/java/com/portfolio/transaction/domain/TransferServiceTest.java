package com.portfolio.transaction.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.portfolio.transaction.api.TransferRequest;
import com.portfolio.transaction.persistence.Transfer;
import com.portfolio.transaction.persistence.TransferRepository;
import com.portfolio.transaction.workflow.TransferWorkflowStarter;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {
    @Mock TransferRepository repository;
    @Mock TransferWorkflowStarter workflowStarter;
    private TransferService service;

    @BeforeEach
    void setUp() { service = new TransferService(repository, workflowStarter); }

    @Test
    void createsTransferAndStartsWorkflow() {
        var request = new TransferRequest("acct-a", "acct-b", new BigDecimal("12.50"), "USD");
        when(repository.findByIdempotencyKey("req-1")).thenReturn(Optional.empty());
        when(repository.save(org.mockito.ArgumentMatchers.any(Transfer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Transfer result = service.create("req-1", request);

        assertEquals("PENDING", result.getStatus().name());
        assertEquals(new BigDecimal("12.50"), result.getAmount());
        verify(workflowStarter).start(result.getId());
    }

    @Test
    void returnsOriginalTransferForRepeatedIdempotencyKey() {
        var request = new TransferRequest("acct-a", "acct-b", new BigDecimal("12.50"), "USD");
        var existing = new Transfer("req-1", "acct-a", "acct-b", new BigDecimal("12.50"), "USD");
        when(repository.findByIdempotencyKey("req-1")).thenReturn(Optional.of(existing));

        assertEquals(existing, service.create("req-1", request));
        verify(repository, never()).save(org.mockito.ArgumentMatchers.any());
        verify(workflowStarter, never()).start(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void rejectsReusingIdempotencyKeyForDifferentRequest() {
        var request = new TransferRequest("acct-a", "acct-b", new BigDecimal("99.00"), "USD");
        var existing = new Transfer("req-1", "acct-a", "acct-b", new BigDecimal("12.50"), "USD");
        when(repository.findByIdempotencyKey("req-1")).thenReturn(Optional.of(existing));

        assertThrows(IdempotencyConflictException.class, () -> service.create("req-1", request));
        verify(repository, never()).save(org.mockito.ArgumentMatchers.any());
        verify(workflowStarter, never()).start(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void rejectsTransfersToSameAccount() {
        var request = new TransferRequest("acct-a", "acct-a", new BigDecimal("12.50"), "USD");
        assertThrows(IllegalArgumentException.class, () -> service.create("req-1", request));
        verify(repository, never()).save(org.mockito.ArgumentMatchers.any());
    }
}
