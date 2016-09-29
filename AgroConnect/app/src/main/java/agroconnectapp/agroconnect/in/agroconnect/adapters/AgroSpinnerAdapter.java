package agroconnectapp.agroconnect.in.agroconnect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.List;

import agroconnectapp.agroconnect.in.agroconnect.R;


public class AgroSpinnerAdapter extends ArrayAdapter<String> {

    private Context context;
    private Spinner spinner;
    public AgroSpinnerAdapter(Context context, int textViewResourceId, List<String> list, Spinner spinner) {
        super(context, textViewResourceId, list);
        this.context = context;
        this.spinner = spinner;
    }
    public AgroSpinnerAdapter(Context context, int textViewResourceId, String[] list, Spinner spinner) {
        super(context, textViewResourceId, list);
        this.context = context;
        this.spinner = spinner;
    }
    @Override
    public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.spinner_layout, null);
        ((TextView) view.findViewById(R.id.spinnerTV)).setText(getItem(position));
        ImageView image = (ImageView)view.findViewById(R.id.spinnerIV);
        if (spinner.getSelectedItemPosition() == position) {
            view.setBackgroundColor(getContext().getResources().getColor(R.color.color_gray));
            image.setBackground(getContext().getResources().getDrawable(R.mipmap.green_plant));
        }
        else {
            image.setVisibility(View.INVISIBLE);
        }
        return view;
    }
}