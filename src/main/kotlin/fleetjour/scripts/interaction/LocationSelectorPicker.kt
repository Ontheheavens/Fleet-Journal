package fleetjour.scripts.interaction

import com.fs.starfarer.api.campaign.CampaignEntityPickerListener
import com.fs.starfarer.api.campaign.InteractionDialogAPI
import com.fs.starfarer.api.campaign.SectorEntityToken
import com.fs.starfarer.api.ui.IntelUIAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import fleetjour.scripts.EntryWriter
import fleetjour.scripts.panel.Common

/**
 * @author Ontheheavens
 * @since  10.02.2023
 */

class LocationSelectorPicker(private val ui: IntelUIAPI, private val dialog: InteractionDialogAPI,
                             private val intel: EntryWriter): CampaignEntityPickerListener {

    override fun getMenuItemNameOverrideFor(entity: SectorEntityToken?): String? {
        return entity?.containingLocation?.nameWithTypeShort
    }

    override fun pickedEntity(entity: SectorEntityToken?) {
        intel.selectedTargetLocation = entity?.containingLocation?.id!!
        intel.selectedTargetEntity = Common.selectDefaultTargetEntity(intel)
        ui.updateUIForItem(intel)
        intel.assembly.updateTitleByDefault()
        dialog.dismiss()
    }

    override fun cancelledEntityPicking() {
        ui.updateUIForItem(intel)
        dialog.dismiss()
    }

    override fun getSelectedTextOverrideFor(entity: SectorEntityToken?): String? {
        return entity?.containingLocation?.nameWithTypeShort
    }

    override fun createInfoText(info: TooltipMakerAPI?, entity: SectorEntityToken?) {}

    override fun canConfirmSelection(entity: SectorEntityToken?): Boolean {
        return true
    }

    override fun getFuelColorAlphaMult(): Float {
        return 0f
    }

    override fun getFuelRangeMult(): Float {
        return 0f
    }

}