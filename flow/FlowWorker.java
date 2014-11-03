package flow;

import cell.Cell;
import cell.Farm;
import cell.Plant;
import cell.Point3D;

/**
 * A FlowWorker is a thread used by WaterFlow in order to split up the ground water calculations
 * @author Max Ottesen
 */
public class FlowWorker extends Thread {
	private boolean      calculate;
	private boolean      kill;
	private int          minX, maxX;
	private int          minY, maxY;
	private int          zCellCount;
	private double       timeStep;
	private WaterFlow    m;
	private Cell[][][]   grid;
	private Double[][][] change;
	private Double[][][] reservoirs;


	/**
	 * Creates a worker thread to do part of the water flow calculations
	 * @param minX starting value of this thread's x range (inclusive)
	 * @param maxX ending value of this thread's x range (exclusive)
	 * @param minY starting value of this thread's y range (inclusive)
	 * @param maxY ending value of this thread's y range (exclusive)
	 * @param zCellCount the ending of this thread's z range (exclusive). Goes from [0, zCellCount)
	 * @param master the WaterFlow object that this thread reports to
	 * @param grid the Cell[][][] that this thread works with
	 * @param change the Double[][][] that the calculation results are stored in
	 * @param reservoirs the Double[][][] that cross-farm calculation results are stored in
	 * @param timeStep the time step that will be used in the flow calculations
	 */
	public FlowWorker(int minX, int maxX, int minY, int maxY, int zCellCount, WaterFlow master, Cell[][][] grid,
	                  Double[][][] change, Double[][][] reservoirs, double timeStep) {
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
		this.grid = grid;
		this.change = change;
		this.reservoirs = reservoirs;
		this.m = master;
		this.timeStep = timeStep;
		this.calculate = false;
		this.kill = false;
		this.zCellCount = zCellCount;
	}


	/** Starts this worker in a loop where it will wait until it's told to do calculations or until it is killed */
	public void run() {
		//Keep going until it's killed
		while(!kill) {
			//The master has to tell this thread to calculate before it will
			while(!calculate) {
				if(kill){return;}
				try{Thread.sleep(1);}
				catch(InterruptedException e){}
			}

			//Calculates hydraulic heads/percent saturations of cells. Also handles plant water consumption
			synchronized(this) {
				for(int k = zCellCount - 1; k >= 0; k--) { //k's count down so that the hydraulic head calculations can be
				                                           // done in the same loop as the percent saturations
					for(int j = minY; j < maxY; j++) {
						for(int i = minX; i < maxX; i++) {
							if(grid[i][j][k] == null) {
								m.setPercentSaturation(i, j, k, new Double(-1));

								m.setHydraulicHead(i, j, k, new Double(-1));
								continue;
							}

							m.setPercentSaturation(i, j, k, new Double(percentSaturation(grid[i][j][k])));
							m.setHydraulicHead(i, j, k, new Double(hydraulicHead(grid[i][j][k])));

							Plant plant = grid[i][j][k].getPlant();
							//If the simulation is within 1 time step of a day, there is a plant, and it is alive
							if(plant != null && plant.isDeadOrAlive() && (m.getSimulatedTime() % 86400) < timeStep && m.getSimulatedTime() != 0) {
								handlePlant(plant, i, j, k);
							}

						}
					}
				}

				//Sets itself up so the master thread has to tell it to start before it does more calculations
				this.calculate = false;
				m.workerDone();


				while(!calculate) {
					if(kill){return;}
					try{Thread.sleep(1);}
					catch(InterruptedException e){}
				}

				//Flows water between all cells synchronously
				for(int k = 0; k < zCellCount; k++) {
					for(int j = minY; j < maxY; j++) {
						for(int i = minX; i < maxX; i++) {
							if(grid[i][j][k] == null || grid[i][j][k].getWaterVolume() <= 0) {
								continue;
							}

							if(i != 0) flowWaterSide(grid[i][j][k], grid[i - 1][j][k]);
							else flowToReservoir(grid[i][j][k], 3, j, k); //Flow to West reservoir


							if(i != Farm.xCellCount - 1) flowWaterSide(grid[i][j][k], grid[i + 1][j][k]);
							else flowToReservoir(grid[i][j][k], 1, j, k); //Flow to East reservoir


							if(j != 0) flowWaterSide(grid[i][j][k], grid[i][j - 1][k]);
							else flowToReservoir(grid[i][j][k], 2, i, k); //Flow to South reservoir


							if(j != Farm.yCellCount - 1) flowWaterSide(grid[i][j][k], grid[i][j + 1][k]);
							else flowToReservoir(grid[i][j][k], 0, i, k); //Flow to North reservoir


							if(k != 0) flowWaterSide(grid[i][j][k], grid[i][j][k - 1]);
							if(k != zCellCount - 1) flowWaterUp(grid[i][j][k], grid[i][j][k + 1]);
						}
					}
				}

				//Sets itself up so the master thread has to tell it to start before it does more calculations
				this.calculate = false;
				m.workerDone();


				while(!calculate) {
					if(kill){return;}
					try{Thread.sleep(1);}
					catch(InterruptedException e){}
				}

				//Updates water volume of cells
				for(int k = 0; k < zCellCount; k++) {
					for(int j = minY; j < maxY; j++) {
						for(int i = minX; i < maxX; i++) {
							if(grid[i][j][k] == null) {
								continue;
							}
							grid[i][j][k].setWaterVolume(grid[i][j][k].getWaterVolume() + change[i][j][k]);
						}
					}
				}
				//Sets itself up so the master thread has to tell it to start before it does more calculations
				this.calculate = false;
				m.workerDone();
			}
		}
	}


