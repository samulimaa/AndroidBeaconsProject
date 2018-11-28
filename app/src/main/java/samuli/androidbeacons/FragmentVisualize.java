package samuli.androidbeacons;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class FragmentVisualize extends Fragment implements DatabaseDataAvailable {

    private PieChart pieChart;
    private PieChart pieChart2;

    private BarChart barChart;

    LinearLayout linearLayout;

    private ArrayList<Integer> currentUserTime = new ArrayList<>();
    private ArrayList<Integer> allUsersTime = new ArrayList<>();

    private HashMap<String, Integer> currentUserMap = new HashMap<>();
    private HashMap<String, Integer> allUsersMap = new HashMap<>();

    private int beaconsAmount;

    Context mainActivityContext = MainActivity.getContext();

    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_visualize,container,false);
        this.view = view;
        linearLayout = view.findViewById(R.id.linearLayout);
        getFromDatabase();
        return view;
    }


    private void getFromDatabase() {
        DatabaseGetter databaseGetter = new DatabaseGetter();
        databaseGetter.setNotifierDataAvailable(this);
        databaseGetter.start();
    }

    @Override
    public void dataAvailable(JSONObject jsonObject) {

        System.out.println(jsonObject);

        currentUserMap = JSONParser.parseCurrentUserTime(jsonObject, 7);
        allUsersMap = JSONParser.parseAllUsersTime(jsonObject);

        System.out.println(currentUserMap.toString());
        System.out.println(allUsersMap.toString());

        beaconsAmount = JSONParser.parseBeaconsAmount(jsonObject);
        System.out.println(beaconsAmount);

        pieChart = view.findViewById(R.id.idPieChart);
        pieChart = createPieChart(pieChart, currentUserMap, "You");

        pieChart2 = view.findViewById(R.id.idPieChart2);
        pieChart2 = createPieChart(pieChart2, allUsersMap, "All users");


        ArrayList<String> beaconNames = JSONParser.parseAndSortBeaconNames(jsonObject);

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

    private PieChart createPieChart(PieChart pieChart, HashMap<String, Integer> dataHashMap, String centerText) {
        pieChart.setCenterText(centerText);
        pieChart.setCenterTextSize(15f);
        pieChart.setDrawEntryLabels(true);
        pieChart.setEntryLabelTextSize(15f);
        pieChart.getDescription().setText("");
        pieChart.getDescription().setTextSize(15f);
        pieChart.getLegend().setEnabled(true);

        ArrayList<PieEntry> yEntrys = new ArrayList<>();

        TreeMap<String, Integer> sortedDataMap = new TreeMap(dataHashMap);

        for (Map.Entry<String, Integer> entry : sortedDataMap.entrySet()) {
            yEntrys.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet pieDataSet = new PieDataSet(yEntrys, "");
        pieDataSet.setValueTextColor(ContextCompat.getColor(mainActivityContext, R.color.lightblue));
        pieDataSet.setValueTextSize(20);

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(ContextCompat.getColor(mainActivityContext, R.color.blue));
        colors.add(ContextCompat.getColor(mainActivityContext, R.color.magenta));
        colors.add(ContextCompat.getColor(mainActivityContext, R.color.green));
        colors.add(ContextCompat.getColor(mainActivityContext, R.color.maroon));
        colors.add(ContextCompat.getColor(mainActivityContext, R.color.orange));
        pieDataSet.setColors(colors);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);

        pieChart.invalidate();

        return pieChart;
    }

    private BarChart createBarChart(BarChart barChart, HashMap<String, Integer> dataMap) {

        TreeMap<String, Integer> dataTreeMap = new TreeMap(dataMap);

        ArrayList<Integer> barYData = new ArrayList<>();
        barYData.addAll(dataTreeMap.values());

        ArrayList<String> barXData = new ArrayList<>();
        barXData.addAll(dataTreeMap.keySet());

        ArrayList<BarEntry> group = new ArrayList<>();
        for (int i = 0; i < barYData.size(); i++) {
            group.add(new BarEntry(i, barYData.get(i)));
        }

        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(barXData));
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setTextSize(15f);
        barChart.getDescription().setEnabled(false);

        barChart.getAxisLeft().setAxisMinimum(0);
        barChart.getAxisLeft().setTextSize(15f);
        barChart.getAxisRight().setTextSize(15f);
        BarDataSet barDataSet = new BarDataSet(group, null);
        barDataSet.setValueTextSize(15f);

        List<IBarDataSet> barDataSets = new ArrayList<>();
        barDataSets.add(barDataSet);

        BarData barData = new BarData(barDataSets);
        barData.setBarWidth(0.8f);
        barData.setDrawValues(true);

        barChart.setFitBars(true);
        barChart.setPinchZoom(false);

        barChart.setData(barData);

        barChart.invalidate();
        return barChart;
    }
}
