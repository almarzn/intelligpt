package com.github.almarzn.intelligpt.listeners

import com.github.almarzn.intelligpt.ChatGptClient
import com.intellij.codeInsight.intention.impl.BaseIntentionAction
import com.intellij.openapi.application.ApplicationManager.getApplication
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.profile.codeInspection.ui.DescriptionEditorPane
import com.intellij.profile.codeInspection.ui.readHTML
import com.intellij.profile.codeInspection.ui.toHTML
import com.intellij.psi.PsiFile
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.components.panels.StatelessCardLayout
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.MathUtil
import com.intellij.util.ui.JBDimension
import com.intellij.util.ui.JBUI
import org.jetbrains.concurrency.runAsync
import javax.swing.JEditorPane
import javax.swing.JScrollPane

class ExplainIntent : BaseIntentionAction() {

    val token = "eyJhbGciOiJkaXIiLCJlbmMiOiJBMjU2R0NNIn0..qG-kGzoWM_NCfHFZ.MKr9HEdHI0RdXWRAqZ2QHdOAwgi1Rxc9TUbvXfPUts5if9IQoppHyTSsMxowGtfWI-ka8f4HnaFlsVpT4PNl1G0cTxfhsEF6gNnKxOiyMRSS4j24gc5kyPQQxSmIJ9maoiofJVtEXmoj1LlMJU33-qnPbdnP-KTAdsxhfVgWMxzEFr3VZUzmlAV6pMbbSNTY19Qo40wPICbzrncUm5IAz49ldFtqiQ6W95hXTRwPfXCoErQFWFH8ta2WkkpAI_bmixOztXX8X3ofqFccLza7lrJrSQEsuYaPC0cTglHglqUiVLfeKP5qnzosB1VMjHVoSTThxVGCipgGqtNnE41rv-U2lETLQXTe2v-vS9N6-r0_EQqpmKZy8XI_gWaXJODfV11pGED3sTWSSsZ5uZwCuE8MHdRB8ExsEKsC_6K7n9fBSavudSY6yMY9QCBCI3pcESoT5SNb_OsKEVQNKHOnyvaZLTEtlyDg8JdwHMSHu7mobcCgLrreFNgeC4g2EjMJS64s2jBTvqPGBUlYcXcE5Bwc3X-xUXKY-l-cNqd5XwJZ2DymIs-kBDpb1FfFnder6qa1glNqleBb35_f7xvG-esOo1ixMX8APerrxLX5TMeuqREUas4QBm30hSVdCt5lxUKttJNFBYwxFK2QOZGRPzDLeAJb6YWBmQActZHVMl9-6Nph6h8ZVj3urdIJvvOekSptqx8XGjwaoc_ltkMRw2DUI_aej92hw75py8sGEV9xyBg_uw6i7vp2Nr38XSepPdReKW_VpgO74lFU_c0i1xPHP_C-V39THWGeZyv_r9h1ftv4a0fCbOBKdNoUdo8rVnKqTSdCOi7aWPpIUYG-ttMMqY1KqKOcX6DoAUaYts4hy2vPkHR9KDIztgppRvzrBMhcpYGf1MXeqvFENqLDjW0qAo1ydFRdyiF3fnllIgJw_TuzOXSDaTI7o9Spo8Yk9_2A1N6KmK6ErM1F8MOlkX3qRR2E__RjWcgVLYM1MqBBKWsnORXGFQHVulsD4Ld9TniusmWYh7zqo9Kq__lKejCxNbco_z_7S_kmVbq6KWXPQAGpKbHEm5t8hrrGUNqUwr_mpOFMVleujC3Om9rCzQgeKv14n_SwSeoQ7v3INJgJ0qu6G3j262Ml4zJmNgL2-7lwm2m3NwgL80YT8_7zytnIlThCKOJybKFMCKKproRNSxZKzm8Uy5dan6uLgCdGC8qDt0-0m9g31qV3kA8s4UCzTMFS4cc9ZWNKarQbanrJPGiiO6tzzYpwd46-xFFG_KX6qESzpeY0hIZDR443Dzwc3YqtXERiZdmy6jj0dk9JHq9jrPcz5vFIHKSGsa2xBqcoQwYAPwHK58RxGUSSR4_EJa6hRtX8gQV57RudACfZUu-vnXsnXRaET0z6GVEl4SR4cwhzUHUF43p50dfx-e78m-uSxZget_o_gnEb6ptBf6k4kqJT9ot3vMKm-dHGI2cKgMsKv9JhkKhbNinHgsHQzt9cvTLDf_iTgf3yd1__coxpgVXZdX4mG4WvaYABfJqVfG5xsm4FIJt4QKQlZPadJmhoO_5x2ET9ossWBX_V5w-6-BOwTazI-ty82ozK5j48OA0DjAPxgQLw-YfFEduaqcSGU7aKz1nYjq5ZhI7ctS3RZagnBKSQHF-44JygoV8sNUM-GEWbwA34NLbNB3PeN1RpBSsGoDshpxj9QFcWE0B8lL64BMH3yW4OnyBj9eyqsRoYxuYI61HJ4BhS-YRel7HJBpG8K4DJEZmrSiE-CxvSvtDfPTi63qAOY1R_b70JHqsMK1AUidmJHP0Il--V3qeS-3xpjojN9td6vatlC6Qa_uNWBbgHNXGo_C0TTDjAiANy4QFs1eq_uIYQCMc9kR6UadVt_58oTpP1Jz2fPD3Xlzt-gSRXTyhRCwALwKbQbNMY6iKtUtHDLjkGUSAnQ_R4eADuqfI43xB7nX4GLlMED8EGHIxb8BE7mlpEIe7ch8Xn-ZEiKfDB50WULc1e4gDRwlAVwen38cXjwPRaaM07pv1AwnzRNvVnmqZJW-8WKP-htgoV45x94DXQbzDvuKiM2fs2Wml0Hiwt5oOIOilhH-5wsXGI8XVfzWLAtlIl1u0m1c2O1InzGsvsYhz3P8heIRSwV3gRbrHdfO-Pd4hL54YL1N-leK92XsmyTUN80FHEi-keiKhiEbCWd-u-vz-LIYPzHZkTB1_E47fPX4sfqPaOZ-oVYcYEaZUFq_jJQvRoPY_JoUhO-I-2A5XAKJyMaA.wxGYoq9k4pQGavV3XxOW5A"

