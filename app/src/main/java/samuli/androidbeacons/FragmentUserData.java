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

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class FragmentUserData extends Fragment implements DatabaseDataAvailable {

    private PieChart pieChart;
    private PieChart pieChart2;

    LinearLayout linearLayout;

    private HashMap<String, Integer> currentUserMap = new HashMap<>();
    private HashMap<String, Integer> allUsersMap = new HashMap<>();

    Context mainActivityContext = MainActivity.getContext();
    private View view;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_userdata,container,false);
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

        pieChart = view.findViewById(R.id.idPieChart);
        pieChart = createPieChart(pieChart, currentUserMap, "You");

        pieChart2 = view.findViewById(R.id.idPieChart2);
        pieChart2 = createPieChart(pieChart2, allUsersMap, "All users");

        //notifier.dataAvailable(jsonObject);

    }

    private PieChart createPieChart(PieChart pieChart, HashMap<String, Integer> dataHashMap, String centerText) {
        pieChart.setCenterText(centerText +"\n Total: " + totalSeconds(dataHashMap) + "s");
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
        pieChart.setData(pieData);

        pieChart.invalidate();

        return pieChart;
    }

    private int totalSeconds(HashMap<String, Integer> dataHashMap) {
        int totalSeconds = 0;
        for(Integer i : dataHashMap.values()) {
            totalSeconds += i;
        }
        return totalSeconds;
    }
}
