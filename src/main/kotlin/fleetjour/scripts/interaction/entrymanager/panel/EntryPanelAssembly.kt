package fleetjour.scripts.interaction.entrymanager.panel

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.impl.campaign.ids.Tags
import com.fs.starfarer.api.ui.*
import com.fs.starfarer.api.util.Misc
import fleetjour.scripts.EntryWriter

/**
 * @author Ontheheavens
 * @since  17.02.2023
 */

object EntryPanelAssembly {

    const val INTEL_OTHER: String = "Other"

    @Transient
    var SORTING_TAGS: LinkedHashMap<String, Boolean> = linkedMapOf()

    @Transient
    var panelInstance: CustomPanelAPI? = null

    @Transient
    var headerInstance: TooltipMakerAPI? = null

    @Transient
    var controlSectionInstance: CustomPanelAPI? = null

    var controlButtonsRedrawQueued: Boolean = false

    init {
        resetTags()
    }

    private fun resetTags() {
        SORTING_TAGS[Tags.INTEL_IMPORTANT] = true
        SORTING_TAGS[Tags.INTEL_NEW] = true
        SORTING_TAGS[Tags.INTEL_BOUNTY] = true
        SORTING_TAGS[Tags.INTEL_MAJOR_EVENT] = true
        SORTING_TAGS[Tags.INTEL_CONTACTS] = true
        SORTING_TAGS[Tags.INTEL_FLEET_DEPARTURES] = true
        SORTING_TAGS[Tags.INTEL_SMUGGLING] = true
        SORTING_TAGS[Tags.INTEL_EXPLORATION] = true
        SORTING_TAGS[Tags.INTEL_FLEET_LOG] = true
        SORTING_TAGS[Tags.INTEL_TRADE] = true
        SORTING_TAGS[Tags.INTEL_MISSIONS] = true
        SORTING_TAGS[Tags.INTEL_ACCEPTED] = true
        SORTING_TAGS[Tags.INTEL_STORY] = true
        SORTING_TAGS[Tags.INTEL_COMMISSION] = true
        SORTING_TAGS[Tags.INTEL_HOSTILITIES] = true
        SORTING_TAGS[Tags.INTEL_MILITARY] = true
        SORTING_TAGS[Tags.INTEL_BEACON] = true
        SORTING_TAGS[Tags.INTEL_GATES] = true
        SORTING_TAGS[Tags.INTEL_COLONIES] = true
        SORTING_TAGS[Tags.INTEL_COMM_SNIFFERS] = true
        SORTING_TAGS[Tags.INTEL_DECIVILIZED] = true
        SORTING_TAGS[Tags.INTEL_PRODUCTION] = true
        SORTING_TAGS[INTEL_OTHER] = true
    }

    fun assemble(panel: CustomPanelAPI) {
        panelInstance = panel
        val header = Header.create(panel)
        headerInstance = header
        val footer = Footer.create(panel)
        val contentHeightOffset = header!!.position.height + footer!!.position.height
        val contentHeight = PanelConstants.PANEL_HEIGHT - ((PanelConstants.PANEL_CONTENT_OFFSET * 2)
                + contentHeightOffset + 23f)
        val entriesSection = EntriesSection.create(panel, contentHeight)
        val sortingSection = createSortingPanel(panel, contentHeight - 91f)
        panel.addComponent(sortingSection).aboveRight(footer, 25f)
        sortingSection.position.setXAlignOffset(-7f)
        entriesSection!!.position.belowLeft(header, 4f)
        renderControlSection()
    }

    fun renderControlSection() {
        if (controlSectionInstance != null) {
            panelInstance?.removeComponent(controlSectionInstance)
        }
        val controlSection = panelInstance?.let { createControlSection(it) }
        panelInstance?.addComponent(controlSection)
        controlSection?.position?.belowRight(headerInstance, 4f)
        controlSectionInstance = controlSection
    }

