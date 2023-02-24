package fleetjour.scripts.objects

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.SectorEntityToken
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin.ArrowData
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin.ListInfoMode
import com.fs.starfarer.api.impl.campaign.intel.misc.FleetLogIntel
import com.fs.starfarer.api.ui.SectorMapAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import fleetjour.scripts.panel.Common

/**
 * @author Ontheheavens
 * @since  14.02.2023
 */

class JournalEntry(entity: SectorEntityToken, private val title: String, private val brief: String,
    private val contents: ArrayList<String>) : FleetLogIntel() {

    private val target: SectorEntityToken = this.makeDoubleWithSameOrbit(entity)

    private val optionalType: String = Common.getHullClassOfDerelict(entity)

    private var shouldRemove = false

    init {
        this.setRemoveTrigger(entity)
    }

    override fun shouldRemoveIntel(): Boolean {
        return super.shouldRemoveIntel() || shouldRemove
    }

    override fun reportRemovedIntel() {
        super.reportRemovedIntel()
        target.containingLocation.removeEntity(target)
    }

    private fun makeDoubleWithSameOrbit(entity: SectorEntityToken): SectorEntityToken {
        val copy = entity.containingLocation.createToken(entity.location.x, entity.location.y)
        if (entity.orbit != null) {
            copy.orbit = entity.orbit.makeCopy()
        }
        copy.containingLocation.addEntity(copy)
        setRemoveTrigger(copy)
        return copy
    }

    override fun createIntelInfo(info: TooltipMakerAPI, mode: ListInfoMode?) {
        val color = getTitleColor(mode)
        val highlightColor = Misc.getHighlightColor()
        info.addPara(name, color, 0f)
        info.addSpacer(3f)
        info.setParaFontColor(Misc.getGrayColor())
        this.bullet(info)
        if (brief != "") {
            info.addPara(brief, 0f)
        }
        if (optionalType != "") {
            info.addPara("Class: %s", 0f, Misc.getHighlightColor(), optionalType)
        }
        info.addPara("Location: %s", 0f, highlightColor, target.containingLocation.name)
        val days = daysSincePlayerVisible
        if (days >= 1) {
            this.addDays(info, "ago.", days)
        }
    }

    override fun createSmallDescription(info: TooltipMakerAPI, width: Float, height: Float) {
        if (contents.size > 0) {
            info.addSpacer(10f)
            for (paragraph in contents) {
                info.addPara(paragraph, 2f)
            }
        } else {
            info.addSpacer(-10f)
        }
        addDeleteButton(info, width)
    }

    override fun getName(): String {
        return title
    }

    override fun getSmallDescriptionTitle(): String {
        return name
    }

    override fun getMapLocation(map: SectorMapAPI?): SectorEntityToken {
        return target
    }

    override fun getCommMessageSound(): String? {
        return if (sound != null) sound else getSoundMinorMessage()
    }

    override fun getArrowData(map: SectorMapAPI?): List<ArrowData>? {
        val playerFleet = Global.getSector().playerFleet ?: return null
        val result: MutableList<ArrowData> = ArrayList()
        if (playerFleet.containingLocation === target.containingLocation && playerFleet.containingLocation != null &&
            !playerFleet.containingLocation.isHyperspace) return null
        val arrow = ArrowData(playerFleet, target)
        arrow.color = factionForUIColors.baseUIColor
        result.add(arrow)
        return result
    }

}