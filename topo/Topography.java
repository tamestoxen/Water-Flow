package topo;

import cell.Cell;
import cell.Farm;
import cell.Point3D;
import java.util.Random;

/**
 * Topography is a class that is only used to generate the shape of a piece of land.
 * @author Max Ottesen
 */
public class Topography {
	private static final double     MAX_RELIEF = 0.05; //meters. The most the topography over the entire grid is allowed to vary
	private static final double     TOLERANCE  = 0.01; //meters. Changes larger this amount will not be accepted
	private static final int        SIZE       = Farm.SIZE; //meters. length and width
	private static final double[][] HEIGHTS    = {{100, 300}, {100, 300}, {100, 400}}; //centimeters. {height of each layer, height of all layers with the same height}
	private static final Random     rand       = new Random();


	/**
	 * Takes a latitude and longitude that correspond to a piece of land and shapes a {@link Farm}
	 * so that its topography mimics that piece of land. Right now, it is set up to generate land
	 * randomly, though.
	 * @param latitude  the latitude of the piece of land that the returned Farm will mimic
	 * @param longitude the latitude of the piece of land that the returned Farm will mimic
	 * @return a Farm that has been given topographic shape
	 */
	public static Farm createFarm(double latitude, double longitude) {
		double[][] deviation; //meters

		//If the given lat/lon don't correspond to a real life location, use randomly generated topography
		if(longitude < -180 || longitude > 180 || latitude < -90 || latitude > 90) {
			//Generates a 2D array of doubles to correspond to heights of a specific i,j column. This is essentially the shape
			// of the land that the program will run on. It's a random, but smooth, topography.
			deviation = getDeviations();
		}
		//Otherwise, get the elevation data from the internet
		else {
			//Queries a google database to get elevation data
			ElevationData ed = new ElevationData(longitude, latitude);
			deviation = ed.getElevations();
		}
		
    double[] minmax = adjustForMinMax(deviation);

		int baseLayers = (int)(HEIGHTS[0][1]/HEIGHTS[0][0]) + (int)(HEIGHTS[1][1]/HEIGHTS[1][0]) + (int)(HEIGHTS[2][1]/HEIGHTS[2][0]);

		Cell[][][] grid = new Cell[SIZE][SIZE][baseLayers+1+(int)(minmax[1]*100)];


		//Sets the top to air (null)
		for(int j = 0; j < SIZE; j++) {
			for(int i = 0; i < SIZE; i++) {
				grid[i][j][baseLayers+(int)(minmax[1]*100)] = null;
			}
		}


		//Goes cell by cell and sets the height, depth, and coordinates of each cell.
		//If you have a surface or air cell, set it to be one.
		for(int k = 0; k < baseLayers+(int)(minmax[1]*100) ; k++) {
			for(int j = 0; j < SIZE; j++) {
				for(int i = 0; i < SIZE; i++) {
					if(k >= 0 && k < HEIGHTS[2][1]/HEIGHTS[2][0]) {
						grid[i][j][k] = new Cell(HEIGHTS[2][0], getDepth(i, j, k, deviation), new Point3D(i, j, k));
						grid[i][j][k].setSurface(false);
					}
					else if(k >= HEIGHTS[2][1]/HEIGHTS[2][0] && k < HEIGHTS[1][1]/HEIGHTS[1][0] + HEIGHTS[2][1]/HEIGHTS[2][0]) {
						grid[i][j][k] = new Cell(HEIGHTS[1][0], getDepth(i, j, k, deviation), new Point3D(i, j, k));
						grid[i][j][k].setSurface(false);
					}
					else if(k >= HEIGHTS[1][1]/HEIGHTS[1][0] + HEIGHTS[2][1]/HEIGHTS[2][0] && k < baseLayers) {
						grid[i][j][k] = new Cell(HEIGHTS[0][0], getDepth(i, j, k, deviation), new Point3D(i, j, k));
						if(k == baseLayers-1 && deviation[i][j] == 0.0) {
							grid[i][j][k].setSurface(true);
						}
						else {
							grid[i][j][k].setSurface(false);
						}
					}
					else {
						double d = getDepth(i, j, k, deviation);
						if(d != -1)
						{
							grid[i][j][k] = new Cell(1, d, new Point3D(i, j, k));

							if(d == 0) {
								grid[i][j][k].setSurface(true);
							}
							else {
								grid[i][j][k].setSurface(false);
							}
						}
						else {
							grid[i][j][k] = null;
						}
					}

				}
			}
		}

		Farm farm = new Farm();
		farm.setLatitude(latitude);
		farm.setLongitude(longitude);
		farm.setRelief(minmax[1]);
		farm.setGrid(grid);
		farm.setZCellCount(grid[0][0].length);

		return farm;
	}


