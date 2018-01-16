package com.kylemoore;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

public class SlowOperation implements Runnable {
  private static final Logger LOGGER = Logging.getLogger(SlowOperation.class);
  
  @Override
  public void run() {
    LOGGER.quiet("Starting slow task");
//    gw.lang.reflect.gs.GosuClassPathThing.init(); //TODO not even necessary to explicitly call this?
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      LOGGER.quiet("Finished slow task");
    }
  }

}
