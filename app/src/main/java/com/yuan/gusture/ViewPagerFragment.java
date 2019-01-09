package com.yuan.gusture;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author yuanye
 * @date 2018/12/25
 */
public class ViewPagerFragment extends Fragment {

    private RecyclerView rlvList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_layout, container, false);
        rlvList = view.findViewById(R.id.rlv_list);
        rlvList.setLayoutManager(new LinearLayoutManager(getContext()));
        rlvList.setAdapter(new RLVAdapter(getContext()) {
            @Override
            public int getItemLayout(ViewGroup parent, int viewType) {
                return android.R.layout.simple_list_item_1;
            }

            @Override
            public void onBindHolder(ViewHolder holder, int position) {
                holder.setText(android.R.id.text1, position + "");
            }

            @Override
            public int getItemCount() {
                return 40;
            }
        });
        return view;
    }
}
