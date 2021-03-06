package data;

public interface IVector<E> {

	/** getter for coordinates */
	public double getX();

	/** getter for coordinates */
	public double getY();

	/** getter for coordinates */
	public double getZ();

	/** transforms the vector in an array and return the size of the array */
	public int getSize();

	/** return the boolean stating if the object is valid or not */
	public boolean isCorrect();

	/** set the boolean value of correctness */
	public void setCorrectness(boolean correctness);

	/** return the value of the object with its correctness */
	public String toString();

}
