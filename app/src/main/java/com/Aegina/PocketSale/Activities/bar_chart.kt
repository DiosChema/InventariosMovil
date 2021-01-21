package com.Aegina.PocketSale.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.Aegina.PocketSale.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.components.XAxis.XAxisPosition.*
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter


class bar_chart : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bar_chart)

        val chart: BarChart = findViewById(R.id.barchart)

        val group1: ArrayList<BarEntry> = ArrayList()
        group1.add(BarEntry(2f, 100f))
        group1.add(BarEntry(5f, 150f))
        group1.add(BarEntry(8f, 120f))

        // create BarEntry for group 2

        // create BarEntry for group 2
        val group2: ArrayList<BarEntry> = ArrayList()
        group2.add(BarEntry(3f, 40f))
        group2.add(BarEntry(6f, 70f))
        group2.add(BarEntry(9f, 30f))

        // creating dataset for group1

        // creating dataset for group1
        val barDataSet1 = BarDataSet(group1, "Ganancia")
        barDataSet1.setColors(R.color.blue)

        // creating dataset for group2

        // creating dataset for group2
        val barDataSet2 = BarDataSet(group2, "Costo")
        barDataSet2.setColors(R.color.red)

        // combined all dataset into an arraylist

        // combined all dataset into an arraylist
        val dataSets: ArrayList<BarDataSet> = ArrayList()
        dataSets.add(barDataSet1)
        dataSets.add(barDataSet2)


        val labels: ArrayList<String> = ArrayList()
        labels.add("JAN")
        labels.add("FEB")
        labels.add("MAR")
        labels.add("APR")
        labels.add("MAY")
        labels.add("JUN")

        val xAxisFormatter: ValueFormatter =
            DayAxisValueFormatter(chart)
        //val data = BarData(labels, dataSets) // initialize the Bardata with argument labels and dataSet

        val xAxis = chart.xAxis
        xAxis.setPosition(BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(3f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(xAxisFormatter);

        val data = BarData(barDataSet1, barDataSet2)
        chart.data = data
    }

    class DayAxisValueFormatter(private val chart: BarLineChartBase<*>) :
        ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return "your text $value"
        }

    }
}