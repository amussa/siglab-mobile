package org.openlmis.core.view.widget;

import static org.openlmis.core.utils.Constants.NO_EXPIRY_DATE;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.openlmis.core.R;
import org.openlmis.core.exceptions.LMISException;
import org.openlmis.core.utils.Constants;
import org.openlmis.core.utils.DateUtil;
import org.openlmis.core.view.fragment.BaseDialogFragment;
import org.openlmis.core.view.fragment.ConfirmGenerateLotNumberDialogFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import roboguice.inject.InjectView;

public class AddLotDialogFragment extends BaseDialogFragment {
    public static boolean IS_OCCUPIED = false;

    @InjectView(R.id.ly_lot_number)
    private TextInputLayout lyLotNumber;

    @InjectView(R.id.et_lot_number)
    protected EditText etLotNumber;

    @InjectView(R.id.dp_add_new_lot)
    protected DatePicker datePicker;

    @InjectView(R.id.btn_cancel)
    private TextView btnCancel;

    @InjectView(R.id.btn_complete)
    private Button btnComplete;

    @InjectView(R.id.tv_expiry_date_warning)
    private TextView expiryDateWarning;

    @InjectView(R.id.drug_name)
    private TextView drugName;

    @Getter
    private String lotNumber;

    @Getter
    private String expiryDate;

    @InjectView(R.id.cb_not_allowed)
    CheckBox notAllowed;

    @InjectView(R.id.touchArea_checkbox)
    LinearLayout taCheckbox;

    @Setter
    private SingleClickButtonListener listener;
    private AddLotWithoutNumberListener addLotWithoutNumberListener;
    private BaseLotListView.OnDismissListener onDismissListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_add_lot, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //hideDay();
        if (getArguments() != null) {
            String drugNameFromArgs = getArguments().getString(Constants.PARAM_STOCK_NAME);
            if (drugNameFromArgs != null) {
                this.drugName.setVisibility(View.VISIBLE);
                this.drugName.setText(drugNameFromArgs);
            }
        }
        btnCancel.setOnClickListener(listener);
        btnComplete.setOnClickListener(listener);
        setNotAvailableCheckboxListener();
        this.setCancelable(false);
    }

    private void setNotAvailableCheckboxListener() {
        taCheckbox.setOnClickListener(new SingleClickButtonListener() {
            @Override
            public void onSingleClick(View v) {
                notAllowed.setChecked(!notAllowed.isChecked());
            }
        });
        notAllowed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    datePicker.setEnabled(false);
                    datePicker.setVisibility(View.GONE);
                    datePicker.updateDate(NO_EXPIRY_DATE.get(Calendar.YEAR), NO_EXPIRY_DATE.get(Calendar.MONTH), NO_EXPIRY_DATE.get(Calendar.DAY_OF_MONTH));
                } else {
                    datePicker.setEnabled(true);
                    datePicker.setVisibility(View.VISIBLE);
                    Calendar now = new GregorianCalendar();
                    datePicker.updateDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                }
            }
        });
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    private void hideDay() {
        if (datePicker == null) {
            return;
        }
        ViewGroup datePickerLayout = (ViewGroup) datePicker.getChildAt(0);
        if (datePickerLayout == null) {
            return;
        }

        try {
            int dayIdentifier = Resources.getSystem().getIdentifier("day", "id", "android");
            ViewGroup pickers = (ViewGroup) datePickerLayout.getChildAt(0);
            for (int i = 0; i < pickers.getChildCount(); i++) {
                View childView = pickers.getChildAt(i);
                if (childView.getId() == dayIdentifier) {
                    childView.setVisibility(View.GONE);
                    return;
                }

            }
        } catch (NullPointerException e) {
            new LMISException(e).reportToFabric();
        }
    }

    public boolean validate() {
        clearErrorMessage();
        Date enteredDate = new GregorianCalendar(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth()).getTime();
        expiryDate = DateUtil.formatDate(enteredDate, DateUtil.DEFAULT_DATE_FORMAT);

        if (StringUtils.isBlank(etLotNumber.getText().toString())) {
            showConfirmNoLotNumberDialog();
            return false;
        }

        lotNumber = etLotNumber.getText().toString().trim().toUpperCase();
        return true;
    }

    private void showConfirmNoLotNumberDialog() {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.PARAM_MSG_CONFIRM_GENERATE_LOT_NUMBER, getString(R.string.msg_confirm_empty_lot_number, drugName.getText()));
        final ConfirmGenerateLotNumberDialogFragment confirmDialog = new ConfirmGenerateLotNumberDialogFragment();
        confirmDialog.setArguments(bundle);
        confirmDialog.setPositiveClickListener(new SingleClickButtonListener() {
            @Override
            public void onSingleClick(View v) {
                confirmDialog.dismiss();
                addLotWithoutNumberListener.addLotWithoutNumber(expiryDate);
                AddLotDialogFragment.this.dismiss();
            }
        });
        confirmDialog.show(getFragmentManager(), "confirm generate lot number");
    }


    private void clearErrorMessage() {
        lyLotNumber.setErrorEnabled(false);
        expiryDateWarning.setVisibility(View.GONE);
    }

    public boolean hasIdenticalLot(List<String> existingLots) {
        if (existingLots.contains(etLotNumber.getText().toString().toUpperCase())) {
            lyLotNumber.setError(getResources().getString(R.string.error_lot_already_exists));
            etLotNumber.getBackground().setColorFilter(getResources().getColor(R.color.color_red), PorterDuff.Mode.SRC_ATOP);
            return true;
        }
        return false;
    }

    public void setAddLotWithoutNumberListener(AddLotWithoutNumberListener addLotWithoutNumberListener) {
        this.addLotWithoutNumberListener = addLotWithoutNumberListener;
    }

    public void setOnDismissListener(BaseLotListView.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    public interface AddLotWithoutNumberListener {
        void addLotWithoutNumber(String expiryDate);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (onDismissListener != null) {
            onDismissListener.onDismissAction();
        }
        IS_OCCUPIED = false;
        super.onDismiss(dialog);
    }
}
