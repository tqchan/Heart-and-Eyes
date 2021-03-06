package jp.anlab.hearteyes.taku.heart_and_eyes;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;

import org.afree.chart.AFreeChart;
import org.afree.chart.axis.NumberAxis;
import org.afree.chart.axis.NumberTickUnit;
import org.afree.chart.axis.TickUnit;
import org.afree.chart.axis.TickUnits;
import org.afree.chart.axis.ValueAxis;
import org.afree.chart.plot.XYPlot;
import org.afree.chart.renderer.xy.XYBarRenderer;
import org.afree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.afree.data.xy.XYSeries;
import org.afree.data.xy.XYSeriesCollection;
import org.afree.graphics.SolidColor;
import org.afree.graphics.geom.Font;

import java.util.ArrayList;

/**
 * Created by taku on 2016/04/12.
 */
public class GraphView extends Graph {
    private static final String TAG = GraphView.class.getSimpleName();
    private final GraphView self = this;
    private AFreeChart aFreeChart;

    /**
     * コンストラクタ
     */
    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * グラフ生成 心拍数
     */
    public void createChart(ArrayList<Integer> wellnessArray, String title, double Ymin, double Ymax) {
        // X軸の定義
        NumberAxis domainAxis = new NumberAxis("時間");

        // Y軸の定義
        NumberAxis rangeAxis = new NumberAxis(title);

        // XY軸のタイトルのフォント設定
        // 種類、大きさ
        Font xyTitleFont = new Font(Typeface.SANS_SERIF, Typeface.BOLD, 30);
        domainAxis.setLabelFont(xyTitleFont);
        rangeAxis.setLabelFont(xyTitleFont);

        // XY軸の目盛のフォントの設定
        // フォントの種類、大きさ
        Font tickFont = new Font(Typeface.MONOSPACE, Typeface.NORMAL, 20);
        domainAxis.setTickLabelFont(tickFont);
        rangeAxis.setTickLabelFont(tickFont);

        // 折れ線の定義
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true,false);

        // 太さ
        renderer.setSeriesStroke(0, 5f);
        // 色
        renderer.setSeriesPaintType(0, new SolidColor(Color.rgb(255, 166, 0)));

        // 最大描写領域を設定
        setMaximumDrawWidth(2000);
        setMaximumDrawHeight(2000);

        // データのセット
        XYSeries xy = new XYSeries("データ");
        int i = 0;
        for (int value : wellnessArray) {
            xy.add(i + 1, value);
            i++;
        }
        XYSeriesCollection dataset = new XYSeriesCollection(xy);

        XYPlot plot = new XYPlot(dataset, domainAxis, rangeAxis, renderer);

        // y軸のレンジ
        ValueAxis yAxis = plot.getRangeAxis();
        yAxis.setRange(Ymin, Ymax);

        // X軸のレンジ
        ValueAxis xAxis = plot.getDomainAxis();
        if (wellnessArray.size() > 15) {
            double chartMin = -10.0d + wellnessArray.size();
            double chartMax = 10.0d + wellnessArray.size();
            xAxis.setRange(chartMin, chartMax);
        } else {
            xAxis.setRange(0.0d, 20.0d);
        }


