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

package org.openlmis.core.model.repository;


import android.content.Context;

import com.google.inject.Inject;

import org.openlmis.core.R;
import org.openlmis.core.exceptions.LMISException;
import org.openlmis.core.manager.MovementReasonManager;
import org.openlmis.core.manager.SharedPreferenceMgr;
import org.openlmis.core.manager.UserInfoMgr;
import org.openlmis.core.model.BaseInfoItem;
import org.openlmis.core.model.FacilityEquipment;
import org.openlmis.core.model.Product;
import org.openlmis.core.model.Regimen;
import org.openlmis.core.model.RegimenItem;
import org.openlmis.core.model.RnRForm;
import org.openlmis.core.model.RnrFormItem;
import org.openlmis.core.model.StockCard;
import org.openlmis.core.model.StockMovementItem;
import org.openlmis.core.utils.Constants;
import org.openlmis.core.utils.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import roboguice.inject.InjectResource;

public class MMIARepository extends RnrFormRepository {

    @InjectResource(R.string.label_new_patients)
    public String ATTR_NEW_PATIENTS;
    @InjectResource(R.string.label_sustaining)
    public String ATTR_SUSTAINING;
    @InjectResource(R.string.label_alteration)
    public String ATTR_ALTERATION;
    @InjectResource(R.string.label_total_month_dispense)
    public String ATTR_TOTAL_MONTH_DISPENSE;
    @InjectResource(R.string.label_total_patients)
    public String ATTR_TOTAL_PATIENTS;
    @InjectResource(R.string.label_ptv)
    public String ATTR_PTV;
    @InjectResource(R.string.label_ppe)
    public String ATTR_PPE;


    @InjectResource(R.string.label_equipment)
    public String ATTR_EQUIPMENT;

    @InjectResource(R.string.label_serial_number)
    public String ATTR_SERIAL_NUMBER;

    @InjectResource(R.string.label_days_equipment_worked)
    public String ATTR_DAYS_EQUIPMENT_WORKED;

    @InjectResource(R.string.label_days_equipment_out_of_order)
    public String ATTR_DAYS_EQUIPMENT_OUT_OF_ORDER;

    @InjectResource(R.string.label_date_last_preventive_maintenance)
    public String ATTR_DATE_LAST_PREVENTIVE_MAINTENANCE;

    @InjectResource(R.string.label_date_next_preventive_maintenance)
    public String ATTR_DATE_NEXT_PREVENTIVE_MAINTENANCE;

    @InjectResource(R.string.label_remaining_hours)
    public String ATTR_REMAINING_HOURS;

    @InjectResource(R.string.label_dpi_patients_tested)
    public String ATTR_DPI_PATIENTS_TESTED;

    @InjectResource(R.string.label_dpi_tests_performed)
    public String ATTR_DPI_TESTS_PERFORMED;

    @InjectResource(R.string.label_cv_dbs_patients_tested)
    public String ATTR_CV_DBS_PATIENTS_TESTED;

    @InjectResource(R.string.label_cv_dbs_tests_performed)
    public String ATTR_CV_DBS_TESTS_PERFORMED;

    @InjectResource(R.string.label_cv_plasma_patients_tested)
    public String ATTR_CV_PLASMA_PATIENTS_TESTED;

    @InjectResource(R.string.label_cv_plasma_tests_performed)
    public String ATTR_CV_PLASMA_TESTS_PERFORMED;


    @Inject
    ProgramRepository programRepository;

    @Inject
    ProductRepository productRepository;

    @Inject
    ProductProgramRepository productProgramRepository;

    @Inject
    public MMIARepository(Context context) {
        super(context);
        programCode = Constants.currentLmisProgram.getCode();
    }

    @Override
    protected List<RegimenItem> generateRegimeItems(RnRForm form) throws LMISException {
        List<RegimenItem> regimenItems = new ArrayList<>();
        for (Regimen regimen : regimenRepository.listDefaultRegime()) {
            RegimenItem item = new RegimenItem();
            item.setForm(form);
            item.setRegimen(regimen);
            regimenItems.add(item);
        }
        return regimenItems;
    }

