package com.test

import org.gradle.api.Plugin
import org.gradle.api.Project

public class TestPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.extensions.create('aaa', MyExtension)
        project.task('testaaa') << {
            println project.aaa.message
            println("name------->"+project.path)
//            project.gradle.addListener(new TimeListener())
        }
    }
}