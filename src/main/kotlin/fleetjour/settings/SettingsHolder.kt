package fleetjour.settings

import com.fs.starfarer.api.Global
import org.json.JSONException
import java.io.IOException

/**
 * @author Ontheheavens
 * @since  23.02.2023
 */

object SettingsHolder {

    var MANAGE_ENTRIES_BUTTON_ENABLED: Boolean = false

    @Throws(IOException::class, JSONException::class)
    fun loadSettings(fileName: String?) {
        val settings = Global.getSettings().loadJSON(fileName)
        this.MANAGE_ENTRIES_BUTTON_ENABLED = settings.getBoolean("enable_manage_entries_button")
    }

}