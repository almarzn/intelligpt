package com.github.almarzn.intelligpt.ui

import com.intellij.openapi.editor.colors.CodeInsightColors
import com.intellij.openapi.editor.colors.EditorColorsScheme
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.ui.Gray
import com.intellij.ui.JBColor
import com.intellij.util.ui.JBUI
import javax.swing.BoxLayout
import javax.swing.JPanel

internal class IntentionPreviewEditorsPanel(private val editors: List<EditorEx>) : JPanel() {
  init {
    layout = BoxLayout(this, BoxLayout.Y_AXIS)
    editors.forEachIndexed { index, editor ->
      add(editor.component)
      if (index < editors.size - 1) {
        add(createSeparatorLine(editor.colorsScheme))
      }
    }
  }

  private fun createSeparatorLine(colorsScheme: EditorColorsScheme): JPanel {
    var color = colorsScheme.getColor(CodeInsightColors.METHOD_SEPARATORS_COLOR)
    color = color ?: JBColor.namedColor("Group.separatorColor", JBColor(Gray.xCD, Gray.x51))

    return JBUI.Panels.simplePanel().withBorder(JBUI.Borders.customLine(color, 1, 0, 0, 0))
  }
}