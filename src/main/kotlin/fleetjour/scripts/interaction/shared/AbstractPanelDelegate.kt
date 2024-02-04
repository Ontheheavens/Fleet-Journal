package fleetjour.scripts.interaction.shared

import com.fs.starfarer.api.campaign.CustomUIPanelPlugin
import com.fs.starfarer.api.campaign.CustomVisualDialogDelegate
import com.fs.starfarer.api.campaign.InteractionDialogAPI
import com.fs.starfarer.api.ui.CustomPanelAPI

/**
 * @author Ontheheavens
 * @since 04.02.2024
 */
abstract class AbstractPanelDelegate (
    private val plugin: AbstractPanelPlugin,
    private val dialog: InteractionDialogAPI
) : CustomVisualDialogDelegate {

    private var callbacks: CustomVisualDialogDelegate.DialogCallbacks? = null

    override fun init(panel: CustomPanelAPI?, callbacks: CustomVisualDialogDelegate.DialogCallbacks?) {
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