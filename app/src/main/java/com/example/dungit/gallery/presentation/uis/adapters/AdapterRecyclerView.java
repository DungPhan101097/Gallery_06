package com.example.dungit.gallery.presentation.uis.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dungit.gallery.R;
import com.example.dungit.gallery.presentation.entities.ListPhotoSameDate;
import com.example.dungit.gallery.presentation.entities.Photo;

import java.util.ArrayList;

/**
 * Created by DUNGIT on 4/18/2018.
 */

public class AdapterRecyclerView extends RecyclerView.Adapter<AdapterRecyclerView.ViewHolder> {

    private ArrayList<ListPhotoSameDate> data;
    private Context context;
    private AdapterInnerRecyclerView adpInner;
    private ArrayList<ViewHolder> arr_viewholder= new ArrayList<>();
    private ArrayList<AdapterInnerRecyclerView> arr_adpInner = new ArrayList<>();
    private  ViewHolder viewholder;
    private static boolean isGrid = true;


    public AdapterRecyclerView(Context context, ArrayList<ListPhotoSameDate> data) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterRecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_row,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        viewholder = holder;
        arr_viewholder.add(holder);
        ListPhotoSameDate curLstPhoto = data.get(position);
        holder.tvDate.setText(curLstPhoto.getDate());
        holder.rvItem.setHasFixedSize(true);
        adpInner=new AdapterInnerRecyclerView(context, curLstPhoto.getLstPhotoHaveSameDate());
        arr_adpInner.add(adpInner);
        //if(holder.adapterInner == null){
        holder.rvItem.setAdapter(adpInner);
        if(isGrid)
            holder.rvItem.setLayoutManager(new GridLayoutManager(context, 4 ));
        else
            holder.rvItem.setLayoutManager( new LinearLayoutManager(context));
        // }

        //holder.rvItem.setAdapter(holder.adapterInner);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void NotifyChange()
    {
        for (int i=0;i<arr_adpInner.size();i++)
            arr_adpInner.get(i).notifyDataSetChanged();
    }
    public boolean toggle()
    {
        return adpInner.toggleItemViewType();
    }
    public void setLayout(boolean isSwitched)
    {
        isGrid = isSwitched;
        for(int i=0;i<arr_viewholder.size();i++)
            arr_viewholder.get(i).rvItem.setLayoutManager(isSwitched ? new GridLayoutManager(context, 4):new LinearLayoutManager(context));
    }
    public boolean getViewType()
    {
        return adpInner.getViewType();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDate;
        private RecyclerView rvItem;
        private AdapterInnerRecyclerView adapterInner;

        public ViewHolder(View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date);
            rvItem = itemView.findViewById(R.id.rv_item);

        }

    }
}