    @Override
    protected List<BaseInfoItem> generateBaseInfoItems(final RnRForm form) {
        List<FacilityEquipment> facilityEquipments = SharedPreferenceMgr.getInstance().getFacilityEquipments();
        List<BaseInfoItem> baseInfoItems = new ArrayList<>();
        for (FacilityEquipment facilityEquipment : facilityEquipments) {
            baseInfoItems.add(new BaseInfoItem(ATTR_EQUIPMENT, facilityEquipment.getName(), BaseInfoItem.TYPE.STRING, form));
            baseInfoItems.add(new BaseInfoItem(ATTR_SERIAL_NUMBER, facilityEquipment.getSerial(), BaseInfoItem.TYPE.STRING, form));
            baseInfoItems.add(new BaseInfoItem(ATTR_DAYS_EQUIPMENT_WORKED, BaseInfoItem.TYPE.INT, form));
            baseInfoItems.add(new BaseInfoItem(ATTR_DAYS_EQUIPMENT_OUT_OF_ORDER, BaseInfoItem.TYPE.INT, form));
            baseInfoItems.add(new BaseInfoItem(ATTR_DATE_LAST_PREVENTIVE_MAINTENANCE, BaseInfoItem.TYPE.DATE, form));
            baseInfoItems.add(new BaseInfoItem(ATTR_DATE_NEXT_PREVENTIVE_MAINTENANCE, BaseInfoItem.TYPE.DATE, form));
            baseInfoItems.add(new BaseInfoItem(ATTR_REMAINING_HOURS, BaseInfoItem.TYPE.INT, form));
            baseInfoItems.add(new BaseInfoItem(ATTR_DPI_PATIENTS_TESTED, BaseInfoItem.TYPE.INT, form));
            baseInfoItems.add(new BaseInfoItem(ATTR_DPI_TESTS_PERFORMED, BaseInfoItem.TYPE.INT, form));
            baseInfoItems.add(new BaseInfoItem(ATTR_CV_DBS_PATIENTS_TESTED, BaseInfoItem.TYPE.INT, form));
            baseInfoItems.add(new BaseInfoItem(ATTR_CV_DBS_TESTS_PERFORMED, BaseInfoItem.TYPE.INT, form));
            baseInfoItems.add(new BaseInfoItem(ATTR_CV_PLASMA_PATIENTS_TESTED, BaseInfoItem.TYPE.INT, form));
            baseInfoItems.add(new BaseInfoItem(ATTR_CV_PLASMA_TESTS_PERFORMED, BaseInfoItem.TYPE.INT, form));
        }
        return baseInfoItems;
    }

    public long getTotalPatients(RnRForm form) {
        for (BaseInfoItem item : form.getBaseInfoItemListWrapper()) {
//            if (ATTR_TOTAL_PATIENTS.equals(item.getName())) {
//                return Long.parseLong(item.getValue());
//            }
        }
        return 0L;
    }

    @Override
    public List<RnrFormItem> generateRnrFormItems(RnRForm form, List<StockCard> stockCards) throws LMISException {
        List<RnrFormItem> rnrFormItems = super.generateRnrFormItems(form, stockCards);
        return fillAllMMIAProducts(form, rnrFormItems);
    }

    @Override
    protected RnrFormItem createRnrFormItemByPeriod(StockCard stockCard, Date startDate, Date endDate) throws LMISException {
        RnrFormItem rnrFormItem = new RnrFormItem();
        List<StockMovementItem> stockMovementItems = stockMovementRepository.queryStockItemsByCreatedDate(stockCard.getId(), startDate, endDate);

        if (stockMovementItems.isEmpty()) {
            rnrFormHelper.initRnrFormItemWithoutMovement(rnrFormItem, lastRnrInventory(stockCard));
        } else {
            rnrFormItem.setInitialAmount(stockMovementItems.get(0).calculatePreviousSOH());
            rnrFormHelper.assignTotalValues(rnrFormItem, stockMovementItems);
        }

        rnrFormItem.setProduct(stockCard.getProduct());
        return rnrFormItem;

//        RnrFormItem rnrFormItem = this.createMMIARnrFormItemByPeriod(stockCard, startDate, endDate);
//
//        rnrFormItem.setProduct(stockCard.getProduct());
//        Date earliestLotExpiryDate = stockCard.getEarliestLotExpiryDate();
//        if (earliestLotExpiryDate != null) {
//            rnrFormItem.setValidate(DateUtil.formatDate(earliestLotExpiryDate, DateUtil.SIMPLE_DATE_FORMAT));
//        }
//
//        return rnrFormItem;
    }