        aFreeChart = new AFreeChart(plot);
        aFreeChart.removeLegend();
        setChart(aFreeChart);
    }

    /**
     * グラフ生成 まばたき
     */
    public void createChart2(ArrayList<Integer> wellnessArray, String title, double Ymin, double Ymax) {
        // X軸の定義
        NumberAxis domainAxis = new NumberAxis("時間");

        // Y軸の定義
        NumberAxis rangeAxis = new NumberAxis(title);

        // XY軸のタイトルのフォント設定
        // 種類、大きさ
        Font xyTitleFont = new Font(Typeface.SANS_SERIF, Typeface.BOLD, 30);
        domainAxis.setLabelFont(xyTitleFont);
        rangeAxis.setLabelFont(xyTitleFont);

        // XY軸の目盛のフォントの設定
        // フォントの種類、大きさ
        Font tickFont = new Font(Typeface.MONOSPACE, Typeface.NORMAL, 20);
        domainAxis.setTickLabelFont(tickFont);
        rangeAxis.setTickLabelFont(tickFont);

        // 折れ線の定義
//        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true,false);
        XYBarRenderer renderer = new XYBarRenderer();

        // 太さ
        renderer.setSeriesStroke(0, 5f);
        // 色
        renderer.setSeriesPaintType(0, new SolidColor(Color.rgb(255, 166, 0)));

        // 最大描写領域を設定
        setMaximumDrawWidth(2000);
        setMaximumDrawHeight(2000);

        // データのセット
        XYSeries xy = new XYSeries("データ");
        int i = 0;
        for (int value : wellnessArray) {
            xy.add(i + 1, value);
            i++;
        }
        XYSeriesCollection dataset = new XYSeriesCollection(xy);

        XYPlot plot = new XYPlot(dataset, domainAxis, rangeAxis, renderer);

        // y軸のレンジ
        ValueAxis yAxis = plot.getRangeAxis();
        yAxis.setRange(Ymin, Ymax);
        TickUnits ty = new TickUnits();
        TickUnit unitY = new NumberTickUnit(1);
        ty.add(unitY);
        yAxis.setStandardTickUnits(ty);

        // X軸のレンジ
        ValueAxis xAxis = plot.getDomainAxis();
        if (wellnessArray.size() > 15) {
            double chartMin = -10.0d + wellnessArray.size();
            double chartMax = 10.0d + wellnessArray.size();
            xAxis.setRange(chartMin, chartMax);
        } else {
            xAxis.setRange(0.0d, 20.0d);
        }


        aFreeChart = new AFreeChart(plot);
        aFreeChart.removeLegend();
        setChart(aFreeChart);
    }

    /**
     * うなずきのグラフ
     */
    public void createChart3(ArrayList<Integer> wellnessArray, String title, double Ymin, double Ymax) {
        // X軸の定義
        NumberAxis domainAxis = new NumberAxis("時間");

        // Y軸の定義
        NumberAxis rangeAxis = new NumberAxis(title);

        // XY軸のタイトルのフォント設定
        // 種類、大きさ
        Font xyTitleFont = new Font(Typeface.SANS_SERIF, Typeface.BOLD, 30);
        domainAxis.setLabelFont(xyTitleFont);
        rangeAxis.setLabelFont(xyTitleFont);

        // XY軸の目盛のフォントの設定
        // フォントの種類、大きさ
        Font tickFont = new Font(Typeface.MONOSPACE, Typeface.NORMAL, 20);
        domainAxis.setTickLabelFont(tickFont);
        rangeAxis.setTickLabelFont(tickFont);

        // 折れ線の定義
//        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true,false);
        XYBarRenderer renderer = new XYBarRenderer();

        // 太さ
        renderer.setSeriesStroke(0, 5f);
        // 色
        renderer.setSeriesPaintType(0, new SolidColor(Color.rgb(255, 166, 0)));

        // 最大描写領域を設定
        setMaximumDrawWidth(2000);
        setMaximumDrawHeight(2000);

        // データのセット
        XYSeries xy = new XYSeries("データ");
        int i = 0;
        for (int value : wellnessArray) {
            xy.add(i + 1, value);
            i++;
        }
        XYSeriesCollection dataset = new XYSeriesCollection(xy);

        XYPlot plot = new XYPlot(dataset, domainAxis, rangeAxis, renderer);

        // y軸のレンジ
        ValueAxis yAxis = plot.getRangeAxis();
        yAxis.setRange(Ymin, Ymax);
        TickUnits ty = new TickUnits();
        TickUnit unitY = new NumberTickUnit(1);
        ty.add(unitY);
        yAxis.setStandardTickUnits(ty);

        // X軸のレンジ
        ValueAxis xAxis = plot.getDomainAxis();
        if (wellnessArray.size() > 15) {
            double chartMin = -10.0d + wellnessArray.size();
            double chartMax = 10.0d + wellnessArray.size();
            xAxis.setRange(chartMin, chartMax);
        } else {
            xAxis.setRange(0.0d, 20.0d);
        }


        aFreeChart = new AFreeChart(plot);
        aFreeChart.removeLegend();
        setChart(aFreeChart);
    }
}