    private fun createSortingPanel(panel: CustomPanelAPI, height: Float): CustomPanelAPI {
        val width = PanelConstants.RIGHTSIDE_BUTTONS_WIDTH + 6f
        val sortingPanel = panel.createCustomPanel(width, height, EntryPanelOverseer.plugin)
        val backgroundContainer = sortingPanel.createUIElement(width, height, false)
        backgroundContainer.setForceProcessInput(false)
        val heading = backgroundContainer.addSectionHeading("Tags", Alignment.MID, 0f)
        val frame = addFrameBox(backgroundContainer, height)
        heading.position.aboveLeft(frame, -3f)
        heading.position.setXAlignOffset(-6f)
        sortingPanel.addUIElement(backgroundContainer).inBR(0f, 0f)
        val buttonsContainer = sortingPanel.createUIElement(width - 8f, height - 8f, true)
        val colorBase = Misc.getBasePlayerColor()
        val colorDark = Misc.getDarkPlayerColor()
        val colorBright = Misc.getBrightPlayerColor()
        val anchor = buttonsContainer.addSpacer(0f)
        anchor.position.setXAlignOffset(0f)
        SORTING_TAGS.forEach { tag ->
            val tagButton: ButtonAPI = buttonsContainer.addAreaCheckbox(tag.key, null, colorBase, colorDark,
                colorBright, width - 14f, 25f, 0f
            )
            tagButton.isChecked = tag.value
            buttonsContainer.addSpacer(3f)
            val buttonWrapper = object : InteractiveButton(tagButton, Type.TAG) {
                var state: Boolean = tagButton.isChecked
                override fun applyEffect() {
                    SORTING_TAGS[tag.key] = state
                    EntriesSection.sectionRedrawQueued = true
                    controlButtonsRedrawQueued = true
                }
                override fun check() {
                    if (tagButton.isChecked == state) {
                        return
                    } else {
                        state = tagButton.isChecked
                        this.applyEffect()
                    }
                }
            }
            ButtonListener.getIndex()?.add(buttonWrapper)
        }
        sortingPanel.addUIElement(buttonsContainer).inBR(4f, -6f)
        return sortingPanel
    }

    private fun addFrameBox(sidebarFrame: TooltipMakerAPI, height: Float): UIComponentAPI {
        val sidebarBoxContainer = sidebarFrame.beginImageWithText(null, 2f)
        val baseBoxColor = Misc.interpolateColor(Misc.getDarkPlayerColor(), Misc.getBrightPlayerColor(), 0.5f)
        val boxColor = Misc.scaleColorOnly(baseBoxColor, 0.9f)
        sidebarBoxContainer.addAreaCheckbox("", null, Misc.getBasePlayerColor(), boxColor,
            Misc.getBasePlayerColor(), 186f, height, 0f)
        val offsetX = PanelConstants.RIGHTSIDE_BUTTONS_WIDTH + 2f
        sidebarBoxContainer.prev.position.setXAlignOffset(-offsetX)
        sidebarBoxContainer.prev.position.setYAlignOffset(-9f)
        sidebarFrame.addImageWithText(2f)
        return sidebarFrame.prev
    }

    private fun createControlSection(parentPanel: CustomPanelAPI): CustomPanelAPI {
        val width = PanelConstants.RIGHTSIDE_BUTTONS_WIDTH + 6f
        val panel = parentPanel.createCustomPanel(width, 20f, EntryPanelOverseer.plugin)
        val buttonsContainer = panel.createUIElement(width - 8f, 20f, false)
        buttonsContainer.addSpacer(8f)
        val colorBase = Misc.getBasePlayerColor()
        val colorDark = Misc.getDarkPlayerColor()
        val anchor = buttonsContainer.addSpacer(0f)
        anchor.position.inTL(5f, 2f)
        var hideButtonText = "Hide entry"
        if (EntriesSection.selectedEntry?.isHidden == true) {
            hideButtonText = "Show entry"
        }
        val hideButtonInstance = buttonsContainer.addButton(hideButtonText, null, colorBase, colorDark,
            Alignment.MID, CutStyle.ALL, PanelConstants.RIGHTSIDE_BUTTONS_WIDTH + 6f, 25f, 2f)
        hideButtonInstance.isEnabled = false
        val hideButtonWrapper = object : InteractiveButton(hideButtonInstance, Type.STANDARD) {
            override fun applyEffect() {
                EntriesSection.changeEntryVisibility()
            }
        }
        ButtonListener.getIndex()?.add(hideButtonWrapper)
        buttonsContainer.addSpacer(8f)
        val deleteButtonInstance = buttonsContainer.addButton("Delete entry", null, colorBase, colorDark,
            Alignment.MID, CutStyle.ALL, PanelConstants.RIGHTSIDE_BUTTONS_WIDTH + 6f, 25f, 2f)
        if (!EntriesSection.showedEntries.containsKey(EntriesSection.selectedEntry) ||
             EntriesSection.showedEntries.size == 0 ||
             EntriesSection.selectedEntry == null ||
             EntriesSection.selectedEntry is EntryWriter) {
            deleteButtonInstance.isEnabled = false
        }
        val deleteButtonWrapper = object : InteractiveButton(deleteButtonInstance, Type.STANDARD) {
            override fun applyEffect() {
                if (EntriesSection.selectedEntry == null) return
                if (EntriesSection.selectedEntry is EntryWriter) return
                if (!EntriesSection.showedEntries.containsKey(EntriesSection.selectedEntry!!)) return
                Global.getSector().intelManager.removeIntel(EntriesSection.selectedEntry)
                EntriesSection.selectedEntry = null
                EntriesSection.sectionRedrawQueued = true
            }
        }
        ButtonListener.getIndex()?.add(deleteButtonWrapper)
        panel.addUIElement(buttonsContainer).inTL(-6f, 0f)
        return panel
    }

}