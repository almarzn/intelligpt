package com.github.almarzn.intelligpt.ui

import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel

/**
 * Supports creating and managing a [JPanel] for the Settings Dialog.
 */
class AppSettingsComponent {
    val panel: JPanel
    private lateinit var apiKeyTextField: JBTextField
    private lateinit var phraseTextField: JBTextField

    init {
        panel = panel {
             row {
                 label("API key")
                 apiKeyTextField = textField().component
             }
            row {
                label("Phrase")
                phraseTextField = textField().component
            }
            row {
                cell()
                browserLink("Generate an API Key", "https://beta.openai.com/account/api-keys")
            }
        }
    }
    
    var phrase: String
        get() = phraseTextField.text
        set(newText) {
            phraseTextField.text = newText
        }

    val preferredFocusedComponent: JComponent
        get() = apiKeyTextField
    var apiKey: String
        get() = apiKeyTextField.text
        set(newText) {
            apiKeyTextField.text = newText
        }
}