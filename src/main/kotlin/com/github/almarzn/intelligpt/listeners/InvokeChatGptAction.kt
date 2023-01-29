package com.github.almarzn.intelligpt.listeners

import com.github.almarzn.intelligpt.api.ChatGptClient
import com.github.almarzn.intelligpt.api.NoApiKeyConfiguredException
import com.github.almarzn.intelligpt.services.IntelliGptNotifier
import com.intellij.openapi.project.Project
import java.util.concurrent.Callable

class InvokeChatGptAction(
        private val project: Project,
        private val prompt: String
): Callable<String> {
    override fun call(): String? {
        return try {
            ChatGptClient().generateResponse(prompt);
        } catch (e: NoApiKeyConfiguredException) {
            IntelliGptNotifier.notifyMissingApi(project)

            null
        } catch (e: Exception) {
            IntelliGptNotifier.notifyError(project, "Cannot fetch response from OpenAI: " + e.message)

            null
        }
    }
}