package fleetjour.scripts.interaction.entrymanager

import com.fs.starfarer.api.ui.IntelUIAPI
import fleetjour.scripts.EntryWriter
import fleetjour.scripts.interaction.entrymanager.panel.EntryManagerConstants
import fleetjour.scripts.interaction.shared.AbstractPanelDialog

/**
 * @author Ontheheavens
 * @since  16.02.2023
 */

class EntryManagerDialog(ui: IntelUIAPI, private val journal: EntryWriter) : AbstractPanelDialog(ui) {

    override fun showCustomDialog() {
        dialog.showCustomVisualDialog(
            EntryManagerConstants.PANEL_WIDTH,
            EntryManagerConstants.PANEL_HEIGHT, EntryManagerDelegate(EntryManagerPlugin(ui, journal), this.dialog)
        )
    }

}