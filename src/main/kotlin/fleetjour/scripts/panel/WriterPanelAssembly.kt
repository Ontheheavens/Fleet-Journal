package fleetjour.scripts.panel

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.SectorEntityToken
import com.fs.starfarer.api.campaign.StarSystemAPI
import com.fs.starfarer.api.ui.*
import com.fs.starfarer.api.ui.Fonts.GROUP_NUM_FONT
import com.fs.starfarer.api.ui.Fonts.ORBITRON_24AABOLD
import com.fs.starfarer.api.util.Misc
import fleetjour.scripts.EntryWriter
import fleetjour.scripts.objects.SelectableEntity
import fleetjour.settings.SettingsHolder

/**
 * @author Ontheheavens
 * @since  06.02.2023
 */

class WriterPanelAssembly(private val intel: EntryWriter, panel: CustomPanelAPI, width: Float, height : Float) {

    enum class Buttons {
        ADD_PARAGRAPH,
        APPEND_TO_PARAGRAPH,
        MOVE_PARAGRAPH_UP,
        MOVE_PARAGRAPH_DOWN,
        DELETE_WORD,
        REMOVE_PARAGRAPH,
        SELECT_PARAGRAPH,
        CURRENT_LOCATION,
        SELECT_LOCATION,
        SYSTEM_CENTER,
        ORBIT_FOCUS,
        SELECT_ENTITY,
        WRITE_ENTRY,
        MANAGE_ENTRIES,
        SET_TITLE,
        SET_BRIEF,
        ICON_FORWARD,
        ICON_BACKWARD
    }

    enum class EntityDisplayTags {
        PLANETS,
        SALVAGE,
        OBJECTIVES,
        OTHER
    }

    var selectableEntities: ArrayList<SelectableEntity> = arrayListOf()

    val mainPanel: CustomPanelAPI = panel
    private val panelWidth: Float = width
    private val panelHeight: Float = height
    val parent: EntryWriter = intel

    private lateinit var headerInstance: CustomPanelAPI

    private lateinit var headerContainer: TooltipMakerAPI
    private lateinit var iconComponent: UIComponentAPI

    private lateinit var draftPanelInstance: CustomPanelAPI
    lateinit var inputFieldInstance: TextFieldAPI
    lateinit var titleFieldInstance: TextFieldAPI
    lateinit var briefFieldInstance: TextFieldAPI
    private lateinit var selectorPanelInstance: CustomPanelAPI

    private lateinit var targetStatePanel: TooltipMakerAPI
    private lateinit var entitiesSelectorPanel: CustomPanelAPI

    lateinit var writeButton: ButtonAPI

    lateinit var removeButton: ButtonAPI
    lateinit var upButton: ButtonAPI
    lateinit var downButton: ButtonAPI
    lateinit var appendButton: ButtonAPI
    lateinit var deleteButton: ButtonAPI

    lateinit var currentLocationButton: ButtonAPI
    lateinit var selectLocationButton: ButtonAPI

    lateinit var systemCenterButton: ButtonAPI
    lateinit var orbitFocusButton: ButtonAPI

    lateinit var firstTagSorter: ButtonAPI
    lateinit var secondTagSorter: ButtonAPI
    lateinit var thirdTagSorter: ButtonAPI
    lateinit var fourthTagSorter: ButtonAPI

    fun cycleIconsForward() {
        if (parent.selectedIconIndex + 1 == Constants.icons.size) {
            parent.selectedIconIndex = 0
        } else {
            parent.selectedIconIndex++
        }
    }

    fun cycleIconsBackward() {
        if (parent.selectedIconIndex == 0) {
            parent.selectedIconIndex = Constants.icons.size - 1
        } else {
            parent.selectedIconIndex--
        }
    }

    fun assemblePanel() {
        headerInstance = this.createHeader()
        mainPanel.addComponent(headerInstance).inTL(10f, 0f)
        val editorContainer = this.createEditorContainer()
        mainPanel.addComponent(editorContainer).inBL(0f, 0f)
        selectorPanelInstance = EntitySelectorPanel.create(this, panelHeight)
        mainPanel.addComponent(selectorPanelInstance).belowRight(headerInstance, 0f)
        val sortingButtons: CustomPanelAPI = EntitySelectorPanel.createSortingButtons(this, mainPanel)
        mainPanel.addComponent(sortingButtons).inBR(Constants.RIGHTSIDE_OFFSET - 10f, 0f)
        this.renderStateContainer()
        this.renderDraftPanel()
        this.renderEntitiesContainer()
        if (this.intel.draftParagraphs.size > 0) {
            DraftPanel.highlightSelectedParagraph(this)
        }
    }

