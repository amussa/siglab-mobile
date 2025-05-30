package org.openlmis.core.view.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.openlmis.core.R;
import org.openlmis.core.enums.VIAReportType;
import org.openlmis.core.presenter.PatientDataReportPresenter;
import org.openlmis.core.view.adapter.MalariaDataReportAdapter;
import org.openlmis.core.view.viewmodel.malaria.PatientDataReportViewModel;

import java.util.List;

import roboguice.RoboGuice;
import rx.Subscriber;

public class PTVProgramFragment extends Fragment {

    PatientDataReportPresenter presenter;

    private RecyclerView rvPatientDataPeriods;

    private MalariaDataReportAdapter malariaDataReportAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ptv_program, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        malariaDataReportAdapter = new MalariaDataReportAdapter(getContext(), VIAReportType.PTV);
        rvPatientDataPeriods = (RecyclerView) getView().findViewById(R.id.rv_patient_data_periods);
        initializeAdapter();
        presenter = injectPresenter();
        presenter.getViewModels(VIAReportType.PTV).subscribe(getViewModelsSubscriber());
    }

    private void initializeAdapter() {
        rvPatientDataPeriods.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPatientDataPeriods.setAdapter(malariaDataReportAdapter);
    }

    private PatientDataReportPresenter injectPresenter(){
        presenter = RoboGuice.getInjector(getActivity()).getInstance(PatientDataReportPresenter.class);
        return presenter;
    }

    @NonNull
    private Subscriber<List<PatientDataReportViewModel>> getViewModelsSubscriber() {
        return new Subscriber<List<PatientDataReportViewModel>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(List<PatientDataReportViewModel> patientDataReportViewModels) {
                malariaDataReportAdapter.setViewModels(patientDataReportViewModels);
                malariaDataReportAdapter.notifyDataSetChanged();
            }
        };
    }
}
