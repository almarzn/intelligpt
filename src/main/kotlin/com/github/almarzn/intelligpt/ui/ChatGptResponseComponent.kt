package com.github.almarzn.intelligpt.ui

import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBLoadingPanel
import java.awt.BorderLayout
import javax.swing.JLabel
import com.intellij.ide.plugins.MultiPanel
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.ui.PopupBorder
import com.intellij.util.ui.JBEmptyBorder
import com.intellij.util.ui.JBHtmlEditorKit
import com.intellij.util.ui.JBUI
import java.awt.Dimension
import javax.swing.JComponent
import javax.swing.JEditorPane
import javax.swing.JPanel

class ChatGptResponseComponent(project: Project, targetComponent: JComponent) : JBLoadingPanel(BorderLayout(),
                                                                            { panel -> ResponseLoadingDecorator(panel, project) }) {
  private var NO_PREVIEW_LABEL = JLabel("No response").also { setupLabel(it) }
  private var LOADING_LABEL = JLabel("Loading response...").also { setupLabel(it) }

  var html: String = ""
    var actionGroup: DefaultActionGroup = DefaultActionGroup()

  internal val MAX_HEIGHT = 300
  internal val MIN_WIDTH = 300

  val multiPanel: MultiPanel = object : MultiPanel() {
    override fun create(key: Int): JComponent {
      return when (key) {
        NO_PREVIEW -> NO_PREVIEW_LABEL
        LOADING_PREVIEW -> LOADING_LABEL
        else -> {
            val editor = object : JEditorPane() {
              var prefHeight: Int? = null

              override fun getPreferredSize(): Dimension {
                if (prefHeight == null) {
                  val pos = modelToView2D(document.endPosition.offset.coerceAtLeast(1) - 1)
                  if (pos != null) {
                    prefHeight = pos.maxY.toInt() + 10
                  }
                }
                return Dimension(MIN_WIDTH, prefHeight ?: Integer.MAX_VALUE)
              }
            }
            editor.editorKit = JBHtmlEditorKit()
            editor.text = html
            editor.size = Dimension(MIN_WIDTH, Integer.MAX_VALUE)
            val panel = JPanel(BorderLayout())
            panel.background = editor.background
            panel.add(editor, BorderLayout.CENTER)
            val actionToolbar = ActionManager.getInstance().createActionToolbar(
                    "chat_gpt_response",
                    actionGroup,
                    true
            )
            actionToolbar.targetComponent = targetComponent
            panel.add(actionToolbar.component, BorderLayout.SOUTH)
            panel.border = JBEmptyBorder(5)
            return panel
          }

        }
      }
  }

  init {
    add(multiPanel)
    border = PopupBorder.Factory.create(true, true)
    setLoadingText("Loading response...")
  }

  companion object {
    const val NO_PREVIEW = -1
    const val LOADING_PREVIEW = -2

    private fun setupLabel(label: JLabel) {
      label.border = JBUI.Borders.empty(3)
      label.background = EditorColorsManager.getInstance().globalScheme.defaultBackground
    }
  }
}