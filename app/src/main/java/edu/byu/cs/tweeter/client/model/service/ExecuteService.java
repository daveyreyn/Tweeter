package edu.byu.cs.tweeter.client.model.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.backgroundTask.BackgroundTask;

public class ExecuteService {


    public void execute(BackgroundTask task){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(task);
    }
}
