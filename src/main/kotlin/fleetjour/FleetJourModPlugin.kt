package fleetjour

import com.fs.starfarer.api.BaseModPlugin
import com.fs.starfarer.api.Global
import fleetjour.scripts.AutoWriterScript
import fleetjour.scripts.EntryWriter
import fleetjour.scripts.QuickWriterListener
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
        val sector = Global.getSector()
        val manager = sector.intelManager
        if (manager.getFirstIntel(EntryWriter::class.java) == null) {
            manager.addIntel(EntryWriter())
        }
        if (!sector.hasScript(QuickWriterListener::class.java)) {
            val listener = QuickWriterListener()
            sector.addScript(listener)
        }
        ensureAutoWriterScriptPresence()
    }

    private fun ensureAutoWriterScriptPresence() {
        val sector = Global.getSector()
        val listenerManager = sector.listenerManager

        val existingListeners = listenerManager.getListeners(AutoWriterScript::class.java)
        if (existingListeners == null || existingListeners.size < 1) {
            listenerManager.addListener(AutoWriterScript())
        }
    }

}

