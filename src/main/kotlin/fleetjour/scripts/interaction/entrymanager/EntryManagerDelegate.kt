package fleetjour.scripts.interaction.entrymanager

import com.fs.starfarer.api.campaign.CustomUIPanelPlugin
import com.fs.starfarer.api.campaign.CustomVisualDialogDelegate
import com.fs.starfarer.api.campaign.CustomVisualDialogDelegate.DialogCallbacks
import com.fs.starfarer.api.campaign.InteractionDialogAPI
import com.fs.starfarer.api.ui.CustomPanelAPI

/**
 * @author Ontheheavens
 * @since  16.02.2023
 */

class EntryManagerDelegate(
    private val plugin: EntryManagerPlugin,
    private val dialog: InteractionDialogAPI) : CustomVisualDialogDelegate {

    private var callbacks: DialogCallbacks? = null

    override fun init(panel: CustomPanelAPI?, callbacks: DialogCallbacks?) {
        this.callbacks = callbacks
        plugin.initialize(panel, callbacks, dialog)
    }

    override fun getCustomPanelPlugin(): CustomUIPanelPlugin {
        return plugin
    }

    override fun getNoiseAlpha(): Float {
        return 0f
    }

    override fun advance(amount: Float) {}

    override fun reportDismissed(option: Int) {}
}