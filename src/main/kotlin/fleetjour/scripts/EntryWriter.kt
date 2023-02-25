package fleetjour.scripts

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin.IntelSortTier
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin.ListInfoMode
import com.fs.starfarer.api.impl.campaign.ids.Tags
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.IntelUIAPI
import com.fs.starfarer.api.ui.SectorMapAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import fleetjour.scripts.objects.DraftParagraph
import fleetjour.scripts.panel.ButtonChecker
import fleetjour.scripts.panel.Common
import fleetjour.scripts.panel.WriterPanelAssembly

/**
 * @author Ontheheavens
 * @since  06.02.2023
 */

class EntryWriter: BaseIntelPlugin() {

    @Transient
    lateinit var assembly: WriterPanelAssembly

    var draftParagraphs: ArrayList<DraftParagraph> = arrayListOf()
    var paragraphIDCounter: Long = 0
    var selectedParagraphIndex: Int = 0
    var selectedTargetLocation: String = Global.getSector().playerFleet.containingLocation.id
    var selectedTargetEntity: String = Common.selectDefaultTargetEntity(this)

    var inputFieldValue: String = ""
    var titleFieldValue: String = ""
    var briefFieldValue: String = ""

    var customTitleSet: Boolean = false

    var tagsState: HashMap<WriterPanelAssembly.EntityDisplayTags, Boolean> = hashMapOf(
        Pair(WriterPanelAssembly.EntityDisplayTags.PLANETS, true),
        Pair(WriterPanelAssembly.EntityDisplayTags.SALVAGE, true),
        Pair(WriterPanelAssembly.EntityDisplayTags.OBJECTIVES, true),
        Pair(WriterPanelAssembly.EntityDisplayTags.OTHER, true)
    )

    override fun getIntelTags(map: SectorMapAPI?): Set<String>? {
        val tags = super.getIntelTags(map)
        tags.add(Tags.INTEL_FLEET_LOG)
        return tags
    }

    override fun getCommMessageSound(): String? {
        return null
    }

    override fun autoAddCampaignMessage(): Boolean {
        return false
    }

    override fun isNew(): Boolean {
        return false
    }

    override fun getName(): String {
        return "Fleet Journal"
    }

    override fun createIntelInfo(info: TooltipMakerAPI, mode: ListInfoMode?) {
        val color = getTitleColor(mode)
        info.addPara(name, color, 0f)
        info.addPara("Write new fleet log entries.", Misc.getGrayColor(), 1f)
    }

    override fun getSortTier(): IntelSortTier {
        return IntelSortTier.TIER_0
    }

    override fun getIcon(): String {
        return Global.getSettings().getSpriteName("fleetjour_intel", "fleet_journal")
    }

    override fun hasLargeDescription(): Boolean {
        return true
    }

    override fun shouldRemoveIntel(): Boolean {
        return false
    }

    override fun createSmallDescription(info: TooltipMakerAPI?, width: Float, height: Float) {}

    override fun createLargeDescription(panel: CustomPanelAPI?, width: Float, height: Float) {
        panel?.let {
            assembly = WriterPanelAssembly(this, it, width, height)
            assembly.assemblePanel()
        }
    }

    override fun buttonPressConfirmed(buttonId: Any, ui: IntelUIAPI?) {
        ButtonChecker.checkButtons(this, ui, buttonId)
    }

}