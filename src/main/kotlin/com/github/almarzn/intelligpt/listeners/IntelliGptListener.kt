package com.github.almarzn.intelligpt.listeners

import com.github.almarzn.intelligpt.AppSettingsConfigurable
import com.github.almarzn.intelligpt.services.AppSettingsState
import com.github.almarzn.intelligpt.services.IntelliGptNotifier
import com.intellij.notification.NotificationType
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener

internal class IntelliGptListener : ProjectManagerListener {

    override fun projectOpened(project: Project) {
        if (AppSettingsState.instance.apiKey.isNullOrBlank()) {
            IntelliGptNotifier.notifyMissingApi(project, NotificationType.INFORMATION)
        }
    }
}