	/**
	 * Calculates the amount of water that should flow from one cell to another. This
	 *  should not be used to calculate water flowing upward!
	 * @param cellI the cell to flow water from
	 * @param cellX the cell to flow water to
	 */
	private void flowWaterSide(Cell cellI, Cell cellX) {
		if(cellX == null) {
			return;
		}
		Point3D ci = cellI.getCoordinate();
		Point3D cx = cellX.getCoordinate();

		//The saturation of the giving cell
		double iSatur = m.getPercentSaturation(ci.x, ci.y, ci.z);

		//Only do calculations if...
		//Percent saturation is greater than percent adhesion
		if(iSatur <= cellI.getSoil().getWaterAdhesion()) {
			return;
		}
		//The hydraulic head of the cell is greater than the cell its flowing to
		if(m.getHydraulicHead(ci.x, ci.y, ci.z) <= m.getHydraulicHead(cx.x, cx.y, cx.z)) {
			return;
		}
		//The cell being flowed to isn't full
		if(m.getPercentSaturation(cx.x, cx.y, cx.z) >= .99) {
			return;
		}

		//The average hydraulic conductivity
		double K = (cellI.getSoil().getHydraulicConductivity() + cellX.getSoil().getHydraulicConductivity()) / 2;
		//The area of the face of the cell being flowed from
		double A = cellI.getHeight() * Cell.getCellSize();
		double min = Math.min(1, (m.getHydraulicHead(ci.x, ci.y, ci.z) - m.getHydraulicHead(cx.x, cx.y, cx.z)));

		double flowAmount = K * A * min * timeStep / Cell.getCellSize();

		synchronized(change[cx.x][cx.y][cx.z]) {
			synchronized(change[ci.x][ci.y][ci.z]) {
				change[ci.x][ci.y][ci.z] -= flowAmount;
				change[cx.x][cx.y][cx.z] += flowAmount;
			}
		}
	}


	/**
	 * Calculates the amount of water that should flow from one cell to another. This
	 *  should only be used for water flowing upwards!
	 * @param cellI the cell to flow water from
	 * @param cellX the cell to flow water to
	 */
	private void flowWaterUp(Cell cellI, Cell cellX) {
		if(cellX == null) {
			return;
		}
		Point3D ci = cellI.getCoordinate();
		Point3D cx = cellX.getCoordinate();

		//The percent saturations of each cell
		double iSatur = m.getPercentSaturation(ci.x, ci.y, ci.z);
		double xSatur = m.getPercentSaturation(cx.x, cx.y, cx.z);

		//Only do calculations if...
		//Percent saturation is greater than percent adhesion in giving cell
		if(iSatur <= cellI.getSoil().getWaterAdhesion()) {
			return;
		}
		//Percent saturation is less than percent adhesion in receiving cell
		if(xSatur > cellX.getSoil().getWaterAdhesion()) {
			return;
		}
		//Cell i is more saturated than cell x
		if(iSatur <= xSatur) {
			return;
		}

		//The average hydraulic conductivity
		double K = (cellI.getSoil().getHydraulicConductivity() + cellX.getSoil().getHydraulicConductivity()) / 2;
		//The area of the face of the cell being flowed from
		double A = Cell.getCellSize() * cellI.getHeight();
		double satDif = (iSatur - xSatur) / Cell.getCellSize();

		double flowAmount = K * A * satDif * timeStep;

		synchronized(change[cx.x][cx.y][cx.z]) {
			synchronized(change[ci.x][ci.y][ci.z]) {
				change[ci.x][ci.y][ci.z] -= flowAmount;
				change[cx.x][cx.y][cx.z] += flowAmount;
			}
		}
	}


