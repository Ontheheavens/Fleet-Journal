package fleetjour.scripts.interaction

import com.fs.starfarer.api.campaign.CustomUIPanelPlugin
import com.fs.starfarer.api.campaign.CustomVisualDialogDelegate.DialogCallbacks
import com.fs.starfarer.api.campaign.InteractionDialogAPI
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.IntelUIAPI
import com.fs.starfarer.api.ui.PositionAPI
import fleetjour.scripts.interaction.panel.EntryPanelAssembly
import fleetjour.scripts.interaction.panel.EntryPanelOverseer
import org.lwjgl.input.Keyboard

/**
 * @author Ontheheavens
 * @since  16.02.2023
 */

class EntryManagerPlugin(val ui: IntelUIAPI) : CustomUIPanelPlugin {

    fun initialize(panel: CustomPanelAPI?, callbacks: DialogCallbacks?, dialog: InteractionDialogAPI?) {
        panel?: return
        callbacks?: return
        dialog?: return
        EntryPanelOverseer.initialize(panel, callbacks, dialog, ui, this)
        EntryPanelAssembly.assemble(panel)
    }

    override fun positionChanged(position: PositionAPI?) {}

    override fun renderBelow(alphaMult: Float) {}

    override fun render(alphaMult: Float) {}

    override fun advance(amount: Float) {
        EntryPanelOverseer.advance()
    }

    override fun processInput(events: MutableList<InputEventAPI>?) {
        for (event in events!!) {
            if (event.isConsumed) continue
            if (event.isKeyDownEvent && event.eventValue == Keyboard.KEY_ESCAPE) {
                event.consume()
                EntryPanelOverseer.dismissPanel()
                return
            }
        }
    }

    override fun buttonPressed(buttonId: Any?) {
    }

}