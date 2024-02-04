package fleetjour.scripts.interaction.entrymanager

import com.fs.starfarer.api.campaign.CustomVisualDialogDelegate.DialogCallbacks
import com.fs.starfarer.api.campaign.InteractionDialogAPI
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.IntelUIAPI
import fleetjour.scripts.EntryWriter
import fleetjour.scripts.interaction.entrymanager.panel.EntryPanelAssembly
import fleetjour.scripts.interaction.entrymanager.panel.EntryPanelOverseer
import fleetjour.scripts.interaction.shared.AbstractPanelPlugin

/**
 * @author Ontheheavens
 * @since  16.02.2023
 */

class EntryManagerPlugin(ui: IntelUIAPI, private val journal: EntryWriter) : AbstractPanelPlugin(ui) {

    override fun assemblePanel(
        panel: CustomPanelAPI,
        callbacks: DialogCallbacks,
        dialog: InteractionDialogAPI,
        ui: IntelUIAPI
    ) {
        EntryPanelOverseer.initialize(panel, callbacks, dialog, ui, this, journal)
        EntryPanelAssembly.assemble(panel)
    }

    override fun doEveryFrame(amount: Float) {
        EntryPanelOverseer.advance()
    }

    override fun dismissPanelImpl() {
        EntryPanelOverseer.dismissPanel()
    }


}