import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;

public class GraficoEvolucao extends JFrame {
    private XYSeries series;
    private XYSeriesCollection dataset;
    private JFreeChart chart;
    private ChartPanel chartPanel;

    public GraficoEvolucao(String title) {
        super(title);
        series = new XYSeries("Melhor Fitness");
        dataset = new XYSeriesCollection(series);
        chart = ChartFactory.createXYLineChart("Evolução da Melhor Fitness","Geração","Fitness",dataset, PlotOrientation.VERTICAL, true, true, true);
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 600));
        this.setContentPane(chartPanel);
    }

    public void addDataPoint(int generation, int fitness) {
        series.add(generation, fitness);
    }
}
