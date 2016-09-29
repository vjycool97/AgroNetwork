package agroconnectapp.agroconnect.in.agroconnect;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import agroconnectapp.agroconnect.in.agroconnect.networkclient.model.City;
import agroconnectapp.agroconnect.in.agroconnect.networkclient.model.Commodity;
import agroconnectapp.agroconnect.in.agroconnect.utility.Constants;

/**
 * Created by niteshtarani on 03/04/16.
 */
public class CustomListDialog extends Dialog {

    private Activity activity;
    private int type;
    private OnDialogResult mDialogResult;
    private SharedPreferences sharedPreferences;

    public CustomListDialog(Activity activity, int type) {
        super(activity);
        this.activity = activity;
        this.type = type;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= 21)
            getWindow().setStatusBarColor(activity.getResources().getColor(R.color.colorPrimaryDark));

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        RecyclerView recyclerView = new RecyclerView(activity);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(new CustomAdapter(activity, type));
        setContentView(recyclerView);
    }

    public void setDialogResult(OnDialogResult dialogResult){
        mDialogResult = dialogResult;
    }

    public interface OnDialogResult{
        void returnData(String id, String name);
    }

    class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

        private Context context;
        private int type;

        public CustomAdapter(Context context,int type) {
            this.context = context;
            this.type = type;
        }

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView tv = (TextView) LayoutInflater.from(context).inflate(R.layout.view_spinner_item,null);
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(20);
            tv.setPadding(context.getResources().getDimensionPixelSize(R.dimen.d8), context.getResources().getDimensionPixelSize(R.dimen.d8), context.getResources().getDimensionPixelSize(R.dimen.d8), context.getResources().getDimensionPixelSize(R.dimen.d8));
            tv.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,RecyclerView.LayoutParams.WRAP_CONTENT));
            return new CustomViewHolder(tv);
        }

        @Override
        public void onBindViewHolder(CustomViewHolder holder, int position) {
            final String selectedLocale = sharedPreferences.getString(Constants.selectedLocale,"en");
            final boolean eng = selectedLocale.equals("en");

            if(type == Constants.TYPE_CITY) {
                final City city = NetworkController.getInstance().mCityList.get(position);
                holder.text.setText(city.getLocalName());
                holder.text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String id = city.getCityId();
                        mDialogResult.returnData(id, city.getLocalName());
                    }
                });

            }
            else if(type == Constants.TYPE_COMMODITY) {
                final Commodity commodity = NetworkController.getInstance().mCommodityList.get(position);
                holder.text.setText(commodity.getLocalName());
                holder.text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String id = commodity.getCommodityId();
                        mDialogResult.returnData(id, commodity.getLocalName());
                    }
                });

            }
        }

        @Override
        public int getItemCount() {
            if(type == Constants.TYPE_CITY)
                return NetworkController.getInstance().mCityList.size();
            else if(type == Constants.TYPE_COMMODITY)
                return NetworkController.getInstance().mCommodityList.size();

            return 0;
        }

        class CustomViewHolder extends RecyclerView.ViewHolder {

            TextView text;

            public CustomViewHolder(View itemView) {
                super(itemView);
                text = (TextView) itemView.findViewById(R.id.text1);
            }
        }
    }
}
