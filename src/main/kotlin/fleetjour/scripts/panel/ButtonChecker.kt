package fleetjour.scripts.panel

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.CoreUITabId
import com.fs.starfarer.api.campaign.SectorEntityToken
import com.fs.starfarer.api.campaign.StarSystemAPI
import com.fs.starfarer.api.ui.IntelUIAPI
import fleetjour.scripts.EntryWriter
import fleetjour.scripts.interaction.entrymanager.EntryManagerDialog
import fleetjour.scripts.interaction.LocationSelectorDialog
import fleetjour.scripts.objects.DraftParagraph
import java.util.*

/**
 * @author Ontheheavens
 * @since  09.02.2023
 */

object ButtonChecker {

    fun checkButtons(parent: EntryWriter, ui: IntelUIAPI?, buttonId: Any) {
        this.checkWriteButton(parent, buttonId)
        this.checkManageEntriesButton(ui, buttonId)
        this.checkAddButton(parent, buttonId)
        this.checkRemoveButton(parent, buttonId)
        this.checkMovementButtons(parent, buttonId)
        this.checkAppendButton(parent, buttonId)
        this.checkDeleteButton(parent, buttonId)
        this.checkSelectParagraphButtons(parent, buttonId)
        this.checkCurrentLocationButton(parent, buttonId)
        this.checkSelectLocationButton(parent, ui, buttonId)
        this.checkSystemCenterButton(parent, buttonId)
        this.checkOrbitFocusButton(parent, buttonId)
        this.checkSortingCheckBoxes(parent, buttonId)
        this.checkSelectEntityButtons(parent, buttonId)
        this.checkSetTitleButton(parent, buttonId)
        this.checkSetBriefButton(parent, buttonId)
    }

    private fun checkWriteButton(parent: EntryWriter, buttonId: Any) {
        if (buttonId != WriterPanelAssembly.Buttons.WRITE_ENTRY) return
        if (!this.shouldEnableWriteButton(parent)) return
        val newEntry = parent.writeNewEntry()
        Global.getSector().campaignUI.showCoreUITab(CoreUITabId.INTEL, newEntry)
    }

    private fun checkManageEntriesButton(ui: IntelUIAPI?, buttonId: Any) {
        if (buttonId != WriterPanelAssembly.Buttons.MANAGE_ENTRIES) return
        ui?.showDialog(null, EntryManagerDialog(ui))
    }

    private fun checkSetTitleButton(parent: EntryWriter, buttonId: Any) {
        if (buttonId != WriterPanelAssembly.Buttons.SET_TITLE) return
        val text = parent.assembly.titleFieldInstance.text
        parent.customTitleSet = text != ""
        parent.titleFieldValue = text
        parent.assembly.writeButton.isEnabled = this.shouldEnableWriteButton(parent)
    }

    private fun checkSetBriefButton(parent: EntryWriter, buttonId: Any) {
        if (buttonId != WriterPanelAssembly.Buttons.SET_BRIEF) return
        val text = parent.assembly.briefFieldInstance.text
        parent.briefFieldValue = text
        parent.assembly.writeButton.isEnabled = this.shouldEnableWriteButton(parent)
    }

    fun shouldEnableWriteButton(parent: EntryWriter): Boolean {
        return parent.assembly.titleFieldInstance.text != ""
    }

    fun shouldEnableDeleteButton(parent: EntryWriter): Boolean {
        if (parent.draftParagraphs.size <= 0) return false
        val selectedParagraph = parent.draftParagraphs[parent.selectedParagraphIndex]
        return selectedParagraph.content.contains(" ")
    }

    private fun checkAddButton(parent: EntryWriter, buttonId: Any) {
        val assembly = parent.assembly
        if (buttonId != WriterPanelAssembly.Buttons.ADD_PARAGRAPH) return
        if (assembly.inputFieldInstance.text == "") return
        val addedContent: String = assembly.inputFieldInstance.text
        parent.addParagraph(addedContent)
        parent.assembly.inputFieldInstance.text = ""
        assembly.renderDraftPanel()
    }

