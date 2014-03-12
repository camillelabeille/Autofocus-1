/**
 * Classes used to display view of the current calibration
 */
package ihm;

import java.util.logging.Logger;

import data.IVector;
import ellipsoide.Sphere;

/**
 * Class to build the Accelerometers view
 * 
 * @author Guillaume
 * 
 */
public class DrawAccel extends Draw {

	private static Logger logger = Logger.getLogger(DrawAccel.class.getName());

	/**
	 * 
	 */
	private static final long serialVersionUID = -1632196092075420985L;

	/**
	 * Create the sphere and the layout of the accelerometer's view s the sphere
	 * to display
	 */

	public DrawAccel(Sphere p_sphere, int thresholdOK) {
		super(p_sphere, "Accélérométrie", thresholdOK);

		// Affichage du texte
		getInstructions()
				.setText("Maintenir le drone dans une position stable");
		getProgressBar().setVisible(true);

	}

	/**
	 * Updates the sphere
	 * 
	 * @param radius
	 * @param newcenter
	 * @param vcourant
	 * @param nbCorrectOK
	 */
	public void update(final double radius, final IVector<Double> newcenter,
			final IVector<Double> vcourant, final int nbCorrectOK) {
		super.update(radius, newcenter, vcourant);

		// Mise à jour de la barre de progression
		setValueProgressBar(nbCorrectOK);

		// Affichage du libellé
		if (nbCorrectOK < (super.thresholdOK / 10)) {
			getInstructions().setText(
					"Changer la position du drone et la maintenir");

		} else
			getInstructions().setText("Maintenir la position");
	}

	/**
	 * Give some informations and put the initial value on the progress bar to
	 * indicate a new position for the drone
	 */
	public void changedStates(boolean tooManyWrongVectors) {
		setValueProgressBar(0);
		getInstructions()
				.setText(
						"Trop de données incorrectes reçues. Merci de maintenir la position.");
	}
}