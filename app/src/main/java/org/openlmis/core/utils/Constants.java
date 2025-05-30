/*
 * This program is part of the OpenLMIS logistics management information
 * system platform software.
 *
 * Copyright © 2015 ThoughtWorks, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version. This program is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details. You should
 * have received a copy of the GNU Affero General Public License along with
 * this program. If not, see http://www.gnu.org/licenses. For additional
 * information contact info@OpenLMIS.org
 */

package org.openlmis.core.utils;

import org.openlmis.core.LMISApp;
import org.openlmis.core.R;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public final class Constants {

    public static Program currentLmisProgram;

    public enum Program {
        MMIA_PROGRAM(MMIA_PROGRAM_CODE, R.string.mmia_list, MMIA_REPORT),
        VIA_PROGRAM(VIA_PROGRAM_CODE, R.string.requisition_list, VIA_REPORT),
        AL_PROGRAM(AL_PROGRAM_CODE, R.string.label_al_name, AL_REPORT),
        PTV_PROGRAM(PTV_PROGRAM_CODE, R.string.label_ptv_name, PTV_REPORT),
        RAPID_TEST_PROGRAM(TEST_KIT_PROGRAM_CODE, R.string.title_rapid_test_reports, RAPID_REPORT),
        LMIS_ABBOTT_M2000_PROGRAM(LMIS_ABBOTT_M2000_PROGRAM_CODE, R.string.lmis_abbott_M2000, LMIS_ABBOTT_M2000_REPORT),
        LMIS_ABBOTT_ALINITY_M_PROGRAM(LMIS_ABBOTT_ALINITY_M_PROGRAM_CODE, R.string.lmis_abbott_alinity_m, LMIS_ABBOTT_ALINITY_M_REPORT),
        LMIS_ROCHE_COBAS_6800_PROGRAM(LMIS_ROCHE_COBAS_6800_PROGRAM_CODE, R.string.lmis_roche_cobas_6800, LMIS_ROCHE_COBAS_6800_REPORT),
        LMIS_ROCHE_COBAS_5800_PROGRAM(LMIS_ROCHE_COBAS_5800_PROGRAM_CODE, R.string.lmis_roche_cobas_5800, LMIS_ROCHE_COBAS_5800_REPORT),
        LMIS_ROCHE_CAPCTM_96_PROGRAM(LMIS_ROCHE_CAPCTM_96_PROGRAM_CODE, R.string.lmis_roche_capctm_96, LMIS_ROCHE_CAPCTM_96_REPORT),
        LMIS_HOLOGIC_PANTER_PROGRAM(LMIS_HOLOGIC_PANTER_PROGRAM_CODE, R.string.lmis_hologic_panter, LMIS_HOLOGIC_PANTER_REPORT),
        LMIS_BIOSECURITY_MATERIAL_PROGRAM(LMIS_BIOSECURITY_MATERIAL_PROGRAM_CODE, R.string.lmis_biosecurity_material, LMIS_BIOSECURITY_MATERIAL_REPORT),
        LMIS_MPIMA_PROGRAM(LMIS_MPIMA_PROGRAM_CODE, R.string.lmis_mpima, LMIS_MPIMA_REPORT)
        ;

        private String code;
        private String reportType;
        private int title;

        Program(String code, int title, String reportType) {
            this.code = code;
            this.title = title;
            this.reportType = reportType;
        }

        public String getCode() {
            return code;
        }

        public String getReportType() {return reportType; }

        public int getTitle() {
            return title;
        }
    }

    // Don't change these program codes!!!
    public static final String MMIA_PROGRAM_CODE = "MMIA";
    public static final String VIA_PROGRAM_CODE = "VIA";
    public static final String ESS_PROGRAM_CODE = "ESS_MEDS";
    public static final String RAPID_TEST_CODE = "TEST_KIT";
    public static final String RAPID_TEST_OLD_CODE = "RAPID_TEST";
    public static final String AL_PROGRAM_CODE = "MALARIA";
    public static final String PTV_PROGRAM_CODE = "PTV";
    public static final String TEST_KIT_PROGRAM_CODE = "TEST_KIT";
    public static final String LMIS_ABBOTT_M2000_PROGRAM_CODE = "LMIS_ABBOTT_M2000";
    public static final String LMIS_ABBOTT_ALINITY_M_PROGRAM_CODE = "LMIS_ABBOTT_ALINITY_M";
    public static final String LMIS_ROCHE_COBAS_6800_PROGRAM_CODE = "LMIS_ROCHE_COBAS_6800";
    public static final String LMIS_ROCHE_COBAS_5800_PROGRAM_CODE = "LMIS_ROCHE_COBAS_5800";
    public static final String LMIS_ROCHE_CAPCTM_96_PROGRAM_CODE = "LMIS_ROCHE_CAPCTM_96";
    public static final String LMIS_HOLOGIC_PANTER_PROGRAM_CODE = "LMIS_HOLOGIC_PANTER";
    public static final String LMIS_BIOSECURITY_MATERIAL_PROGRAM_CODE = "LMIS_BIOSECURITY_MATERIAL";
    public static final String LMIS_MPIMA_PROGRAM_CODE = "LMIS_MPIMA";

    public static final List<Constants.Program> PROGRAMES = Arrays.asList(
            Constants.Program.VIA_PROGRAM,
            Constants.Program.MMIA_PROGRAM,
            Constants.Program.AL_PROGRAM,
            Constants.Program.PTV_PROGRAM,
            Constants.Program.RAPID_TEST_PROGRAM,
            Constants.Program.LMIS_ABBOTT_M2000_PROGRAM,
            Constants.Program.LMIS_ABBOTT_ALINITY_M_PROGRAM,
            Constants.Program.LMIS_ROCHE_COBAS_6800_PROGRAM,
            Constants.Program.LMIS_ROCHE_COBAS_5800_PROGRAM,
            Constants.Program.LMIS_ROCHE_CAPCTM_96_PROGRAM,
            Constants.Program.LMIS_HOLOGIC_PANTER_PROGRAM,
            Constants.Program.LMIS_BIOSECURITY_MATERIAL_PROGRAM,
            Constants.Program.LMIS_MPIMA_PROGRAM
    );

     //Don't change these reportTypes codes!!!
    public static final String MMIA_REPORT = "MMIA";
    public static final String VIA_REPORT = "VIA";
    public static final String RAPID_REPORT = "TEST_KIT";
    public static final String AL_REPORT = "MALARIA";
    public static final String PTV_REPORT = "PTV";
    public static final String LMIS_ABBOTT_M2000_REPORT = "LMIS_ABBOTT_M2000";
    public static final String LMIS_ABBOTT_ALINITY_M_REPORT = "LMIS_ABBOTT_ALINITY_M";
    public static final String LMIS_ROCHE_COBAS_6800_REPORT = "LMIS_ROCHE_COBAS_6800";
    public static final String LMIS_ROCHE_COBAS_5800_REPORT = "LMIS_ROCHE_COBAS_5800";
    public static final String LMIS_ROCHE_CAPCTM_96_REPORT = "LMIS_ROCHE_CAPCTM_96";
    public static final String LMIS_HOLOGIC_PANTER_REPORT = "LMIS_HOLOGIC_PANTER";
    public static final String LMIS_BIOSECURITY_MATERIAL_REPORT = "LMIS_BIOSECURITY_MATERIAL";
    public static final String LMIS_MPIMA_REPORT = "LMIS_MPIMA";

    public static final String PTV_REGIME_CHILD = "PTV Crianças";
    public static final String PTV_REGIME_ADULT = "PTV Mulheres";


    // Intent Params
    public static final String PARAM_STOCK_CARD_ID = "stockCardId";
    public static final String PARAM_STOCK_NAME = "stockName";
    public static final String PARAM_IS_ACTIVATED = "productIsActivated";
    public static final String PARAM_IS_PHYSICAL_INVENTORY = "isPhysicalInventory";
    public static final String PARAM_IS_ADD_NEW_DRUG = "isAddNewDrug";
    public static final String PARAM_KIT_CODE = "kitCode";
    public static final String PARAM_KIT_NUM = "kitNum";
    public static final String PARAM_KIT_NAME = "kitName";
    public static final String PARAM_PROGRAM_CODE = "programCode";
    public static final String PARAM_FORM_ID = "formId";
    public static final String PARAM_PREVIOUS_FORM = "previousForm";
    public static final String PARAM_IS_FROM_ARCHIVE = "isFromArchive";
    public static final String PARAM_IS_KIT = "isKit";
    public static final String PARAM_SELECTED_INVENTORY_DATE = "selectedInventoryDate";
    public static final String PARAM_IS_MISSED_PERIOD = "isMissedPeriod";
    public static final String PARAM_PERIOD = "period";
    public static final String PARAM_USERNAME = "username";
    public static final String PARAM_PASSWORD = "password";
    public static final String PARAM_CUSTOM_REGIMEN = "customRegimen";
    public static final String PARAM_SELECTED_EMERGENCY = "selected_emergency";
    public static final String PARAM_PERIOD_BEGIN = "periodBegin";
    public static final String PARAM_ADDED_DRUGS_TO_VIA = "addedDrugsToVIA";
    public static final String PARAM_ADDED_DRUG_CODES_IN_VIA = "addedDrugsInVIA";
    public static final String PARAM_MOVEMENT_TYPE = "movementType";
    public static final String PARAM_LOT_DETAILS = "lotDetails";
    public static final String PARAM_MSG_CONFIRM_GENERATE_LOT_NUMBER = "confirmGenerateLotNumberMessage";
    public static final String PARAM_KIT_MOVEMENT_REASON = "kitMovementReason";
    public static final String PARAM_KIT_OTHER_MOVEMENT_REASON = "kitOtherMovementReason";

    // Request Params
    public static final int REQUEST_FROM_STOCK_LIST_PAGE = 100;
    public static final int REQUEST_UNPACK_KIT = 200;
    public static final int REQUEST_FROM_RNR_LIST_PAGE = 300;
    public static final int REQUEST_SELECT_PERIOD_END = 400;
    public static final int REQUEST_CREATE_OR_MODIFY_RAPID_TEST_FORM = 500;
    public static final int REQUEST_CREATE_OR_MODIFY_PATIENT_DATA_REPORT_FORM = 500;

    public static final int REQUEST_ADD_DRUGS_TO_VIA = 500;
    public static final int REQUEST_NEW_MOVEMENT_PAGE = 600;

    // Broadcast Intent Filter
    public static final String INTENT_FILTER_START_SYNC_DATA = LMISApp.getContext().getPackageName() + ".start.sync_data";
    public static final String INTENT_FILTER_FINISH_SYNC_DATA = LMISApp.getContext().getPackageName() + ".finish.sync_data";
    public static final String INTENT_FILTER_ERROR_SYNC_DATA = LMISApp.getContext().getPackageName() + ".error.sync_data";

    //PTV
    public static final String PTV_PRODUCT_FIRST_CODE = "08S40";
    public static final String PTV_PRODUCT_SECOND_CODE = "08S15";
    public static final String PTV_PRODUCT_THIRD_CODE = "08S22";
    public static final String PTV_PRODUCT_FOURTH_CODE = "08S17";
    public static final String PTV_PRODUCT_FIFTH_CODE = "08S23";

    public static long DEFAULT_FORM_ID = 0;

    public static final String ENTRIES = "Entries";
    public static final String LOSSES_AND_ADJUSTMENTS = "Losses and Adjustments";
    public static final String REQUISITIONS = "Requisitions";
    public static final String FINAL_STOCK = "Final Stock";
    public static final String TOTAL = "Total";

    public static final Calendar NO_EXPIRY_DATE = new GregorianCalendar(2100, 11, 31);

    private Constants() {

    }
}
