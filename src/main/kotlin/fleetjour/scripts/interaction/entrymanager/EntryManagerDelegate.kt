package fleetjour.scripts.interaction.entrymanager

import com.fs.starfarer.api.campaign.InteractionDialogAPI
import fleetjour.scripts.interaction.shared.AbstractPanelDelegate

/**
 * @author Ontheheavens
 * @since  16.02.2023
 */

class EntryManagerDelegate(
     plugin: EntryManagerPlugin,
     dialog: InteractionDialogAPI
) : AbstractPanelDelegate(plugin, dialog)