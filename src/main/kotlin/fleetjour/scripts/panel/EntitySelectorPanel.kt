package fleetjour.scripts.panel

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.SectorEntityToken
import com.fs.starfarer.api.ui.*
import com.fs.starfarer.api.util.Misc
import fleetjour.scripts.objects.SelectableEntity
import java.util.*

/**
 * @author Ontheheavens
 * @since  10.02.2023
 */

object EntitySelectorPanel {

    fun create(parent: WriterPanelAssembly, panelHeight: Float): CustomPanelAPI {
        val width = Constants.SELECTOR_WIDTH
        val height = panelHeight - Constants.CONTENT_HEIGHT_OFFSET
        val selectorPanel = parent.mainPanel.createCustomPanel(width, height, null)
        val header: TooltipMakerAPI = this.createPanelHeader(selectorPanel)
        selectorPanel.addUIElement(header).inTL(0f, 0f)
        val controlPanel = this.createControlPanel(parent, selectorPanel)
        selectorPanel.addUIElement(controlPanel).belowRight(header, 10f)
        val bottomLineContainer = selectorPanel.createUIElement(Constants.SELECTOR_WIDTH, 1f, false)
        val bottomLine: ButtonAPI = bottomLineContainer.addButton("", null, width, 0f, 0f)
        bottomLine.position.inTR(0f, 0f)
        selectorPanel.addUIElement(bottomLineContainer).belowRight(controlPanel, 10f)
        return selectorPanel
    }

    private fun createPanelHeader(selectorPanel: CustomPanelAPI): TooltipMakerAPI {
        val headerContainer = selectorPanel.createUIElement(Constants.SELECTOR_WIDTH, 18f, false)
        headerContainer.addSectionHeading("Target", Alignment.MID, 0f)
        headerContainer.addSpacer(10f)
        return headerContainer
    }

    private fun createControlPanel(assembly: WriterPanelAssembly, selectorPanel: CustomPanelAPI): TooltipMakerAPI {
        val width = Constants.CONTROL_SECTION_WIDTH
        val buttonHeight = 25f
        val controlContainer = selectorPanel.createUIElement(width, Constants.TARGET_SECTION_HEIGHT, false)
        assembly.currentLocationButton = controlContainer.addButton("Current location",
            WriterPanelAssembly.Buttons.CURRENT_LOCATION, width, buttonHeight, 0f)
        val currentLocTooltip = object: BaseTooltipCreator() {
            private fun getText(): String {
                val currentLocation = Global.getSector().playerFleet.containingLocation
                return "Current location: " + currentLocation.name
            }
            override fun getTooltipWidth(tooltipParam: Any?): Float {
                return controlContainer.computeStringWidth(getText())
            }
            override fun createTooltip(tooltip: TooltipMakerAPI?, expanded: Boolean, tooltipParam: Any?) {
                tooltip ?: return
                tooltip.addPara(getText(), 2f, Misc.getHighlightColor(),
                    Global.getSector().playerFleet.containingLocation.name)
            }
        }
        controlContainer.addTooltipToPrevious(currentLocTooltip, TooltipMakerAPI.TooltipLocation.ABOVE)
        assembly.currentLocationButton.position.inTMid(0f)
        assembly.selectLocationButton = controlContainer.addButton("Select location",
            WriterPanelAssembly.Buttons.SELECT_LOCATION, width, buttonHeight, 0f)
        assembly.selectLocationButton.position.belowMid(assembly.currentLocationButton, 10f)
        assembly.systemCenterButton = controlContainer.addButton("System center",
            WriterPanelAssembly.Buttons.SYSTEM_CENTER, width, buttonHeight, 0f)
        assembly.systemCenterButton.position.belowMid(assembly.selectLocationButton, 20f)
        assembly.orbitFocusButton = controlContainer.addButton("Orbit focus",
            WriterPanelAssembly.Buttons.ORBIT_FOCUS, width, buttonHeight, 0f)
        val orbitTooltip = this.getOrbitFocusTooltip(controlContainer)
        controlContainer.addTooltipToPrevious(orbitTooltip, TooltipMakerAPI.TooltipLocation.BELOW)
        assembly.orbitFocusButton.position.belowMid(assembly.systemCenterButton, 10f)
        return controlContainer
    }

