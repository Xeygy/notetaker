package com.github.xeygy.notetaker.services

import com.intellij.openapi.project.Project
import com.github.xeygy.notetaker.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
