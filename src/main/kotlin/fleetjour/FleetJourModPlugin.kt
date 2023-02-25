package fleetjour

import com.fs.starfarer.api.BaseModPlugin
import com.fs.starfarer.api.Global
import fleetjour.scripts.EntryWriter
import fleetjour.settings.SettingsHolder
import org.json.JSONException
import java.io.IOException

/**
 * @author Ontheheavens
 * @since  05.02.2023
 */

@Suppress("unused")
class FleetJourModPlugin : BaseModPlugin() {

    private val fleetjourSettings = "fleet_journal_settings.ini"

    @Throws(JSONException::class, IOException::class)
    override fun onApplicationLoad() {
        SettingsHolder.loadSettings(fleetjourSettings)
    }

    override fun onGameLoad(newGame: Boolean) {
        super.onGameLoad(newGame)
        val manager = Global.getSector().intelManager
        if (manager.getFirstIntel(EntryWriter::class.java) == null) {
            manager.addIntel(EntryWriter())
        }
    }

}