    private fun checkMovementButtons(parent: EntryWriter, buttonId: Any) {
        if (parent.draftParagraphs.size < 2) return
        if (buttonId == WriterPanelAssembly.Buttons.MOVE_PARAGRAPH_UP && parent.selectedParagraphIndex > 0) {
            Collections.swap(parent.draftParagraphs, parent.selectedParagraphIndex,
                parent.selectedParagraphIndex - 1)
            parent.selectedParagraphIndex--
            parent.assembly.renderDraftPanel()
        }
        if (buttonId == WriterPanelAssembly.Buttons.MOVE_PARAGRAPH_DOWN &&
            parent.selectedParagraphIndex + 1 < parent.draftParagraphs.size) {
            Collections.swap(parent.draftParagraphs, parent.selectedParagraphIndex,
                parent.selectedParagraphIndex + 1)
            parent.selectedParagraphIndex++
            parent.assembly.renderDraftPanel()
        }
    }

    private fun checkAppendButton(parent: EntryWriter, buttonId: Any) {
        if (buttonId != WriterPanelAssembly.Buttons.APPEND_TO_PARAGRAPH) return
        if (parent.draftParagraphs.size == 0) return
        if (parent.assembly.inputFieldInstance.text == "") return
        val selectedParagraph: DraftParagraph = parent.draftParagraphs[parent.selectedParagraphIndex]
        selectedParagraph.content = selectedParagraph.content + " " + (parent.assembly.inputFieldInstance.text)
        parent.assembly.inputFieldInstance.text = ""
        parent.assembly.renderDraftPanel()
    }

    private fun checkDeleteButton(parent: EntryWriter, buttonId: Any) {
        if (buttonId != WriterPanelAssembly.Buttons.DELETE_WORD) return
        if (!this.shouldEnableDeleteButton(parent)) return
        val selectedParagraph = parent.draftParagraphs[parent.selectedParagraphIndex]
        val content = selectedParagraph.content
        selectedParagraph.content = content.substring(0, content.lastIndexOf(" "))
        parent.assembly.renderDraftPanel()
    }

    private fun checkRemoveButton(parent: EntryWriter, buttonId: Any) {
        val queuedForRemoval: DraftParagraph?
        if (buttonId != WriterPanelAssembly.Buttons.REMOVE_PARAGRAPH) return
        if (parent.draftParagraphs.size == 0) return
        queuedForRemoval = parent.draftParagraphs[parent.selectedParagraphIndex]
        if (parent.draftParagraphs.indexOf(queuedForRemoval) < parent.selectedParagraphIndex) {
            parent.selectedParagraphIndex--
        }
        parent.draftParagraphs.remove(queuedForRemoval)
        if (parent.draftParagraphs.size <= parent.selectedParagraphIndex) {
            parent.selectedParagraphIndex--
        }
        if (parent.selectedParagraphIndex < 0) {
            parent.selectedParagraphIndex = 0
        }
        parent.assembly.renderDraftPanel()
    }

    private fun checkSelectParagraphButtons(parent: EntryWriter, buttonId: Any) {
        for (paragraph in parent.draftParagraphs) {
            if (buttonId == WriterPanelAssembly.Buttons.SELECT_PARAGRAPH.toString() + (paragraph.id)) {
                parent.selectedParagraphIndex = parent.draftParagraphs.indexOf(paragraph)
                this.updateSelectParagraphButtons(parent)
            }
        }
    }

    private fun updateSelectParagraphButtons(parent: EntryWriter) {
        parent.assembly.deleteButton.isEnabled = this.shouldEnableDeleteButton(parent)
        parent.assembly.upButton.isEnabled = parent.selectedParagraphIndex > 0
        parent.assembly.downButton.isEnabled = parent.selectedParagraphIndex +
                1 < parent.draftParagraphs.size
        for (checked in parent.draftParagraphs) {
            if (checked == parent.draftParagraphs[parent.selectedParagraphIndex]) {
                checked.selectButton.isChecked = true
                checked.selectButton.highlight()
            } else {
                checked.selectButton.isChecked = false
                checked.selectButton.unhighlight()
            }
        }
    }

    private fun checkCurrentLocationButton(parent: EntryWriter, buttonId: Any) {
        if (buttonId != WriterPanelAssembly.Buttons.CURRENT_LOCATION) return
        val currentLocation = Global.getSector().playerFleet.containingLocation.id
        if (parent.selectedTargetLocation == currentLocation) return
        parent.selectedTargetLocation = currentLocation
        parent.selectedTargetEntity = Common.selectDefaultTargetEntity(parent)
        parent.assembly.renderStateContainer()
        parent.assembly.renderEntitiesContainer()
        parent.assembly.updateTitleByDefault()
    }

