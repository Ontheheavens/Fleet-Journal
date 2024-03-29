package fleetjour.scripts.panel

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.*
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin
import com.fs.starfarer.api.campaign.econ.MarketAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.DerelictShipEntityPlugin
import com.fs.starfarer.api.impl.campaign.FusionLampEntityPlugin
import com.fs.starfarer.api.impl.campaign.HiddenCacheEntityPlugin
import com.fs.starfarer.api.impl.campaign.SupplyCacheEntityPlugin
import com.fs.starfarer.api.impl.campaign.ids.Conditions
import com.fs.starfarer.api.impl.campaign.ids.Entities
import com.fs.starfarer.api.impl.campaign.ids.Tags
import com.fs.starfarer.api.impl.campaign.terrain.DebrisFieldTerrainPlugin
import com.fs.starfarer.api.ui.ButtonAPI
import com.fs.starfarer.api.ui.LabelAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.ui.UIComponentAPI
import com.fs.starfarer.api.util.Misc
import com.fs.starfarer.campaign.BaseCampaignEntity
import fleetjour.scripts.EntryWriter

/**
 * @author Ontheheavens
 * @since  10.02.2023
 */

object Common {

    const val AUTO_LOGGING_PREFIX = "Observed "

    fun selectDefaultTargetEntity(intel: EntryWriter): String {
        val targetLocation = this.findTargetLocation(intel)
        if (targetLocation.isHyperspace) {
            val closestAnchor = this.findClosestJumpPoint()
            return closestAnchor.id
        } else if (targetLocation is StarSystemAPI) {
            val targetSystem: StarSystemAPI = targetLocation
            var center: SectorEntityToken? = targetSystem.star
            center?: let {
                center = fetchFirstEligibleEntity(targetSystem)
            }
            return center!!.id
        }
        return Global.getSector().playerFleet.id
    }

    fun fetchFirstEligibleEntity(system: StarSystemAPI): SectorEntityToken {
        val allEntities = system.allEntities
        val filtered = allEntities.filter { token ->
            token is BaseCampaignEntity && !token.isDiscoverable && token.sensorProfile == 0f
        }
        return filtered[0]
    }

    fun getVariantOfDerelict(plugin: DerelictShipEntityPlugin): ShipVariantAPI? {
        var shipVariant = Global.getSettings().getVariant(plugin.data.ship.variantId)
        shipVariant?: let {
            shipVariant = plugin.data.ship.variant
        }
        return shipVariant
    }

    fun getHullClassOfDerelict(entity: SectorEntityToken): String {
        entity.customPlugin?: return ""
        if (entity.customPlugin !is DerelictShipEntityPlugin) return ""
        val plugin = entity.customPlugin as DerelictShipEntityPlugin
        var shipVariant = Global.getSettings().getVariant(plugin.data.ship.variantId)
        shipVariant?: let {
            shipVariant = plugin.data.ship.variant
        }
        shipVariant?. let {
            return shipVariant.hullSpec.hullNameWithDashClass
        } ?: return ""
    }

    fun addLine(tooltip: TooltipMakerAPI, width: Float): ButtonAPI {
        val line = tooltip.addButton("", null, Misc.getBasePlayerColor(),
            Misc.getDarkPlayerColor(), width, 0f, 0f)
        line.isEnabled = false
        line.highlight()
        return line
    }

    fun createLineHeading(tooltip: TooltipMakerAPI, text: String, width: Float, labelPad: Float): UIComponentAPI {
        val labelWidth: Float = tooltip.computeStringWidth(text)
        val lineWidth: Float = (width - (labelWidth + (labelPad * 2))) / 2f
        val leftLine: ButtonAPI = tooltip.addButton("", null, lineWidth, 0f, 0f)
        val result: UIComponentAPI = tooltip.prev
        val label: LabelAPI = tooltip.addPara(text, Misc.getBasePlayerColor(), 0f)
        label.position.rightOfMid(leftLine, labelPad)
        val rightLine: ButtonAPI = tooltip.addButton("", null, lineWidth, 0f, 0f)
        rightLine.position.rightOfMid(leftLine, (labelPad * 2) + labelWidth)
        return result
    }

    fun findTargetLocation(intel: EntryWriter): LocationAPI {
        val locationId: String = intel.selectedTargetLocation
        var targetLocation: LocationAPI = Global.getSector().playerFleet.containingLocation
        for (location in Global.getSector().allLocations) {
            if (location.id.equals(locationId)) {
                targetLocation = location
                break
            }
        }
        return targetLocation
    }

    fun findTargetEntity(intel: EntryWriter): SectorEntityToken {
        val entityId: String = intel.selectedTargetEntity
        val targetLocation: LocationAPI = this.findTargetLocation(intel)
        val entityInTargetLocation: SectorEntityToken? = targetLocation.getEntityById(entityId)
        entityInTargetLocation?.let {
            return entityInTargetLocation
        }
        val entityInSector = Global.getSector().getEntityById(entityId)
        entityInSector?.let {
            return entityInSector
        }
        if (targetLocation.isNebula) {
            val system = targetLocation as StarSystemAPI
            return this.fetchFirstEligibleEntity(system)
        }
        val fleet = Global.getSector().playerFleet
        val fallback = fleet.containingLocation.createToken(fleet.location)
        fallback.name = "Current coordinates"
        return fallback
    }

