package com.cst2335.covid19casedata_teamproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cst2335.covid19casedata_teamproject.R;
import com.cst2335.covid19casedata_teamproject.data.CaseData;

import java.util.ArrayList;

/*
 *
 * Recyclerview adapter to list province with count
 */
public class ProvinceListAdapter extends RecyclerView.Adapter<ProvinceListAdapter.ViewHolder>{

    private ArrayList<CaseData> mCountryCaseDataList;

    public ProvinceListAdapter(ArrayList<CaseData> mCountryCaseDataList) {
        this.mCountryCaseDataList = mCountryCaseDataList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.listrow_province, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final CaseData mCountryCaseData = mCountryCaseDataList.get(position);
        holder.provinceTxt.setText("Province: "+ (mCountryCaseData.getProvince() != null ? mCountryCaseData.getProvince() : ""));
        holder.countTxt.setText("Count: "+String.valueOf(mCountryCaseData.getCount()));
    }


    @Override
    public int getItemCount() {
        return mCountryCaseDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView provinceTxt,countTxt;

        public ViewHolder(View itemView) {
            super(itemView);
            this.provinceTxt = (TextView) itemView.findViewById(R.id.txt_province);
            this.countTxt = (TextView) itemView.findViewById(R.id.txt_count);
        }
    }
}
