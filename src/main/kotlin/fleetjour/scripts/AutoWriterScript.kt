package fleetjour.scripts

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.CampaignTerrainAPI
import com.fs.starfarer.api.campaign.SectorEntityToken
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin
import com.fs.starfarer.api.campaign.listeners.DiscoverEntityListener
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.impl.campaign.DerelictShipEntityPlugin
import com.fs.starfarer.api.impl.campaign.terrain.DebrisFieldTerrainPlugin
import fleetjour.scripts.objects.JournalEntry
import fleetjour.scripts.panel.Common
import fleetjour.settings.SettingsHolder

/**
 * @author Ontheheavens
 * @since 03.02.2024
 */
class AutoWriterScript : DiscoverEntityListener {

    override fun reportEntityDiscovered(entity: SectorEntityToken?) {
        if (entity == null || !SettingsHolder.AUTO_LOGGING_ENABLED) {
            return
        }
        if (!checkLoggingValidity(entity)) {
            return
        }
        val sector = Global.getSector()

        val intelManager = sector.intelManager
        var writer: IntelInfoPlugin? = intelManager.getFirstIntel(EntryWriter::class.java) ?: return

        val journalEntries = intelManager.getIntel(JournalEntry::class.java)
        if (journalEntries != null) {
            for (journalEntry in journalEntries) {
                val target = (journalEntry as JournalEntry).targetEntityToken
                if (target == entity) {
                    return
                }
            }
        }
        writer = writer as EntryWriter
        writer.writeQuickEntry(entity, true, "Observed ")
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

}