package fleetjour.scripts.interaction.shared

import com.fs.starfarer.api.campaign.InteractionDialogAPI
import com.fs.starfarer.api.campaign.InteractionDialogPlugin
import com.fs.starfarer.api.campaign.rules.MemoryAPI
import com.fs.starfarer.api.combat.EngagementResultAPI
import com.fs.starfarer.api.ui.IntelUIAPI

/**
 * @author Ontheheavens
 * @since 04.02.2024
 */
abstract class AbstractPanelDialog(val ui: IntelUIAPI) : InteractionDialogPlugin {

    protected lateinit var dialog: InteractionDialogAPI

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
            Options.INIT -> showCustomDialog()
            Options.EXIT -> dialog.dismiss()
        }
    }

    abstract fun showCustomDialog()

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