package org.openlmis.core.network.model;

import org.openlmis.core.model.FacilityEquipment;

import java.util.List;

import lombok.Data;

@Data
public class SyncDownFacilityEquipment {
    List<FacilityEquipment> facilityEquipment;
}
