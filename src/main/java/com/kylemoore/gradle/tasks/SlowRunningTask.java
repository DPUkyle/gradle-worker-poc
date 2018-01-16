package com.kylemoore.gradle.tasks;

import com.kylemoore.SlowOperation;
import org.gradle.api.DefaultTask;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.FileCollection;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.Classpath;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;
import org.gradle.workers.IsolationMode;
import org.gradle.workers.WorkerConfiguration;
import org.gradle.workers.WorkerExecutor;

import javax.inject.Inject;
import java.io.File;

public class SlowRunningTask extends DefaultTask implements Runnable {
  private static final Logger LOGGER = Logging.getLogger(SlowRunningTask.class);

  private FileCollection _classpath;
  
  private final WorkerExecutor _workerExecutor;

  public void setClasspath(FileCollection classpath) {
    _classpath = classpath;
  }
  
  @Classpath
  @Optional
  public FileCollection getClasspath() {
    return _classpath;
  }
  
  @Inject
  public SlowRunningTask(WorkerExecutor workerExecutor) {
    _workerExecutor = workerExecutor;
  }
  
  @TaskAction
  @Override
  public void run() {
    LOGGER.quiet("Requesting SlowOperation using IsolationMode.PROCESS");

//    for(File f : getClasspath()) {
//      LOGGER.quiet("Found cp file " + f.getAbsolutePath());
//    }
    
    _workerExecutor.submit(SlowOperation.class, ( WorkerConfiguration wc ) -> {
      wc.setDisplayName("Daemonized SlowOperation");
      //wc.setClasspath(getClasspath());
      wc.forkOptions(fo -> fo.setDebug(true));
      wc.setIsolationMode(IsolationMode.PROCESS);
    });

    LOGGER.quiet("Done requesting SlowOperation");
  }

}
