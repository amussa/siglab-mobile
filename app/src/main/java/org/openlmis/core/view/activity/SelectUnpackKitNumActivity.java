package org.openlmis.core.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.openlmis.core.R;
import org.openlmis.core.exceptions.MovementReasonNotFoundException;
import org.openlmis.core.googleAnalytics.ScreenName;
import org.openlmis.core.manager.MovementReasonManager;
import org.openlmis.core.utils.Constants;
import org.openlmis.core.view.adapter.UnpackNumAdapter;
import org.openlmis.core.view.fragment.SimpleSelectDialogFragment;
import org.openlmis.core.view.widget.SingleClickButtonListener;
import org.roboguice.shaded.goole.common.base.Function;
import org.roboguice.shaded.goole.common.collect.FluentIterable;

import java.util.List;

import lombok.Getter;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_select_unpack_kit_number)
public class SelectUnpackKitNumActivity extends BaseActivity {

    @Getter
    private List<MovementReasonManager.MovementReason> movementReasons;
    private String[] reasonDescriptionList;

    @InjectView(R.id.et_movement_reason)
    protected EditText etMovementReason;

    @InjectView(R.id.ly_movement_reason)
    protected TextInputLayout lyMovementReason;

    @InjectView(R.id.et_other_movement_reason)
    protected EditText etOtherMovementReason;

    @InjectView(R.id.ly_other_movement_reason)
    protected TextInputLayout lyOtherMovementReason;

    @InjectView(R.id.vg_unpack_num_container)
    protected GridView gridView;

    @InjectView(R.id.tv_select_unpack_kit_number)
    protected TextView tvLabel;

    @InjectView(R.id.btn_next)
    protected View btnNext;

    @InjectView(R.id.tv_select_num_warning)
    protected View tvSelectNumWarning;

    private UnpackNumAdapter adapter;

    private static final int MAX_UNPACK_QUANTITY = 100;

    private static final String PARAM_KIT_SOH = "param_kit_soh";

    @Override
    protected ScreenName getScreenName() {
        return ScreenName.SelectUnpackKitNumberScreen;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        clearTextInputLayoutError();
    }

