package org.openlmis.core.view.widget;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.openlmis.core.LMISApp;
import org.openlmis.core.R;
import org.openlmis.core.manager.MovementReasonManager;
import org.openlmis.core.utils.Constants;
import org.openlmis.core.utils.ToastUtil;
import org.openlmis.core.view.adapter.LotMovementAdapter;
import org.openlmis.core.view.viewmodel.BaseStockMovementViewModel;
import org.openlmis.core.view.viewmodel.LotMovementViewModel;
import org.roboguice.shaded.goole.common.base.Function;
import org.roboguice.shaded.goole.common.collect.FluentIterable;

import java.util.ArrayList;
import java.util.List;

import lombok.Setter;
import roboguice.RoboGuice;
import roboguice.inject.InjectView;

public class BaseLotListView extends FrameLayout {
    public static final String ADD_LOT = "add_new_lot";
    protected Context context;

    @InjectView(R.id.ly_action_panel)
    protected View actionPanel;

    @InjectView(R.id.btn_add_new_lot)
    protected TextView btnAddNewLot;

    @InjectView(R.id.rv_new_lot_list)
    protected RecyclerView newLotListView;

    @InjectView(R.id.rv_existing_lot_list)
    protected RecyclerView existingLotListView;

    protected AddLotDialogFragment addLotDialogFragment;

    protected LotMovementAdapter newLotMovementAdapter;
    protected LotMovementAdapter existingLotMovementAdapter;

    @Setter
    protected BaseStockMovementViewModel viewModel;

    public BaseLotListView(Context context) {
        super(context);
    }

    public BaseLotListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    protected void init(Context context) {
        this.context = context;
        inflateLayout(context);
        RoboGuice.injectMembers(getContext(), this);
        RoboGuice.getInjector(getContext()).injectViewMembers(this);
    }

    protected void inflateLayout(Context context) {
        inflate(context, R.layout.view_lot_list, this);
    }

    public void initLotListView(BaseStockMovementViewModel viewModel) {
        this.viewModel = viewModel;
        initExistingLotListView();
        initNewLotListView();
        btnAddNewLot.setOnClickListener(getAddNewLotOnClickListener());
    }

    public void initExistingLotListView() {
        existingLotListView.setLayoutManager(new LinearLayoutManager(getContext()));
        existingLotMovementAdapter = new LotMovementAdapter(viewModel.getExistingLotMovementViewModelList());
        existingLotListView.setAdapter(existingLotMovementAdapter);
    }

    public void initNewLotListView() {
        newLotListView.setLayoutManager(new LinearLayoutManager(getContext()));
        newLotMovementAdapter = new LotMovementAdapter(viewModel.getNewLotMovementViewModelList(), viewModel.getProduct().getProductNameWithCodeAndStrength());
        newLotListView.setAdapter(newLotMovementAdapter);
    }

    public void setActionAddNewEnabled(boolean actionAddNewEnabled) {
        btnAddNewLot.setEnabled(actionAddNewEnabled);
    }


    public void refreshNewLotList() {
        newLotMovementAdapter.notifyDataSetChanged();
    }

    public void addNewLot(LotMovementViewModel lotMovementViewModel) {
        viewModel.getNewLotMovementViewModelList().add(lotMovementViewModel);
        refreshNewLotList();
    }

    public AddLotDialogFragment.AddLotWithoutNumberListener getAddLotWithoutNumberListener() {
        return new AddLotDialogFragment.AddLotWithoutNumberListener() {
            @Override
            public void addLotWithoutNumber(String expiryDate) {
                btnAddNewLot.setEnabled(true);
                String lotNumber = LotMovementViewModel.generateLotNumberForProductWithoutLot(viewModel.getProduct().getCode(), expiryDate);
                if (getLotNumbers().contains(lotNumber)) {
                    ToastUtil.show(LMISApp.getContext().getString(R.string.error_lot_without_number_already_exists));
                } else {
                    addNewLot(new LotMovementViewModel(lotNumber, expiryDate, MovementReasonManager.MovementType.PHYSICAL_INVENTORY));
                }
            }
        };
    }

    @NonNull
    public List<String> getLotNumbers() {
        final List<String> existingLots = new ArrayList<>();
        existingLots.addAll(FluentIterable.from(viewModel.getNewLotMovementViewModelList()).transform(new Function<LotMovementViewModel, String>() {
            @Override
            public String apply(LotMovementViewModel lotMovementViewModel) {
                return lotMovementViewModel.getLotNumber();
            }
        }).toList());
        existingLots.addAll(FluentIterable.from(viewModel.getExistingLotMovementViewModelList()).transform(new Function<LotMovementViewModel, String>() {
            @Override
            public String apply(LotMovementViewModel lotMovementViewModel) {
                return lotMovementViewModel.getLotNumber();
            }
        }).toList());
        return existingLots;
    }

    @NonNull
    public SingleClickButtonListener getAddNewLotOnClickListener() {
        return new SingleClickButtonListener() {
            @Override
            public void onSingleClick(View v) {
                showAddLotDialogFragment();
            }
        };
    }

    public boolean showAddLotDialogFragment() {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.PARAM_STOCK_NAME, viewModel.getProduct().getFormattedProductName());
            addLotDialogFragment = new AddLotDialogFragment();
            addLotDialogFragment.setArguments(bundle);
            addLotDialogFragment.setListener(getAddNewLotDialogOnClickListener());
            addLotDialogFragment.setOnDismissListener(getOnAddNewLotDialogDismissListener());
            addLotDialogFragment.setAddLotWithoutNumberListener(getAddLotWithoutNumberListener());
            addLotDialogFragment.show(((Activity) context).getFragmentManager(), ADD_LOT);
            return true;
    }

    @NonNull
    public OnDismissListener getOnAddNewLotDialogDismissListener() {
        return new OnDismissListener() {
            @Override
            public void onDismissAction() {
                setActionAddNewEnabled(true);
            }
        };
    }

    @NonNull
    protected SingleClickButtonListener getAddNewLotDialogOnClickListener() {
        return new SingleClickButtonListener() {
            @Override
            public void onSingleClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_complete:
                        if (addLotDialogFragment.validate() && !addLotDialogFragment.hasIdenticalLot(getLotNumbers())) {
                            addNewLot(new LotMovementViewModel(addLotDialogFragment.getLotNumber(), addLotDialogFragment.getExpiryDate(), viewModel.getMovementType()));
                            addLotDialogFragment.dismiss();
                        }
                        break;
                    case R.id.btn_cancel:
                        addLotDialogFragment.dismiss();
                        break;
                }
            }
        };
    }

    public interface OnDismissListener {
        void onDismissAction();
    }
}