    fun getCurrentOrbitFocus(): SectorEntityToken? {
        var currentFocus = Global.getSector().playerFleet.orbitFocus ?: return null
        if (currentFocus is CampaignTerrainAPI && currentFocus.orbitFocus != null) {
            currentFocus = currentFocus.orbitFocus
        }
        return currentFocus
    }

    fun getDiscoveredEntitiesInLocation(assembly: WriterPanelAssembly, location: LocationAPI): List<SectorEntityToken> {
        val allEntities: List<SectorEntityToken> = location.allEntities
        val filtered = allEntities.filter { token -> this.shouldDisplayEntity(assembly, token) &&
                !token.isDiscoverable && token.sensorProfile == 0f }
        return filtered
    }

    fun getRelevantDistance(assembly: WriterPanelAssembly, token: SectorEntityToken): Float {
        val targetLocation = this.findTargetLocation(assembly.parent)
        val playerFleet = Global.getSector().playerFleet
        val result: Float = if (targetLocation.isHyperspace) {
            Misc.getDistanceToPlayerLY(token)
        } else if (targetLocation == playerFleet.containingLocation) {
            Misc.getDistance(token.location, playerFleet.location)
        } else {
            val systemCenter = token.starSystem.center
            Misc.getDistance(token.location, systemCenter.location)
        }
        return result
    }

    fun getDistanceToPlayerInUnits(token: SectorEntityToken): Float {
        val playerLocation = Global.getSector().playerFleet.location
        return Misc.getDistance(token.location, playerLocation)
    }

    fun getDistanceToPlayerInLY(token: SectorEntityToken): Float {
        return Misc.getDistanceToPlayerLY(token)
    }

    fun getDistanceToSystemCenter(token: SectorEntityToken): Float {
        val systemCenter = token.starSystem.center
        return Misc.getDistance(token.location, systemCenter.location)
    }

    private fun shouldDisplayEntity(assembly: WriterPanelAssembly, entity: SectorEntityToken): Boolean {
        if (assembly.parent.tagsState[WriterPanelAssembly.EntityDisplayTags.PLANETS] == true && entity is PlanetAPI) {
            return true
        }
        if (assembly.parent.tagsState[WriterPanelAssembly.EntityDisplayTags.SALVAGE] == true) {
            when {
                entity.customPlugin is DerelictShipEntityPlugin -> return true
                entity.customPlugin is DebrisFieldTerrainPlugin -> return true
                entity.hasTag(Tags.SALVAGEABLE) || entity.hasTag(Tags.WRECK) -> return true
            }
        }
        if (assembly.parent.tagsState[WriterPanelAssembly.EntityDisplayTags.OBJECTIVES] == true) {
            when {
                entity.hasTag(Tags.COMM_RELAY) || entity.hasTag(Tags.NAV_BUOY) ||
                entity.hasTag(Tags.SENSOR_ARRAY) || entity.hasTag (Tags.STABLE_LOCATION) -> return true
                }
            }
        if (assembly.parent.tagsState[WriterPanelAssembly.EntityDisplayTags.OTHER] == true) {
            when {
                entity is OrbitalStationAPI -> return true
                entity is JumpPointAPI -> return true
                entity.hasTag(Tags.STATION) ||
                    entity.hasTag(Tags.GATE) ||
                    entity.hasTag(Tags.STELLAR_MIRROR) ||
                    entity.hasTag(Tags.STELLAR_SHADE) ||
                    entity.hasTag(Tags.CRYOSLEEPER) ||
                    entity.hasTag(Tags.CORONAL_TAP) ||
                    entity.hasTag(Tags.WARNING_BEACON) -> return true
            }
        }
        return false
    }

