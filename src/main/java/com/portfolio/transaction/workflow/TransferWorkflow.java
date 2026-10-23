package com.portfolio.transaction.workflow;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface TransferWorkflow {
    @WorkflowMethod
    void processTransfer(String transferId);
}
