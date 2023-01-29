package com.github.almarzn.intelligpt.services

import com.intellij.icons.AllIcons
import com.intellij.ide.actions.ShowSettingsUtilImpl
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project

object IntelliGptNotifier {
    fun notifyError(project: Project?,
                    content: String?) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup("IntelliGpt")
                .createNotification(content!!, NotificationType.ERROR)
                .notify(project)
    }
    fun notifyMissingApi(project: Project?, notificationType: NotificationType = NotificationType.ERROR) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup("IntelliGpt")
                .createNotification("ChatGPT API Key is not configured.", notificationType)
                .addAction(object: DumbAwareAction({ "Configure Plugin" }, AllIcons.Nodes.Editorconfig) {

                    override fun actionPerformed(event: AnActionEvent) {
                        ShowSettingsUtilImpl.showSettingsDialog(project, "org.intellij.sdk.settings.AppSettingsConfigurable", "")
                    }
                })
                .notify(project)
    }
}