	/**
	 * Calculates the amount of water that should flow out of the edge of the farm
	 * @param cell the cell that water is flowing from
	 * @param x the x coordinate of the reservoir to flow into
	 * @param y the y coordinate of the reservoir to flow into
	 * @param z the Z coordinate of the reservoir to flow into
	 */
	private void flowToReservoir(Cell cell, int x, int y, int z) {
		Point3D p = cell.getCoordinate();
		double iSatur = m.getPercentSaturation(p.x, p.y, p.z);

		//Only do calculation if percent saturation is greater than the percent adhesion of the giving cell
		if(iSatur <= cell.getSoil().getWaterAdhesion()) {
			return;
		}

		double K = cell.getSoil().getHydraulicConductivity();
		double A = Cell.getCellSize() * cell.getHeight();
		double min = Math.min(1, m.getHydraulicHead(p.x, p.y, p.z)/Cell.getCellSize());

		double flowAmount = K * A * min * timeStep / 10000;

		synchronized(reservoirs[x][y][z]) {
			synchronized(change[p.x][p.y][p.z]) {
				change[p.x][p.y][p.z] -= flowAmount;
				reservoirs[x][y][z] += flowAmount;
			}
		}
	}


	/**
	 * Computes the hydraulic head of the given cell
	 * @param c the cell being considered
	 * @return the hydraulic head of the given cell
	 */
	private double hydraulicHead(Cell c) {
		double height = c.getHeight();
		int x = c.getCoordinate().x;
		int y = c.getCoordinate().y;
		int z = c.getCoordinate().z;
		double saturation = m.getPercentSaturation(x, y, z);

		//Adds the heights of all the cells above the given cell that are fully saturated
		double heightAbove = 0;
		double s;
		for(int i = 1; i < zCellCount; i++) {
			s = m.getPercentSaturation(x, y, z + i);
			if(s > .99) {
				heightAbove += grid[x][y][z + i].getHeight();
			}
			else {
				break;
			}
		}
		//returns the hydraulic head
		return saturation * height + heightAbove;
	}


	/**
	 * Computes the percent saturation of the given cell
	 * @param c the cell being considered
	 * @return the percent saturation of the given cell
	 */
	private double percentSaturation(Cell c) {
		return c.getWaterVolume() / c.getSoil().getWaterCapacity();
	}


	/** @return the total amount of water in this worker's system */
	public double getTotalWater() {
		synchronized(this) {
			double totalWater = 0;
			//k's count down so that the hydraulic head calculations can be done in the same loop as the percent saturations
			for(int k = 0; k < zCellCount; k++) {
				for(int j = minY; j < maxY; j++) {
					for(int i = minX; i < maxX; i++) {
						if(grid[i][j][k] == null) {
							continue;
						}
						totalWater += grid[i][j][k].getWaterVolume();
					}
				}
			}
			return totalWater;
		}
	}


	/**
	 * Takes a plant and removes the water it will use from the system
	 * @param plant the plant that will be handled
	 * @param i its x coordinate
	 * @param j its y coordinate
	 * @param k its z coordinate
	 */
	private void handlePlant(Plant plant, int i, int j, int k) {
		if(!WaterFlow.includePlants) {
			return;
		}

		double availableWater = 0;
		int depth = plant.getMatureDepth();
		int z = k;
		for(int x = depth; x > 0; x -= grid[i][j][z].getHeight()) {
		  availableWater += grid[i][j][z--].getWaterVolume();
		}
		plant.grow(availableWater);
		double toDrink = plant.getWaterConsumption(); //Amount of water that still needs to be removed
		z = k;
		for(int x = depth; x > 0; x -= grid[i][j][z].getHeight()) {
		  if(toDrink == 0) {
		    break;
		  }
			Cell c = grid[i][j][z];
			
			if(c.getWaterVolume() < toDrink) {
				synchronized(change[i][j][z--]) {
					change[i][j][z] -= c.getWaterVolume();
				}
				toDrink -= c.getWaterVolume();
			}
			else {
				synchronized(change[i][j][z--]) {
					change[i][j][z] -= toDrink;
				}
				toDrink = 0;
			}
		}
	}


	/** Lets this thread now that it's OK to start doing its calculations */
	public void startCalculations() {
		this.calculate = true;
	}


	/** Kills this thread by letting its run() loop end */
	public void kill() {
		this.kill = true;
	}
}
