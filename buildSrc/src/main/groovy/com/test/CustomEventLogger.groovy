package com.test

import org.gradle.BuildListener
import org.gradle.BuildResult
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import org.gradle.api.tasks.TaskState



//自定义的log输出
class CustomEventLogger implements BuildListener, TaskExecutionListener {
    @Override
    void buildStarted(Gradle gradle) {
        println "buildStarted"
    }

    @Override
    void settingsEvaluated(Settings settings) {
        println "settingsEvaluated"
    }

    @Override
    void projectsLoaded(Gradle gradle) {
        println "projectsLoaded"
    }

    @Override
    void projectsEvaluated(Gradle gradle) {
        println "projectsEvaluated"
    }

    public void beforeExecute(Task task) {
        println "beforeExecute:[$task.name] 工程名:${task.project.name}"
    }

    public void afterExecute(Task task, TaskState state) {
        println "afterExecute:[$task.name] 工程名:${task.project.name}"
    }

    public void buildFinished(BuildResult result) {
        println 'buildFinished'
        if (result.failure != null) {
            result.failure.printStackTrace()
        }
    }
}