package com.kylemoore.dummy

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class DummyPluginIntegrationTest extends Specification {

  File buildScript
          
  String buildScriptContent = 
  """
    plugins {
        id 'com.kylemoore.dummy' apply false
    }
    
    repositories {
      jcenter()
    }
    
    apply plugin: 'com.kylemoore.dummy'
  """

  @Rule
  final TemporaryFolder testProjectDir = new TemporaryFolder()

  def setup() {
    testProjectDir.create()
    buildScript = testProjectDir.newFile('build.gradle')
  }
  
  def 'apply dummy plugin and run SlowRunningTask in long-lived process'() {
    given:
    buildScript << buildScriptContent

    when:
    GradleRunner runner = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withPluginClasspath()
            .withArguments('slowTask', '-is')
            .withDebug(true) //TODO why does this cause exit 143 and system.err output?
            .forwardOutput()

    BuildResult result = runner.build()

    then:
    result.output.contains('Finished slow task')
    result.task(":slowTask").outcome == SUCCESS
  }

}
