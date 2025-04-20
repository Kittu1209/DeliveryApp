package com.example.fooddeliveryapp_student;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterViewHolder> {
    private List<Filter> filters;
    private OnFilterClickListener listener;

    public interface OnFilterClickListener {
        void onFilterClick(Filter filter);
    }

    public FilterAdapter(List<Filter> filters, OnFilterClickListener listener) {
        this.filters = filters;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FilterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_filter, parent, false);
        return new FilterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterViewHolder holder, int position) {
        Filter filter = filters.get(position);
        holder.bind(filter, listener);
    }

    @Override
    public int getItemCount() {
        return filters.size();
    }

    public void updateFilters(List<Filter> newFilters) {
        filters = newFilters;
        notifyDataSetChanged();
    }

    static class FilterViewHolder extends RecyclerView.ViewHolder {
        private TextView filterName;
        private View selectionIndicator;

        public FilterViewHolder(@NonNull View itemView) {
            super(itemView);
            filterName = itemView.findViewById(R.id.filter_name);
            selectionIndicator = itemView.findViewById(R.id.selection_indicator);
        }

        public void bind(final Filter filter, final OnFilterClickListener listener) {
            filterName.setText(filter.getName());
            selectionIndicator.setVisibility(filter.isSelected() ? View.VISIBLE : View.INVISIBLE);

            itemView.setOnClickListener(v -> {
                listener.onFilterClick(filter);
            });
        }
    }
}
