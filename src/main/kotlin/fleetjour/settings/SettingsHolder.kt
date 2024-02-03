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

    var AUTO_LOGGING_ENABLED: Boolean = true

    var FRIGATE_DERELICT_LOGGING_ENABLED: Boolean = true

    var DESTROYER_DERELICT_LOGGING_ENABLED: Boolean = true

    var CRUISER_DERELICT_LOGGING_ENABLED: Boolean = true

    var CAPITAL_DERELICT_LOGGING_ENABLED: Boolean = true

    var DEBRIS_FIELD_LOGGING_ENABLED: Boolean = true

    var QUICK_ENTRY_HOTKEY = "J"

    @Throws(IOException::class, JSONException::class)
    fun loadSettings(fileName: String?) {
        val settings = Global.getSettings().loadJSON(fileName)
        this.MANAGE_ENTRIES_BUTTON_ENABLED = settings.getBoolean("enable_manage_entries_button")

        this.AUTO_LOGGING_ENABLED = settings.getBoolean("enable_auto_logging")

        this.FRIGATE_DERELICT_LOGGING_ENABLED = settings.getBoolean("enable_frigate_derelict_logging")
        this.DESTROYER_DERELICT_LOGGING_ENABLED = settings.getBoolean("enable_destroyer_derelict_logging")
        this.CRUISER_DERELICT_LOGGING_ENABLED = settings.getBoolean("enable_cruiser_derelict_logging")
        this.CAPITAL_DERELICT_LOGGING_ENABLED = settings.getBoolean("enable_capital_derelict_logging")

        this.DEBRIS_FIELD_LOGGING_ENABLED = settings.getBoolean("enable_debris_field_logging")
    }

}