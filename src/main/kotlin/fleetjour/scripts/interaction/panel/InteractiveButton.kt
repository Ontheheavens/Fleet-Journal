package fleetjour.scripts.interaction.panel

import com.fs.starfarer.api.ui.ButtonAPI

/**
 * @author Ontheheavens
 * @since  17.02.2023
 */

open class InteractiveButton(val instance: ButtonAPI, val type: Type) {

    enum class Type {
        STANDARD,
        ENTRY,
        TAG
    }

    open fun applyEffect() {}

    open fun check() {
        if (instance.isChecked) {
            instance.isChecked = false
            applyEffect()
        }
    }

}