package org.openlmis.core.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.openlmis.core.utils.DateUtil;
import org.openlmis.core.view.viewmodel.LotMovementViewModel;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@DatabaseTable(tableName = "draft_lot_items")
@NoArgsConstructor
public class DraftLotItem extends BaseModel{

    @DatabaseField
    Long quantity;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    Product product;

    @DatabaseField
    String lotNumber;

    @DatabaseField(canBeNull = false, dataType = DataType.DATE_STRING, format = DateUtil.DB_DATE_FORMAT)
    Date expirationDate;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private DraftInventory draftInventory;

    @DatabaseField
    boolean newAdded;

    public DraftLotItem(LotMovementViewModel lotMovementViewModel, Product product, boolean isNewAdded) {
        try {
            quantity = Long.parseLong(lotMovementViewModel.getQuantity());
        } catch (Exception e) {
            quantity = null;
        }
        setExpirationDate(DateUtil.parseString(lotMovementViewModel.getExpiryDate(), DateUtil.DEFAULT_DATE_FORMAT));
        setLotNumber(lotMovementViewModel.getLotNumber());
        setProduct(product);
        newAdded = isNewAdded;
    }
}
