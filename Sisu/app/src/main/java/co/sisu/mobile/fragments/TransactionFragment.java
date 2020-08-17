package co.sisu.mobile.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.SearchView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.controllers.ApiManager;
import co.sisu.mobile.controllers.ColorSchemeManager;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.controllers.DateManager;
import co.sisu.mobile.controllers.NavigationManager;
import co.sisu.mobile.enums.ApiReturnTypes;
import co.sisu.mobile.models.ClientObject;
import co.sisu.mobile.models.MarketStatusModel;
import co.sisu.mobile.models.ScopeBarModel;

/**
 * Created by bradygroharing on 2/21/18.
 */

public class TransactionFragment extends Fragment implements View.OnClickListener, AsyncServerEventListener, PopupMenu.OnMenuItemClickListener, SearchView.OnQueryTextListener {
    private ParentActivity parentActivity;
    private DataController dataController;
    private NavigationManager navigationManager;
    private ApiManager apiManager;
    private ColorSchemeManager colorSchemeManager;
    private DateManager dateManager;
    private ProgressBar loader;
    private LayoutInflater inflater;

    private int numOfRows = 2;
    private TextView scopeSelectorText, marketStatusFilterText;
    private PopupMenu scopePopup, marketStatusPopup;
    private SearchView clientSearch;
    private ConstraintLayout paginateInfo;
    private JSONObject paginateObject;
    private String count;
    private ScrollView tileScrollView;
    private boolean updatingClients = false;
    private ImageView addButton;
    private TextView leftSelector, rightSelector;
    private PopupMenu dateSelectorPopup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        parentActivity = (ParentActivity) getActivity();
        dataController = parentActivity.getDataController();
        navigationManager = parentActivity.getNavigationManager();
        apiManager = parentActivity.getApiManager();
        dateManager = parentActivity.getDateManager();
        loader = parentActivity.findViewById(R.id.parentLoader);
        this.inflater = inflater;
        JSONObject tileTemplate = parentActivity.getRecordClientsList();

//        try {
//            if(tileTemplate.has("pagination")) {
//                paginateObject = tileTemplate.getJSONObject("pagination");
//            }
//
//            if(tileTemplate.has("count")) {
//                count = tileTemplate.getString("count");
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }


        return createFullView(container, tileTemplate);
    }

    public void teamSwap() {
        parentActivity.resetDashboardTiles();
//        createAndAnimateProgressBars(dataController.updateScoreboardTimeline());
//        loader.setVisibility(View.VISIBLE);
//        apiManager.getTileSetup(this, parentActivity.getAgent().getAgent_id(), parentActivity.getSelectedTeamId(), selectedStartTime, selectedEndTime, dashboardType);
//        parentActivity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                setupUiVisuals();
//            }
//        });
    }

    @SuppressLint("ResourceType")
    private View createFullView(ViewGroup container, JSONObject tileTemplate) {
        loader.setVisibility(View.VISIBLE);
        JSONArray tile_rows = null;

        RelativeLayout parentRelativeLayout;
        View parentLayout = inflater.inflate(R.layout.fragment_transaction, container, false);

        RelativeLayout dateSelector = parentLayout.findViewById(R.id.transactionFragmentDateSelector);
        dateSelector.setId(1);
        clientSearch = parentLayout.findViewById(R.id.transactionFragmentSearch);
        clientSearch.setId(2);
        if (tileTemplate != null) {
            colorSchemeManager = parentActivity.getColorSchemeManager();

            // Create the parent layout that all the rows will go in
            parentLayout.setBackgroundColor(colorSchemeManager.getAppBackground());
            parentRelativeLayout = parentLayout.findViewById(R.id.transactionFragmentClientLayout);
//            initContextFilterSelector(parentLayout);
//            initTimelineSelector(parentLayout);
//            initDateSelector(parentLayout);
//            initPopupMenu();
//            initializeCalendarHandler();
//            initDashboardTypeSelector(parentLayout);
            //

            try {
                tile_rows = tileTemplate.getJSONArray("clients");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("NUM OF CLIENTS", String.valueOf(tile_rows.length()));
            for(int i = 0; i < tile_rows.length(); i++) {
                try {
                    View rowView = createClientRow(tile_rows.getJSONObject(i), container);
                    if(rowView != null) {
                        // Add one here to account for the spinner's ID.
                        rowView.setId(numOfRows + 1);
                        RelativeLayout.LayoutParams horizontalParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        horizontalParam.addRule(RelativeLayout.BELOW, numOfRows);

                        parentRelativeLayout.addView(rowView, horizontalParam);
                        numOfRows++;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
        loader.setVisibility(View.INVISIBLE);
        return parentLayout;
    }

    private void setColorScheme() {
        leftSelector.setBackgroundColor(colorSchemeManager.getButtonBackground());
        leftSelector.setTextColor(colorSchemeManager.getLighterTextColor());

        rightSelector.setBackgroundColor(colorSchemeManager.getButtonBackground());
        rightSelector.setTextColor(colorSchemeManager.getLighterTextColor());
    }

    @SuppressLint("ResourceType")
    private View createClientRow (JSONObject clientObject, ViewGroup container) {
//        Log.e("ROW OBJECT", String.valueOf(rowObject));
        View rowView = inflater.inflate(R.layout.adapter_transaction_client_tile, container, false);
        try {
//            List<View> rowViews = new ArrayList<>();
//            View v = createClientView(container, tileObject);
//            v.setId(i);
//            rowViews.add(v);
//            view = inflater.inflate(R.layout.activity_tile_template_test, container, false);
            TextView header = rowView.findViewById(R.id.client_list_title);
            String dateText = "";
            if(clientObject.has("appt_set_dt")) {
                String dateString = clientObject.getString("appt_set_dt");
                if(dateString != null) {
                    dateText = formatDateTime(dateString);
                }
            }
            else if(clientObject.has("appt_dt")) {
                String dateString = clientObject.getString("appt_dt");
                if(dateString != null) {
                    dateText = formatDateTime(dateString);
                }
            }
            else if(clientObject.has("signed_dt")) {
                String dateString = clientObject.getString("signed_dt");
                if(dateString != null) {
                    dateText = formatDateTime(dateString);
                }
            }
            else if(clientObject.has("uc_dt")) {
                String dateString = clientObject.getString("uc_dt");
                if(dateString != null) {
                    dateText = formatDateTime(dateString);
                }
            }
            else if(clientObject.has("closed_dt")) {
                String dateString = clientObject.getString("closed_dt");
                if(dateString != null) {
                    dateText = formatDateTime(dateString);
                }
            }
            header.setText(clientObject.getString("text") + " " + dateText);

            rowView.setOnClickListener(view -> {
//                try {
//                    ClientObject selectedClient = new ClientObject(tileObject.getJSONObject("tile_data"));
//                    parentActivity.setSelectedClient(selectedClient);
//                    paginateInfo.setVisibility(View.GONE);
//                    navigationManager.stackReplaceFragment(ClientManageFragment.class);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

            });

            ImageView thumbnail = rowView.findViewById(R.id.client_list_thumbnail);
            try {
                if(clientObject.getBoolean("is_locked")) {
                    if(clientObject.getString("type_id").equalsIgnoreCase("b")) {
                        Drawable drawable = parentActivity.getResources().getDrawable(R.drawable.lock_icon).mutate();
                        drawable.setColorFilter(ContextCompat.getColor(parentActivity, R.color.colorYellow), PorterDuff.Mode.SRC_ATOP);
                        thumbnail.setImageDrawable(drawable);
                    } else {
                        Drawable drawable = parentActivity.getResources().getDrawable(R.drawable.lock_icon).mutate();
                        drawable.setColorFilter(ContextCompat.getColor(parentActivity, R.color.colorCorporateOrange), PorterDuff.Mode.SRC_ATOP);
                        thumbnail.setImageDrawable(drawable);
                    }
                }
                else {
                    if(clientObject.getString("type_id").equalsIgnoreCase("b")) {
                        Drawable drawable = parentActivity.getResources().getDrawable(R.drawable.seller_icon_active).mutate();
                        drawable.setColorFilter(ContextCompat.getColor(parentActivity, R.color.colorYellow), PorterDuff.Mode.SRC_ATOP);
                        thumbnail.setImageDrawable(drawable);
                    } else {
                        thumbnail.setImageResource(R.drawable.seller_icon_active);
                    }
                }
            } catch (JSONException e) {
                try {
                    if(clientObject.getString("type_id").equalsIgnoreCase("b")) {
                        Drawable drawable = parentActivity.getResources().getDrawable(R.drawable.seller_icon_active).mutate();
                        drawable.setColorFilter(ContextCompat.getColor(parentActivity, R.color.colorYellow), PorterDuff.Mode.SRC_ATOP);
                        thumbnail.setImageDrawable(drawable);
                    } else {
                        thumbnail.setImageResource(R.drawable.seller_icon_active);
                    }
                } catch(JSONException e1) {
                    e.printStackTrace();
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return rowView;
    }

    private String formatDateTime(String dateString) {
        String formattedDateString = "";
        Date d;
//        Tue, 17 Jul 2018 20:31:00 GMT
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy");
        String dateStrippedString = dateString.replace(" GMT", "");

        Calendar calendar = Calendar.getInstance();
        try {
            d = sdf.parse(dateStrippedString);
            calendar.setTime(d);

            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");

            formattedDateString = "(" + format1.format(calendar.getTime()) + ")";

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return formattedDateString;
    }

    private float getTextViewSizing(String size) {
        float returnSize;
        switch(size) {
            case "small":
                returnSize = getResources().getDimension(R.dimen.font_small);
                break;
            case "medium":
            case "mediam": //I don't know if Rick is still sending this typo down
                returnSize = getResources().getDimension(R.dimen.font_large);
                break;
            case "large":
                returnSize = getResources().getDimension(R.dimen.font_larger);
                break;
            default:
                returnSize = getResources().getDimension(R.dimen.font_mega);
                Log.e("TEXTVIEW SIZE", "Error setting TextView Size: " + size);
                break;
        }

        return returnSize;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        leftSelector = view.findViewById(R.id.miniDateSelectorDate);
        leftSelector.setOnClickListener(this);
        rightSelector = view.findViewById(R.id.miniDateSelectorDateFormat);
        rightSelector.setOnClickListener(this);

        leftSelector.setText(parentActivity.getRecordClientListType() + " date:");
        setColorScheme();
//        clientSearch.setBackgroundColor(colorSchemeManager.getAppBackground());
//        SearchView.SearchAutoComplete search = clientSearch.findViewById(android.support.v7.appcompat.R.id.search_src_text);
//        search.setTextColor(colorSchemeManager.getLighterTextColor());
//        search.setHighlightColor(colorSchemeManager.getAppBackground());
//        search.setHintTextColor(colorSchemeManager.getLighterTextColor());
//
//        clientSearch.setOnQueryTextListener(this);
//
//        TextView paginationText = paginateInfo.findViewById(R.id.paginateText);
//        try {
//            paginationText.setText("Showing: 1 to " + count + " of " + paginateObject.getString("total") + " entities");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        initScopePopupMenu();
//        initMarketStatusPopupMenu();
//        addButton.bringToFront();
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.miniDateSelectorDateFormat:
                showDatePickerDialog();
                break;
            default:
                break;
        }
    }

    private void showDatePickerDialog() {
        DatePickerDialog dialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Dialog, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Calendar cal = Calendar.getInstance();
                cal.set(year, month, day);
                dateManager.setRecordDateToDate(cal);
                updateDisplayDate(year, month, day);
            }
        }, dateManager.getRecordYear(), dateManager.getRecordMonth() - 1, dateManager.getRecordDay());

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.show();
    }

    private void updateDisplayDate(int year, int month, int day) {
        Date d;
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        month += 1;
        String formatDate = year + "/" + month + "/" + day;
        String formattedMonth = String.valueOf(month);
        if(month < 10) {
            formattedMonth = "0" + formattedMonth;
        }
        String formattedDay = String.valueOf(day);
        if(day < 10) {
            formattedDay = "0" + formattedDay;
        }
        String displayDate = year + "-" + formattedMonth + "-" + formattedDay;

        try {
            d = formatter.parse(formatDate);
            Calendar updatedTime = Calendar.getInstance();
            updatedTime.setTime(d);

            rightSelector.setText(displayDate);
        } catch (ParseException e) {
            parentActivity.showToast("Error parsing selected date");
            e.printStackTrace();
        }
    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {

    }

    @Override
    public void onEventCompleted(Object returnObject, ApiReturnTypes returnType) {

    }

    @Override
    public void onEventFailed(Object returnObject, String asyncReturnType) {

    }

    @Override
    public void onEventFailed(Object returnObject, ApiReturnTypes returnType) {

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        parentActivity.resetClientTiles(query, 1);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

}
