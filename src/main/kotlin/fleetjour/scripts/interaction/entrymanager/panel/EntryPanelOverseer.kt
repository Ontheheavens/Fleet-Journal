package fleetjour.scripts.interaction.entrymanager.panel

import com.fs.starfarer.api.campaign.CustomUIPanelPlugin
import com.fs.starfarer.api.campaign.CustomVisualDialogDelegate.DialogCallbacks
import com.fs.starfarer.api.campaign.InteractionDialogAPI
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.IntelUIAPI

/**
 * @author Ontheheavens
 * @since 30.12.2022
 */

object EntryPanelOverseer {

    var panel: CustomPanelAPI? = null
    private var callbacks: DialogCallbacks? = null
    private var dialog: InteractionDialogAPI? = null
    var plugin: CustomUIPanelPlugin? = null
    private var intelUI: IntelUIAPI? = null

    fun initialize(panel: CustomPanelAPI, callbacks: DialogCallbacks,
                   dialog: InteractionDialogAPI, ui: IntelUIAPI, plugin: CustomUIPanelPlugin) {
        EntryPanelOverseer.panel = panel
        EntryPanelOverseer.callbacks = callbacks
        EntryPanelOverseer.dialog = dialog
        EntryPanelOverseer.plugin = plugin
        intelUI = ui
    }

    fun advance() {
        ButtonListener.checkIndex()
        if (EntriesSection.sectionRedrawQueued) {
            EntriesSection.renderEntries()
            EntriesSection.sectionRedrawQueued = false
        }
        if (EntryPanelAssembly.controlButtonsRedrawQueued) {
            EntryPanelAssembly.renderControlSection()
            EntryPanelAssembly.controlButtonsRedrawQueued = false
        }
    }

    fun dismissPanel() {
        // Dismissing callbacks dialog means just closing the custom panel.
        callbacks?.dismissDialog()
        ButtonListener.clearIndex()
        // Dismissing dialog itself means exit from initialization dialog too.
        dialog?.dismiss()
        intelUI?.updateIntelList()
        intelUI?.recreateIntelUI()
    }

}