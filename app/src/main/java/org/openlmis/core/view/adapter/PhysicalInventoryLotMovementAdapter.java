package org.openlmis.core.view.adapter;

import org.openlmis.core.view.viewmodel.LotMovementViewModel;

import java.util.List;

public class PhysicalInventoryLotMovementAdapter extends LotMovementAdapter {
    public PhysicalInventoryLotMovementAdapter(List<LotMovementViewModel> data) {
        super(data);
    }

    public PhysicalInventoryLotMovementAdapter(List<LotMovementViewModel> data, String productName) {
        super(data, productName);
    }

    public int validateLotNonEmptyQuantity() {
        int position = -1;
        for (LotMovementViewModel lotMovementViewModel : lotList) {
            lotMovementViewModel.setValid(true);
            lotMovementViewModel.setQuantityLessThanSoh(true);
        }
        for (int i = 0; i < lotList.size(); i++) {
            if (!lotList.get(i).validateLotWithNoEmptyFields()) {
                position = i;
                break;
            }
        }

        this.notifyDataSetChanged();
        return position;
    }

    public int validateLotPositiveQuantity() {
        int position = -1;
        for (LotMovementViewModel lotMovementViewModel : lotList) {
            lotMovementViewModel.setValid(true);
            lotMovementViewModel.setQuantityLessThanSoh(true);
        }
        for (int i = 0; i < lotList.size(); i++) {
            if (!lotList.get(i).validateLotWithPositiveQuantity()) {
                position = i;
                break;
            }
        }

        this.notifyDataSetChanged();
        return position;
    }
}
