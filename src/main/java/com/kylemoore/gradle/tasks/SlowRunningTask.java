package com.kylemoore.gradle.tasks;

import com.kylemoore.SlowOperation;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.TaskAction;
import org.gradle.workers.IsolationMode;
import org.gradle.workers.WorkerConfiguration;
import org.gradle.workers.WorkerExecutor;

import javax.inject.Inject;

public class SlowRunningTask extends DefaultTask implements Runnable {
  private static final Logger LOGGER = Logging.getLogger(SlowRunningTask.class);

  private final WorkerExecutor _workerExecutor;

  @Inject
  public SlowRunningTask(WorkerExecutor workerExecutor) {
    _workerExecutor = workerExecutor;
  }
  
  @TaskAction
  @Override
  public void run() {
    LOGGER.quiet("Requesting SlowOperation using IsolationMode.PROCESS");

    _workerExecutor.submit(SlowOperation.class, ( WorkerConfiguration wc ) -> {
      wc.setDisplayName("Daemonized SlowOperation");
      wc.setIsolationMode(IsolationMode.PROCESS);
    });

    LOGGER.quiet("Done requesting SlowOperation");
  }

}
