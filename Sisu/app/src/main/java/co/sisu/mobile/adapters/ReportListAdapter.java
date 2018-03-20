package co.sisu.mobile.adapters;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v7.content.res.AppCompatResources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.models.Metric;

/**
 * Created by Brady Groharing on 2/24/2018.
 */

public class ReportListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Metric> mDataSource;

    public ReportListAdapter(Context context, List<Metric> items) {
        mContext = context;
        mDataSource = (ArrayList<Metric>) items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mDataSource.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataSource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get view for row item
        View rowView = mInflater.inflate(R.layout.adapter_report_list, parent, false);

        // Get title element
        TextView titleTextView = rowView.findViewById(R.id.report_list_title);

        // Get subtitle element
        TextView subtitleTextView = rowView.findViewById(R.id.report_list_subtitle);

        // Get percentage text element
        TextView percentageTextView = rowView.findViewById(R.id.report_percentage_text);

        // Get thumbnail element
        ImageView thumbnailImageView = rowView.findViewById(R.id.report_list_thumbnail);

        ProgressBar progressBar = rowView.findViewById(R.id.progressBar);

        Metric metric = (Metric) getItem(position);

        titleTextView.setText(metric.getTitle());

        subtitleTextView.setText(metric.getCurrentNum() + " of " + metric.getGoalNum());
        percentageTextView.setText(metric.getPercentComplete() + "% complete");
        progressBar.setProgress(metric.getPercentComplete());
        progressBar.setScaleY(4f);
        int color = ContextCompat.getColor(mContext, metric.getColor());
        progressBar.setProgressTintList(ColorStateList.valueOf(color));
        animateBars(progressBar);
        thumbnailImageView.setImageResource(metric.getThumbnailId());

        return rowView;
    }

    private void animateBars(ProgressBar progressBar){
        final int ANIMATION_DURATION = 2500;
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 0, progressBar.getProgress());
        animation.setDuration(ANIMATION_DURATION);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }
}
