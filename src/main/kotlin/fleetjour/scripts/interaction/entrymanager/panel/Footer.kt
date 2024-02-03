package fleetjour.scripts.interaction.entrymanager.panel

import com.fs.starfarer.api.ui.*
import com.fs.starfarer.api.util.Misc
import fleetjour.scripts.interaction.entrymanager.panel.PanelConstants.PANEL_CONTENT_OFFSET
import fleetjour.scripts.panel.Common

/**
 * @author Ontheheavens
 * @since  17.02.2023
 */

object Footer {

    fun create(panel: CustomPanelAPI): TooltipMakerAPI? {
        val contentWidth = PanelConstants.CONTENT_WIDTH
        val footer = panel.createUIElement(contentWidth, 10f, false)
        footer.setForceProcessInput(true)
        val footerLine: ButtonAPI = Common.addLine(footer, contentWidth -
                (PanelConstants.RIGHTSIDE_BUTTONS_WIDTH + 21f))
        footerLine.position.setYAlignOffset(17f)
        footerLine.position.setXAlignOffset(-4f)
        createExitButton(footer)
        panel.addUIElement(footer).inBR(2f, 2f)
        return footer
    }

    private fun createExitButton(footer: TooltipMakerAPI) {
        footer.setButtonFontVictor14()
        val exitButtonInstance = footer.addButton("DISMISS", null, Misc.getBasePlayerColor(),
            Misc.getDarkPlayerColor(), Alignment.MID, CutStyle.TL_BR,
            PanelConstants.RIGHTSIDE_BUTTONS_WIDTH + 6f, 25f, 2f)
        val offset: Float = PANEL_CONTENT_OFFSET
        exitButtonInstance.position.inBR(offset, offset)
        val buttonWrapper = object : InteractiveButton(exitButtonInstance, Type.STANDARD) {
            override fun applyEffect() {
                EntryPanelOverseer.dismissPanel()
            }
        }
        ButtonListener.getIndex()?.add(buttonWrapper)
    }


}