	/**
	 * Generates a random, but smooth topography using the given MAX_RELIEF, TOLERANCE, and SIZE. The MAX_RELIEF is the total
	 *  amount of difference there is allowed to be in the elevations. The TOLERANCE is the largest height difference between
	 *  two blocks that will be accepted. The SIZE is the length and width of the land
	 * @return a 2D array of doubles that correspond to heights
	 */
	private static double[][] getDeviations() {
		double chance; //that this height will used
		double previous2, previous1; //the heights of surrounding cells
		double num; //randomly generated number
		double[][] deviation = new double[SIZE][SIZE]; //the heights of each column above the lowest point

		for(int j = 0; j < SIZE; j++) {
			for(int i = 0; i < SIZE; i++) {
				chance = .5; //50/50 chance of taking the generated number
				num = rand.nextDouble() * MAX_RELIEF; // 0 <= num < MAX_RELIEF

				//Looks at cells to left to help decide whether or not the new number will be kept
				if(i - 2 >= 0) {
					previous2 = deviation[i - 2][j];
					previous1 = deviation[i - 1][j];
					chance += chance(previous1, previous2, num);
				}
				else if(i - 1 >= 0) {
					previous1 = deviation[i - 1][j];
					chance += chance(previous1, num);
				}

				//Looks at cells above to help decide whether or not the new number will be kept
				if(j - 2 >= 0) {
					previous2 = deviation[i][j - 2];
					previous1 = deviation[i][j - 1];
					chance += chance(previous1, previous2, num);
				}
				else if(j - 1 >= 0) {
					previous1 = deviation[i][j - 1];
					chance += chance(previous1, num);
				}

				//Looks at cells diagonally up and left to help decide whether or not the new number will be kept
				if(i - 2 >= 0 && j - 2 >= 0) {
					previous2 = deviation[i - 2][j - 2];
					previous1 = deviation[i - 1][j - 1];
					chance += chance(previous1, previous2, num);
				}
				else if(i - 1 >= 0 && j - 1 >= 0) {
					previous1 = deviation[i - 1][j - 1];
					chance += chance(previous1, num);
				}

				//Roll and see if you come up successful. If so, keep the value.
				if(rand.nextDouble() <= chance) {
					deviation[i][j] = num;
				}
				//Otherwise, try again for this cell.
				else {
					i--;
					continue;
				}
			}
		}
		return deviation;
	}


	/**
	 * Takes a given set of deviations in height and adjusts them to be in the range 0 to maxDeviation.
	 * @param deviation the deviations in height to adjust
	 * @return the minimum and maximum deviations
	 */
	private static double[] adjustForMinMax(double[][] deviation) {
		double[] minmax = {MAX_RELIEF, 0.0}; //Start with the min at MAX and the max at 0

		//Find absolute minimum and maximum
		for(int j = 0; j < SIZE; j++) {
			for(int i = 0; i < SIZE; i++) {
				if(deviation[i][j]  < minmax[0]) {
					minmax[0] = deviation[i][j];
				}
				if(deviation[i][j] > minmax[1]) {
					minmax[1] = deviation[i][j];
				}
			}
		}

		//Force the deviations into a range from 0 to max deviation
		for(int j = 0; j < SIZE; j++) {
			for(int i = 0; i < SIZE; i++) {
				deviation[i][j] -= minmax[0];
				deviation[i][j] = (int)(100*deviation[i][j])/100.0; //Round off to 2 decimal places
			}
		}

		//Set the minmax into a range from 0 to max deviation
		minmax[1] -= minmax[0];
		minmax[0] = 0;

		return minmax;
	}


