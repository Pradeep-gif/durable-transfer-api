package com.portfolio.transaction.workflow;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;
import java.time.Duration;

public class TransferWorkflowImpl implements TransferWorkflow {
    private final TransferActivities activities = Workflow.newActivityStub(TransferActivities.class,
            ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(20)).build());

    @Override
    public void processTransfer(String transferId) {
        activities.execute(transferId);
    }
}
