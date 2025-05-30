package org.openlmis.core.utils.mapper;

import com.google.inject.Inject;

import org.openlmis.core.exceptions.LMISException;
import org.openlmis.core.model.HealthFacilityService;
import org.openlmis.core.model.PTVProgram;
import org.openlmis.core.model.PTVProgramStockInformation;
import org.openlmis.core.model.ServiceDispensation;
import org.openlmis.core.model.repository.HealthFacilityServiceRepository;
import org.openlmis.core.view.viewmodel.ptv.PTVViewModel;

import java.util.ArrayList;
import java.util.List;

import static org.openlmis.core.utils.Constants.ENTRIES;
import static org.openlmis.core.utils.Constants.FINAL_STOCK;
import static org.openlmis.core.utils.Constants.LOSSES_AND_ADJUSTMENTS;
import static org.openlmis.core.utils.Constants.PTV_PRODUCT_FIFTH_CODE;
import static org.openlmis.core.utils.Constants.PTV_PRODUCT_FIRST_CODE;
import static org.openlmis.core.utils.Constants.PTV_PRODUCT_FOURTH_CODE;
import static org.openlmis.core.utils.Constants.PTV_PRODUCT_SECOND_CODE;
import static org.openlmis.core.utils.Constants.PTV_PRODUCT_THIRD_CODE;
import static org.openlmis.core.utils.Constants.REQUISITIONS;
import static org.openlmis.core.utils.Constants.TOTAL;

public class PTVProgramToPTVViewModelMapper {

    @Inject
    private HealthFacilityServiceRepository healthFacilityServiceRepository;

    private PTVViewModel ptvViewModelEntry;
    private PTVViewModel ptvViewModelLossesAndAdjustments;
    private PTVViewModel ptvViewModelRequisition;
    private PTVViewModel ptvFinalStock;
    private List<PTVViewModel> ptvViewModels;
    private int productOrdinal;

    @Inject
    public PTVProgramToPTVViewModelMapper() {
    }

    public List<PTVViewModel> buildPlaceholderRows(PTVProgram ptvProgram) throws LMISException {
        ptvViewModels = new ArrayList<>();
        List<HealthFacilityService> healthFacilities = healthFacilityServiceRepository.getAll();
        List<PTVProgramStockInformation> ptvProgramStocksInformation = new ArrayList<>(ptvProgram.getPtvProgramStocksInformation());
        createRowsForFacilities(ptvViewModels, healthFacilities);
        ptvViewModelEntry = new PTVViewModel(ENTRIES);
        ptvViewModelLossesAndAdjustments = new PTVViewModel(LOSSES_AND_ADJUSTMENTS);
        ptvViewModelRequisition = new PTVViewModel(REQUISITIONS);
        ptvFinalStock = new PTVViewModel(FINAL_STOCK);
        for (PTVProgramStockInformation ptvProgramStockInformation : ptvProgramStocksInformation) {
            List<ServiceDispensation> serviceDispensations = new ArrayList<>(ptvProgramStockInformation.getServiceDispensations());
            setQuantitiesForModels(healthFacilities, ptvProgramStockInformation, serviceDispensations);
        }
        addPTVInformation();
        return ptvViewModels;
    }

    private void addPTVInformation() {
        ptvViewModels.add(new PTVViewModel(TOTAL));
        ptvViewModels.add(ptvViewModelEntry);
        ptvViewModels.add(ptvViewModelLossesAndAdjustments);
        ptvViewModels.add(ptvViewModelRequisition);
        ptvViewModels.add(ptvFinalStock);
    }

    private void setQuantitiesForModels(List<HealthFacilityService> healthFacilities, PTVProgramStockInformation ptvProgramStockInformation, List<ServiceDispensation> serviceDispensations) {
        for (int position = 0; position < healthFacilities.size(); position++) {
            ServiceDispensation serviceDispensation = serviceDispensations.get(position);
            String serviceName = serviceDispensation.getHealthFacilityService().getName();
            if (serviceName.equals(ptvViewModels.get(position).getPlaceholderItemName())) {
                switch (ptvProgramStockInformation.getProduct().getCode()) {
                    case PTV_PRODUCT_FIRST_CODE:
                        productOrdinal = 1;
                        setQuantitiesForProduct(ptvProgramStockInformation, position, serviceDispensation);
                        break;
                    case PTV_PRODUCT_SECOND_CODE:
                        productOrdinal = 2;
                        setQuantitiesForProduct(ptvProgramStockInformation, position, serviceDispensation);
                        break;
                    case PTV_PRODUCT_THIRD_CODE:
                        productOrdinal = 3;
                        setQuantitiesForProduct(ptvProgramStockInformation, position, serviceDispensation);
                        break;
                    case PTV_PRODUCT_FOURTH_CODE:
                        productOrdinal = 4;
                        setQuantitiesForProduct(ptvProgramStockInformation, position, serviceDispensation);
                        break;
                    case PTV_PRODUCT_FIFTH_CODE:
                        productOrdinal = 5;
                        setQuantitiesForProduct(ptvProgramStockInformation, position, serviceDispensation);
                        break;
                }

            }
        }
    }

    private void createRowsForFacilities(List<PTVViewModel> ptvViewModels, List<HealthFacilityService> healthFacilities) {
        for (HealthFacilityService healthFacility : healthFacilities) {
            PTVViewModel ptvViewModel = new PTVViewModel(healthFacility.getName());
            ptvViewModels.add(ptvViewModel);
        }
    }

    private void setQuantitiesForProduct(PTVProgramStockInformation ptvProgramStockInformation, int position, ServiceDispensation serviceDispensation) {
        ptvViewModels.get(position).setQuantity(productOrdinal, serviceDispensation.getQuantity());
        ptvViewModelEntry.setQuantity(productOrdinal, ptvProgramStockInformation.getEntries());
        ptvViewModelLossesAndAdjustments.setQuantity(productOrdinal, ptvProgramStockInformation.getLossesAndAdjustments());
        ptvViewModelRequisition.setQuantity(productOrdinal, ptvProgramStockInformation.getRequisition());
    }
}