    override fun getFamilyName() = "Explain ChatGPT"

    override fun getText() = "ChatGPT: Explain code"

    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?) = true
    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        val selectedText = editor!!.selectionModel.selectedText

        val loading = showLoader(editor)

        runAsync {
            val response = ChatGptClient().generateResponse("Explain the purpose of the following code: \n$selectedText")

            getApplication().invokeLater {
                loading.dispose()

                val description = DescriptionEditorPane()

                description.margin = JBUI.insets(16)
                val scrollPane = ScrollPaneFactory.createScrollPane(description, true)
                scrollPane.alignmentX = 0f
                val createPopup = JBPopupFactory.getInstance().createComponentPopupBuilder(StatelessCardLayout.wrap(scrollPane), description)
                        .createPopup()


//                scrollPane.preferredSize = JBUI.size(500, getHeight(pane, scrollPane, 500, 0))

                description.readHTML(description.toHTML(response.trim(), false))
                createPopup.show(JBPopupFactory.getInstance().guessBestPopupLocation(editor))
            }

        }
    }

    private val MIN_DEFAULT = JBDimension(300, 36)
    private val MAX_DEFAULT = JBDimension(950, 500)

    private fun getHeight(myEditorPane: JEditorPane, myScrollPane: JScrollPane, width: Int, extraHeight: Int): Int {
        myEditorPane.setBounds(0, 0, width, MAX_DEFAULT.height)
        val preferredSize = myEditorPane.preferredSize

        val height = preferredSize.height + extraHeight
        val scrollBar = myScrollPane.horizontalScrollBar
        val reservedForScrollBar = if (width < preferredSize.width && scrollBar.isOpaque) scrollBar.preferredSize.height else 0
        val insets = myScrollPane.insets
        return MathUtil.clamp(height, MIN_DEFAULT.height, MAX_DEFAULT.height) + insets.top + insets.bottom + reservedForScrollBar
    }

    private fun showLoader(editor: Editor): JBPopup {
        return JBPopupFactory.getInstance().createComponentPopupBuilder(panel {
            row {
                label("Asking ChatGPT to do your work...")
            }
        }, null)
                .createPopup()
                .apply { showInBestPositionFor(editor) }
    }
}