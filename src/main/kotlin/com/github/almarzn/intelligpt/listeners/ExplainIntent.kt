package com.github.almarzn.intelligpt.listeners

import com.github.almarzn.intelligpt.action.InsertAsCommentAction
import com.github.almarzn.intelligpt.ui.ChatGptResponseComponent
import com.github.almarzn.intelligpt.ui.ChatGptResponseComponent.Companion.LOADING_PREVIEW
import com.github.almarzn.intelligpt.ui.ChatGptResponseComponent.Companion.NO_PREVIEW
import com.intellij.codeInsight.generation.actions.CommentByLineCommentAction
import com.intellij.codeInsight.intention.FileModifier
import com.intellij.codeInsight.intention.impl.BaseIntentionAction
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actions.SplitLineAction
import com.intellij.openapi.editor.actions.StartNewLineBeforeAction
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.util.NlsSafe
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.ui.ScreenUtil
import com.intellij.util.concurrency.AppExecutorUtil
import org.apache.commons.lang.StringEscapeUtils
import java.awt.Dimension
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent

private const val MAX_WIDTH = 600

class ExplainIntent : BaseIntentionAction() {
    override fun getFamilyName() = "Explain using Chat GPT"

    override fun getText() = "ChatGPT: Explain code"

    //    This function is used to check if the editor selection model currently has a non-blank selected text. It,
    //    returns true if the editor selection model has a non-blank selected text, or false if otherwise.
    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?) = editor
            ?.selectionModel
            ?.selectedText
            ?.isNotBlank() ?: false

    override fun startInWriteAction() = false

    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        val selectedText = editor!!.selectionModel.selectedText

        val (popup, component) = buildPopup(editor, project)

        component.multiPanel.select(LOADING_PREVIEW, true)

        component.startLoading()

        ReadAction.nonBlocking(InvokeChatGptAction(
                project,
                "Explain the purpose of the following : \n$selectedText"
        ))
                .expireWith(popup)
                .coalesceBy(this)
                .finishOnUiThread(ModalityState.defaultModalityState()) { response ->
                    if (response == null) {
                        component.select(popup, 1, html = "Could not talk to ChatGPT.")

                        return@finishOnUiThread
                    }

                    component.select(popup, 1, html = StringEscapeUtils.escapeHtml(response))

                    component.actionGroup.add(InsertAsCommentAction(popup, file, editor, response))
                }.submit(AppExecutorUtil.getAppExecutorService())
    }

    private fun buildPopup(editor: Editor, project: Project): Pair<JBPopup, ChatGptResponseComponent> {
        val component = ChatGptResponseComponent(project, editor.component)

        component.multiPanel.select(LOADING_PREVIEW, true)

        val popup = JBPopupFactory.getInstance().createComponentPopupBuilder(component, null).setShowBorder(false).createPopup()

        component.addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent?) {
                var size = popup.size
                val key = component.multiPanel.key
                if (key != NO_PREVIEW) {
                    size = Dimension(size.width.coerceAtLeast(MAX_WIDTH), size.height)
                }
                popup.content.preferredSize = size
                popup.size = size
            }
        })

        popup.showInBestPositionFor(editor)

        return Pair(popup, component)
    }


    private fun ChatGptResponseComponent.select(popup: JBPopup, index: Int, editors: List<EditorEx> = emptyList(), @NlsSafe html: String = "") {
        val component = this

        component.stopLoading()
        component.html = html
        component.multiPanel.select(index, true)

        val size = component.preferredSize
        val location = popup.locationOnScreen
        val screen = ScreenUtil.getScreenRectangle(location)

        if (screen != null) {
            val delta = screen.width + screen.x - location.x
            if (size.width > delta) {
                size.width = delta
            }
        }

        popup.pack(true, true)
    }

}