    private fun getOrbitFocusTooltip(parentTooltip: TooltipMakerAPI): BaseTooltipCreator {
        val focusTooltip: BaseTooltipCreator = object : BaseTooltipCreator() {
            private fun getText(): String {
                return if (Common.getCurrentOrbitFocus() != null) {
                    "Currently orbiting: " + Common.getCurrentOrbitFocus()!!.name
                } else {
                    "Not on orbit trajectory."
                }
            }
            override fun getTooltipWidth(tooltipParam: Any?): Float {
                return parentTooltip.computeStringWidth(getText())
            }
            override fun createTooltip(tooltip: TooltipMakerAPI?, expanded: Boolean, tooltipParam: Any?) {
                tooltip ?: return
                tooltip.addPara(getText(), 2f, Misc.getHighlightColor(), Common.getCurrentOrbitFocus()?.name)
            }
        }
        return focusTooltip
    }

    fun createStatePanel(assembly: WriterPanelAssembly, selectorPanel: CustomPanelAPI): TooltipMakerAPI {
        val width = selectorPanel.position.width - (Constants.CONTROL_SECTION_WIDTH + 18f)
        val stateContainer = selectorPanel.createUIElement(width, Constants.TARGET_SECTION_HEIGHT, false)
        stateContainer.addSpacer(0f).position.inTL(0f, 14f)
        val locationHeading = Common.createLineHeading(stateContainer, "Location", width, 4f)
        val locationName: String = Common.findTargetLocation(assembly.parent).name
        val locationLabel = stateContainer.addPara(locationName, 2f)
        locationLabel.position.belowLeft(locationHeading, 26f)
        locationLabel.position.setXAlignOffset(5f)
        locationLabel.setAlignment(Alignment.MID)
        stateContainer.addSpacer(39f).position.setXAlignOffset(-5f)
        val entityHeading = Common.createLineHeading(stateContainer, "Entity", width, 4f)
        var entityName: String = Common.findTargetEntity(assembly.parent).name
        // Workaround for nebulas: they return this as the name of their star.
        // So, need to be substituted for something presentable.
        if (entityName == "unknown location") {
            entityName = locationName
        }
        val entityLabel = stateContainer.addPara(entityName, 2f)
        entityLabel.position.belowLeft(entityHeading, 26f)
        entityLabel.position.setXAlignOffset(5f)
        entityLabel.setAlignment(Alignment.MID)
        return stateContainer
    }

    fun createEntitiesSelector(assembly: WriterPanelAssembly, selectorPanel: CustomPanelAPI): CustomPanelAPI {
        val width = selectorPanel.position.width
        val height = selectorPanel.position.height - (Constants.TARGET_SECTION_HEIGHT +
                Constants.SORTING_BUTTONS_HEIGHT + 43f)
        val container = selectorPanel.createCustomPanel(width, height, null)
        val entitiesContainer = container.createUIElement(width, height, true)
        assembly.selectableEntities.clear()
        val inputEntities = this.getSortedEntities(assembly)
        for (entity in inputEntities) {
            if (this.shouldNotDisplayByDistance(assembly, entity)) continue
            val displayed = SelectableEntity(entity)
            assembly.selectableEntities.add(displayed)
            val displayedContainer: CustomPanelAPI = displayed.createContainer(assembly, container)
            entitiesContainer.addCustom(displayedContainer, 2f)
            entitiesContainer.addSpacer(-15f)
            if (entity.id == assembly.parent.selectedTargetEntity) {
                this.highlightEntity(displayed)
            }
        }
        container.addUIElement(entitiesContainer).inTL(0f, 0f)
        return container
    }

    private fun getSortedEntities(assembly: WriterPanelAssembly): List<SectorEntityToken> {
        val targetLocation = Common.findTargetLocation(assembly.parent)
        val inputEntities = Common.getDiscoveredEntitiesInLocation(assembly, targetLocation)
        val targetLocationIsHyperspace = targetLocation.isHyperspace
        val sorterDistanceInHyperspace: Comparator<SectorEntityToken> = Comparator { first, second ->
            compareValues(Common.getDistanceToPlayerInLY(first), Common.getDistanceToPlayerInLY(second)) }
        val sorterDistanceToPlayer: Comparator<SectorEntityToken> = Comparator { first, second ->
            compareValues(Common.getDistanceToPlayerInUnits(first), Common.getDistanceToPlayerInUnits(second)) }
        val sorterDistanceToCenter: Comparator<SectorEntityToken> = Comparator { first, second ->
            compareValues(Common.getDistanceToSystemCenter(first), Common.getDistanceToSystemCenter(second)) }
        if (targetLocationIsHyperspace) {
            Collections.sort(inputEntities, sorterDistanceInHyperspace)
        } else if (Common.findTargetLocation(assembly.parent) == Global.getSector().playerFleet.containingLocation) {
            Collections.sort(inputEntities, sorterDistanceToPlayer)
        } else {
            Collections.sort(inputEntities, sorterDistanceToCenter)
        }
        return inputEntities
    }

