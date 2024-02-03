package fleetjour.scripts

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.CoreUITabId
import com.fs.starfarer.api.campaign.SectorEntityToken
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
import fleetjour.scripts.objects.JournalEntry
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

    fun writeNewEntry(): JournalEntry {
        val title = this.titleFieldValue
        val brief = this.briefFieldValue
        val contents = arrayListOf<String>()
        for (paragraph in this.draftParagraphs) {
            contents.add(paragraph.content)
        }
        val newEntry = JournalEntry(Common.findTargetEntity(this), title, brief, contents)
        Global.getSector().intelManager.addIntel(newEntry)
        this.draftParagraphs.clear()
        this.selectedParagraphIndex = 0
        this.titleFieldValue = ""
        this.briefFieldValue = ""
        this.customTitleSet = false
        return newEntry
    }

    fun beginEntryRewrite(entry: JournalEntry) {
        this.titleFieldValue = entry.title
        this.briefFieldValue = entry.brief

        val targetEntityToken = entry.targetEntityToken
        this.selectedTargetEntity = targetEntityToken.id
        this.selectedTargetLocation = targetEntityToken.containingLocation.id

        this.draftParagraphs.clear()
        this.selectedParagraphIndex = 0

        for (paragraph in entry.contents) {
            addParagraph(paragraph)
        }

        Global.getSector().campaignUI.showCoreUITab(CoreUITabId.INTEL, this)
    }

    fun applyEntityInfo(entity: SectorEntityToken) {
        val title = "Notable " + Common.getTypeForIntelInfo(entity)
        this.titleFieldValue = title
        this.briefFieldValue = "Name: " + entity.name

        this.selectedTargetEntity = entity.id
        this.selectedTargetLocation = entity.containingLocation.id

        this.draftParagraphs.clear()
        this.selectedParagraphIndex = 0
        addCircumstanceParagraphs(entity)

        Global.getSector().campaignUI.showCoreUITab(CoreUITabId.INTEL, this)
    }

    private fun addCircumstanceParagraphs(entity: SectorEntityToken) {
        addCurrentDate()
        addParagraph("Location: " + entity.containingLocation.name)
        if (entity.faction != null && !entity.faction.isNeutralFaction) {
            addParagraph("Faction: " + entity.faction.displayName)
        }
    }

    private fun addCurrentDate() {
        addParagraph("Date: " + Global.getSector().clock.dateString)
    }

    fun addParagraph(text: String): DraftParagraph {
        val paragraph = DraftParagraph(text, ++this.paragraphIDCounter)
        if (draftParagraphs.size == 0) {
            draftParagraphs.add(paragraph)
        } else {
            draftParagraphs.add(this.selectedParagraphIndex + 1, paragraph)
            this.selectedParagraphIndex++
        }
        return paragraph
    }

    fun writeQuickEntry(entity: SectorEntityToken,
                        forceNoMessage: Boolean = false,
                        titlePrefix: String = "Notable "): JournalEntry {
        val title = titlePrefix + Common.getTypeForIntelInfo(entity)
        val brief = "Name: " + entity.name

        val contents = arrayListOf<String>()
        contents.add("Date: " + Global.getSector().clock.dateString)
        contents.add("Location: " + entity.containingLocation.name)
        if (entity.faction != null && !entity.faction.isNeutralFaction) {
            contents.add("Faction: " + entity.faction.displayName)
        }

        val newEntry = JournalEntry(entity, title, brief, contents)
        Global.getSector().intelManager.addIntel(newEntry, forceNoMessage)
        return newEntry
    }

}