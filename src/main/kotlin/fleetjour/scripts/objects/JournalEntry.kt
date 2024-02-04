package fleetjour.scripts.objects

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.SectorEntityToken
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin.ArrowData
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin.ListInfoMode
import com.fs.starfarer.api.impl.campaign.intel.misc.FleetLogIntel
import com.fs.starfarer.api.ui.IntelUIAPI
import com.fs.starfarer.api.ui.SectorMapAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import fleetjour.scripts.EntryWriter
import fleetjour.scripts.panel.Common
import org.lwjgl.input.Keyboard

/**
 * @author Ontheheavens
 * @since  14.02.2023
 */

open class JournalEntry(
    entity: SectorEntityToken,
    val title: String,
    val brief: String,
    val contents: List<String>
) : FleetLogIntel() {

    private val target: SectorEntityToken = this.makeDoubleWithSameOrbit(entity)

    private val optionalType: String = Common.getHullClassOfDerelict(entity)

    private var shouldRemove = false

    private val buttonRewrite = "button_rewrite"

    val targetEntityToken: SectorEntityToken
        get() = target

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
        if (brief.startsWith("Name: ")) {
            info.addPara("Name: %s", 0f, highlightColor, brief.substringAfter("Name: "))
        } else if (brief != "") {
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
        addRewriteButton(info, width)
        info.addSpacer(-15f)
        addDeleteButton(info, width)
    }

    private fun addRewriteButton(info: TooltipMakerAPI, width: Float) {
        val opad = 10f
        val button = info.addButton(
            "Rewrite log entry", buttonRewrite,
            factionForUIColors.baseUIColor, factionForUIColors.darkUIColor,
            width.toInt().toFloat(), 20f, opad * 2f
        )
        button.setShortcut(Keyboard.KEY_R, true)
    }

    override fun buttonPressConfirmed(buttonId: Any, ui: IntelUIAPI) {
        super.buttonPressConfirmed(buttonId, ui)
        if (buttonId === buttonRewrite) {
            val sector = Global.getSector()
            val intelManager = sector.intelManager
            var writer: IntelInfoPlugin? = intelManager.getFirstIntel(EntryWriter::class.java) ?: return
            writer = writer as EntryWriter
            writer.beginEntryRewrite(this)
            return
        }
        ui.updateUIForItem(this)
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