    fun getEntityType(entity: SectorEntityToken): String {
        when {
            entityIsProbe(entity) -> return "Probe"
            entity is PlanetAPI -> return entity.typeNameWithWorld
            entity is OrbitalStationAPI -> return "Orbital Station"
            entity is JumpPointAPI -> kotlin.run {
                val jumpPoint: JumpPointAPI = entity
                when {
                    jumpPoint.isStarAnchor -> return "Star Gravity Well"
                    jumpPoint.isGasGiantAnchor -> "Gas Giant Gravity Well"
                    else -> return "Jump Point"
                }
            }
            entity.hasTag(Tags.COMM_RELAY) ||
                    entity.hasTag(Tags.NAV_BUOY) ||
                    entity.hasTag(Tags.SENSOR_ARRAY) ||
                    entity.hasTag(Tags.STABLE_LOCATION) -> return "Objective"
            entity.hasTag(Tags.STATION) -> return "Station"
            entity.hasTag(Tags.GATE) -> return "Gate"
            entity.hasTag(Tags.STELLAR_MIRROR) -> return "Stellar Mirror"
            entity.hasTag(Tags.STELLAR_SHADE) -> return "Stellar Shade"
            entityIsCryosleeper(entity) -> return "Cryosleeper"
            entity.hasTag(Tags.CORONAL_TAP) -> return "Coronal Tap"
            entity.hasTag(Tags.WARNING_BEACON) -> return "Warning Beacon"
            (entity is CampaignTerrainAPI) &&
                    (entity.plugin is DebrisFieldTerrainPlugin) -> return "Debris Field"
            entity.customPlugin is DerelictShipEntityPlugin -> return "Derelict"
            entity.customPlugin is FusionLampEntityPlugin -> return "Fusion Lamp"
            entity.customPlugin is SupplyCacheEntityPlugin || entity.customPlugin is HiddenCacheEntityPlugin ->
                return "Cache"
            entity.hasTag(Tags.WRECK) -> return "Wreck"
            entity.hasTag(Tags.SALVAGEABLE) -> return "Salvageable"
            else -> return "Notable Entity"
        }
        return "Notable Entity"
    }

    fun getTypeForIntelInfo(entity: SectorEntityToken): String {
        when {
            entityIsProbe(entity) -> return "Probe"
            entity is JumpPointAPI -> return "Point"
            entity is CampaignFleetAPI -> return "Fleet"
            entity is AsteroidAPI -> return "Asteroid"
            entity.hasTag(Tags.WRECK) -> return "Wreck"
            entity.hasTag(Tags.GATE) -> return "Gate"
            entityIsCryosleeper(entity) -> return "Cryosleeper"
            entity.hasTag(Tags.STELLAR_MIRROR) ||
                    entity.hasTag(Tags.STELLAR_SHADE) ||
                    entity.hasTag(Tags.CORONAL_TAP) -> return "Megastructure"
            entity.hasTag(Tags.WARNING_BEACON) -> return "Beacon"
            entity.customPlugin is DerelictShipEntityPlugin -> return "Derelict"
            entity.customPlugin is SupplyCacheEntityPlugin || entity.customPlugin is HiddenCacheEntityPlugin ->
                return "Cache"
            (entity is CampaignTerrainAPI) && (entity.plugin is DebrisFieldTerrainPlugin) -> return "Debris"
            entity.hasTag(Tags.STAR) || entity.isSystemCenter || entity.isStar -> return "Star System"
            entity.hasTag(Tags.GAS_GIANT) -> return "Gas Giant"
            entity.hasTag(Tags.PLANET) -> return "Planet"
            entity.hasTag(Tags.STATION) -> return "Station"
            entity.hasTag(Tags.COMM_RELAY) ||
                    entity.hasTag(Tags.NAV_BUOY) ||
                    entity.hasTag(Tags.SENSOR_ARRAY) -> return "Objective"
            entity.hasTag(Tags.SALVAGEABLE) -> return "Salvage"
        }
        return "Entity"
    }

    fun getWriter(): EntryWriter? {
        val sector = Global.getSector()

        val intelManager = sector.intelManager
        var writer: IntelInfoPlugin? = intelManager.getFirstIntel(EntryWriter::class.java) ?: return null
        writer = writer as EntryWriter
        return writer
    }

    fun getRuinsType(market: MarketAPI): String {
        when {
            market.hasCondition(Conditions.RUINS_SCATTERED) -> {
                return "Scattered"
            }
            market.hasCondition(Conditions.RUINS_EXTENSIVE) -> {
                return "Extensive"
            }
            market.hasCondition(Conditions.RUINS_WIDESPREAD) -> {
                return "Widespread"
            }
            market.hasCondition(Conditions.RUINS_VAST) -> {
                return "Vast"
            }
        }
        return "None"
    }

    fun entityIsProbe(entity: SectorEntityToken): Boolean {
        return entity.customEntityType != null && (entity.customEntityType.equals(Entities.GENERIC_PROBE) ||
                entity.customEntityType.equals(Entities.DERELICT_SURVEY_PROBE))
    }

    fun entityIsCryosleeper(entity: SectorEntityToken): Boolean {
        val hasCustomType = entity.customEntityType != null && entity.customEntityType.equals(Entities.DERELICT_CRYOSLEEPER)
        return hasCustomType || entity.hasTag(Tags.CRYOSLEEPER)
    }

    private fun findClosestJumpPoint(): SectorEntityToken {
        val allAnchors: List<SectorEntityToken> = Global.getSector().getEntitiesWithTag(Tags.JUMP_POINT)
        lateinit var closest: SectorEntityToken
        var shortestDistance = 10000f
        for (anchor in allAnchors) {
            if (Misc.getDistanceToPlayerLY(anchor) < shortestDistance) {
                closest = anchor
                shortestDistance = Misc.getDistanceToPlayerLY(anchor)
            }
        }
        return closest
    }

}