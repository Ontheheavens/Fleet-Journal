package fleetjour.scripts.interaction.panel

import com.fs.starfarer.api.ui.*

/**
 * @author Ontheheavens
 * @since  17.02.2023
 */

object Header {

    fun create(panel: CustomPanelAPI): TooltipMakerAPI? {
        val header = panel.createUIElement(PanelConstants.CONTENT_WIDTH - 4f, 18f, false)
        header.addSectionHeading("Manage entries", Alignment.MID, 2f)
        panel.addUIElement(header).inTMid(PanelConstants.PANEL_CONTENT_OFFSET + 2f)
        return header
    }

}