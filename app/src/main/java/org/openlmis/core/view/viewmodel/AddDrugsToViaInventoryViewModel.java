package org.openlmis.core.view.viewmodel;

import org.apache.commons.lang3.StringUtils;
import org.openlmis.core.model.Product;

import lombok.Data;

@Data
public class AddDrugsToViaInventoryViewModel extends InventoryViewModel {
    String quantity;

    public AddDrugsToViaInventoryViewModel(Product product) {
        super(product);
    }

    @Override
    public boolean validate() {
        valid = !checked || StringUtils.isNumeric(quantity);
        return valid;
    }
}
