package obsolete;

import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;
import org.ejml.ops.SpecializedOps;

/**
 * <p>
 * This is a straight forward implementation of the Levenberg-Marquardt (LM) algorithm. LM is used to minimize
 * non-linear cost functions:<br>
 * <br>
 * S(P) = Sum{ i=1:m , [y<sub>i</sub> - f(x<sub>i</sub>,P)]<sup>2</sup>}<br>
 * <br>
 * where P is the set of parameters being optimized.
 * </p>
 *
 * <p>
 * In each iteration the parameters are updated using the following equations:<br>
 * <br>
 * P<sub>i+1</sub> = (H + &lambda; I)<sup>-1</sup> d <br>
 * d =  (1/N) Sum{ i=1..N , (f(x<sub>i</sub>;P<sub>i</sub>) - y<sub>i</sub>) * jacobian(:,i) } <br>
 * H =  (1/N) Sum{ i=1..N , jacobian(:,i) * jacobian(:,i)<sup>T</sup> }
 * </p>
 * <p>
 * Whenever possible the allocation of new memory is avoided.  This is accomplished by reshaping matrices.
 * A matrix that is reshaped won't grow unless the new shape requires more memory than it has available.
 * </p>
 * @author Peter Abeles
 */
public class LevenbergMarquardt {
    // how much the numerical jacobian calculation perturbs the parameters by.
    // In better implementation there are better ways to compute this delta.  See Numerical Recipes.
    private final static double DELTA = 1e-8;

    private double initialLambda;

    // the function that is optimized
    private Function func;

    // the optimized parameters and associated costs
    private DenseMatrix64F param;
    private double initialCost;
    private double finalCost;

    // used by matrix operations
    private DenseMatrix64F d;
    private DenseMatrix64F H;
    private DenseMatrix64F negDelta;
    private DenseMatrix64F tempParam;
    private DenseMatrix64F A;

    // variables used by the numerical jacobian algorithm
    private DenseMatrix64F temp0;
    private DenseMatrix64F temp1;
    // used when computing d and H variables
    private DenseMatrix64F tempDH;

    // Where the numerical Jacobian is stored.
    private DenseMatrix64F jacobian;

    /**
     * Creates a new instance that uses the provided cost function.
     *
     * @param funcCost Cost function that is being optimized.
     */
    public LevenbergMarquardt( Function funcCost )
    {
        this.initialLambda = 1;

        // declare data to some initial small size. It will grow later on as needed.
        int maxElements = 1;
        int numParam = 1;

        this.temp0 = new DenseMatrix64F(maxElements,1);
        this.temp1 = new DenseMatrix64F(maxElements,1);
        this.tempDH = new DenseMatrix64F(maxElements,1);
        this.jacobian = new DenseMatrix64F(numParam,maxElements);

        this.func = funcCost;

        this.param = new DenseMatrix64F(numParam,1);
        this.d = new DenseMatrix64F(numParam,1);
        this.H = new DenseMatrix64F(numParam,numParam);
        this.negDelta = new DenseMatrix64F(numParam,1);
        this.tempParam = new DenseMatrix64F(numParam,1);
        this.A = new DenseMatrix64F(numParam,numParam);
    }


    public double getInitialCost() {
        return initialCost;
    }

    public double getFinalCost() {
        return finalCost;
    }

    public DenseMatrix64F getParameters() {
        return param;
    }

    /**
     * Finds the best fit parameters.
     *
     * @param initParam The initial set of parameters for the function.
     * @param X The inputs to the function.
     * @param Y The "observed" output of the function
     * @return true if it succeeded and false if it did not.
     */
    public boolean optimize( DenseMatrix64F initParam ,
                             DenseMatrix64F X ,
                             DenseMatrix64F Y )
    {
       // X = scalepoints(X,initParam);
    	
    	
    	
    	initParam.set(initialPar(X));
    	
    	  	
    	
    	configure(initParam,X,Y);
    	
    	System.out.println(X.getNumCols());

        // save the cost of the initial parameters so that it knows if it improves or not
        initialCost = cost(param,X,Y);
        
        System.out.print(true);
        
        // iterate until the difference between the costs is insignificant
        // or it iterates too many times
        if( !adjustParam(X, Y, initialCost) ) {
            finalCost = Double.NaN;
            return false;
        }

        return true;
    }