    private fun createHeader(): CustomPanelAPI {
        val width = panelWidth - Constants.RIGHTSIDE_OFFSET
        val headerPanel: CustomPanelAPI = mainPanel.createCustomPanel(width, Constants.HEADER_HEIGHT, null)
        headerContainer = headerPanel.createUIElement(width, 2f, false)
        val image = addHeaderImage(headerContainer)
        this.createHeaderTextFields(headerContainer, image)
        headerContainer.setButtonFontVictor14()
        writeButton = headerContainer.addButton("Write entry", Buttons.WRITE_ENTRY, 140f, 28f, 0f)
        writeButton.position.inTR(0f, 57f)
        writeButton.isEnabled = ButtonChecker.shouldEnableWriteButton(parent)
        if (SettingsHolder.MANAGE_ENTRIES_BUTTON_ENABLED) {
            val manageButton = headerContainer.addButton("Manage entries", Buttons.MANAGE_ENTRIES, 140f, 28f, 0f)
            manageButton.position.aboveLeft(writeButton, 16f)
        }
        headerContainer.addSpacer(-50f)
        val bottomLine: ButtonAPI = headerContainer.addButton("", null,
            width, 0f, 0f)
        bottomLine.position.belowLeft(image, 10f)
        headerPanel.addUIElement(headerContainer).inTL(0f, 0f)
        return headerPanel
    }

    private fun createHeaderTextFields(headerContainer: TooltipMakerAPI, imageAnchor: UIComponentAPI) {
        val fieldWidth = Constants.TITLE_FIELD_WIDTH
        headerContainer.setParaFont(ORBITRON_24AABOLD)
        val titleLabel = headerContainer.addPara("Title:", Misc.getBasePlayerColor(), 24f)
        headerContainer.setParaFontDefault()
        titleLabel.position.rightOfTop(imageAnchor, 12f)
        titleLabel.position.setYAlignOffset(-15f)
        val titleLabelAnchor = headerContainer.prev
        val titleAnchor = headerContainer.addSpacer(-24f)
        titleFieldInstance = headerContainer.addTextField(fieldWidth, 0f)
        titleFieldInstance.text = this.parent.titleFieldValue
        val titleTextField = headerContainer.prev
        titleTextField.position.rightOfMid(titleAnchor, 60f)
        val setTitleButton = headerContainer.addButton("Set", Buttons.SET_TITLE, 40f, 28f, 0f)
        setTitleButton.position.rightOfMid(titleTextField, 10f)
        headerContainer.setParaFont(ORBITRON_24AABOLD)
        val briefLabel = headerContainer.addPara("Brief:", Misc.getBasePlayerColor(), 24f)
        headerContainer.setParaFontDefault()
        briefLabel.position.belowLeft(titleLabelAnchor, 20f)
        val briefAnchor = headerContainer.addSpacer(-24f)
        briefFieldInstance = headerContainer.addTextField(fieldWidth, 0f)
        briefFieldInstance.text = this.parent.briefFieldValue
        val briefTextField = headerContainer.prev
        briefTextField.position.rightOfMid(briefAnchor, 60f)
        val setBriefButton = headerContainer.addButton("Set", Buttons.SET_BRIEF, 40f, 28f, 0f)
        setBriefButton.position.rightOfMid(briefTextField, 10f)

        createIconWidget(headerContainer, setTitleButton)
    }

    fun renderDraftIcon() {
        if (this::iconComponent.isInitialized) {
            headerContainer.removeComponent(iconComponent)
        }
        val iconID = Constants.icons[parent.selectedIconIndex]
        headerContainer.addImage(iconID, 0f)
        iconComponent = headerContainer.prev

        val tooltip: BaseTooltipCreator = object: BaseTooltipCreator() {
            override fun getTooltipWidth(tooltipParam: Any?): Float {
                return 300f
            }
            override fun createTooltip(tooltip: TooltipMakerAPI?, expanded: Boolean, tooltipParam: Any?) {
                tooltip?: return
                tooltip.addSectionHeading("Icon Info", Alignment.MID, 1f)
                tooltip.beginGrid(300f, 1)
                var row = 0
                tooltip.addToGrid(0, 0, "Selected icon:", "#" + parent.selectedIconIndex, Misc.getHighlightColor())
                tooltip.addToGrid(0, 1, "Total count:", Constants.icons.size.toString(), Misc.getHighlightColor())
                tooltip.addGrid(6f)
            }
        }
        headerContainer.addTooltipTo(tooltip, iconComponent, TooltipMakerAPI.TooltipLocation.BELOW)

        val imagePosition = iconComponent.position
        imagePosition.rightOfMid(titleFieldInstance, 70f)
        imagePosition.setYAlignOffset(-22f)
    }

