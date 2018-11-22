package samuli.androidbeacons;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class FragmentVisualize extends Fragment implements DatabaseDataAvailable {

    private PieChart pieChart;
    private PieChart pieChart2;

    private BarChart barChart;

    private ArrayList<Integer> currentUserTime = new ArrayList<>();
    private ArrayList<Integer> allUsersTime = new ArrayList<>();

    private HashMap<String, Integer> currentUserMap = new HashMap<>();
    private HashMap<String, Integer> allUsersMap = new HashMap<>();

    Context mainActivityContext = MainActivity.getContext();

    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_visualize,container,false);
        this.view = view;
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

        for (Map.Entry<String, Integer> entry: currentUserMap.entrySet()) {
            currentUserTime.add(entry.getValue());
        }

        for (Map.Entry<String, Integer> entry : allUsersMap.entrySet()) {
            allUsersTime.add(entry.getValue());
        }

        pieChart = view.findViewById(R.id.idPieChart);
        pieChart = createPieChart(pieChart, currentUserTime, "You");

        pieChart2 = view.findViewById(R.id.idPieChart2);
        pieChart2 = createPieChart(pieChart2, allUsersTime, "All users");

        //barChart = view.findViewById(R.id.idBarChart);
        //barChart = createBarChart(barChart);
    }

    private PieChart createPieChart(PieChart pieChart, ArrayList<Integer> yData, String centerText) {
        pieChart.setCenterText(centerText);
        pieChart.setCenterTextSize(15f);
        pieChart.setDrawEntryLabels(true);
        pieChart.setEntryLabelTextSize(15f);
        pieChart.getDescription().setText("");
        pieChart.getDescription().setTextSize(15f);
        pieChart.getLegend().setEnabled(true);

        ArrayList<PieEntry> yEntrys = new ArrayList<>();

        for(int i = 0; i < yData.size(); i++){
            yEntrys.add(new PieEntry(yData.get(i), i));
        }

        PieDataSet pieDataSet = new PieDataSet(yEntrys, "");
        pieDataSet.setValueTextColor(ContextCompat.getColor(mainActivityContext, R.color.lightblue));
        pieDataSet.setValueTextSize(20);

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(ContextCompat.getColor(mainActivityContext, R.color.blue));
        colors.add(ContextCompat.getColor(mainActivityContext, R.color.yellow));
        colors.add(ContextCompat.getColor(mainActivityContext, R.color.green));
        colors.add(ContextCompat.getColor(mainActivityContext, R.color.maroon));
        colors.add(ContextCompat.getColor(mainActivityContext, R.color.orange));
        pieDataSet.setColors(colors);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);

        pieChart.invalidate();

        return pieChart;
    }

    private BarChart createBarChart(BarChart barChart) {

        ArrayList<Integer> barYData = new ArrayList<>();
        barYData.add(5);
        barYData.add(6);
        barYData.add(4);
        barYData.add(8);

        ArrayList<String> barXData = new ArrayList<>();
        barXData.add("aa");
        barXData.add("aaa");
        barXData.add("aaaa");

        ArrayList<BarEntry> group = new ArrayList<>();
        for (int i = 0; i < barYData.size(); i++) {
            group.add(new BarEntry(i,barYData.get(i)));
        }

        BarDataSet barDataSet = new BarDataSet(group, "label");

        List<IBarDataSet> barDataSets = new ArrayList<>();
        barDataSets.add(barDataSet);

        BarData barData = new BarData(barDataSets);
        barData.setBarWidth(0.5f);
        barData.setDrawValues(true);

        barChart.setFitBars(true);
        barChart.setPinchZoom(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        barChart.setData(barData);

        barChart.invalidate();
        return barChart;
    }
}