    /**
     * Iterate until the difference between the costs is insignificant
     * or it iterates too many times
     */
    private boolean adjustParam(DenseMatrix64F X, DenseMatrix64F Y,
                                double prevCost) {
        // lambda adjusts how big of a step it takes
        double lambda = initialLambda;
        // the difference between the current and previous cost
        double difference = 1000;
        
       
        for( int iter = 0; iter < 20 || difference < 1e-6 ; iter++ ) {
            // compute some variables based on the gradient
            
        	
        	computeDandH(param,X,Y);

            // try various step sizes and see if any of them improve the
            // results over what has already been done
            boolean foundBetter = false;
            for( int i = 0; i < 5; i++ ) {
                computeA(A,H,lambda);
                
                System.out.println("hein");
                
                if(CommonOps.solve(A,d,negDelta) ) {
                	System.out.println("quoi");
                    return false;
                }
                // compute the candidate parameters
                CommonOps.sub(param,negDelta,tempParam);
                
                
                
                for(i=0;i<tempParam.getNumRows();i++){
                System.out.println(tempParam.get(i)+"hallo");
                }
                
                
                double cost = cost(tempParam,X,Y);
                System.out.println(cost+""+ prevCost);
                if( cost < prevCost ) {
                    // the candidate parameters produced better results so use it
                    foundBetter = true;
                    param.set(tempParam);
                    difference = prevCost - cost;
                    prevCost = cost;
                    lambda /= 10.0;
                } else {
                    lambda *= 10.0;
                }
            }

            // it reached a point where it can't improve so exit
            if( !foundBetter )
                break;
        }
        finalCost = prevCost;
        return true;
    }

    /**
     * Performs sanity checks on the input data and reshapes internal matrices.  By reshaping
     * a matrix it will only declare new memory when needed.
     */
    protected void configure( DenseMatrix64F initParam , DenseMatrix64F Xthree , DenseMatrix64F Y )
    {
    	
    	DenseMatrix64F X = new DenseMatrix64F(Xthree.getNumRows(),0);
    	
    	//X = scalepoints(Xthree,initParam);
    	
        if( Y.getNumRows() != X.getNumRows() ) {
            throw new IllegalArgumentException("Different vector lengths");
        } else if( Y.getNumCols() != 1 /*|| X.getNumCols() != 1 */) {
            throw new IllegalArgumentException("Inputs must be a column vector");
        }
        
               
        int numParam = initParam.getNumElements();
        int numPoints = Y.getNumRows();

        if( param.getNumElements() != initParam.getNumElements() ) {
            // reshaping a matrix means that new memory is only declared when needed
            this.param.reshape(numParam,1, false);
            this.d.reshape(numParam,1, false);
            this.H.reshape(numParam,numParam, false);
            this.negDelta.reshape(numParam,1, false);
            this.tempParam.reshape(numParam,1, false);
            this.A.reshape(numParam,numParam, false);
        }
        
       
        this.param.set(initParam);
        

        // reshaping a matrix means that new memory is only declared when needed
        temp0.reshape(numPoints,1, false);
        temp1.reshape(numPoints,1, false);
        tempDH.reshape(numPoints,1, false);
        jacobian.reshape(numParam,numPoints, false);


    }

    /**
     * Computes the d and H parameters.  Where d is the average error gradient and
     * H is an approximation of the hessian.
     */
    private void computeDandH( DenseMatrix64F param , DenseMatrix64F x , DenseMatrix64F y )
    {
    	
    	
        func.compute(param,x, tempDH);
        
        CommonOps.subEquals(tempDH,y);

        for (int o = 0; o<x.getNumRows();o++){
        	System.out.println(x.get(o)+" " + tempDH.get(o));
        }
        
        
        
        
        
        computeNumericalJacobian(param,x,jacobian);

        
        
       // x = scalepoints(x,param);
        
        int numParam = param.getNumElements();
        //int length = x.getNumElements();
        int length = x.getNumRows();
        
        // d = average{ (f(x_i;p) - y_i) * jacobian(:,i) }
        for( int i = 0; i < numParam; i++ ) {
            double total = 0;
            for( int j = 0; j < length; j++ ) {
                total += tempDH.get(j,0)*jacobian.get(i,j);
            }
            d.set(i,0,total/length);
        }

        // compute the approximation of the hessian
        CommonOps.multTransB(jacobian,jacobian,H);
        CommonOps.scale(1.0/length,H);
    }

    /**
     * A = H + lambda*I <br>
     * <br>
     * where I is an identity matrix.
     */
    private void computeA( DenseMatrix64F A , DenseMatrix64F H , double lambda )
    {
        final int numParam = param.getNumElements();

        A.set(H);
        for( int i = 0; i < numParam; i++ ) {
            A.set(i,i, A.get(i,i) + lambda);
        }
    }

