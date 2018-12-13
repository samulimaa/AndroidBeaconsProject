package samuli.androidbeacons;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class FragmentUserData extends Fragment implements DatabaseDataAvailable, AdapterView.OnItemSelectedListener {

    private PieChart pieChart;
    private PieChart pieChart2;

    LinearLayout linearLayout;

    private JSONObject jsonObjectData;
    private JSONObject jsonObjectUsers;

    private HashMap<String, Integer> currentUserMap = new HashMap<>();

    private TreeMap<String, Integer> userDataMap = new TreeMap<>();

    private ArrayList<String> allUsersList = new ArrayList<>();

    Context mainActivityContext = MainActivity.getContext();

    ArrayAdapter<String> adapter;

    private View view;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_userdata, container, false);
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
    public void dataAvailable(JSONObject jsonObjectData, JSONObject jsonObjectUsers) {

        this.jsonObjectData = jsonObjectData;
        this.jsonObjectUsers = jsonObjectUsers;

        currentUserMap = JSONParser.parseUserTime(jsonObjectData, 7);

        System.out.println(currentUserMap.toString());

        pieChart = view.findViewById(R.id.idPieChart);
        pieChart = createPieChart(pieChart, currentUserMap, "You");

        allUsersList.add("All users");
        allUsersList.addAll(JSONParser.parseAllUsers(jsonObjectUsers).keySet());

        Spinner spinner = view.findViewById(R.id.spinner);
        adapter = new ArrayAdapter<>(mainActivityContext, android.R.layout.simple_spinner_dropdown_item, allUsersList);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        pieChart2 = view.findViewById(R.id.idPieChart2);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String userName = adapter.getItem(position);
        if (position == 0) {
            userDataMap = new TreeMap<String, Integer>(JSONParser.parseAllUsersTime(jsonObjectData));
        } else {
            userDataMap = new TreeMap<String, Integer>(JSONParser.parseUserTime(jsonObjectData, JSONParser.parseAllUsers(jsonObjectUsers).get(userName)));
        }

        pieChart2 = createPieChart(pieChart2, userDataMap, userName);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private PieChart createPieChart(PieChart pieChart, Map<String, Integer> dataHashMap, String centerText) {
        pieChart.setCenterText(centerText + "\n Total: " + secondsToMinutesAndSeconds(totalSeconds(dataHashMap)));
        pieChart.setCenterTextSize(15f);
        pieChart.setDrawEntryLabels(true);
        pieChart.setEntryLabelTextSize(15f);
        pieChart.getDescription().setText("");
        pieChart.getDescription().setTextSize(15f);
        pieChart.getLegend().setEnabled(true);
        pieChart.getLegend().setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        pieChart.getLegend().setTextColor(ContextCompat.getColor(mainActivityContext, R.color.white));

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
        pieData.setValueTextSize(15f);

        pieData.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return secondsToMinutesAndSeconds((int) value);
            }
        });


        pieChart.setData(pieData);


        pieChart.invalidate();

        return pieChart;
    }

    private int totalSeconds(Map<String, Integer> dataHashMap) {
        int totalSeconds = 0;
        for (Integer i : dataHashMap.values()) {
            totalSeconds += i;
        }
        return totalSeconds;
    }

    private String secondsToMinutesAndSeconds(int inputSeconds) {
        int minutes = inputSeconds / 60;
        int seconds = inputSeconds % 60;
        if (minutes < 1) {
            return (seconds + "s");
        } else {
            return (minutes + "min " + seconds + "s");
        }
    }
}
