package math.statistics;

import java.util.Arrays;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction.Parametric;
import org.apache.commons.math3.fitting.CurveFitter;
import org.apache.commons.math3.optim.nonlinear.vector.jacobian.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.stat.regression.SimpleRegression;

/**
 * @author C. Savkli, Jan 20, 2015
 * @version 1.0
 */

public class LeastSquaresFit
{
	public LeastSquaresFit()
	{
		// Linear
		double[][] dataSetLinear={{0, 1},{1, 2},{2, 3},{3, 4}};
		double[] fLin = linearFit(dataSetLinear);
		PolynomialFunction fL = new PolynomialFunction(fLin);
        System.out.println("Linear : " + fL);
		
		//Polynomial
		double[][] dataSet1={{0, 5},{1, 3},{2, 3},{3, 5},{4, 9}};
		double[] fPoly = polynomialFit(dataSet1, 3);
		PolynomialFunction fP = new PolynomialFunction(fPoly);
        System.out.println("Polynomial : " + fP);
		
		//Exponential
		double[][] dataSetExponential={{0, 1},{1, 0.5},{2, 0.25},{3, 0.125}};
		double[] fExp = exponentialFit(dataSetExponential);
        System.out.println("Exponential : " + Arrays.toString(fExp));
	}
	
	public static double[] polynomialFit(double[][] data, double[] initCoefficients)
	{
		CurveFitter<Parametric> fitter = new CurveFitter<Parametric>(new LevenbergMarquardtOptimizer());

		for(int i=0; i< data.length; i++)
		{
			fitter.addObservedPoint(data[i][0],data[i][1]);
		}
		
		//Compute optimal coefficients.
		double[] best = fitter.fit(new PolynomialFunction.Parametric(), initCoefficients);

		return best;
	}
	
	public static double[] polynomialFit(double[][] data, int degree)
	{
		//the initial guess for the coefficients of the polynomial.
		double[] init = new double[degree];// 12.9 - 3.4 x + 2.1 x^2
		for(int i=0; i<degree; i++)
		{
			init[i]=1.0;
		}
		
		//Compute optimal coefficients.
		double[] best = polynomialFit(data, init);

		return best;
	}
	
	//
	// y = a * e^( b*x )  ,  log(y) = log(a) + b*x,    returns {a,b}
	//
	public static double[] exponentialFit(double[][] data)
	{
		double[][] eData = data.clone();
		
		for(int i=0; i< eData.length; i++)
		{
			double[] d = eData[i];
			d[1] = Math.log(d[1]);
		}
		
		double[] fit = linearFit(eData);
		fit[0] = Math.exp(fit[0]);
		
		return fit;
	}
	
	public static double[] linearFit(double[][] data)
	{
		SimpleRegression regression = new SimpleRegression();
		regression.addData(data);
		System.out.println(regression.getSlopeStdErr() + " " + regression.getSignificance());
		double[] fit = {regression.getIntercept(), regression.getSlope()};
		return fit;
	}
		
	public static void main(String[] args)
	{
		new LeastSquaresFit();
	}
}
