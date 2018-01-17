When 'upgrading' gradle-gosu-plugin to leverage the new Gradle Worker API, I am concerned about strange system.err dumps during test shutdown.

The tests run successfully ~, but I have a hunch this is related to the TestKit API getting confused when it spawns multiple client workers~.

**UPDATE:** The cause of the below stack trace seems to be caused by invoking a build from o.g.t.r.GradleRunner with debug set to true.
If that TestKit build then executes a task which spawns a worker (IsolationMode.PROCESS), the worker does not shutdown cleanly.
_Removing `.withDebug(true)` from the GradleRunner configuration makes the issue disappear._

So, this has no apparent effect on the plugin under test, but the ST was scary and I worry that it could lead other plugin authors down the wrong path.

Example ST:
```
org.gradle.process.internal.ExecException: Process 'Gradle Worker Daemon 1' finished with non-zero exit value 143
        at org.gradle.process.internal.DefaultExecHandle$ExecResultImpl.assertNormalExitValue(DefaultExecHandle.java:382)
        at org.gradle.process.internal.worker.DefaultWorkerProcess.waitForStop(DefaultWorkerProcess.java:190)
        at org.gradle.process.internal.worker.DefaultWorkerProcessBuilder$MemoryRequestingWorkerProcess.waitForStop(DefaultWorkerProcessBuilder.java:228)
        at org.gradle.process.internal.worker.DefaultMultiRequestWorkerProcessBuilder$1.invoke(DefaultMultiRequestWorkerProcessBuilder.java:144)
        at com.sun.proxy.$Proxy160.stop(Unknown Source)
        at org.gradle.workers.internal.WorkerDaemonClient.stop(WorkerDaemonClient.java:61)
        at org.gradle.internal.concurrent.CompositeStoppable.stop(CompositeStoppable.java:98)
        at org.gradle.workers.internal.WorkerDaemonClientsManager.stopWorkers(WorkerDaemonClientsManager.java:139)
        at org.gradle.workers.internal.WorkerDaemonClientsManager.stop(WorkerDaemonClientsManager.java:108)
        at org.gradle.internal.concurrent.CompositeStoppable.stop(CompositeStoppable.java:98)
        at org.gradle.internal.service.DefaultServiceRegistry$ManagedObjectProvider.stop(DefaultServiceRegistry.java:592)
        at org.gradle.internal.concurrent.CompositeStoppable.stop(CompositeStoppable.java:98)
        at org.gradle.internal.service.DefaultServiceRegistry$ManagedObjectProvider.stop(DefaultServiceRegistry.java:592)
        at org.gradle.internal.concurrent.CompositeStoppable.stop(CompositeStoppable.java:98)
        at org.gradle.internal.service.DefaultServiceRegistry$ManagedObjectProvider.stop(DefaultServiceRegistry.java:592)
        at org.gradle.internal.concurrent.CompositeStoppable.stop(CompositeStoppable.java:98)
        at org.gradle.internal.service.DefaultServiceRegistry$OwnServices.stop(DefaultServiceRegistry.java:518)
        at org.gradle.internal.service.DefaultServiceRegistry$CachingProvider.stop(DefaultServiceRegistry.java:1017)
        at org.gradle.internal.concurrent.CompositeStoppable.stop(CompositeStoppable.java:98)
        at org.gradle.internal.service.DefaultServiceRegistry$CompositeProvider.stop(DefaultServiceRegistry.java:1078)
        at org.gradle.internal.concurrent.CompositeStoppable.stop(CompositeStoppable.java:98)
        at org.gradle.internal.service.DefaultServiceRegistry.close(DefaultServiceRegistry.java:265)
        at org.gradle.internal.concurrent.CompositeStoppable$2.stop(CompositeStoppable.java:83)
        at org.gradle.internal.concurrent.CompositeStoppable.stop(CompositeStoppable.java:98)
        at org.gradle.internal.service.scopes.DefaultGradleUserHomeScopeServiceRegistry.close(DefaultGradleUserHomeScopeServiceRegistry.java:62)
        at org.gradle.internal.concurrent.CompositeStoppable$2.stop(CompositeStoppable.java:83)
        at org.gradle.internal.concurrent.CompositeStoppable.stop(CompositeStoppable.java:98)
        at org.gradle.internal.service.DefaultServiceRegistry$ManagedObjectProvider.stop(DefaultServiceRegistry.java:592)
        at org.gradle.internal.concurrent.CompositeStoppable.stop(CompositeStoppable.java:98)
        at org.gradle.internal.service.DefaultServiceRegistry$OwnServices.stop(DefaultServiceRegistry.java:518)
        at org.gradle.internal.service.DefaultServiceRegistry$CachingProvider.stop(DefaultServiceRegistry.java:1017)
        at org.gradle.internal.concurrent.CompositeStoppable.stop(CompositeStoppable.java:98)
        at org.gradle.internal.service.DefaultServiceRegistry$CompositeProvider.stop(DefaultServiceRegistry.java:1078)
        at org.gradle.internal.concurrent.CompositeStoppable.stop(CompositeStoppable.java:98)
        at org.gradle.internal.service.DefaultServiceRegistry.close(DefaultServiceRegistry.java:265)
        at org.gradle.internal.concurrent.CompositeStoppable$2.stop(CompositeStoppable.java:83)
        at org.gradle.internal.concurrent.CompositeStoppable.stop(CompositeStoppable.java:98)
        at org.gradle.tooling.internal.provider.DefaultConnection.shutdown(DefaultConnection.java:154)
        at org.gradle.tooling.internal.consumer.connection.ShutdownAwareConsumerConnection.stop(ShutdownAwareConsumerConnection.java:35)
        at org.gradle.tooling.internal.consumer.connection.ParameterValidatingConsumerConnection.stop(ParameterValidatingConsumerConnection.java:35)
        at org.gradle.internal.concurrent.CompositeStoppable.stop(CompositeStoppable.java:98)
        at org.gradle.tooling.internal.consumer.loader.CachingToolingImplementationLoader.close(CachingToolingImplementationLoader.java:53)
        at org.gradle.internal.concurrent.CompositeStoppable$2.stop(CompositeStoppable.java:83)
        at org.gradle.internal.concurrent.CompositeStoppable.stop(CompositeStoppable.java:98)
        at org.gradle.tooling.internal.consumer.loader.SynchronizedToolingImplementationLoader.close(SynchronizedToolingImplementationLoader.java:63)
        at org.gradle.internal.concurrent.CompositeStoppable$2.stop(CompositeStoppable.java:83)
        at org.gradle.internal.concurrent.CompositeStoppable.stop(CompositeStoppable.java:98)
        at org.gradle.internal.service.DefaultServiceRegistry$ManagedObjectProvider.stop(DefaultServiceRegistry.java:592)
        at org.gradle.internal.concurrent.CompositeStoppable.stop(CompositeStoppable.java:98)
        at org.gradle.internal.service.DefaultServiceRegistry$OwnServices.stop(DefaultServiceRegistry.java:518)
        at org.gradle.internal.service.DefaultServiceRegistry$CachingProvider.stop(DefaultServiceRegistry.java:1017)
        at org.gradle.internal.concurrent.CompositeStoppable.stop(CompositeStoppable.java:98)
        at org.gradle.internal.service.DefaultServiceRegistry.close(DefaultServiceRegistry.java:265)
        at org.gradle.tooling.internal.consumer.ConnectorServices.close(ConnectorServices.java:49)
        at org.gradle.tooling.internal.consumer.DefaultGradleConnector.close(DefaultGradleConnector.java:57)
        at org.gradle.testkit.runner.internal.ToolingApiGradleExecutor$1.run(ToolingApiGradleExecutor.java:75)
        at java.lang.Thread.run(Thread.java:748)

```
