package com.kylemoore.dummy

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Unroll

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

@Unroll
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
    
//    configurations {
//      gosu
//    }
//    
//    dependencies {
//      gosu 'org.gosu-lang.gosu:gosu-core:1.14.7'
//    }
    
    apply plugin: 'com.kylemoore.dummy'
  """

  @Rule
  final TemporaryFolder testProjectDir = new TemporaryFolder()

//  @Rule
//  final TemporaryFolder testKitDir = new TemporaryFolder()
  
  def setup() {
    testProjectDir.create()
//    testKitDir.create()
    buildScript = testProjectDir.newFile('build.gradle')
  }
  
  def 'apply dummy plugin and run SlowRunningTask in long-lived process [Gradle #gradleVersion]'() {
    given:
    buildScript << buildScriptContent

    when:
    GradleRunner runner = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
//            .withTestKitDir(testKitDir.root)
            .withPluginClasspath()
            .withArguments('slowTask', '-is')
            .withDebug(true) //FIXME this causes exit 143 and system.err for Gosu typesystem shutdown
            .forwardOutput()
//            .withGradleVersion(gradleVersion)

    BuildResult result = runner.build()

    then:
    result.output.contains('Finished slow task')
    result.task(":slowTask").outcome == SUCCESS
    
    where:
    gradleVersion << ['4.4.1'] //['4.2.1', '4.4.1']
  }

}
