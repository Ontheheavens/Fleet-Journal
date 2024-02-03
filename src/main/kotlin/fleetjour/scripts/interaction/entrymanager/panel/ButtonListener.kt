package fleetjour.scripts.interaction.entrymanager.panel

/**
 * @author Ontheheavens
 * @since  17.02.2023
 */

object ButtonListener {

    private var buttonsIndex: HashSet<InteractiveButton>? = hashSetOf()

    fun getIndex(): HashSet<InteractiveButton>? {
        if (buttonsIndex == null) {
            buttonsIndex = hashSetOf()
        }
        return buttonsIndex
    }

    fun clearIndex() {
        buttonsIndex = null
    }

    fun checkIndex() {
        buttonsIndex ?: return
        buttonsIndex!!.forEach { button ->
            button.check()
        }
    }

}