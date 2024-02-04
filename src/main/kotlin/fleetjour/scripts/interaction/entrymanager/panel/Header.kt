package fleetjour.scripts.interaction.entrymanager.panel

import com.fs.starfarer.api.ui.*

/**
 * @author Ontheheavens
 * @since  17.02.2023
 */

object Header {

    fun create(panel: CustomPanelAPI): TooltipMakerAPI? {
        val header = panel.createUIElement(EntryManagerConstants.CONTENT_WIDTH - 4f, 18f, false)
        header.addSectionHeading("Manage entries", Alignment.MID, 2f)
        panel.addUIElement(header).inTMid(EntryManagerConstants.PANEL_CONTENT_OFFSET + 2f)
        return header
    }

}