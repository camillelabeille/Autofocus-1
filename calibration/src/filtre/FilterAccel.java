/**Package grouping all classes used to filter data*/
package filtre;

import ihm.DrawAccel;

import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import data.IVector;

public class FilterAccel extends Filter {

	private static Logger logger = Logger
			.getLogger(FilterAccel.class.getName());

	/** The accelerator of the sphere */
	private DrawAccel drawAccel;
	/** The number of correct vector added */

	private int thresholdOK;
	/**
	 * The threshold that trigger the reinitialization of the progress bar
	 */
	private int thresholdWrong;

	/**
	 * Creates a filter who plots the vector in a two dimensional window (simple
	 * orthogonal projection on xy)
	 * 
	 * @param windowSize
	 * @param type
	 */
	public FilterAccel(int thresholdOK, int thresholdWrong, DrawAccel drawAccel) {
		this.thresholdOK = thresholdOK; // 200
		this.thresholdWrong = thresholdWrong; // 40
		nbCorrectVect = 0;
		nbWrongVect = 0;
		this.drawAccel = drawAccel;
	}

	/**
	 * Add the vector given in argument to the filter and update the sphere with
	 * new radius and center
	 * 
	 * @param v
	 *            vector to add
	 */
	@Override
	public void add(final IVector<Double> v) {
		super.add(v);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				drawAccel.update(rayon, center, v, nbCorrectVect);
			}
		});
		if ((nbWrongVect > thresholdWrong) || (nbCorrectVect > thresholdOK)) {
			drawAccel.changedStates((nbWrongVect > thresholdWrong));
			nbWrongVect = 0;
			nbCorrectVect = 0;
		}
	}

}
