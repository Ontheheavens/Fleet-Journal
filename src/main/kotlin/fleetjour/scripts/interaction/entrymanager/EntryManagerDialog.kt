package fleetjour.scripts.interaction.entrymanager

import com.fs.starfarer.api.campaign.InteractionDialogAPI
import com.fs.starfarer.api.campaign.InteractionDialogPlugin
import com.fs.starfarer.api.campaign.rules.MemoryAPI
import com.fs.starfarer.api.combat.EngagementResultAPI
import com.fs.starfarer.api.ui.IntelUIAPI
import fleetjour.scripts.interaction.entrymanager.panel.PanelConstants

/**
 * @author Ontheheavens
 * @since  16.02.2023
 */

class EntryManagerDialog(val ui: IntelUIAPI) : InteractionDialogPlugin {

    private lateinit var dialog: InteractionDialogAPI

    private enum class Options {
        INIT, EXIT
    }

    override fun init(dialog: InteractionDialogAPI) {
        this.dialog = dialog
        dialog.setOptionOnEscape("", Options.EXIT)
        dialog.promptText = ""
        optionSelected("", Options.INIT)
    }

    override fun optionSelected(optionText: String?, optionData: Any?) {
        if (optionData == null) return
        when (optionData as Options) {
            Options.INIT -> dialog.showCustomVisualDialog(
                PanelConstants.PANEL_WIDTH,
                PanelConstants.PANEL_HEIGHT, EntryManagerDelegate(EntryManagerPlugin(ui), this.dialog)
            )
            Options.EXIT -> dialog.dismiss()
        }
    }

    override fun optionMousedOver(optionText: String?, optionData: Any?) {}

    override fun advance(amount: Float) {}

    override fun backFromEngagement(battleResult: EngagementResultAPI?) {}

    override fun getContext(): Any? {
        return null
    }

    override fun getMemoryMap(): MutableMap<String, MemoryAPI>? {
        return null
    }
}