	/**
	 * Takes coordinates and tells you how deep in the ground they are
	 * @param i the x coordinate of the cell
	 * @param j the y coordinate of the cell
	 * @param k the z coordinate of the cell
	 * @param deviations the height deviations in the surface of the land
	 * @return the depth of the cell
	 */
	private static double getDepth(int i, int j, int k, double[][] deviations) {
		double depth = 0;
		int baseLayers = (int)(HEIGHTS[0][1]/HEIGHTS[0][0]) + (int)(HEIGHTS[1][1]/HEIGHTS[1][0]) + (int)(HEIGHTS[2][1]/HEIGHTS[2][0]);

		//if k is above the height of a given column, just set the depth to -1 to indicate air
		if(k > (baseLayers + deviations[i][j]*100)) {
			depth = -1;
		}

		//Up until the point where you start seeing topography, just add the depth of the solid block below the topography + the topography
		if(k >= 0 && k < HEIGHTS[2][1]/HEIGHTS[2][0]) {
			depth += HEIGHTS[0][1]+HEIGHTS[1][1];
			depth += ((HEIGHTS[2][1]/HEIGHTS[2][0]-1)-k)*HEIGHTS[2][0];
			depth += deviations[i][j] * 100;
		}
		else if(k >= HEIGHTS[2][1]/HEIGHTS[2][0] && k < HEIGHTS[1][1]/HEIGHTS[1][0] + HEIGHTS[2][1]/HEIGHTS[2][0]) {
			depth += HEIGHTS[1][1];
			depth += ((HEIGHTS[1][1]/HEIGHTS[1][0] + HEIGHTS[2][1]/HEIGHTS[2][0]-1)-k)*HEIGHTS[1][0];
			depth += deviations[i][j] * 100;
		}
		else if(k >= HEIGHTS[1][1]/HEIGHTS[1][0] + HEIGHTS[2][1]/HEIGHTS[2][0] && k < baseLayers) {
			depth += ((baseLayers-1)-k)*HEIGHTS[0][0];
			depth += deviations[i][j] * 100;
		}
		//Once you start hitting the topography, only add the depth of the topography above
		else {
			depth +=  deviations[i][j]*100 - (k-baseLayers);
		}

		return depth;
	}


	/**
	 * Takes a height and helps determine whether it will be kept or not depending on the surrounding heights.
	 * @param previous1 the deviation in height of the square two squares away from the num in question
	 * @param previous2 the deviation in height of the square one square away from the num in question
	 * @param num the deviation in height of the square in question
	 * @return a number that will affect the chance of num being picked
	 */
	private static double chance(double previous1, double previous2, double num) {
		double chance = 0;
		double change1 = previous1 - previous2;
		double change2 = num - previous1;

		//If the change being considered is smaller than change of the previous cell, the change being considered has a higher chance of
		// being picked. This helps smooth some of the jerkiness (because the numbers are random) out.
		if(Math.abs(change1) <= Math.abs(change2)) {
			chance += .1;
		}

		//If the change being considered is larger than change of the previous cell, the change being considered has a lower chance of
		// being picked. This again helps smooth jerkiness out.
		else {
			chance -= .1;

			//If the slope of the change being considered and the change of the previous cell are both pointing the same way,
			// the change being considered has a higher chance of being picked. This means that hills have a higher chance of
			// occurring rather than just small bumps everywhere.
			if(change1*change2 > 0) {
				chance += .025;
			}

			//If the slope of the change being considered and the change of the previous cell are pointing in different directions,
			// the change being considered has a lower chance of being picked. Again, this encourages hills.
			else if(change1*change2 < 0) {
				chance -= .025;
			}
		}

		//If the change being considered is outside the range of TOLERANCE, then the chance of being picked is 0.
		if(Math.abs(change2) > TOLERANCE) {
			return -5.0;
		}

		return chance;
	}


	/**
	 * Takes a given height and helps decide whether it will be kept based on surrounding heights
	 * @param previous the deviation in height of the square next to the num in question
	 * @param num the deviation in height of the square in question
	 * @return a number that will affect the chance of num being picked
	 */
	private static double chance(double previous, double num) {
		//If there is only 1 cell neighboring a cell you are calculating the height for (in a given direction), then simply
		// pick a height that is within the range TOLERANCE
		if(Math.abs(num - previous) <= TOLERANCE) {
			return .1;
		}
		return -5.0;
	}


	/** This should only be used for testing */
	public static void main(String[] args) {
		Farm f = createFarm(1000, 1000);

		System.out.println(f.zCellCount * Farm.xCellCount * Farm.yCellCount);

		// Get maximum size of heap in bytes. The heap cannot grow beyond this size.
		// Any attempt will result in an OutOfMemoryException.
		System.out.println(Runtime.getRuntime().maxMemory());

		// Get current size of heap in bytes
		System.out.println(Runtime.getRuntime().totalMemory());

		// Get amount of free memory within the heap in bytes. This size will increase
		// after garbage collection and decrease as new objects are created.
		System.out.println(Runtime.getRuntime().freeMemory());
	}
}