    private void init() {
        Intent intent = getIntent();
        final String kitName = intent.getStringExtra(Constants.PARAM_KIT_NAME);
        tvLabel.setText(getString(R.string.label_select_unpack_num, kitName));

        final String productCode = intent.getStringExtra(Constants.PARAM_KIT_CODE);
        long kitSOH = Math.min(intent.getLongExtra(PARAM_KIT_SOH, 0L), MAX_UNPACK_QUANTITY);
        adapter = new UnpackNumAdapter(this, kitSOH, kitName);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tvSelectNumWarning.setVisibility(View.INVISIBLE);
            }
        });

        btnNext.setOnClickListener(new SingleClickButtonListener() {
            @Override
            public void onSingleClick(View v) {
                if (validate()) {
                    try {
                        int unpackNum = gridView.getCheckedItemPosition() + 1;
                        MovementReasonManager.MovementReason movementReason = MovementReasonManager.getInstance().queryByDesc(etMovementReason.getText().toString());
                        String otherMovementReason = etOtherMovementReason.getText().toString();
                        startActivityForResult(UnpackKitActivity.getIntentToMe(SelectUnpackKitNumActivity.this, productCode, unpackNum, kitName, movementReason, otherMovementReason), Constants.REQUEST_UNPACK_KIT);
                    } catch (MovementReasonNotFoundException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        etMovementReason.setOnClickListener(getMovementReasonOnClickListener());
        etMovementReason.setKeyListener(null);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQUEST_UNPACK_KIT) {
            setResult(Activity.RESULT_OK);
            finish();
        }
    }

    public static Intent getIntentToMe(Activity activity, String kitName, String productCode, long kitSoh) {
        Intent intent = new Intent(activity, SelectUnpackKitNumActivity.class);
        intent.putExtra(Constants.PARAM_KIT_NAME, kitName);
        intent.putExtra(Constants.PARAM_KIT_CODE, productCode);
        intent.putExtra(PARAM_KIT_SOH, kitSoh);
        return intent;
    }

    @NonNull
    private View.OnClickListener getMovementReasonOnClickListener() {
        return new SingleClickButtonListener() {
            @Override
            public void onSingleClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putStringArray(SimpleSelectDialogFragment.SELECTIONS, getMovementReasonDescriptionList());
                SimpleSelectDialogFragment reasonsDialog = new SimpleSelectDialogFragment();
                reasonsDialog.setArguments(bundle);
                reasonsDialog.setMovementTypeOnClickListener(new MovementTypeOnClickListener(reasonsDialog));
                reasonsDialog.show(getFragmentManager(), "SELECT_REASONS");
            }
        };
    }

    public void setOtherMovementReasonVisibility() {
        try {
            if (StringUtils.isBlank(etMovementReason.getText().toString())) {
                return;
            }
            MovementReasonManager.MovementReason movementReason = MovementReasonManager.getInstance().queryByDesc(etMovementReason.getText().toString());
            if (movementReason.getCode().equals(MovementReasonManager.OTHERS)) {
                lyOtherMovementReason.setVisibility(View.VISIBLE);
            } else {
                lyOtherMovementReason.setVisibility(View.GONE);
            }
        } catch (MovementReasonNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public String[] getMovementReasonDescriptionList() {
        if (ArrayUtils.isEmpty(reasonDescriptionList)) {
            movementReasons = MovementReasonManager.getInstance().buildReasonListForMovementType(MovementReasonManager.MovementType.RECEIVE);
            reasonDescriptionList = FluentIterable.from(movementReasons).transform(new Function<MovementReasonManager.MovementReason, String>() {
                @Override
                public String apply(MovementReasonManager.MovementReason movementReason) {
                    return movementReason.getDescription();
                }
            }).toArray(String.class);
        }
        return reasonDescriptionList;
    }

    private boolean validate() {
        boolean isValid = true;
        clearTextInputLayoutError();
        if (gridView.getCheckedItemPosition() == GridView.INVALID_POSITION) {
            tvSelectNumWarning.setVisibility(View.VISIBLE);
            isValid = false;
        }
        if (StringUtils.isBlank(etMovementReason.getText().toString())) {
            lyMovementReason.setError(getResources().getString(R.string.msg_empty_movement_reason));
            etMovementReason.getBackground().setColorFilter(getResources().getColor(R.color.color_red), PorterDuff.Mode.SRC_ATOP);
            etMovementReason.requestFocus();
            isValid = false;
        }
        if (StringUtils.isNotBlank(etMovementReason.getText().toString())) {
            try {
                MovementReasonManager.MovementReason movementReason = MovementReasonManager.getInstance().queryByDesc(etMovementReason.getText().toString());
                if (movementReason.getCode().equals(MovementReasonManager.OTHERS)) {
                    if (StringUtils.isBlank(etOtherMovementReason.getText().toString())) {
                        lyOtherMovementReason.setError(getResources().getString(R.string.msg_empty_movement_reason));
                        etOtherMovementReason.getBackground().setColorFilter(getResources().getColor(R.color.color_red), PorterDuff.Mode.SRC_ATOP);
                        etOtherMovementReason.requestFocus();
                        isValid = false;
                    }
                }
            } catch (MovementReasonNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return isValid;
    }

    private void clearTextInputLayoutError() {
        lyMovementReason.setErrorEnabled(false);
        lyOtherMovementReason.setErrorEnabled(false);
    }

    class MovementTypeOnClickListener implements AdapterView.OnItemClickListener {
        private SimpleSelectDialogFragment reasonsDialog;

        public MovementTypeOnClickListener(SimpleSelectDialogFragment reasonsDialog) {
            this.reasonsDialog = reasonsDialog;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            etMovementReason.setText(getMovementReasonDescriptionList()[position]);
            setOtherMovementReasonVisibility();
            reasonsDialog.dismiss();
        }
    }
}
