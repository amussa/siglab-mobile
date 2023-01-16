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
import org.openlmis.core.model.BaseInfoItem;
import org.openlmis.core.model.Product;
import org.openlmis.core.model.Regimen;
import org.openlmis.core.model.RegimenItem;
import org.openlmis.core.model.RnRForm;
import org.openlmis.core.model.RnrFormItem;
import org.openlmis.core.model.StockCard;
import org.openlmis.core.model.StockMovementItem;
import org.openlmis.core.utils.Constants;
import org.openlmis.core.utils.DateUtil;
import org.roboguice.shaded.goole.common.base.Function;
import org.roboguice.shaded.goole.common.collect.FluentIterable;

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
        ArrayList<String> attrs = new ArrayList<>();
        attrs.add(ATTR_NEW_PATIENTS);
        attrs.add(ATTR_SUSTAINING);
        attrs.add(ATTR_ALTERATION);
        attrs.add(ATTR_PTV);
        attrs.add(ATTR_PPE);
        attrs.add(ATTR_TOTAL_MONTH_DISPENSE);
        attrs.add(ATTR_TOTAL_PATIENTS);

        return FluentIterable.from(attrs).transform(new Function<String, BaseInfoItem>() {
            @Override
            public BaseInfoItem apply(String attr) {
                return new BaseInfoItem(attr, BaseInfoItem.TYPE.INT, form);
            }
        }).toList();
    }

    public long getTotalPatients(RnRForm form) {
        for (BaseInfoItem item : form.getBaseInfoItemListWrapper()) {
            if (ATTR_TOTAL_PATIENTS.equals(item.getName())) {
                return Long.parseLong(item.getValue());
            }
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
        RnrFormItem rnrFormItem = this.createMMIARnrFormItemByPeriod(stockCard, startDate, endDate);

        rnrFormItem.setProduct(stockCard.getProduct());
        Date earliestLotExpiryDate = stockCard.getEarliestLotExpiryDate();
        if (earliestLotExpiryDate != null) {
            rnrFormItem.setValidate(DateUtil.formatDate(earliestLotExpiryDate, DateUtil.SIMPLE_DATE_FORMAT));
        }

        return rnrFormItem;
    }

    protected RnrFormItem createMMIARnrFormItemByPeriod(StockCard stockCard, Date startDate, Date endDate) throws LMISException {
        RnrFormItem rnrFormItem = new RnrFormItem();
        List<StockMovementItem> stockMovementItems = stockMovementRepository.queryStockItemsByCreatedDate(stockCard.getId(), startDate, endDate);

        if (stockMovementItems.isEmpty()) {
            this.initMMiARnrFormItemWithoutMovement(rnrFormItem, lastRnrInventory(stockCard));
        } else {
            rnrFormItem.setInitialAmount(getMMiAInitialAmount(stockCard, stockMovementItems));
            this.assignMMIATotalValues(rnrFormItem, stockMovementItems);
        }

        rnrFormItem.setProduct(stockCard.getProduct());
        return rnrFormItem;
    }

    protected long getMMiAInitialAmount(StockCard stockCard,List<StockMovementItem> stockMovementItems) throws LMISException {
        List<RnRForm> rnRForms = listInclude(RnRForm.Emergency.No, programCode);
        if (rnRForms.size() == 1) {
            return stockMovementItems.get(0).calculatePreviousSOH();
        }
        return lastRnrInventory(stockCard);
    }

    private void assignMMIATotalValues(RnrFormItem rnrFormItem, List<StockMovementItem> stockMovementItems) {
        long totalReceived = 0;

        for (StockMovementItem item : stockMovementItems) {
            if (MovementReasonManager.MovementType.RECEIVE == item.getMovementType()) {
                totalReceived += item.getMovementQuantity();
            }
        }
        rnrFormItem.setReceived(totalReceived);
    }

    private void initMMiARnrFormItemWithoutMovement(RnrFormItem rnrFormItem, long lastRnrInventory) throws LMISException {
        rnrFormItem.setReceived(0);
        rnrFormItem.setCalculatedOrderQuantity(0L);
        rnrFormItem.setInitialAmount(lastRnrInventory);
    }

    protected ArrayList<RnrFormItem> fillAllMMIAProducts(RnRForm form, List<RnrFormItem> rnrFormItems) throws LMISException {
        List<Product> products;

        List<String> programCodes = programRepository.queryProgramCodesByProgramCodeOrParentCode(programCode);
        List<Long> productIds = productProgramRepository.queryActiveProductIdsByProgramsWithKits(programCodes, false);
        products = productRepository.queryProductsByProductIds(productIds);

        ArrayList<RnrFormItem> result = new ArrayList<>();

        for (Product product : products) {
            RnrFormItem rnrFormItem = new RnrFormItem();
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
