package com.example.peakform.ui.feedback;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.peakform.R;
import com.example.peakform.data.entity.FeedbackEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InsightsAdapter extends RecyclerView.Adapter<InsightsAdapter.ViewHolder> {
    private List<FeedbackEvent> insights = new ArrayList<>();
    private final SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    public void setList(List<FeedbackEvent> newList) {
        this.insights = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feedback_insight, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FeedbackEvent item = insights.get(position);
        holder.tvDate.setText(sdf.format(new Date(item.timestamp)));
        holder.tvInsightMessage.setText(item.message);

        // Fixed color from your colors.xml
        int color = ContextCompat.getColor(holder.itemView.getContext(), R.color.insight_blue_bg);
        holder.card.setCardBackgroundColor(color);
    }

    @Override
    public int getItemCount() { return insights.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvInsightMessage;
        CardView card;
        ViewHolder(View v) {
            super(v);
            tvDate = v.findViewById(R.id.tvInsightDate);
            tvInsightMessage = v.findViewById(R.id.tvInsightMessage);
            card = v.findViewById(R.id.insightCard);
        }
    }
}