package fleetjour.scripts.interaction.shared

import com.fs.starfarer.api.ui.ButtonAPI

/**
 * @author Ontheheavens
 * @since 04.02.2024
 */
abstract class GroupButton(instance: ButtonAPI, val type: Type): InteractiveButton(instance) {

    private var checkedLastFrame = false
    private var othersUnchecked = false
    private var checkedAtCreation = false

    enum class Type {
        ENTRY,
    }

    private fun affectOthersInGroup() {
        for (button in getGroupButtons()) {
            if (button !is GroupButton) continue
            if (button.type !== this.type) continue
            if (button === this) continue
            val inner = button.instance

            inner.isChecked = false
            inner.unhighlight()
        }
    }

    abstract fun getGroupButtons(): Set<InteractiveButton>

    override fun check() {
        val instance: ButtonAPI = this.instance
        if (!instance.isChecked) {
            checkedLastFrame = false
            othersUnchecked = false
            return
        }
        if (!othersUnchecked) {
            affectOthersInGroup()
            othersUnchecked = true
        }
        if (checkedAtCreation) {
            checkedAtCreation = false
            checkedLastFrame = true
            return
        }
        if (!checkedLastFrame) {
            applyEffect()
            checkedLastFrame = true
        }
    }

}