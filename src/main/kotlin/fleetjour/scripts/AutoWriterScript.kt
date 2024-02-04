package fleetjour.scripts

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.CampaignTerrainAPI
import com.fs.starfarer.api.campaign.PlanetAPI
import com.fs.starfarer.api.campaign.SectorEntityToken
import com.fs.starfarer.api.campaign.listeners.DiscoverEntityListener
import com.fs.starfarer.api.campaign.listeners.SurveyPlanetListener
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.impl.campaign.DerelictShipEntityPlugin
import com.fs.starfarer.api.impl.campaign.HiddenCacheEntityPlugin
import com.fs.starfarer.api.impl.campaign.SupplyCacheEntityPlugin
import com.fs.starfarer.api.impl.campaign.ids.Tags
import com.fs.starfarer.api.impl.campaign.terrain.DebrisFieldTerrainPlugin
import com.fs.starfarer.api.util.Misc
import fleetjour.scripts.objects.JournalEntry
import fleetjour.scripts.panel.Common
import fleetjour.settings.SettingsHolder

/**
 * @author Ontheheavens
 * @since 03.02.2024
 */
class AutoWriterScript : DiscoverEntityListener, SurveyPlanetListener {

    override fun reportEntityDiscovered(entity: SectorEntityToken?) {
        if (entity == null || !SettingsHolder.AUTO_LOGGING_ENABLED) {
            return
        }
        if (!checkLoggingValidity(entity)) {
            return
        }
        val writer: EntryWriter = Common.getWriter() ?: return
        val autoEntry = writer.writeQuickEntry(entity, Common.AUTO_LOGGING_PREFIX)
        autoEntry.icon = getIconForEntity(entity)
        Global.getSector().intelManager.addIntel(autoEntry, true)
    }

    private fun checkLoggingValidity(entity: SectorEntityToken): Boolean {
        return checkDerelictValidity(entity) && checkDebrisValidity(entity)
    }

    private fun checkDebrisValidity(entity: SectorEntityToken): Boolean {
        if (entity !is CampaignTerrainAPI || (entity as CampaignTerrainAPI).plugin !is DebrisFieldTerrainPlugin) {
            return true
        }
        return SettingsHolder.DEBRIS_FIELD_LOGGING_ENABLED
    }

    private fun checkDerelictValidity(entity: SectorEntityToken): Boolean {
        if (entity.customPlugin !is DerelictShipEntityPlugin) {
            return true
        }
        val plugin = entity.customPlugin as DerelictShipEntityPlugin
        val variant = Common.getVariantOfDerelict(plugin)
        val size = variant.hullSize
        return when (size) {
            ShipAPI.HullSize.FRIGATE -> SettingsHolder.FRIGATE_DERELICT_LOGGING_ENABLED
            ShipAPI.HullSize.DESTROYER -> SettingsHolder.DESTROYER_DERELICT_LOGGING_ENABLED
            ShipAPI.HullSize.CRUISER -> SettingsHolder.CRUISER_DERELICT_LOGGING_ENABLED
            ShipAPI.HullSize.CAPITAL_SHIP -> SettingsHolder.CAPITAL_DERELICT_LOGGING_ENABLED
            ShipAPI.HullSize.DEFAULT -> false
            ShipAPI.HullSize.FIGHTER -> false
            else -> false
        }
    }

    private fun getIconForEntity(entity: SectorEntityToken): String {
        var iconID = "entry_exclamation"
        when {
            Common.entityIsCryosleeper(entity) -> iconID = "entry_cryosleeper"
            Common.entityIsProbe(entity) -> iconID = "entry_probe"
            entity.customPlugin is SupplyCacheEntityPlugin || entity.customPlugin is HiddenCacheEntityPlugin ->
                iconID = "entry_cache"
            entity.customPlugin is DerelictShipEntityPlugin -> iconID = "entry_derelict"
            entity is CampaignTerrainAPI && (entity as CampaignTerrainAPI).plugin is DebrisFieldTerrainPlugin ->
                iconID = "entry_debris"
            entity.hasTag(Tags.STAR) || entity.isSystemCenter || entity.isStar -> iconID = "entry_stellar_body"
            entity.hasTag(Tags.GAS_GIANT) -> iconID = "entry_stellar_body"
            entity.hasTag(Tags.PLANET) -> iconID = "entry_stellar_body"
            entity.hasTag(Tags.STATION) -> iconID = "entry_station"
        }
        return Global.getSettings().getSpriteName("fleetjour_intel", iconID)
    }

    override fun reportPlayerSurveyedPlanet(planet: PlanetAPI?) {
        if (planet == null || planet.market == null || !SettingsHolder.AUTO_LOGGING_ENABLED) {
            return
        }
        if (Misc.hasUnexploredRuins(planet.market)) {

            val writer: EntryWriter = Common.getWriter() ?: return
            val title = Common.AUTO_LOGGING_PREFIX + "Ruins"
            val brief = "Planet: " + planet.name

            val contents = writer.createCircumstanceStrings(planet)
            contents.add("Ruins: " + Common.getRuinsType(planet.market))

            val ruinsEntry = object: JournalEntry(planet, title, brief, contents) {
                override fun shouldRemoveIntel(): Boolean {
                    return super.shouldRemoveIntel() || !Misc.hasUnexploredRuins(planet.market)
                }
            }

            ruinsEntry.icon = Global.getSettings().getSpriteName("fleetjour_intel", "entry_ruins")
            Global.getSector().intelManager.addIntel(ruinsEntry, true)
        }
    }


}