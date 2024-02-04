package fleetjour.scripts.interaction.shared

import com.fs.starfarer.api.campaign.CustomUIPanelPlugin
import com.fs.starfarer.api.campaign.CustomVisualDialogDelegate
import com.fs.starfarer.api.campaign.InteractionDialogAPI
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.IntelUIAPI
import com.fs.starfarer.api.ui.PositionAPI
import org.lwjgl.input.Keyboard

/**
 * @author Ontheheavens
 * @since 04.02.2024
 */
abstract class AbstractPanelPlugin(val ui: IntelUIAPI) : CustomUIPanelPlugin {

    fun initialize(panel: CustomPanelAPI?, callbacks: CustomVisualDialogDelegate.DialogCallbacks?, dialog: InteractionDialogAPI?) {
        panel?: return
        callbacks?: return
        dialog?: return
        assemblePanel(panel, callbacks, dialog, ui)
    }

    abstract fun assemblePanel(panel: CustomPanelAPI,
                               callbacks: CustomVisualDialogDelegate.DialogCallbacks,
                               dialog: InteractionDialogAPI,
                               ui: IntelUIAPI)

    abstract fun doEveryFrame(amount: Float)

    abstract fun dismissPanelImpl()

    override fun positionChanged(position: PositionAPI?) {}

    override fun renderBelow(alphaMult: Float) {}

    override fun render(alphaMult: Float) {}

    override fun advance(amount: Float) {
        doEveryFrame(amount)
    }

    override fun processInput(events: MutableList<InputEventAPI>?) {
        for (event in events!!) {
            if (event.isConsumed) continue
            if (event.isKeyDownEvent && event.eventValue == Keyboard.KEY_ESCAPE) {
                event.consume()
                dismissPanelImpl()
                return
            }
        }
    }

    override fun buttonPressed(buttonId: Any?) {}

}