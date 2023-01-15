package org.openlmis.core.view.holder;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.openlmis.core.LMISApp;
import org.openlmis.core.R;
import org.openlmis.core.utils.Constants;
import org.openlmis.core.utils.DateUtil;
import org.openlmis.core.utils.SingleTextWatcher;
import org.openlmis.core.view.activity.BaseActivity;
import org.openlmis.core.view.activity.InitialInventoryActivity;
import org.openlmis.core.view.activity.InventoryActivity;
import org.openlmis.core.view.activity.NewStockMovementActivity;
import org.openlmis.core.view.activity.PhysicalInventoryActivity;
import org.openlmis.core.view.activity.UnpackKitActivity;
import org.openlmis.core.view.adapter.LotMovementAdapter;
import org.openlmis.core.view.fragment.SimpleDialogFragment;
import org.openlmis.core.view.viewmodel.LotMovementViewModel;

import java.util.Date;

import roboguice.inject.InjectView;

public class LotMovementViewHolder extends BaseViewHolder {

    @InjectView(R.id.et_lot_amount)
    private EditText etLotAmount;

    @InjectView(R.id.ly_lot_amount)
    private TextInputLayout lyLotAmount;

    @InjectView(R.id.et_lot_info)
    private EditText etLotInfo;

    @InjectView(R.id.vg_lot_soh)
    private LinearLayout vgLotSOH;

    @InjectView(R.id.tv_lot_soh)
    private TextView tvLotSOH;

    @InjectView(R.id.tv_lot_soh_tip)
    private TextView tvLotSOHTip;

    @InjectView(R.id.iv_del)
    private ImageView iconDel;
    private LotMovementAdapter.MovementChangedListener movementChangeListener;

    public LotMovementViewHolder(View itemView) {
        super(itemView);
    }

    public void populate(final LotMovementViewModel viewModel, final LotMovementAdapter lotMovementAdapter) {
        populateLotInfo(viewModel);
        populateLotSOHBanner(viewModel);
        updateDeleteIcon(viewModel.isNewAdded(), getOnClickListenerForDeleteIcon(viewModel, lotMovementAdapter));
        populateAmountField(viewModel);
    }

    private void populateLotSOHBanner(LotMovementViewModel viewModel) {
        if (context instanceof InitialInventoryActivity || context instanceof UnpackKitActivity) {
            vgLotSOH.setVisibility(View.GONE);
        } else {
            if (viewModel.isNewAdded()) {
                if (viewModel.quantityGreaterThanZero()) {
                    vgLotSOH.setVisibility(View.GONE);
                } else {
                    tvLotSOHTip.setText(context.getResources().getString(R.string.label_new_added_lot));
                }
            } else {
                tvLotSOH.setText(viewModel.getLotSoh());
            }
        }
    }

    private void populateLotInfo(LotMovementViewModel viewModel) {
        String expiration;
        Date expiryDate = DateUtil.parseString(viewModel.getExpiryDate(), DateUtil.DEFAULT_DATE_FORMAT);
        if (expiryDate.equals(Constants.NO_EXPIRY_DATE.getTime())) {
            expiration = LMISApp.getInstance().getString(R.string.label_no_expiration_date);
        } else {
            expiration = viewModel.getExpiryDate();
        }
        etLotInfo.setText(viewModel.getLotNumber() + " - " + expiration);
        etLotInfo.setKeyListener(null);
        etLotInfo.setOnKeyListener(null);
        etLotInfo.setBackground(null);
    }

    private void populateAmountField(LotMovementViewModel viewModel) {
        final EditTextWatcher textWatcher = new EditTextWatcher(viewModel);
        etLotAmount.removeTextChangedListener(textWatcher);
        etLotAmount.setText(viewModel.getQuantity());
        etLotAmount.addTextChangedListener(textWatcher);
        etLotAmount.setHint(LMISApp.getInstance().getString(R.string.hint_lot_amount));
        lyLotAmount.setErrorEnabled(false);

        if (!viewModel.isValid()) {
            setQuantityError(context.getResources().getString(R.string.msg_empty_quantity));
        }
        if (!viewModel.isQuantityLessThanSoh()) {
            setQuantityError(context.getResources().getString(R.string.msg_invalid_quantity));
        }
    }

