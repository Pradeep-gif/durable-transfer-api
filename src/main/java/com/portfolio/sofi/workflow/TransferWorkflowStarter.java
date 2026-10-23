package com.portfolio.sofi.workflow;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class TransferWorkflowStarter {
    private final io.temporal.client.WorkflowClient client;
    private final String taskQueue;

    public TransferWorkflowStarter(io.temporal.client.WorkflowClient client,
                                   @org.springframework.beans.factory.annotation.Value("${app.temporal.task-queue:transfer-queue}") String taskQueue) {
        this.client = client;
        this.taskQueue = taskQueue;
    }

    public void start(UUID transferId) {
        TransferWorkflow workflow = client.newWorkflowStub(TransferWorkflow.class,
                io.temporal.client.WorkflowOptions.newBuilder()
                        .setWorkflowId("transfer-" + transferId)
                        .setTaskQueue(taskQueue)
                        .build());
        io.temporal.client.WorkflowClient.start(workflow::processTransfer, transferId.toString());
    }
}
