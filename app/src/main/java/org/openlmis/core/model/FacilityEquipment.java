package org.openlmis.core.model;

import lombok.Data;

@Data
public class FacilityEquipment {
    private Long id;
    private String name;
    private String serial;
    private Long facilityId;
    private Long programId;
    private String code;
}