package fleetjour.scripts.objects

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.SectorEntityToken
import com.fs.starfarer.api.impl.campaign.DerelictShipEntityPlugin
import com.fs.starfarer.api.ui.*
import com.fs.starfarer.api.util.Misc
import fleetjour.scripts.panel.Common
import fleetjour.scripts.panel.WriterPanelAssembly
import java.awt.Color

/**
 * @author Ontheheavens
 * @since  13.02.2023
 */

@Suppress("UNUSED_CHANGED_VALUE")
class SelectableEntity(private val entity: SectorEntityToken) {

    val entityId: String = entity.id
    lateinit var selectButton: ButtonAPI

    fun createContainer(assembly: WriterPanelAssembly, parent: CustomPanelAPI): CustomPanelAPI {
        val width = parent.position.width
        val container: CustomPanelAPI = parent.createCustomPanel(width, 40f, null)
        val labelTooltip = container.createUIElement(width, 15f, false)
        val textWidth = width - 16f
        labelTooltip.textWidthOverride = textWidth
        var text: String = entity.name
        if (entity.customPlugin is DerelictShipEntityPlugin) {
            val hull = Common.getHullClassOfDerelict(entity)
            text = entity.name + " ($hull)"
            if (hull == "") {
                text = entity.name
            }
        }
        val contentLabel = labelTooltip.addPara(text, 0f)
        contentLabel.position.inTL(1f, 2f)
        contentLabel.opacity = 0f
        this.addInteractiveButton(assembly, labelTooltip, contentLabel)
        val frontContentLabel = labelTooltip.addPara(text, 0f)
        frontContentLabel.position.inTL(1f, 2f)
        var measurement = ""
        if (Common.findTargetLocation(assembly.parent).isHyperspace) measurement = " LY"
        val distance = Misc.getRoundedValueMaxOneAfterDecimal(Common.getRelevantDistance(assembly, entity))
        val distanceLabel = labelTooltip.addPara(distance + measurement, Misc.getHighlightColor(), 0f)
        distanceLabel.position.inTR(18f, 2f)
        distanceLabel.setAlignment(Alignment.RMID)
        container.addUIElement(labelTooltip).inTL(0f, 2f)
        return container
    }

    private fun addInteractiveButton(assembly: WriterPanelAssembly, labelTooltip: TooltipMakerAPI, contentLabel: LabelAPI) {
        val selectButtonData = WriterPanelAssembly.Buttons.SELECT_ENTITY.toString() + "" + entityId
        val backgroundButtonColor = Misc.scaleColor(Misc.getBrightPlayerColor(), 0.5f)
        val transparentColor: Color = Misc.scaleAlpha(backgroundButtonColor, 0f)
        selectButton = labelTooltip.addAreaCheckbox("", selectButtonData, backgroundButtonColor,
            transparentColor, backgroundButtonColor, contentLabel.position.width + 14f,
            contentLabel.position.height + 14f, 0f
        )
        selectButton.position.inTL(-8f, -5f)
        selectButton.highlightBrightness = 1f
        val tooltip: BaseTooltipCreator = object: BaseTooltipCreator() {
            override fun isTooltipExpandable(tooltipParam: Any?): Boolean {
                return false
            }
            override fun getTooltipWidth(tooltipParam: Any?): Float {
                return 300f
            }
            override fun createTooltip(tooltip: TooltipMakerAPI?, expanded: Boolean, tooltipParam: Any?) {
                tooltip?: return
                tooltip.addSectionHeading("Entity", Alignment.MID, 1f)
                tooltip.beginGrid(300f, 1)
                var row = 0
                tooltip.addToGrid(0, row++, "Name:", entity.name, Misc.getHighlightColor())
                tooltip.addToGrid(0, row++, "Type:", Common.getEntityType(entity), Misc.getHighlightColor())
                if (entity.customPlugin is DerelictShipEntityPlugin) {
                    val hull = Common.getHullClassOfDerelict(entity)
                    tooltip.addToGrid(0, row++, "Class:", hull, Misc.getHighlightColor())
                }
                var measurement = ""
                val targetLoc = Common.findTargetLocation(assembly.parent)
                if (targetLoc.isHyperspace) measurement = " LY"
                val distance = Misc.getRoundedValueMaxOneAfterDecimal(Common.getRelevantDistance(assembly, entity))
                var distanceAnchor = "Distance from fleet:"
                if (targetLoc != Global.getSector().playerFleet.containingLocation && !targetLoc.isHyperspace) {
                    distanceAnchor = "Distance from system center:"
                }
                tooltip.addToGrid(0, row++, distanceAnchor, distance + measurement, Misc.getHighlightColor())
                tooltip.addGrid(6f)
            }
        }
        labelTooltip.addTooltipToPrevious(tooltip, TooltipMakerAPI.TooltipLocation.LEFT)
    }

}