    /**
     * Computes the "cost" for the parameters given.
     *
     * cost = (1/N) Sum (f(x;p) - y)^2
     */
    private double cost( DenseMatrix64F param , DenseMatrix64F X , DenseMatrix64F Y)
    {
    	
    	//X = scalepoints(X,param);
    	
    	
        func.compute(param,X, temp0);
        
            
        double error = SpecializedOps.diffNormF(temp0,Y);

        return error*error / (double)X.numRows;
    }

    /**
     * Computes a simple numerical Jacobian.
     *
     * @param param The set of parameters that the Jacobian is to be computed at.
     * @param pt The point around which the Jacobian is to be computed.
     * @param deriv Where the jacobian will be stored
     */
    protected void computeNumericalJacobian( DenseMatrix64F param ,
                                             DenseMatrix64F pt ,
                                             DenseMatrix64F deriv )
    {
        double invDelta = 1.0/DELTA;

        func.compute(param,pt, temp0);

        // compute the jacobian by perturbing the parameters slightly
        // then seeing how it effects the results.
        for( int i = 0; i < param.numRows; i++ ) {
            param.data[i] += DELTA;
            func.compute(param,pt, temp1);
            // compute the difference between the two parameters and divide by the delta
            CommonOps.add(invDelta,temp1,-invDelta,temp0,temp1);
            // copy the results into the jacobian matrix
            System.arraycopy(temp1.data,0,deriv.data,i*pt.numRows,pt.numRows);

            param.data[i] -= DELTA;
        }
    }
    
    private DenseMatrix64F scalepoints(DenseMatrix64F points, DenseMatrix64F parameter){
    	
    	DenseMatrix64F input = new DenseMatrix64F(points.getNumRows(),1);
    	
    	if (parameter.getNumRows()==6){
			
			for (int i = 0; i< points.getNumRows();i++){

			double smx = (points.get(i,0) - parameter.get(0))*parameter.get(3);
			double smy = (points.get(i,1) - parameter.get(1))*parameter.get(4);
			double smz = (points.get(i,2) - parameter.get(2))*parameter.get(5);
			
			
			input.set(i, 0, Math.sqrt(smx*smx + smy*smy + smz*smz));
						
			}
		}
		else {System.out.println("param error");}
		double sr = input.getNumRows();
		System.out.println("compu ok"+ sr);
		return input;
    }
    
	public DenseMatrix64F initialPar(DenseMatrix64F points){
		
		double sf = 1;
		
		
		
		DenseMatrix64F x = new DenseMatrix64F(points.getNumRows(),1);
		DenseMatrix64F y = new DenseMatrix64F(points.getNumRows(),1);
		DenseMatrix64F z = new DenseMatrix64F(points.getNumRows(),1);
		
		x = CommonOps.extract(points,0,points.getNumRows(),0,1);
		y = CommonOps.extract(points,0,points.getNumRows(),1,2);
		z = CommonOps.extract(points,0,points.getNumRows(),2,3);
		
	
		
		DenseMatrix64F out = new DenseMatrix64F(6,1);
				
		out.set(0,0,(CommonOps.elementMax(x)+CommonOps.elementMin(x))/2);
		out.set(1,0,(CommonOps.elementMax(y)+CommonOps.elementMin(y))/2);
		out.set(2,0,(CommonOps.elementMax(z)+CommonOps.elementMin(z))/2);
		
		out.set(3,0,2*sf/(CommonOps.elementMax(x)-CommonOps.elementMin(x)));
		out.set(4,0,2*sf/(CommonOps.elementMax(y)-CommonOps.elementMin(y)));
		out.set(5,0,2*sf/(CommonOps.elementMax(z)-CommonOps.elementMin(z)));
		
		
		
		out.set(0,0,2);
		out.set(1,0,2);
		out.set(2,0,2);
		out.set(3,0,0.5);
		out.set(4,0,0.55);
		out.set(5,0,0.5);

		return out;
	}


    /**
     * The function that is being optimized.
     */
    public interface Function {
        /**
         * Computes the output for each value in matrix x given the set of parameters.
         *
         * @param param The parameter for the function.
         * @param x the input points.
         * @param y the resulting output.
         */
        public void compute( DenseMatrix64F param , DenseMatrix64F x , DenseMatrix64F y );
    }
}