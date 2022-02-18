package com.aldogg.test;

import java.io.IOException;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.api.VerticalBarPlot;

/** */
public class GenerateBarPlots {

    public static void main(String[] args) throws IOException {

        Table speeds = Table.read().csv("C:\\Users\\aldo\\IdeaProjects\\bitsorter\\speed.csv");

        Plot.show(VerticalBarPlot.create("Size: 10000000, Range 0:10000000", speeds.where(
                speeds
                        .intColumn("Size")
                        .isEqualTo(10000000)
                        .and(speeds.stringColumn("Range").isEqualTo("0:10000000"))),
                "Sorter",
                "Time"));

        Plot.show(VerticalBarPlot.create("Size: 10000000, Range 0:100000", speeds.where(
                speeds
                        .intColumn("Size")
                        .isEqualTo(10000000)
                        .and(speeds.stringColumn("Range").isEqualTo("0:100000"))),
                "Sorter",
                "Time"));
    }
}
