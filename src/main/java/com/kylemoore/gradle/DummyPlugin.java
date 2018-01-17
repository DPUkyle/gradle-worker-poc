package com.kylemoore.gradle;

import com.kylemoore.gradle.tasks.SlowRunningTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class DummyPlugin implements Plugin<Project> {
  
  @Override
  public void apply( Project project ) {
    SlowRunningTask srt = project.getTasks().create("slowTask", SlowRunningTask.class);
  }
  
}
