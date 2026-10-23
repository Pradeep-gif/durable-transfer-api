package com.portfolio.transaction.workflow;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.WorkerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TemporalConfiguration {
    @Bean
    WorkflowServiceStubs workflowServiceStubs(@Value("${app.temporal.target:localhost:7233}") String target) {
        return WorkflowServiceStubs.newServiceStubs(
                WorkflowServiceStubsOptions.newBuilder().setTarget(target).build());
    }

    @Bean
    WorkflowClient workflowClient(WorkflowServiceStubs stubs) {
        return WorkflowClient.newInstance(stubs, WorkflowClientOptions.getDefaultInstance());
    }

    @Bean(destroyMethod = "shutdown")
    WorkerFactory workerFactory(WorkflowClient client, TransferActivityImpl activity,
                                @Value("${app.temporal.task-queue:transfer-queue}") String taskQueue) {
        WorkerFactory factory = WorkerFactory.newInstance(client);
        var worker = factory.newWorker(taskQueue);
        worker.registerWorkflowImplementationTypes(TransferWorkflowImpl.class);
        worker.registerActivitiesImplementations(activity);
        factory.start();
        return factory;
    }
}
