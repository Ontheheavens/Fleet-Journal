package fleetjour.scripts.interaction.entrymanager

import com.fs.starfarer.api.ui.IntelUIAPI
import fleetjour.scripts.interaction.entrymanager.panel.PanelConstants
import fleetjour.scripts.interaction.shared.AbstractPanelDialog

/**
 * @author Ontheheavens
 * @since  16.02.2023
 */

class EntryManagerDialog(ui: IntelUIAPI) : AbstractPanelDialog(ui) {

    override fun showCustomDialog() {
        dialog.showCustomVisualDialog(
            PanelConstants.PANEL_WIDTH,
            PanelConstants.PANEL_HEIGHT, EntryManagerDelegate(EntryManagerPlugin(ui), this.dialog)
        )
    }

}