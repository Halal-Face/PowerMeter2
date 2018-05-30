package com.halalface.powermeter2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{
    private static final String TAG = "RecyclerViewAdapter";
    private ArrayList<String> names = new ArrayList<>();
    private Context my_context;
    ViewHolder mHolder;

    public RecyclerViewAdapter(ArrayList<String> names, Context my_context) {
        this.names = names;
        this.my_context = my_context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent, false);
        ViewHolder holder = new ViewHolder(view);
        mHolder = holder;
        return mHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called");
        holder.name_textview.setText(names.get(position));
        holder.parent_layout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: onBind");
                mHolder.child_layout.setVisibility(View.VISIBLE);
                mHolder.save_button.setVisibility(View.VISIBLE);
            }
        });
        mHolder.save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHolder.child_layout.setVisibility(View.INVISIBLE);
                mHolder.save_button.setVisibility(View.INVISIBLE);
            }
        });

    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name_textview;
        EditText weight_edittext;
        EditText rep_edittext;
        EditText set_edittest;
        ImageButton save_button;
        LinearLayout parent_layout;
        LinearLayout child_layout;

        public ViewHolder(View itemView) {
            super(itemView);
            name_textview = itemView.findViewById(R.id.name);
            weight_edittext = itemView.findViewById(R.id.weight);
            rep_edittext = itemView.findViewById(R.id.rep);
            set_edittest = itemView.findViewById(R.id.set);
            save_button = itemView.findViewById(R.id.save);
            parent_layout = itemView.findViewById(R.id.parent_layout);
            child_layout = itemView.findViewById(R.id.child_layout);


        }
    }
}
