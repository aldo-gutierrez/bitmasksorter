package com.aldogg.test;

import java.io.IOException;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.api.VerticalBarPlot;

/** */
public class GenerateBarPlots {

    public static void main(String[] args) throws IOException {

        Table speeds = Table.read().csv("C:\\Users\\aldo\\IdeaProjects\\bitsorter\\speed_object.csv");
        int size;
        int range;

        size = 10000000;
        range = 10000000;
        Plot.show(VerticalBarPlot.create("Size: "+ size +", Range 0:"+ range, speeds.where(
                speeds
                        .intColumn("Size")
                        .isEqualTo(size)
                        .and(speeds.stringColumn("Range").isEqualTo("0:"+range))),
                "Sorter",
                "Time"));

        size = 10000000;
        range = 100000;
        Plot.show(VerticalBarPlot.create("Size: "+ size +", Range 0:"+ range, speeds.where(
                speeds
                        .intColumn("Size")
                        .isEqualTo(size)
                        .and(speeds.stringColumn("Range").isEqualTo("0:"+range))),
                "Sorter",
                "Time"));

        size = 40000000;
        range = 1000000000;
        Plot.show(VerticalBarPlot.create("Size: "+ size +", Range 0:"+ range, speeds.where(
                speeds
                        .intColumn("Size")
                        .isEqualTo(size)
                        .and(speeds.stringColumn("Range").isEqualTo("0:"+range))),
                "Sorter",
                "Time"));

    }
}
