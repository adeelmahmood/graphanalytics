package plotting;

import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.plot.DataSetPlot;
import com.panayotis.gnuplot.plot.FunctionPlot;
import com.panayotis.gnuplot.plot.Plot;
import com.panayotis.gnuplot.style.NamedPlotColor;
import com.panayotis.gnuplot.style.PlotStyle;
import com.panayotis.gnuplot.style.Style;
import com.panayotis.gnuplot.plot.AbstractPlot;
import com.panayotis.gnuplot.terminal.FileTerminal;

/**
 * @author C. Savkli, Jan 20, 2015
 * @version 1.0
 */

public class JavaPlotTest
{
	public JavaPlotTest()
	{
		plot();
		//multiPlot();
		//plot3d();
		//plotHeatMap();
		//savePlot();
	}
	
	public void plot()
	{
		double[][] dataSet={{-1.00, 2.021},{-0.99, 2.22113},{-0.98, 2.0998},{-0.97, 2.02111},{0.99, -2.434}};
		String plot = "0.37 - 2.28 * x - 0.55 * x**2";

		JavaPlot p = new JavaPlot();
		//JavaPlot p = new JavaPlot("C:/Program Files/gnuplot/binary/pgnuplot.exe");
		        
        // Add a few plots
		Plot cos =  new FunctionPlot("cos(x)");
		Plot dat = new DataSetPlot(dataSet);
        p.addPlot(plot);
        p.addPlot("sin(x)");
        p.addPlot(cos);
        p.addPlot(dat);
        p.setTitle("Random");
        p.getAxis("x").setBoundaries(-2, 2);
        p.set("xlabel","'time'");
        p.set("ylabel","'stock'");
        p.set("key", "left bottom");
        
        // Adjust plot style for one of the curves
        PlotStyle stl = ((AbstractPlot) p.getPlots().get(1)).getPlotStyle();
        stl.setStyle(Style.POINTS);
        stl.setLineType(NamedPlotColor.RED);
        stl.setPointType(2);
        stl.setPointSize(1);
        p.plot();             
	}
    
	public void multiPlot()
	{
		JavaPlot p = new JavaPlot();
        Plot cos =  new FunctionPlot("cos(x)");        
        p.addPlot("sin(x)");
        p.newGraph();
        p.addPlot(cos);
        p.setTitle("Random");
        p.getAxis("x").setBoundaries(-2, 2);
        p.set("xlabel","'time'");
        p.set("ylabel","'stock'");
        p.set("key", "left bottom");
        p.plot();             
	}
	
	public void plot3d()
	{
		JavaPlot p3 = new JavaPlot(true);
		
		Plot ycos =  new FunctionPlot("y*cos(x)");
        p3.addPlot(ycos);
        p3.set("grid", "");
        p3.set("isosamples", "100");
        p3.set("palette", "rgbformulae 22,13,-31");
        p3.set("xrange", "[-10:10]");
        p3.set("yrange", "[-2:2]");
        p3.set("key", "left top");
        
        p3.plot();
	}
	
	public void plotHeatMap()
	{
		JavaPlot p3 = new JavaPlot(true);
		
		Plot ycos =  new FunctionPlot("y*cos(x)");
        p3.addPlot(ycos);
        p3.set("pm3d", "map");  // Make it a heat map
        p3.set("grid", "");
        p3.set("isosamples", "100");
        p3.set("palette", "rgbformulae 22,13,-31");
        p3.set("xrange", "[-10:10]");
        p3.set("yrange", "[-2:2]");
        p3.set("key", "left top");
        
        p3.plot();
	}
	
	private static void savePlot() 
	{
        JavaPlot p = new JavaPlot();
        p.addPlot("sin(x)");
        p.addPlot("cos(x)");
        FileTerminal png = new FileTerminal("pngcairo", "C://workspace//Blueprints//demo.png");
        
        p.setTerminal(png);
        p.plot();
    }	
	
	public static void main(String[] args)
	{
		new JavaPlotTest();
	}
}
