package com.github.almarzn.intelligpt

import com.github.almarzn.intelligpt.services.AppSettingsState.Companion.instance
import com.github.almarzn.intelligpt.ui.AppSettingsComponent
import com.intellij.openapi.options.Configurable
import org.jetbrains.annotations.Nls
import javax.swing.JComponent

/**
 * Provides controller functionality for application settings.
 */
class AppSettingsConfigurable : Configurable {
    private var mySettingsComponent: AppSettingsComponent? = null

    // A default constructor with no arguments is required because this implementation
    // is registered as an applicationConfigurable EP
    override fun getDisplayName(): @Nls(capitalization = Nls.Capitalization.Title) String? {
        return "IntelliGPT"
    }

    override fun getPreferredFocusedComponent(): JComponent? {
        return mySettingsComponent!!.preferredFocusedComponent
    }

    override fun createComponent(): JComponent? {
        mySettingsComponent = AppSettingsComponent()
        mySettingsComponent!!.apiKey = instance.apiKey ?: ""
        mySettingsComponent!!.phrase = instance.phrase ?: "Explain the purpose of the following : "
        return mySettingsComponent!!.panel
    }

    override fun isModified(): Boolean {
        val settings = instance
        return mySettingsComponent!!.apiKey != settings.apiKey || mySettingsComponent!!.phrase != settings.phrase
    }

    override fun apply() {
        val settings = instance
        settings.apiKey = mySettingsComponent!!.apiKey
        settings.phrase = mySettingsComponent!!.phrase
    }

    override fun reset() {
    }

    override fun disposeUIResources() {
        mySettingsComponent = null
    }
}