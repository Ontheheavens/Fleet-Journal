package fleetjour.scripts.interaction.shared

import com.fs.starfarer.api.ui.ButtonAPI

/**
 * @author Ontheheavens
 * @since  17.02.2023
 */

abstract class InteractiveButton(val instance: ButtonAPI) {

    abstract fun applyEffect()

    open fun check() {
        if (instance.isChecked) {
            instance.isChecked = false
            applyEffect()
        }
    }

}