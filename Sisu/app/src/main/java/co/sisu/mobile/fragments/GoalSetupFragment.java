package co.sisu.mobile.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.models.AgentGoalsObject;
import co.sisu.mobile.models.AgentModel;
import co.sisu.mobile.models.AsyncUpdateAgentGoalsJsonObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class GoalSetupFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, TextWatcher {

    EditText desiredIncome, trackingReasons, contacts, bAppointments, sAppointments, bSigned, sSigned, bContract, sContract, bClosed, sClosed;
    ParentActivity parentActivity;
    Switch timelineSwitch;
    TextView activityTitle;
    AsyncUpdateAgentGoalsJsonObject updateAgentGoalsJsonObject;
    private boolean dateSwap;
    private boolean isAnnualChecked = true;

    public GoalSetupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ScrollView contentView = (ScrollView) inflater.inflate(R.layout.fragment_setup, container, false);
        ScrollView.LayoutParams viewLayout = new ScrollView.LayoutParams(container.getWidth(), container.getHeight());
        contentView.setLayoutParams(viewLayout);
        return contentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        parentActivity = (ParentActivity) getActivity();
        initFields();
        initSwitch();
        setupFieldsWithGoalData(true);
    }

    private void initSwitch() {
        timelineSwitch = getView().findViewById(R.id.goalsTimelineSelector);
        timelineSwitch.setChecked(true);
        timelineSwitch.setOnCheckedChangeListener(this);
    }

    private void setupFieldsWithGoalData(boolean isAnnual) {
        AgentModel agent = parentActivity.getAgentInfo();
        dateSwap = true;

        if(isAnnual) {
            activityTitle.setText(R.string.yearlyTitle);
            desiredIncome.setText(agent.getDesired_income());
        }
        else {
            activityTitle.setText(R.string.monthlyTitle);
            String formattedIncome = agent.getDesired_income().replace(".0", "");
            int toDisplay = Integer.valueOf(formattedIncome) / 12;
            desiredIncome.setText(String.valueOf(toDisplay));
        }


        trackingReasons.setText(agent.getVision_statement());
        for(AgentGoalsObject go : agent.getAgentGoalsObject()) {
//            Log.e("Goals Setup", go.getName() + " " + go.getValue());
            String value = go.getValue();
            if(!isAnnual) {
                value = String.valueOf(Integer.valueOf(go.getValue()) / 12);
            }
            switch (go.getName()) {
                case "Contacts":
                    contacts.setText(value);
                    break;
                case "Sellers Closed":
                    sClosed.setText(value);
                    break;
                case "Sellers Under Contract":
                    sContract.setText(value);
                    break;
                case "Seller Appointments":
                    sAppointments.setText(value);
                    break;
                case "Sellers Signed":
                    sSigned.setText(value);
                    break;
                case "Buyers Signed":
                    bSigned.setText(value);
                    break;
                case "Buyer Appointments":
                    bAppointments.setText(value);
                    break;
                case "Buyers Under Contract":
                    bContract.setText(value);
                    break;
                case "Buyers Closed":
                    bClosed.setText(value);
                    break;
            }
        }

        dateSwap = false;
    }

    private void initFields() {
        desiredIncome = getView().findViewById(R.id.desiredIncome);
        desiredIncome.addTextChangedListener(this);
        trackingReasons = getView().findViewById(R.id.goalsReason);
        trackingReasons.addTextChangedListener(this);

        contacts = getView().findViewById(R.id.contacts);
        contacts.addTextChangedListener(this);
        bAppointments = getView().findViewById(R.id.buyerAppts);
        bAppointments.addTextChangedListener(this);
        sAppointments = getView().findViewById(R.id.sellerAppts);
        sAppointments.addTextChangedListener(this);
        bSigned = getView().findViewById(R.id.signedBuyers);
        bSigned.addTextChangedListener(this);
        sSigned = getView().findViewById(R.id.signedSellers);
        sSigned.addTextChangedListener(this);
        bContract = getView().findViewById(R.id.buyersUnderContract);
        bContract.addTextChangedListener(this);
        sContract = getView().findViewById(R.id.sellersUnderContract);
        sContract.addTextChangedListener(this);
        bClosed = getView().findViewById(R.id.buyersClosed);
        bClosed.addTextChangedListener(this);
        sClosed = getView().findViewById(R.id.sellersClosed);
        sClosed.addTextChangedListener(this);
        activityTitle = getView().findViewById(R.id.activityTitle);
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        setupFieldsWithGoalData(isChecked);
        isAnnualChecked = isChecked;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        if(!dateSwap && !s.equals("")) {
            if (contacts.getText().hashCode() == s.hashCode())
            {
                updateField("Contacts", Integer.valueOf(String.valueOf(s)));
            }
            else if (bAppointments.getText().hashCode() == s.hashCode())
            {
                updateField("Buyer Appointments", Integer.valueOf(String.valueOf(s)));
            }
            else if (sAppointments.getText().hashCode() == s.hashCode())
            {
                updateField("Sellers Appointments", Integer.valueOf(String.valueOf(s)));
            }
            else if (bSigned.getText().hashCode() == s.hashCode())
            {
                updateField("Buyers Signed", Integer.valueOf(String.valueOf(s)));
            }
            else if (sSigned.getText().hashCode() == s.hashCode())
            {
                updateField("Sellers Signed", Integer.valueOf(String.valueOf(s)));
            }
            else if (bContract.getText().hashCode() == s.hashCode())
            {
                updateField("Buyers Under Contract", Integer.valueOf(String.valueOf(s)));
            }
            else if (sContract.getText().hashCode() == s.hashCode())
            {
                updateField("Sellers Under Contract", Integer.valueOf(String.valueOf(s)));
            }
            else if (bClosed.getText().hashCode() == s.hashCode())
            {
                updateField("Buyers Closed", Integer.valueOf(String.valueOf(s)));
            }
            else if (sClosed.getText().hashCode() == s.hashCode())
            {
                updateField("Sellers Closed", Integer.valueOf(String.valueOf(s)));
            }
        }
    }

    private void updateField(String fieldName, int value) {
        if(!isAnnualChecked) {
            value = value * 12;
        }

        parentActivity.setSpecificGoal(fieldName, value);
    }
}