    protected RnrFormItem createMMIARnrFormItemByPeriod(StockCard stockCard, Date startDate, Date endDate) throws LMISException {
        RnrFormItem rnrFormItem = new RnrFormItem();
        //List<StockMovementItem> stockMovementItems = stockMovementRepository.queryStockItemsByCreatedDate(stockCard.getId(), startDate, endDate);
        List<StockMovementItem> stockMovementItems = stockMovementRepository.queryStockMovementsByMovementDate(stockCard.getId(), startDate, endDate);

        if (stockMovementItems.isEmpty()) {
            this.initMMiARnrFormItemWithoutMovement(rnrFormItem, lastRnrInventory(stockCard));
        } else {
            rnrFormItem.setInitialAmount(getMMiAInitialAmount(stockCard, stockMovementItems));
            this.assignMMIATotalValues(rnrFormItem, stockMovementItems);
        }

        rnrFormItem.setProduct(stockCard.getProduct());
        return rnrFormItem;
    }

    protected Long getMMiAInitialAmount(StockCard stockCard,List<StockMovementItem> stockMovementItems) throws LMISException {
        List<RnRForm> rnRForms = listInclude(RnRForm.Emergency.No, programCode);
        Long initialAmount = lastRnrInventory(stockCard);
        if (initialAmount == null) {
            initialAmount = stockMovementItems.get(0).calculatePreviousSOH();
        }
        return initialAmount;
    }

    private void assignMMIATotalValues(RnrFormItem rnrFormItem, List<StockMovementItem> stockMovementItems) {
        long totalReceived = 0;
        long totalIssued = 0;
        long adjustment = 0;
        long losses = 0;
        long inventory = 0;
        for (StockMovementItem item : stockMovementItems) {
            if (MovementReasonManager.MovementType.RECEIVE == item.getMovementType()) {
                totalReceived += item.getMovementQuantity();
            } else if (MovementReasonManager.MovementType.ISSUE == item.getMovementType()) {
                totalIssued += item.getMovementQuantity();
            } else if (MovementReasonManager.MovementType.POSITIVE_ADJUST == item.getMovementType()) {
                adjustment += item.getMovementQuantity();
            } else if (MovementReasonManager.MovementType.NEGATIVE_ADJUST == item.getMovementType()) {
                adjustment -= item.getMovementQuantity();
            } else if (MovementReasonManager.MovementType.LOSSES == item.getMovementType()) {
                losses -= item.getMovementQuantity();
            }
            inventory = item.getStockOnHand();
        }
        rnrFormItem.setInventory(inventory);
        rnrFormItem.setReceived(totalReceived);
        rnrFormItem.setIssued(totalIssued);
        rnrFormItem.setAdjustment(adjustment);
        rnrFormItem.setLosses(losses);
    }

    private void initMMiARnrFormItemWithoutMovement(RnrFormItem rnrFormItem, long lastRnrInventory) throws LMISException {
        rnrFormItem.setInventory(lastRnrInventory);
        rnrFormItem.setInitialAmount(lastRnrInventory);
        rnrFormItem.setReceived(0);
        rnrFormItem.setIssued(0L);
        rnrFormItem.setAdjustment(0L);
        rnrFormItem.setCalculatedOrderQuantity(0L);
    }

    protected ArrayList<RnrFormItem> fillAllMMIAProducts(RnRForm form, List<RnrFormItem> rnrFormItems) throws LMISException {
        List<Product> products;

        List<String> programCodes = programRepository.queryProgramCodesByProgramCodeOrParentCode(programCode);
        List<Long> productIds = productProgramRepository.queryActiveProductIdsByProgramsWithKits(programCodes, false);
        products = productRepository.queryProductsByProductIds(productIds);

        ArrayList<RnrFormItem> result = new ArrayList<>();

        for (Product product : products) {
            RnrFormItem rnrFormItem = new RnrFormItem();
            rnrFormItem.setInventory(0L);
            rnrFormItem.setInitialAmount(0L);
            rnrFormItem.setReceived(0L);
            rnrFormItem.setIssued(0L);
            rnrFormItem.setAdjustment(0L);
            rnrFormItem.setForm(form);
            rnrFormItem.setProduct(product);
            RnrFormItem stockFormItem = getStockCardRnr(product, rnrFormItems);
            if (stockFormItem == null) {
                rnrFormItem.setInitialAmount(lastRnrInventory(product));
            } else {
                rnrFormItem = stockFormItem;
            }
            result.add(rnrFormItem);
        }
        return result;
    }

    private RnrFormItem getStockCardRnr(Product product, List<RnrFormItem> rnrStockFormItems) {
        for (RnrFormItem item : rnrStockFormItems) {
            if (item.getProduct().getId() == product.getId()) {
                return  item;
            }
        }
        return null;
    }
}
