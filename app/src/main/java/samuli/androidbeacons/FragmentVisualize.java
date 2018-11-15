package samuli.androidbeacons;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;

public class FragmentVisualize extends Fragment {

    PieChart pieChart;
    PieChart pieChart2;

    private int[] yData = {13, 8, 5};
    //private String[] xData = {"Keltainen", "Punainen", "Pinkki"};
    private int[] yData2 = {4, 9, 9};

    Context mainActivityContext = MainActivity.getContext();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_visualize,container,false);

        pieChart = view.findViewById(R.id.idPieChart);
        pieChart = createPieChart(pieChart, yData, "You");

        pieChart2 = view.findViewById(R.id.idPieChart2);
        pieChart2 = createPieChart(pieChart2, yData2, "All users");

        return view;
    }

    private PieChart createPieChart(PieChart pieChart, int[] yData, String centerText) {
        pieChart.setCenterText(centerText);
        pieChart.setCenterTextSize(15f);
        pieChart.setDrawEntryLabels(true);
        pieChart.setEntryLabelTextSize(15f);
        pieChart.getDescription().setText("");
        pieChart.getDescription().setTextSize(15f);

        ArrayList<PieEntry> yEntrys = new ArrayList<>();

        for(int i = 0; i < yData.length; i++){
            yEntrys.add(new PieEntry(yData[i] , i));
        }


        PieDataSet pieDataSet = new PieDataSet(yEntrys, "");
        pieDataSet.setValueTextColor(ContextCompat.getColor(mainActivityContext, R.color.lightblue));
        pieDataSet.setValueTextSize(20);

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(ContextCompat.getColor(mainActivityContext, R.color.yellow));
        colors.add(ContextCompat.getColor(mainActivityContext, R.color.maroon));
        colors.add(ContextCompat.getColor(mainActivityContext, R.color.pink));
        pieDataSet.setColors(colors);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);

        pieChart.invalidate();

        return pieChart;
    }
}
