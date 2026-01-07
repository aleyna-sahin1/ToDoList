package com.aleynasahin.todolist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aleynasahin.todolist.databinding.RecyclerRowBinding;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListHolder> {

    ArrayList<List> toDoListArrayList;
    OnItemLongClickListener listener;

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.listener = listener;
    }

    public ListAdapter(ArrayList<List> toDoListArrayList) {
        this.toDoListArrayList = toDoListArrayList;
    }

    @NonNull
    @Override
    public ListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowBinding binding =
                RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ListHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ListHolder holder, int position) {
        holder.binding.recyclerViewTextView
                .setText(toDoListArrayList.get(position).todo);
    }

    @Override
    public int getItemCount() {
        return toDoListArrayList.size();
    }

    public class ListHolder extends RecyclerView.ViewHolder {

        RecyclerRowBinding binding;

        public ListHolder(RecyclerRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnLongClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onItemLongClick(getAdapterPosition());
                }
                return true;
            });
        }
    }
}

