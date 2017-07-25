package com.example.yps.assignment_5;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by yPs on 4/15/2017.
 */

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.MyViewHolder> {

//data
    private List<UserListModel> userModelList;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
String TAG = "mTag";

    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 0;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    //constructor
    public UserListAdapter(List<UserListModel> userModelList,RecyclerView recyclerView) {
        this.userModelList = userModelList;
        Log.i(TAG,"UserListAdapter | Constructor Called!" + totalItemCount +"|"+ lastVisibleItem +"|"+ visibleThreshold
        +"Also, "+recyclerView.getLayoutManager()+"//"+(recyclerView.getLayoutManager() instanceof LinearLayoutManager));

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();


            recyclerView
                    .addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView,
                                               int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            Log.i(TAG,"onScroll Called!" + totalItemCount +"|"+ lastVisibleItem +"|"+ visibleThreshold);
                            totalItemCount = linearLayoutManager.getItemCount();
                            lastVisibleItem = linearLayoutManager
                                    .findLastVisibleItemPosition();
                            if (!loading
                                    && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                // End has been reached
                                // Do something
                                Log.i(TAG,"Scroll: Loadin is FALSE");
                                if (onLoadMoreListener != null) {
                                    onLoadMoreListener.onLoadMore();
                                }
                                loading = true;
                            }
                        }
                    });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return userModelList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView nickNameRow, countryNameRow, stateNameRow, yearRow;
        private OnLoadMoreListener onLoadMoreListener;

        public MyViewHolder(View view) {
            super(view);
            nickNameRow = (TextView) view.findViewById(R.id.nicknameRow);
            countryNameRow= (TextView) view.findViewById(R.id.countryNameRow);
            stateNameRow= (TextView) view.findViewById(R.id.stateNameRow);
            yearRow= (TextView) view.findViewById(R.id.yearRow);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        UserListModel userListModel = userModelList.get(position);
        holder.nickNameRow.setText(userListModel.getNickname_adp());
        holder.countryNameRow.setText(userListModel.getCountryName_adp());
        holder.stateNameRow.setText(userListModel.getStateName_adp());
        holder.yearRow.setText(userListModel.getYear_adp());
    }

    @Override
    public int getItemCount() {
        return userModelList.size();
    }

    public void setLoaded() {
        loading = false;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void clearData() {
        int size = this.userModelList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.userModelList.remove(0);
            }
            this.notifyItemRangeRemoved(0, size);
        }
    }

 /*   public static class StudentViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;

        public TextView tvEmailId;

        public Student student;

        public StudentViewHolder(View v) {
            super(v);
            tvName = (TextView) v.findViewById(R.id.tvName);

            tvEmailId = (TextView) v.findViewById(R.id.tvEmailId);

            v.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(),
                            "OnClick :" + student.getName() + " \n "+student.getEmailId(),
                            Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
        }
    }
}
*/


}//end