    private fun checkSelectLocationButton(parent: EntryWriter, ui: IntelUIAPI?, buttonId: Any) {
        if (buttonId != WriterPanelAssembly.Buttons.SELECT_LOCATION) return
        ui?.showDialog(null, LocationSelectorDialog(parent, ui))
    }

    private fun checkSystemCenterButton(parent: EntryWriter, buttonId: Any) {
        if (buttonId != WriterPanelAssembly.Buttons.SYSTEM_CENTER) return
        val selectedLocation = Common.findTargetLocation(parent)
        if (selectedLocation !is StarSystemAPI) return
        val system: StarSystemAPI = selectedLocation
        var center: SectorEntityToken? = system.star
        center ?: let {
            center = Common.fetchFirstEligibleEntity(system)
        }
        if (center == Common.findTargetEntity(parent)) return
        parent.selectedTargetEntity = center!!.id
        parent.selectedTargetLocation = center!!.containingLocation.id
        parent.assembly.renderStateContainer()
        this.updateSelectEntityButtons(parent)
        parent.assembly.updateTitleByDefault()
    }

    private fun checkOrbitFocusButton(parent: EntryWriter, buttonId: Any) {
        if (buttonId != WriterPanelAssembly.Buttons.ORBIT_FOCUS) return
        val currentFocus = Common.getCurrentOrbitFocus()
        if (Common.findTargetEntity(parent) == currentFocus) return
        var shouldRedrawEntities = false
        if (currentFocus != null) {
            shouldRedrawEntities = Common.findTargetLocation(parent) != currentFocus.containingLocation
            parent.selectedTargetEntity = currentFocus.id
            parent.selectedTargetLocation = currentFocus.containingLocation.id
        }
        parent.assembly.renderStateContainer()
        if (shouldRedrawEntities) {
            parent.assembly.renderEntitiesContainer()
        }
        this.updateSelectEntityButtons(parent)
        parent.assembly.updateTitleByDefault()
    }

    private fun checkSortingCheckBoxes(parent: EntryWriter, buttonId: Any) {
        val buttonHolder = parent.assembly
        if (buttonId == WriterPanelAssembly.EntityDisplayTags.PLANETS) {
            parent.tagsState[WriterPanelAssembly.EntityDisplayTags.PLANETS] =
                buttonHolder.firstTagSorter.isChecked
            buttonHolder.renderEntitiesContainer()
        }
        if (buttonId == WriterPanelAssembly.EntityDisplayTags.SALVAGE) {
            parent.tagsState[WriterPanelAssembly.EntityDisplayTags.SALVAGE] =
                buttonHolder.secondTagSorter.isChecked
            buttonHolder.renderEntitiesContainer()
        }
        if (buttonId == WriterPanelAssembly.EntityDisplayTags.OBJECTIVES) {
            parent.tagsState[WriterPanelAssembly.EntityDisplayTags.OBJECTIVES] =
                buttonHolder.thirdTagSorter.isChecked
            buttonHolder.renderEntitiesContainer()
        }
        if (buttonId == WriterPanelAssembly.EntityDisplayTags.OTHER) {
            parent.tagsState[WriterPanelAssembly.EntityDisplayTags.OTHER] =
                buttonHolder.fourthTagSorter.isChecked
            buttonHolder.renderEntitiesContainer()
        }
    }

    private fun checkSelectEntityButtons(parent: EntryWriter, buttonId: Any) {
        for (entity in parent.assembly.selectableEntities) {
            if (buttonId == WriterPanelAssembly.Buttons.SELECT_ENTITY.toString() + (entity.entityId)) {
                parent.selectedTargetEntity = entity.entityId
                this.updateSelectEntityButtons(parent)
                parent.assembly.renderStateContainer()
                parent.assembly.updateTitleByDefault()
            }
        }
    }

    private fun updateSelectEntityButtons(parent: EntryWriter) {
        for (checked in parent.assembly.selectableEntities) {
            if (checked.entityId == parent.selectedTargetEntity) {
                checked.selectButton.isChecked = true
                checked.selectButton.highlight()
            } else {
                checked.selectButton.isChecked = false
                checked.selectButton.unhighlight()
            }
        }
    }

}