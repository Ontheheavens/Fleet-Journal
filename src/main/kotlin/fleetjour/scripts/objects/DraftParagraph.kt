package fleetjour.scripts.objects

import com.fs.starfarer.api.ui.*
import com.fs.starfarer.api.util.Misc
import fleetjour.scripts.panel.WriterPanelAssembly
import java.awt.Color

/**
 * @author Ontheheavens
 * @since  08.02.2023
 */

class DraftParagraph(var content: String, val id: Long) {

    var contentHeight: Float = 0f

    @Transient
    lateinit var selectButton: ButtonAPI

    fun create(parent: CustomPanelAPI): CustomPanelAPI {
        val width = parent.position.width
        val container: CustomPanelAPI = parent.createCustomPanel(width, 40f, null)
        val labelTooltip = container.createUIElement(width, 15f, false)
        val textWidth = width - 16f
        labelTooltip.textWidthOverride = textWidth
        val contentLabel = labelTooltip.addPara(content, 0f)
        contentHeight = contentLabel.position.height
        contentLabel.position.inTL(1f, 2f)
        contentLabel.opacity = 0f
        this.addInteractiveButton(labelTooltip, contentLabel)
        val frontContentLabel = labelTooltip.addPara(content, 0f)
        frontContentLabel.position.inTL(1f, 2f)
        container.addUIElement(labelTooltip).inTL(0f, 2f)
        return container
    }

    private fun addInteractiveButton(labelTooltip: TooltipMakerAPI, contentLabel: LabelAPI) {
        val selectButtonData = WriterPanelAssembly.Buttons.SELECT_PARAGRAPH.toString() + "" + id
        val backgroundButtonColor = Misc.scaleColor(Misc.getBrightPlayerColor(), 0.5f)
        val transparentColor: Color = Misc.scaleAlpha(backgroundButtonColor, 0f)
        selectButton = labelTooltip.addAreaCheckbox(
            "", selectButtonData,
            backgroundButtonColor, transparentColor,
            backgroundButtonColor, contentLabel.position.width + 14f, contentLabel.position.height + 14f, 0f
        )
        selectButton.position.inTL(-7f, -5f)
        selectButton.highlightBrightness = 1f
    }

}