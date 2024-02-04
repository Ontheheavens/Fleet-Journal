package fleetjour.scripts

import com.fs.starfarer.api.EveryFrameScript
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin
import fleetjour.settings.SettingsHolder
import org.lwjgl.input.Keyboard

/**
 * @author Ontheheavens
 * @since 03.02.2024
 */
class QuickWriterListener : EveryFrameScript {

    private var canWrite: Boolean = true
    private var elapsedSinceLastWrite = 0f

    override fun isDone(): Boolean {
        return false
    }

    override fun runWhilePaused(): Boolean {
        return true
    }

    override fun advance(amount: Float) {
        checkForQuickWrite(amount)
    }

    private fun checkForQuickWrite(amount: Float) {
        if (!canWrite) {
            elapsedSinceLastWrite += amount
            if (elapsedSinceLastWrite > 1.0f) {
                elapsedSinceLastWrite = 0f
                canWrite = true
            }
            return
        }
        val sector = Global.getSector()
        val mousedOverEntity = sector.mousedOverEntity ?: return
        if (!isQuickHotkeyPressed()) {
            return
        }
        val intelManager = sector.intelManager
        var writer: IntelInfoPlugin? = intelManager.getFirstIntel(EntryWriter::class.java) ?: return
        writer = writer as EntryWriter
        if (isControlPressed()) {
            writer.applyEntityInfo(mousedOverEntity)
        } else {
            val quickEntry = writer.writeQuickEntry(mousedOverEntity)
            Global.getSector().intelManager.addIntel(quickEntry)
        }
        canWrite = false
    }

    private fun isQuickHotkeyPressed(): Boolean {
        val quickEntryKey = Keyboard.getKeyIndex(SettingsHolder.QUICK_ENTRY_HOTKEY)
        return Keyboard.isKeyDown(quickEntryKey)
    }

    private fun isControlPressed(): Boolean {
        return Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)
    }

}