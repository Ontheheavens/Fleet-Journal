package fleetjour.scripts.interaction

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.*
import com.fs.starfarer.api.campaign.rules.MemoryAPI
import com.fs.starfarer.api.combat.EngagementResultAPI
import com.fs.starfarer.api.impl.campaign.ids.Tags
import com.fs.starfarer.api.ui.IntelUIAPI
import com.fs.starfarer.api.util.Misc
import fleetjour.scripts.EntryWriter

/**
 * @author Ontheheavens
 * @since  10.02.2023
 */

class LocationSelectorDialog(private val intel: EntryWriter, private val ui: IntelUIAPI): InteractionDialogPlugin {

    override fun init(dialog: InteractionDialogAPI?) {
        val anchors: List<SectorEntityToken> = Global.getSector().getEntitiesWithTag(Tags.SYSTEM_ANCHOR)
        val starSystemCenters = mutableListOf<SectorEntityToken>()
        for (token in anchors) {
            val system: StarSystemAPI = Misc.getStarSystemForAnchor(token)
            if (!system.isEnteredByPlayer) continue
            starSystemCenters.add(system.center)
        }
        dialog!!.showCampaignEntityPicker("Select location", "Selected:", "Confirm",
            Global.getSector().playerFaction, starSystemCenters, LocationSelectorPicker(ui, dialog, intel)
        )
    }

    override fun optionSelected(optionText: String?, optionData: Any?) {}

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