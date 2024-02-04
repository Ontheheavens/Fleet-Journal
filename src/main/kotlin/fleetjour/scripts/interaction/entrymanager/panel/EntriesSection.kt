package fleetjour.scripts.interaction.entrymanager.panel

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import fleetjour.scripts.interaction.entrymanager.panel.EntryPanelAssembly.INTEL_OTHER
import fleetjour.scripts.interaction.entrymanager.panel.EntryPanelAssembly.SORTING_TAGS
import java.util.*

/**
 * @author Ontheheavens
 * @since  17.02.2023
 */

object EntriesSection {

    @Transient
    var parentPanelInstance: CustomPanelAPI? = null

    @Transient
    var listPanelInstance: CustomPanelAPI? = null

    @Transient
    var showedEntries: LinkedHashMap<IntelInfoPlugin, IntelEntry> = linkedMapOf()

    @Transient
    var selectedEntry: IntelInfoPlugin? = null
    var sectionRedrawQueued: Boolean = false
    private var sectionHeight: Float = 0f

    fun create(panel: CustomPanelAPI, height: Float): CustomPanelAPI? {
        parentPanelInstance = panel
        sectionHeight = height
        showedEntries = linkedMapOf()
        selectedEntry = null
        renderEntries()
        return listPanelInstance
    }

    fun renderEntries() {
        if (listPanelInstance != null) {
            parentPanelInstance?.removeComponent(listPanelInstance)
        }
        val sectionContainer = parentPanelInstance?.createCustomPanel(
            EntryManagerConstants.CONTENT_WIDTH,
            sectionHeight, EntryPanelOverseer.plugin
        )
        val list = createEntriesList(sectionContainer, sectionHeight)
        sectionContainer?.addUIElement(list)?.inTL(0f, 0f)
        parentPanelInstance?.addComponent(sectionContainer)
        listPanelInstance = sectionContainer
        listPanelInstance!!.position.belowLeft(EntryPanelAssembly.headerInstance, 4f)
    }

    fun changeEntryVisibility() {
        if (showedEntries[selectedEntry]?.plugin?.isHidden == true) {
            showedEntries[selectedEntry]!!.plugin.isHidden = false
            showedEntries[selectedEntry]!!.brighten()
        } else {
            showedEntries[selectedEntry]!!.plugin.isHidden = true
            showedEntries[selectedEntry]!!.darken()
        }
    }

    private fun retrieveFilteredEntries(): ArrayList<IntelInfoPlugin> {
        val allEntries = Global.getSector().intelManager.intel
        val enabledTags = hashSetOf<String>()
        SORTING_TAGS.forEach { tag ->
            if (tag.key != INTEL_OTHER && tag.value) {
                enabledTags.add(tag.key)
            }
        }
        val result = arrayListOf<IntelInfoPlugin>()
        allEntries.forEach { entry ->
            val entryTags = entry.getIntelTags(null)
            if (!Collections.disjoint(enabledTags, entryTags)) {
                result.add(entry)
            }
            if (Collections.disjoint(SORTING_TAGS.keys, entryTags) && SORTING_TAGS[INTEL_OTHER] == true) {
                result.add(entry)
            }
        }
        return result
    }

    private fun createEntriesList(section: CustomPanelAPI?, height: Float): TooltipMakerAPI? {
        section?: return null
        val listContainer = section.createUIElement(EntryManagerConstants.ENTRIES_LIST_WIDTH, height, true)
        val allEntries = retrieveFilteredEntries()
        if (allEntries.size == 0) return listContainer
        showedEntries = linkedMapOf()
        for ((count, entry) in allEntries.withIndex()) {
            val entryInstance = IntelEntry(entry)
            if (entry.isHidden) {
                // TODO: Introduce proper checks for natively hidden/hidden by widget when Alex updates.
                continue
            }
            val entryInfo = entryInstance.createEntry(section, entry)
            listContainer.addCustom(entryInfo.first, 4f)
            if (selectedEntry == null && count == 0) {
                selectedEntry = entry
                entryInstance.button.highlight()
            }
            showedEntries[entry] = entryInstance
            listContainer.addSpacer(entryInfo.second - 26f)
        }
        if (showedEntries.containsKey(selectedEntry)) {
            showedEntries[selectedEntry]?.button?.highlight()
        }
        listContainer.addSpacer(-6f)
        return listContainer
    }


}