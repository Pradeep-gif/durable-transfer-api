package com.portfolio.transaction.workflow;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface TransferActivities {
    @ActivityMethod
    void execute(String transferId);
}
