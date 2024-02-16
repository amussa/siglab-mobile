package org.openlmis.core.googleAnalytics;

public enum TrackerActions {
    SelectStockCard("Select Stock Card"),
    SelectReason("Select Reason"),
    SelectMovementDate("Select Movement Date"),
    SelectComplete("Select Complete"),
    SelectApprove("Select Approve"),
    SelectMMIA("Select MMIA"),
    SelectVIA("Select VIA"),
    SelectPTV("Select PTV"),
    SelectAL("Select AL"),
    CreateRnR("Create RnR Form"),
    SelectPeriod("Select Period"),
    SubmitRnR("First Time Approve"),
    AuthoriseRnR("Second Time Approve"),
    SelectInventory("Select Inventory"),
    CompleteInventory("Complete Inventory"),
    ApproveInventory("Approve Inventory"),
    NetworkConnected("Network Connected"),
    NetworkDisconnected("Network Disconnected"),
    SwitchPowerOn("Tablet Power On"),
    SwitchPowerOff("Tablet Power Off"),
    SelectLmisAbbottM2000("Select LMIS Abbott M2000"),
    SelectLmisAbbottAlinityM("Select LMIS Abbott Alinity M"),
    SelectLmisRocheCobas6800("Select LMIS Roche Cobas 6800"),
    SelectLmisRocheCobas5800("Select LMIS Roche Cobas 5800"),
    SelectLmisRocheCapctm96("Select LMIS Roche CAPCTM 96"),
    SelectLmisHologicPanter("Select LMIS Hologic Panter"),
    SelectLmisBiosecurityMaterial("Select LMIS Biosecurity Material"),
    SelectLmisMpima("Select LMIS m-PIMA")
    ;

    private final String trackerAction;

    TrackerActions(String trackerAction) {
        this.trackerAction = trackerAction;
    }

    public String getString() {
        return this.trackerAction;
    }
}
