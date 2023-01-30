package com.github.almarzn.intelligpt.action

import com.intellij.codeInsight.generation.actions.CommentByLineCommentAction
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actions.SplitLineAction
import com.intellij.openapi.editor.actions.StartNewLineBeforeAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile
import com.intellij.psi.codeStyle.CodeStyleManager

class InsertAsCommentAction(
        private val popup: JBPopup,
        private val file: PsiFile?,
        private val editor: Editor,
        private val response: String
) : AnAction("Insert As Comment") {
    override fun actionPerformed(e: AnActionEvent) {
        popup.dispose()

        WriteCommandAction.runWriteCommandAction(editor.project) {
            if (file != null) {
                editor.apply { caretModel.removeSecondaryCarets() }
                        .apply { caretModel.moveToOffset(editor.selectionModel.selectionStart) }


                StartNewLineBeforeAction().actionPerformed(editor, e.dataContext)

                CommentByLineCommentAction().actionPerformed(e)

                editor.document.insertString(editor.selectionModel.selectionStart, response.trim())

                val line = editor.document.getLineNumber(editor.caretModel.offset)

                CodeStyleManager.getInstance(editor.project!!)
                        .reformatText(file, mutableListOf(editor.getLineRange(line)))

                splitLineUntilNotNeeded(editor, line, e)

            }
        }
    }

    private fun splitLineUntilNotNeeded(editor: Editor, line: Int, e: AnActionEvent) {
        val margin = editor.settings.getRightMargin(editor.project)

        val lineRange = editor.getLineRange(line)

        val text = editor.document.getText(lineRange)

        if (margin >= text.length) {
            return
        }

        val lastWhitespace = text.slice(0..margin).lastIndexOf(' ')

        if (lastWhitespace <= 0) {
            return
        }

        editor.caretModel.moveToOffset(editor.document.getLineStartOffset(line) + lastWhitespace)

        SplitLineAction().actionPerformed(e)

        return splitLineUntilNotNeeded(editor, line + 1, e)
    }

    private fun Editor.getLineRange(line: Int): TextRange {
        return this.document.let {
            TextRange(it.getLineStartOffset(line), it.getLineEndOffset(line))
        }
    }

    override fun displayTextInToolbar(): Boolean {
        return true;
    }

}