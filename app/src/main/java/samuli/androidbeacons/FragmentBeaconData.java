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
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class FragmentBeaconData extends Fragment implements DatabaseDataAvailable {

    private View view;
    private LinearLayout linearLayout;
    private Context mainActivityContext = MainActivity.getContext();

    private int beaconsAmount;

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

    public void dataAvailable(JSONObject jsonObjectData, JSONObject jsonObjectUsers) {

        System.out.println(jsonObjectData);
        System.out.println(jsonObjectUsers);

        beaconsAmount = JSONParser.parseBeaconsAmount(jsonObjectData);

        ArrayList<String> beaconNames = JSONParser.parseAndSortBeaconNames(jsonObjectData);

        HashMap<String, HashMap> dataMaps = new HashMap<>();
        HashMap<String, Integer> unsortedMap = new HashMap<>();

        for(int i = 0; i < beaconsAmount; i++) {

            HashMap<String, Integer> barChartData = JSONParser.parseBeaconDates(jsonObjectData, beaconNames.get(i));
            dataMaps.put(beaconNames.get(i), barChartData);
            unsortedMap.put(beaconNames.get(i), totalSeconds(barChartData));
        }

        LinkedHashMap<String, Integer> sortedMap = sortHashMap(unsortedMap);

        LinkedHashMap<String, HashMap> sortedDataMap = new LinkedHashMap<>();
        ArrayList<String> sortedMapKeySet = new ArrayList<>(sortedMap.keySet());

        for (String s : sortedMapKeySet) {
            sortedDataMap.put(s, dataMaps.get(s));
        }

        createUi(sortedDataMap);

    }

    private void createUi(LinkedHashMap<String, HashMap> dataMaps) {

        ArrayList<String> dataMapsKeySet = new ArrayList<>(dataMaps.keySet());
        ArrayList<HashMap> dataMapsValues = new ArrayList<>(dataMaps.values());
        int i = 0;
        for (HashMap barChartData : dataMapsValues) {
            TextView textView = new TextView(mainActivityContext);

            LinearLayout.LayoutParams layoutParamsTextView = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 100);

            BarChart barChart = new BarChart(mainActivityContext);
            LinearLayout.LayoutParams layoutParamsBarChart = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 700);

            textView.setLayoutParams(layoutParamsTextView);
            barChart.setLayoutParams(layoutParamsBarChart);

            System.out.println(barChartData);
            barChart = createBarChart(barChart, barChartData);

            System.out.println(dataMapsKeySet.get(i) + totalSeconds(barChartData));
            System.out.println(secondsToMinutesAndSeconds(totalSeconds(barChartData)));
            textView.setText(dataMapsKeySet.get(i) + ", total: " + secondsToMinutesAndSeconds(totalSeconds(barChartData)));
            textView.setTextSize(20f);

            linearLayout.addView(textView);
            linearLayout.addView(barChart);

            i++;
        }
    }

    private BarChart createBarChart(BarChart barChart, Map<String, Integer> dataMap) {

        TreeMap<String, Integer> swappedDatesMap = new TreeMap<>();
        for (String s : dataMap.keySet()) {
            swappedDatesMap.put(swapDayMonth(s), dataMap.get(s));
        }

        ArrayList<Integer> barYData = new ArrayList<>();
        barYData.addAll(swappedDatesMap.values());

        ArrayList<String> barXDataFormatted = new ArrayList<>();
        for(String s : swappedDatesMap.keySet()) {
           barXDataFormatted.add(swapDayMonth(s).substring(0, 5));
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

        barData.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return secondsToMinutesAndSeconds((int) value);
            }
        });

        barChart.setFitBars(true);
        barChart.setPinchZoom(false);

        barChart.setData(barData);

        barChart.invalidate();
        return barChart;
    }

    private String swapDayMonth(String date) {
        String swapped = "";
        swapped += date.substring(3,6);
        swapped += date.substring(0,3);
        swapped += date.substring(6);
        return swapped;
    }

    private int totalSeconds(Map<String, Integer> map) {
        int total = 0;
        for (int i : map.values()) {
            total += i;
        }
        return total;
    }

    private String secondsToMinutesAndSeconds(int inputSeconds) {
        int minutes = inputSeconds / 60;
        int seconds = inputSeconds % 60;
        if(minutes < 1) {
            return(seconds + "s");
        } else {
            return(minutes + "min " + seconds + "s");
        }
    }

    private LinkedHashMap<String, Integer> sortHashMap(HashMap<String, Integer> unsortedMap) {

        List<String> keys = new ArrayList<>(unsortedMap.keySet());
        List<Integer> values = new ArrayList<>(unsortedMap.values());

        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();

        Collections.sort(keys, Collections.<String>reverseOrder());
        Collections.sort(values, Collections.<Integer>reverseOrder());

        Iterator<Integer> valueIterator = values.iterator();

        while (valueIterator.hasNext()) {
            Integer val = valueIterator.next();
            Iterator<String> keyIterator = keys.iterator();

            while (keyIterator.hasNext()) {
                String key = keyIterator.next();
                Integer value1 = unsortedMap.get(key);
                Integer value2 = val;

                if (value1.equals(value2)) {
                    keyIterator.remove();
                    sortedMap.put(key, val);
                    break;
                }
            }
        }
        return sortedMap;
    }
}
