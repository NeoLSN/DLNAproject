
package com.iac.dlnaproject.fragment;

import com.devspark.progressfragment.ProgressFragment;
import com.iac.dlnaproject.R;
import com.iac.dlnaproject.model.ActivityModel;
import com.iac.dlnaproject.model.Participant;
import com.iac.dlnaproject.model.UIEvent;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseFragment extends ProgressFragment implements Participant {

    private ActivityModel mActivityActionModel;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            setActivityActionModel(((Host)activity).getActivityActionModel());
            mActivityActionModel.Register(this);
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement Participant.Host");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivityActionModel.Unregister(this);
        mActivityActionModel = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_custom_progress, container, false);
    }

    @Override
    public void send(UIEvent message) {
        getActivityActionModel().send(this, message);
    }

    public ActivityModel getActivityActionModel() {
        return mActivityActionModel;
    }

    private void setActivityActionModel(ActivityModel model) {
        mActivityActionModel = model;
    }
}
