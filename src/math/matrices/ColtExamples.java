package math.matrices;

import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import cern.colt.matrix.linalg.EigenvalueDecomposition;
import cern.jet.math.Functions;

/**
 * @author C. Savkli, Feb 26, 2014
 * @version 1.0
 */

public class ColtExamples
{
	private Algebra alg = new Algebra();		
	private Functions F = Functions.functions;

	public ColtExamples()
	{	
		this.testMultiply();
//		this.testSumRowAndColumn();
//		this.testOperateOnElements();
//		this.testOperateOnElementsUsingIteration();
//		this.testOperateOnElementsUsingForEach();
//		this.testOperateOnAllElementsUsingFunctions();
//		this.testAggragateUsingFunctions();
//		this.testMatrixSimilarity();
//		this.testEigendecomposition();
	}
	
	private void testMultiply()
	{
		DoubleMatrix2D a2 = new SparseDoubleMatrix2D(2,2);
        a2.set(0, 0, 1);
        a2.set(0, 1, 2);
        a2.set(1, 0, 3);
        a2.set(1, 1, 1);
        
        DoubleMatrix1D v = new DenseDoubleMatrix1D(2);
        v.set(0, 2);
        v.set(1, 1);
        
        System.out.println(a2+"\n");
        System.out.println(v+"\n");
        v = alg.mult(a2, v);
        System.out.println(v);
	}
	
	public void testSumRowAndColumn()
	{
		DoubleMatrix2D a2 = new SparseDoubleMatrix2D(2,2);
		a2.set(0, 0, 1);
		a2.set(0, 1, 2);
		a2.set(1, 1, 7);
		a2.set(1, 0, 3);
		System.out.println(a2+"\n");
		
		DoubleMatrix1D row = a2.viewRow(1);
		
		System.out.println("row: "+row+"\n");
		
		double sum1 = alg.norm1(row);
		
		double sum2 = row.aggregate(F.plus, F.pow(2));
		
		System.out.println(" row sum: "+sum1 + " " +  sum2);
		
		DoubleMatrix1D col = a2.viewColumn(1);
		
        System.out.println("col: "+col+"\n");

		double sum3 = alg.norm1(col);
		
		double sum4 = col.aggregate(F.plus, F.identity);

		System.out.println(sum3 + " "+sum4);
	}
	
	// Really bad implementation
	private void testOperateOnElements()
	{
		DoubleMatrix2D a2 = new SparseDoubleMatrix2D(2,2);
        a2.set(0, 0, 1);
        a2.set(0, 1, 2);
        a2.set(1, 1, 4);
        a2.set(1, 0, 3);
        System.out.println(a2+"\n");
       
        for(int i=0; i<2; i++)
        {
            for(int j=0; j<2; j++)
            {
            	double val = a2.get(i, j);
            	a2.set(i, j, val*val);
            }
        }
        
        System.out.println(a2);
	}
	
	// Much better implementation
	private void testOperateOnElementsUsingIteration()
	{
		DoubleMatrix2D a2 = new SparseDoubleMatrix2D(2,2);
        a2.set(0, 0, 1);
        a2.set(0, 1, 2);
        a2.set(1, 1, 4);
        a2.set(1, 0, 3);
        
        System.out.println(a2+"\n");
        
        IntArrayList rowList = new IntArrayList();
        IntArrayList columnList = new IntArrayList();
        DoubleArrayList valueList = new DoubleArrayList();
        a2.getNonZeros(rowList, columnList, valueList);
        
        for (int i=0;i<rowList.size();i++)
        {
        	int row = rowList.get(i);
        	int col = columnList.get(i);
        	double val = a2.get(row, col);
            a2.set(row, col, val*val);
        }

        System.out.println(a2);
	}
	
	// Best implementation
	private void testOperateOnElementsUsingForEach()
	{
		DoubleMatrix2D a2 = new SparseDoubleMatrix2D(2,2);
        a2.set(0, 0, 1);
        a2.set(0, 1, 2);
        a2.set(1, 1, 4);
        a2.set(1, 0, 3);
        
        System.out.println(a2+"\n");
        
        a2.forEachNonZero( new cern.colt.function.IntIntDoubleFunction()
        {
        	public double apply(int row, int column, double value){

        		return value*value;
        	}
        }
        );

        System.out.println(a2);
	}
	
	private void testOperateOnAllElementsUsingFunctions()
	{
		DoubleMatrix2D a2 = new SparseDoubleMatrix2D(2,2);
        a2.set(0, 0, 1);
        a2.set(0, 1, 2);
        a2.set(1, 0, 3);
        a2.set(1, 1, 1);
        
        a2.assign(F.mult(2.0));
        System.out.println(a2);
        a2.assign(F.chain(F.square,F.div(2.0)));
        System.out.println(a2);  
	}
	
	private void testAggragateUsingFunctions()
	{
		DoubleMatrix2D a2 = new SparseDoubleMatrix2D(2,2);
        a2.set(0, 0, 1);
        a2.set(0, 1, 2);
        a2.set(1, 0, 3);
        a2.set(1, 1, 1);
        
        double sumSquared = a2.aggregate(F.plus, F.square);

        double expSquaredSum = a2.aggregate(F.plus, F.chain(F.exp,F.chain(F.mult(-1),F.square)));
        System.out.println(sumSquared + " "+ expSquaredSum);
	}
	
	private void testMatrixSimilarity()
	{
		DoubleMatrix2D a1 = new SparseDoubleMatrix2D(2,2);
		a1.set(0, 0, 1);
		a1.set(0, 1, 1);
		a1.set(1, 1, 1);
		a1.set(1, 0, 1);
		
		DoubleMatrix2D a2 = new SparseDoubleMatrix2D(2,2);
		a2.set(0, 0, 1);
		a2.set(0, 1, 2);
		a2.set(1, 1, 0);
		a2.set(1, 0, 1);
		
		System.out.println(a1+"\n");
		System.out.println(a2+"\n");
		
		double result = a1.aggregate(a2, F.plus, F.chain(F.abs,F.minus));
		
		double norm1 = a1.aggregate(F.plus,F.identity);
		double norm2 = a2.aggregate(F.plus,F.identity);		
		
		System.out.println("Similarity: "+ (1 - result/Math.max(norm1, norm2)));
	}
	
	private void testEigendecomposition()
	{
		DoubleMatrix2D a2 = new SparseDoubleMatrix2D(2,2);
        a2.set(0, 0, 0);
        a2.set(0, 1, 1);
        a2.set(1, 0, 1);
        a2.set(1, 1, 0);
        
        System.out.println(a2+"\n");
        
        EigenvalueDecomposition eigenvalueDecomposition = new EigenvalueDecomposition(a2);
        double[] eigenvalues = eigenvalueDecomposition.getRealEigenvalues().toArray();
		DoubleMatrix2D eigenvectors = eigenvalueDecomposition.getV();

		for(int j=0; j<2; j++)
		{
			DoubleMatrix1D eigenvector = eigenvectors.viewColumn(j);
			System.out.println("eigenvalue : "+eigenvalues[j]);
			System.out.println("eigenvector : " + eigenvector+"\n");
		}
	}

	public static void main(String[] args)
	{
        new ColtExamples();
	}
}
