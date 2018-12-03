package samuli.androidbeacons;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class FragmentBeaconData extends Fragment implements DatabaseDataAvailable {

    private View view;
    private LinearLayout linearLayout;
    private Context mainActivityContext = MainActivity.getContext();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_beacondata,container,false);
        this.view = view;
        getFromDatabase();
        linearLayout = view.findViewById(R.id.linearLayout);
        return view;
    }

    private void getFromDatabase() {
        DatabaseGetter databaseGetter = new DatabaseGetter();
        databaseGetter.setNotifierDataAvailable(this);
        databaseGetter.start();
    }


    public void dataAvailable(JSONObject jsonObject) {

        ArrayList<String> beaconNames = JSONParser.parseAndSortBeaconNames(jsonObject);
        int beaconsAmount = JSONParser.parseBeaconsAmount(jsonObject);

        for (int i = 0; i < beaconsAmount; i++) {
            TextView textView = new TextView(mainActivityContext);
            textView.setText(beaconNames.get(i));
            textView.setTextSize(20f);

            LinearLayout.LayoutParams layoutParamsTextView = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 100);

            BarChart barChart = new BarChart(mainActivityContext);
            LinearLayout.LayoutParams layoutParamsBarChart = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 700);

            textView.setLayoutParams(layoutParamsTextView);
            barChart.setLayoutParams(layoutParamsBarChart);

            HashMap<String, Integer> barChartData = JSONParser.parseBeaconDates(jsonObject, beaconNames.get(i));
            System.out.println(barChartData);
            barChart = createBarChart(barChart, barChartData);

            linearLayout.addView(textView);
            linearLayout.addView(barChart);
        }
    }

    private BarChart createBarChart(BarChart barChart, HashMap<String, Integer> dataMap) {

        TreeMap<String, Integer> dataTreeMap = new TreeMap(dataMap);

        ArrayList<Integer> barYData = new ArrayList<>();
        barYData.addAll(dataTreeMap.values());

        ArrayList<String> barXData = new ArrayList<>();
        barXData.addAll(dataTreeMap.keySet());
        ArrayList<String> barXDataFormatted = new ArrayList<>();
        for(String s : barXData) {
           barXDataFormatted.add(s.substring(0, 5));
        }

        ArrayList<BarEntry> group = new ArrayList<>();
        for (int i = 0; i < barYData.size(); i++) {
            group.add(new BarEntry(i, barYData.get(i)));
        }

        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(barXDataFormatted));
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setTextSize(10f);
        barChart.getXAxis().setGranularity(1f);
        barChart.getDescription().setEnabled(false);

        barChart.getAxisLeft().setAxisMinimum(0);
        barChart.getAxisLeft().setTextSize(15f);
        barChart.getAxisRight().setTextSize(15f);
        BarDataSet barDataSet = new BarDataSet(group, null);
        barDataSet.setValueTextSize(15f);

        List<IBarDataSet> barDataSets = new ArrayList<>();
        barDataSets.add(barDataSet);

        BarData barData = new BarData(barDataSets);
        barData.setBarWidth(0.5f);
        barData.setDrawValues(true);

        barChart.setFitBars(true);
        barChart.setPinchZoom(false);

        barChart.setData(barData);

        barChart.invalidate();
        return barChart;
    }
}