    private void setQuantityError(String string) {
        etLotAmount.requestFocus();
        lyLotAmount.setError(string);
    }

    private void updateDeleteIcon(boolean isNewAdded, View.OnClickListener onClickListenerForDeleteIcon) {
        if (isNewAdded) {
            iconDel.setVisibility(View.VISIBLE);
            iconDel.setOnClickListener(onClickListenerForDeleteIcon);
        }
    }

    @NonNull
    private View.OnClickListener getOnClickListenerForDeleteIcon(final LotMovementViewModel viewModel, final LotMovementAdapter lotMovementAdapter) {
        return new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final SimpleDialogFragment dialogFragment = SimpleDialogFragment.newInstance(
                        Html.fromHtml(context.getResources().getString(R.string.msg_remove_new_lot_title)),
                        Html.fromHtml(context.getResources().getString(R.string.msg_remove_new_lot, viewModel.getLotNumber(), viewModel.getExpiryDate(), lotMovementAdapter.getProductName())),
                        context.getResources().getString(R.string.btn_remove_lot),
                        context.getResources().getString(R.string.btn_cancel), "confirm_dialog");
                dialogFragment.show(((BaseActivity) context).getFragmentManager(), "confirm_dialog");
                dialogFragment.setCallBackListener(new SimpleDialogFragment.MsgDialogCallBack() {
                    @Override
                    public void positiveClick(String tag) {
                        lotMovementAdapter.remove(viewModel);
                        if (context instanceof InventoryActivity) {
                            ((InventoryActivity) context).productListRecycleView.getAdapter().notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void negativeClick(String tag) {
                        dialogFragment.dismiss();
                    }
                });
            }
        };
    }

    public void setMovementChangeListener(LotMovementAdapter.MovementChangedListener movementChangedListener) {
        this.movementChangeListener = movementChangedListener;
    }

    class EditTextWatcher extends SingleTextWatcher {

        private final LotMovementViewModel viewModel;

        public EditTextWatcher(LotMovementViewModel viewModel) {
            this.viewModel = viewModel;
        }

        @Override
        public void afterTextChanged(Editable editable) {
            this.viewModel.setDataChanged(true);
            viewModel.setQuantity(editable.toString());
            lyLotAmount.setErrorEnabled(false);
            if (context instanceof NewStockMovementActivity) {
                if (viewModel.isNewAdded()) {
                    if (viewModel.validateLotWithPositiveQuantity()) {
                        vgLotSOH.setVisibility(View.GONE);
                    } else {
                        vgLotSOH.setVisibility(View.VISIBLE);
                        setQuantityError(context.getResources().getString(R.string.msg_empty_quantity));
                    }
                }
                if (!viewModel.validateQuantityNotGreaterThanSOH()) {
                    setQuantityError(context.getResources().getString(R.string.msg_invalid_quantity));
                }
            }

            if (context instanceof PhysicalInventoryActivity) {
                if (viewModel.isNewAdded()) {
                    if (viewModel.validateLotWithPositiveQuantity()) {
                        vgLotSOH.setVisibility(View.GONE);
                    } else {
                        vgLotSOH.setVisibility(View.VISIBLE);
                        setQuantityError(context.getResources().getString(R.string.msg_empty_quantity));
                    }
                } else {
                    if (!viewModel.validateLotWithNoEmptyFields()) {
                        setQuantityError(context.getResources().getString(R.string.msg_empty_quantity));
                    }
                }
            }

            if (context instanceof InitialInventoryActivity) {
                if (!viewModel.validateLotWithPositiveQuantity()) {
                    setQuantityError(context.getResources().getString(R.string.msg_empty_quantity));
                }
            }

            if (movementChangeListener != null) {
                movementChangeListener.movementChange();
            }
        }
    }
}