    private fun createIconWidget(headerContainer: TooltipMakerAPI, setTitleButtonAnchor: UIComponentAPI) {
        val colorBase = Misc.scaleAlpha(Misc.getBasePlayerColor(), 1f)
        val colorDark = Misc.scaleAlpha(Misc.getDarkPlayerColor(), 1f)

        val separator: LabelAPI = headerContainer.addSectionHeading("", Alignment.MID, 0f)
        val separatorPosition = separator.position
        separatorPosition.setSize(1f, 72f)
        separatorPosition.rightOfMid(setTitleButtonAnchor, 10f)
        separatorPosition.setYAlignOffset(-22f)

        val iconBackwardButton = headerContainer.addButton("<", Buttons.ICON_BACKWARD, colorBase, colorDark,
            Alignment.MID, CutStyle.TOP, 38f, 14f, 0f)
        iconBackwardButton.position.rightOfMid(setTitleButtonAnchor, 21f)
        iconBackwardButton.position.setYAlignOffset(7f)

        renderDraftIcon()

        val iconForwardButton = headerContainer.addButton(">", Buttons.ICON_FORWARD, colorBase, colorDark,
            Alignment.MID, CutStyle.BOTTOM, 38f, 14f, 0f)
        iconForwardButton.position.rightOfMid(setTitleButtonAnchor, 21f)
        iconForwardButton.position.setYAlignOffset(-51f)
    }

    private fun addHeaderImage(headerContainer: TooltipMakerAPI): UIComponentAPI {
        val imageName: String = Global.getSettings().getIndustrySpec("highcommand").imageName
        headerContainer.addImage(imageName, 0f)
        val image: UIComponentAPI = headerContainer.prev
        image.position.inTL(0f, 0f)

        val tooltip: BaseTooltipCreator = object: BaseTooltipCreator() {
            override fun getTooltipWidth(tooltipParam: Any?): Float {
                return 300f
            }
            override fun createTooltip(tooltip: TooltipMakerAPI?, expanded: Boolean, tooltipParam: Any?) {
                tooltip?: return
                tooltip.addSectionHeading("Hints", Alignment.MID, 0f)
                tooltip.setBulletedListMode("  - ")
                val highlightColor = Misc.getHighlightColor()
                tooltip.addPara("Mouse over entity and press %s to write quick journal entry of it", 6f, highlightColor, "J")
                tooltip.addPara("Hold %s and press %s to open journal with moused over entity as target",
                    4f, highlightColor, "Control", "J")
                tooltip.addSpacer(1f)
            }
        }
        headerContainer.addTooltipToPrevious(tooltip, TooltipMakerAPI.TooltipLocation.BELOW)

        return image
    }

    fun renderDraftPanel() {
        if (this::draftPanelInstance.isInitialized) {
            mainPanel.removeComponent(draftPanelInstance)
        }
        val draftPanel = DraftPanel.create(this, panelWidth, panelHeight)
        mainPanel.addComponent(draftPanel).leftOfTop(selectorPanelInstance, 4f)
        writeButton.isEnabled = ButtonChecker.shouldEnableWriteButton(parent)
        deleteButton.isEnabled = ButtonChecker.shouldEnableDeleteButton(parent)
        draftPanelInstance = draftPanel
    }

    fun renderStateContainer() {
        if (this::targetStatePanel.isInitialized) {
            selectorPanelInstance.removeComponent(targetStatePanel)
        }
        val currentLocation = Global.getSector().playerFleet.containingLocation.id
        currentLocationButton.isEnabled = parent.selectedTargetLocation != currentLocation
        systemCenterButton.isEnabled = this.shouldEnableSystemCenterButton(intel)
        orbitFocusButton.isEnabled = this.shouldEnableOrbitFocusButton(intel)
        val statePanel = EntitySelectorPanel.createStatePanel(this, selectorPanelInstance)
        selectorPanelInstance.addUIElement(statePanel).inTL(8f, 38f)
        targetStatePanel = statePanel
    }

    fun renderEntitiesContainer() {
        if (this::entitiesSelectorPanel.isInitialized) {
            selectorPanelInstance.removeComponent(entitiesSelectorPanel)
        }
        val entitiesContainer = EntitySelectorPanel.createEntitiesSelector(this, selectorPanelInstance)
        selectorPanelInstance.addComponent(entitiesContainer).inTL(1f, Constants.TARGET_SECTION_HEIGHT + 60f)
        entitiesSelectorPanel = entitiesContainer
    }