    private fun highlightEntity(displayed: SelectableEntity) {
        displayed.selectButton.isChecked = true
        displayed.selectButton.highlight()
    }

    private fun shouldNotDisplayByDistance(assembly: WriterPanelAssembly, entity: SectorEntityToken): Boolean {
        val isHyperspace = Common.findTargetLocation(assembly.parent).isHyperspace
        val isNotInRange = Misc.getDistanceToPlayerLY(entity) > Constants.DISTANCE_THRESHOLD_FOR_DISPLAY
        return isHyperspace && isNotInRange
    }

    @Suppress("USELESS_ELVIS")
    fun createSortingButtons(assembly: WriterPanelAssembly, panel: CustomPanelAPI): CustomPanelAPI {
        val width = Constants.SELECTOR_WIDTH
        val height = Constants.SORTING_BUTTONS_HEIGHT
        val sortingPanel = panel.createCustomPanel(width, height, null)
        val buttonsContainer = sortingPanel.createUIElement(width, height, false)
        val colorBase = Misc.getBasePlayerColor()
        val colorDark = Misc.getDarkPlayerColor()
        val colorBright = Misc.getBrightPlayerColor()
        val pad = Constants.SORTING_BUTTONS_PAD
        val buttonWidth = Constants.SORTING_BUTTONS_WIDTH
        var tagState = assembly.parent.tagsState
        tagState?: let {
            assembly.parent.tagsState = hashMapOf(
                Pair(WriterPanelAssembly.EntityDisplayTags.PLANETS, true),
                Pair(WriterPanelAssembly.EntityDisplayTags.SALVAGE, true),
                Pair(WriterPanelAssembly.EntityDisplayTags.OBJECTIVES, true),
                Pair(WriterPanelAssembly.EntityDisplayTags.OTHER, true)
            )
            tagState = assembly.parent.tagsState
        }
        val firstTag: ButtonAPI = buttonsContainer.addAreaCheckbox("Planets",
            WriterPanelAssembly.EntityDisplayTags.PLANETS, colorBase, colorDark,
            colorBright, buttonWidth, height, 0f)
        firstTag.position.inTL(0f, 0f)
        firstTag.isChecked = tagState[WriterPanelAssembly.EntityDisplayTags.PLANETS] == true
        assembly.firstTagSorter = firstTag
        val secondTag: ButtonAPI = buttonsContainer.addAreaCheckbox("Salvage",
            WriterPanelAssembly.EntityDisplayTags.SALVAGE, colorBase, colorDark,
            colorBright, buttonWidth, height, 0f)
        secondTag.position.rightOfTop(firstTag, pad)
        secondTag.isChecked = tagState[WriterPanelAssembly.EntityDisplayTags.SALVAGE] == true
        assembly.secondTagSorter = secondTag
        val thirdTag: ButtonAPI = buttonsContainer.addAreaCheckbox("Objectives",
            WriterPanelAssembly.EntityDisplayTags.OBJECTIVES, colorBase, colorDark,
            colorBright, buttonWidth, height, 0f)
        thirdTag.position.rightOfTop(secondTag, pad)
        thirdTag.isChecked = tagState[WriterPanelAssembly.EntityDisplayTags.OBJECTIVES] == true
        assembly.thirdTagSorter = thirdTag
        val fourthTag: ButtonAPI = buttonsContainer.addAreaCheckbox("Other",
            WriterPanelAssembly.EntityDisplayTags.OTHER, colorBase, colorDark,
            colorBright, buttonWidth, height, 0f)
        fourthTag.position.rightOfTop(thirdTag, pad)
        fourthTag.isChecked = tagState[WriterPanelAssembly.EntityDisplayTags.OTHER] == true
        assembly.fourthTagSorter = fourthTag
        sortingPanel.addUIElement(buttonsContainer).inBR(0f, 0f)
        return sortingPanel
    }

}