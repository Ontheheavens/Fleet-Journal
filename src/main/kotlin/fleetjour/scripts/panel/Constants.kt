package fleetjour.scripts.panel

import com.fs.starfarer.api.Global

/**
 * @author Ontheheavens
 * @since  10.02.2023
 */

object Constants {

    const val HEADER_HEIGHT = 110f
    const val EDITOR_HEIGHT = 60f
    const val TARGET_SECTION_HEIGHT = 150f

    const val TITLE_FIELD_WIDTH = 269f

    const val SELECTOR_WIDTH = 400f
    const val SORTING_BUTTONS_HEIGHT = 25f
    const val SORTING_BUTTONS_PAD = 4f
    const val SORTING_BUTTONS_WIDTH = (SELECTOR_WIDTH - (SORTING_BUTTONS_PAD * 3f)) / 4f
    const val CONTROL_SECTION_WIDTH = 140f

    const val RIGHTSIDE_OFFSET = 30f
    const val CONTENT_HEIGHT_OFFSET = HEADER_HEIGHT + (EDITOR_HEIGHT + 41f)
    const val DRAFTPANEL_WIDTH_OFFSET = SELECTOR_WIDTH + (RIGHTSIDE_OFFSET + 4f)

    const val DISTANCE_THRESHOLD_FOR_DISPLAY = 2f

    val icons: Map<Int, String> = createIconRepository()

    private fun createIconRepository(): Map<Int, String> {
        val settings = Global.getSettings()
        val category = "fleetjour_intel"
        return hashMapOf(
            Pair(0, settings.getSpriteName("intel", "fleet_log")),
            Pair(1, settings.getSpriteName(category, "entry_cache")),
            Pair(2, settings.getSpriteName(category, "entry_debris")),
            Pair(3, settings.getSpriteName(category, "entry_derelict")),
            Pair(4, settings.getSpriteName(category, "entry_entity")),
            Pair(5, settings.getSpriteName(category, "entry_exclamation")),
            Pair(6, settings.getSpriteName(category, "entry_station")),
            Pair(7, settings.getSpriteName(category, "entry_stellar_body")),
            Pair(8, settings.getSpriteName(category, "entry_probe")),
            Pair(9, settings.getSpriteName(category, "entry_cryosleeper")),
            Pair(10, settings.getSpriteName(category, "entry_ruins"))
        )
    }

}