    private fun createEditorContainer(): CustomPanelAPI {
        val editorPanel: CustomPanelAPI = mainPanel.createCustomPanel(panelWidth, Constants.EDITOR_HEIGHT, null)
        val editorContainer = editorPanel.createUIElement(panelWidth, 2f, false)
        inputFieldInstance = editorContainer.addTextField(panelWidth - Constants.RIGHTSIDE_OFFSET,
            20f, GROUP_NUM_FONT, 4f)
        inputFieldInstance.text = this.parent.inputFieldValue
        if (inputFieldInstance.text == "") {
            inputFieldInstance.text = "Write..."
        }
        val inputField: UIComponentAPI = editorContainer.prev
        inputField.position.inTL(0f, 0f)
        this.addEditorButtons(editorContainer, inputField)
        val textSize = intel.draftParagraphs.size
        if (textSize == 0) {
            removeButton.isEnabled = false
            appendButton.isEnabled = false
            deleteButton.isEnabled = false
        }
        if (textSize < 2) {
            upButton.isEnabled = false
            downButton.isEnabled = false
        }
        editorPanel.addUIElement(editorContainer).inTL(10f, -5f)
        return editorPanel
    }

    private fun addEditorButtons(editorContainer: TooltipMakerAPI, anchor: UIComponentAPI) {
        editorContainer.addPara("Paragraph:", Misc.getBasePlayerColor(), 0f)
        val paragraphLabel = editorContainer.prev
        paragraphLabel.position.belowLeft(anchor, 16f)
        paragraphLabel.position.setXAlignOffset(10f)
        val buttonsAnchor: UIComponentAPI = editorContainer.addSpacer(0f)
        buttonsAnchor.position.setYAlignOffset(8f)
        val buttonWidth = 75f
        val buttonHeight = 25f
        val commitButton: ButtonAPI = editorContainer.addButton("Add", Buttons.ADD_PARAGRAPH,
            buttonWidth, buttonHeight, 0f)
        commitButton.position.rightOfMid(buttonsAnchor, 70f)
        removeButton = editorContainer.addButton("Remove", Buttons.REMOVE_PARAGRAPH,
            buttonWidth, buttonHeight, 0f)
        removeButton.position.rightOfMid(commitButton, 10f)
        upButton = editorContainer.addButton("Up", Buttons.MOVE_PARAGRAPH_UP,
            buttonWidth, buttonHeight, 0f)
        upButton.position.rightOfMid(removeButton, 10f)
        downButton = editorContainer.addButton("Down", Buttons.MOVE_PARAGRAPH_DOWN,
            buttonWidth, buttonHeight, 0f)
        downButton.position.rightOfMid(upButton, 10f)
        val contentLabel = editorContainer.addPara("Content:", Misc.getBasePlayerColor(), 0f)
        contentLabel.position.rightOfMid(downButton, 10f)
        appendButton = editorContainer.addButton("Append", Buttons.APPEND_TO_PARAGRAPH,
            buttonWidth, buttonHeight, 0f)
        appendButton.position.rightOfMid(downButton, 74f)
        deleteButton = editorContainer.addButton("Delete", Buttons.DELETE_WORD,
            buttonWidth, buttonHeight, 0f)
        deleteButton.position.rightOfMid(appendButton, 10f)
    }

    private fun shouldEnableSystemCenterButton(parent: EntryWriter): Boolean {
        val selectedLocation = Common.findTargetLocation(parent)
        if (selectedLocation !is StarSystemAPI) return false
        val system: StarSystemAPI = selectedLocation
        val center: SectorEntityToken = system.center
        return center != Common.findTargetEntity(parent)
    }

    private fun shouldEnableOrbitFocusButton(parent: EntryWriter): Boolean {
        val orbitFocus = Common.getCurrentOrbitFocus() ?: return false
        return orbitFocus.id != parent.selectedTargetEntity
    }

    fun updateTitleByDefault() {
        if (parent.customTitleSet) return
        val entity = Common.findTargetEntity(parent)
        updateTitle(entity)
    }

    private fun updateTitle(entityToken: SectorEntityToken) {
        val title = "Notable " + Common.getTypeForIntelInfo(entityToken)
        this.titleFieldInstance.text = title
        parent.titleFieldValue = title
        writeButton.isEnabled = ButtonChecker.shouldEnableWriteButton(parent)
    }

}