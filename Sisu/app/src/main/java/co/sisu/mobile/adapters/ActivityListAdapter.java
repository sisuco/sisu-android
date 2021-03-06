package co.sisu.mobile.adapters;

import android.content.res.ColorStateList;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.woxthebox.draglistview.DragItemAdapter;

import java.util.ArrayList;

import co.sisu.mobile.R;
import co.sisu.mobile.controllers.ColorSchemeManager;
import co.sisu.mobile.fragments.ActivitySettingsFragment;
import co.sisu.mobile.models.SelectedActivities;

public class ActivityListAdapter extends DragItemAdapter<Pair<Long, Object>, ActivityListAdapter.ViewHolder> {

    private ColorSchemeManager colorSchemeManager;
    private Switch activitySwitch;
    private TextView titleTextView;
    private int mLayoutId;
    private int mGrabHandleId;
    private boolean mDragOnLongPress;
    private boolean isEditMode;
    private ActivitySettingsFragment activitySettingsFragment;

    public ActivityListAdapter(ArrayList<Pair<Long, Object>> list, int layoutId, int grabHandleId, boolean dragOnLongPress, ColorSchemeManager colorSchemeManager, boolean isEditMode, ActivitySettingsFragment activitySettingsFragment) {
        mLayoutId = layoutId;
        mGrabHandleId = grabHandleId;
        mDragOnLongPress = dragOnLongPress;
        this.colorSchemeManager = colorSchemeManager;
        this.isEditMode = isEditMode;
        this.activitySettingsFragment = activitySettingsFragment;
        setItemList(list);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityListAdapter.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.setIsRecyclable(false);
//        Pair<Long, Object> thing = mItemList.get(position);
        final SelectedActivities selectedActivity = (SelectedActivities) mItemList.get(position).second;
        holder.itemView.setTag(mItemList.get(position));

        // Get title element
        titleTextView = holder.itemView.findViewById(R.id.activity_list_title);
        activitySwitch = holder.itemView.findViewById(R.id.activity_list_switch);

        titleTextView.setText(selectedActivity.getName());
        activitySwitch.setChecked(selectedActivity.getValue());
        activitySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.e("CHANGED", String.valueOf(isChecked));
                selectedActivity.setValue(isChecked);
                activitySettingsFragment.onCheckChanged();
            }
        });

        if(isEditMode) {
            activitySwitch.setEnabled(false);
        }

        setColorScheme();
    }

    private void setColorScheme() {
        titleTextView.setTextColor(colorSchemeManager.getDarkerText());
//        activitySwitch.setHighlightColor(colorSchemeManager.getSegmentSelected());
        int[][] states = new int[][] {
                new int[] {-android.R.attr.state_checked},
                new int[] {android.R.attr.state_checked},
        };

        int[] thumbColors = new int[] {
                Color.GRAY,
                colorSchemeManager.getMenuIcon()
        };

        int[] trackColors = new int[] {
                Color.GRAY,
                colorSchemeManager.getMenuIcon()
        };

        DrawableCompat.setTintList(DrawableCompat.wrap(activitySwitch.getThumbDrawable()), new ColorStateList(states, thumbColors));
        DrawableCompat.setTintList(DrawableCompat.wrap(activitySwitch.getTrackDrawable()), new ColorStateList(states, trackColors));

    }

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        // Get view for row item
//        View rowView = mInflater.inflate(R.layout.adapter_activity_list, parent, false);
//        final SelectedActivities selectedActivity = (SelectedActivities) getItem(position);
//        // Get title element
//        TextView titleTextView = rowView.findViewById(R.id.activity_list_title);
//
//        Switch activitySwitch = rowView.findViewById(R.id.activity_list_switch);
//
//        titleTextView.setText(selectedActivity.getName());
//        activitySwitch.setChecked(parseValue(selectedActivity.getValue()));
//        activitySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                selectedActivity.setValue(jsonIsChecked(isChecked));
//            }
//        });
//        return rowView;
//    }

    private boolean parseValue(String value) {
        if(value.equals("0")) {
            return false;
        }
        return true;
    }

    private String jsonIsChecked(boolean isChecked) {
        if(isChecked) {
            return "1";
        }
        return "0";
    }

    @Override
    public long getUniqueItemId(int position) {
        return mItemList.get(position).first;
    }

    class ViewHolder extends DragItemAdapter.ViewHolder {
        TextView mText;

        ViewHolder(final View itemView) {
            super(itemView, mGrabHandleId, mDragOnLongPress);
            mText = itemView.findViewById(R.id.activity_list_title);

        }

        @Override
        public void onItemClicked(View view) {

            try {
//                Pair<Long, Object> clientPair = mItemList.get((int) this.mItemId);
//                final ClientObject clientObject = (ClientObject) clientPair.second;
//                mClientMessagingEvent.onItemClicked(clientObject);
            } catch (ClassCastException cce) {
            }

        }

        @Override
        public boolean onItemLongClicked(View view) {
            return true;
        }
    }
}
