package fleetjour.scripts.interaction.entrymanager.panel

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin
import com.fs.starfarer.api.ui.ButtonAPI
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.ui.UIComponentAPI
import com.fs.starfarer.api.util.Misc
import java.awt.Color

/**
 * @author Ontheheavens
 * @since  20.02.2023
 */

class IntelEntry(val plugin: IntelInfoPlugin) {

    private lateinit var tooltip: TooltipMakerAPI
    private lateinit var info: UIComponentAPI
    lateinit var button: ButtonAPI

    fun brighten() {
        tooltip.bringComponentToTop(info)
    }

    fun darken() {
        tooltip.bringComponentToTop(button)
    }

    fun createEntry(section: CustomPanelAPI, entry: IntelInfoPlugin): Pair<CustomPanelAPI, Float> {
        val width = PanelConstants.ENTRIES_LIST_WIDTH
        val entryContainer = section.createCustomPanel(width, 20f, EntryPanelOverseer.plugin)
        val entryTooltip = entryContainer.createUIElement(width - 8f, 20f, false)
        val dummy = entryTooltip.beginImageWithText(Global.getSettings().getSpriteName("intel",
            "border"), 50f)
        entry.createIntelInfo(dummy, IntelInfoPlugin.ListInfoMode.INTEL)
        dummy.addImage(entry.icon, 40f, 40f, 0f)
        val image = dummy.prev
        image.position.inLMid(-55f)
        image.position.setYAlignOffset(-1f)
        dummy.addSpacer(-40f)
        entryTooltip.addImageWithText(4f)
        val entryInfo = entryTooltip.prev
        val backgroundButtonColor = Misc.scaleColor(Misc.getBrightPlayerColor(), 0.95f)
        val transparentColor: Color = Misc.scaleAlpha(backgroundButtonColor, 0f)
        val selectorHeight = entryInfo.position.height + 28f
        val selectEntryButton = entryTooltip.addAreaCheckbox(
            "", null, backgroundButtonColor,
            transparentColor, backgroundButtonColor, width - 2f, selectorHeight, 0f)
        selectEntryButton.position.inTL(-7f, -10f)
        val buttonWrapper = this.createButton(selectEntryButton)
        ButtonListener.getIndex()?.add(buttonWrapper)
        entryTooltip.bringComponentToTop(entryInfo)
        entryContainer.addUIElement(entryTooltip).inTL(0f, 0f)
        tooltip = entryTooltip
        info = entryInfo
        button = selectEntryButton
        return Pair(entryContainer, selectorHeight)
    }

    private fun createButton(selectEntryButton: ButtonAPI): InteractiveButton {
        val buttonWrapper = object : InteractiveButton(selectEntryButton, Type.ENTRY) {
            private var checkedLastFrame = false
            private var othersUnchecked = false
            private var checkedAtCreation = false
            override fun applyEffect() {
                if (EntriesSection.selectedEntry == this@IntelEntry.plugin) return
                EntriesSection.selectedEntry = this@IntelEntry.plugin
                this.instance.highlight()
                this.instance.isEnabled = false
                this.instance.setButtonDisabledPressedSound("ui_button_pressed")
                EntryPanelAssembly.controlButtonsRedrawQueued = true
            }
            fun affectOthersInGroup() {
                for (button in ButtonListener.getIndex()!!) {
                    if (button.type !== this.type) continue
                    if (button === this) continue
                    button.instance.isChecked = false
                    button.instance.isEnabled = true
                    button.instance.unhighlight()
                }
            }
            override fun check() {
                val instance: ButtonAPI = this.instance
                if (!instance.isChecked) {
                    checkedLastFrame = false
                    othersUnchecked = false
                    return
                }
                if (!othersUnchecked) {
                    affectOthersInGroup()
                    othersUnchecked = true
                }
                if (checkedAtCreation) {
                    checkedAtCreation = false
                    checkedLastFrame = true
                    return
                }
                if (!checkedLastFrame) {
                    applyEffect()
                    checkedLastFrame = true
                }
            }
        }
        return buttonWrapper
    }

}