package com.halalface.powermeter2;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by dell on 2/4/2016.
 */
public class CustomListAdapter extends BaseExpandableListAdapter {

    ArrayList<ListItemModel> groupItem;
    Boolean toggle = false;
    GroupViewHolder groupViewHolder;
    ChildViewHolder childViewHolder;

    Context mContext;

    public LayoutInflater minflater;

    public CustomListAdapter(Context context, ArrayList<ListItemModel> groupItem) {
        this.mContext = context;
        this.groupItem = groupItem;
    }

    public ArrayList<ListItemModel> getGroupItem() {
        return groupItem;
    }


    public void setInflater(LayoutInflater mInflater) {
        this.minflater = mInflater;
    }

    @Override
    public int getGroupCount() {
        return groupItem.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return groupItem.get(i).getArrayList().size();
    }

    @Override
    public Object getGroup(int i) {
        return groupItem.get(i);
    }

    @Override
    public Object getChild(int i, int i2) {
        return groupItem.get(i).getArrayList().get(i2);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i2) {
        return i2;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            groupViewHolder = new GroupViewHolder();
            convertView = minflater.inflate(R.layout.list_header, null);
            groupViewHolder.tvTitle = (TextView) convertView.findViewById(R.id.title);
            groupViewHolder.btnAdd = (Button) convertView.findViewById(R.id.addEdittext);
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }

        groupViewHolder.tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Implement: go to another activity with indepth info on the exercise
            }
        });
        final ExpandableListView eLV = (ExpandableListView) parent;
        groupViewHolder.tvTitle.setText(groupItem.get(groupPosition).getTitle());
        if(getChildrenCount(groupPosition)==0) {

            groupItem.get(groupPosition).getArrayList().add(new EdittextValues(""));
            groupItem.get(groupPosition).getArrayList().add(new EdittextValues(""));
            groupItem.get(groupPosition).getArrayList().add(new EdittextValues(""));
        }

        groupViewHolder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!groupItem.get(groupPosition).toggle) {
                    eLV.expandGroup(groupPosition);

                    getChildrenCount(groupPosition);
                    notifyDataSetChanged();
                    groupItem.get(groupPosition).toggle = !groupItem.get(groupPosition).toggle;
                }
                else{
                    eLV.collapseGroup(groupPosition);


                    groupItem.get(groupPosition).toggle = !groupItem.get(groupPosition).toggle;
                }
            }
        });

        groupViewHolder.tvTitle.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String name = groupItem.get(groupPosition).getTitle().replaceAll(" ", "_");

                Intent intent = new Intent(mContext, ExerciseInDepth.class);
                intent.putExtra("name", name);
                Toast.makeText(mContext, name, Toast.LENGTH_SHORT).show();
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity)mContext, v,"add");

                mContext.startActivity(intent,options.toBundle());

            }
        });

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            childViewHolder = new ChildViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_row, null);


            childViewHolder.et = (EditText) convertView.findViewById(R.id.edittext);

            convertView.setTag(childViewHolder);



        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }

        if (!groupItem.get(groupPosition).getArrayList().get(childPosition).getValue().equals(""))
            childViewHolder.et.setText(groupItem.get(groupPosition).getArrayList().get(childPosition).getValue());
        else
            childViewHolder.et.setText("");

        childViewHolder.et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    final EditText Caption = (EditText) v;
                    groupItem.get(groupPosition).getArrayList().get(childPosition).setValue(Caption.getText().toString());
                }
            }
        });

        switch (childPosition){
            case 0:
                childViewHolder.et.setHint("Weight");
                break;
            case 1:
                childViewHolder.et.setHint("Rep");
                break;
            case 2:
                childViewHolder.et.setHint("Set");
                break;
            default:
                childViewHolder.et.setHint("Child: "+childPosition);
                break;
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i2) {
        return false;
    }

    private class GroupViewHolder {
        public TextView tvTitle;
        public Button btnAdd;
    }

    private class ChildViewHolder {
        public EditText et;
    }


}
