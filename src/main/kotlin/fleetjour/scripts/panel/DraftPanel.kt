package fleetjour.scripts.panel

import com.fs.starfarer.api.ui.Alignment
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.Fonts
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc

/**
 * @author Ontheheavens
 * @since  09.02.2023
 */

object DraftPanel {

    fun create(assembly: WriterPanelAssembly, panelWidth: Float, panelHeight: Float): CustomPanelAPI {
        val width = panelWidth - Constants.DRAFTPANEL_WIDTH_OFFSET
        val height = panelHeight - Constants.CONTENT_HEIGHT_OFFSET
        val draftPanel = assembly.mainPanel.createCustomPanel(width, height, null)
        val header: TooltipMakerAPI = this.createPanelHeader(draftPanel, width)
        draftPanel.addUIElement(header).inTL(0f, 0f)
        val textSize = assembly.parent.draftParagraphs.size
        if (textSize > 0) {
            val draftContainer: TooltipMakerAPI = this.createDraftContainer(assembly, draftPanel, width, height)
            draftPanel.addUIElement(draftContainer).belowLeft(header, 9f)
            this.highlightSelectedParagraph(assembly)
            assembly.removeButton.isEnabled = true
            assembly.appendButton.isEnabled = true
            assembly.deleteButton.isEnabled = true
        } else {
            val placeholderContainer: TooltipMakerAPI = this.createPlaceholder(draftPanel, width, height)
            draftPanel.addUIElement(placeholderContainer).inTMid(height / 2)
            assembly.removeButton.isEnabled = false
            assembly.appendButton.isEnabled = false
            assembly.deleteButton.isEnabled = false
        }
        if (textSize < 2) {
            assembly.upButton.isEnabled = false
            assembly.downButton.isEnabled = false
        } else {
            assembly.upButton.isEnabled = assembly.parent.selectedParagraphIndex > 0
            assembly.downButton.isEnabled = assembly.parent.selectedParagraphIndex +
                    1 < assembly.parent.draftParagraphs.size
        }
        return draftPanel
    }

    private fun createPanelHeader(draftPanel: CustomPanelAPI, width: Float): TooltipMakerAPI {
        val headerContainer = draftPanel.createUIElement(width, 18f, false)
        headerContainer.addSectionHeading("Draft", Alignment.MID, 0f)
        return headerContainer
    }

    private fun createPlaceholder(draftPanel: CustomPanelAPI, panelWidth: Float,
                                  panelHeight: Float): TooltipMakerAPI {
        val placeholderContainer = draftPanel.createUIElement(panelWidth, panelHeight, true)
        placeholderContainer.setParaFont(Fonts.ORBITRON_24AABOLD)
        val placeholder = placeholderContainer.addPara("Description empty", Misc.getGrayColor(), 0f)
        placeholder.setAlignment(Alignment.MID)
        return placeholderContainer
    }

    private fun createDraftContainer(assembly: WriterPanelAssembly, draftPanel: CustomPanelAPI,
                                     panelWidth: Float, panelHeight: Float): TooltipMakerAPI {
        val draftContainer = draftPanel.createUIElement(panelWidth, panelHeight, true)
        assembly.parent.draftParagraphs.forEach { paragraph ->
            val paragraphContainer: CustomPanelAPI = paragraph.create(draftPanel)
            draftContainer.addCustom(paragraphContainer, 2f)
            (paragraph.contentHeight).minus(32f).let { draftContainer.addSpacer(it) }
        }
        return draftContainer
    }

    fun highlightSelectedParagraph(assembly: WriterPanelAssembly) {
        val selectedParagraph = assembly.parent.draftParagraphs[assembly.parent.selectedParagraphIndex]
        selectedParagraph.selectButton.isChecked = true
        selectedParagraph.selectButton.highlight()
    }

}