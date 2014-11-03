package flow;

import cell.*;
import server.FlowData;
import topo.Topography;
import java.util.Random;

/**
 * WaterFlow is a class that computes how water should flow from cell to cell. <p />
 * TODO: Flow water across server <br />
 * TODO: Decide when to rain?
 * @author Max Ottesen
 */
public class WaterFlow {
	private static final boolean includeOutput  = true;
	private static final boolean includeRain    = true;
	private static final boolean startWithWater = false;
	public  static final boolean includePlants  = false;

	private int          timeStep = 1000; //seconds
	private int          finishedWorkers;
	private long         realTime;
	private Integer      simulatedTime;
	private Farm         farm;
	private Cell[][][]   grid;
	private Double[][][] change;
	private Double[][][] hydraulicHead;
	private Double[][][] percentSaturation;
	private Double[][][] reservoirs;
	private FlowWorker[] workers;


	/**
	 * Creates a WaterFlow object that will simulate the water flowing in and through the given Farm
	 * @param farm the Farm that this object will simulate water flow for
	 */
	public WaterFlow(Farm farm) {
		this.farm = farm;
		this.grid = farm.getGrid();
		this.change = new Double[Farm.xCellCount][Farm.yCellCount][farm.zCellCount];
		this.hydraulicHead = new Double[Farm.xCellCount][Farm.yCellCount][farm.zCellCount];
		this.percentSaturation = new Double[Farm.xCellCount][Farm.yCellCount][farm.zCellCount];
		this.reservoirs = new Double[4][Farm.SIZE][farm.zCellCount];
		this.finishedWorkers = 0;
		this.workers = new FlowWorker[4];
		this.simulatedTime = 0;
		reset(change);
		reset(hydraulicHead);
		reset(percentSaturation);
		reset(reservoirs);

		workers[0] = new FlowWorker(0, Farm.xCellCount / 2, 0, Farm.yCellCount / 2, farm.zCellCount, this, grid, change,
		                            reservoirs, timeStep);
		workers[1] = new FlowWorker(Farm.xCellCount / 2, Farm.xCellCount, 0, Farm.yCellCount / 2, farm.zCellCount, this,
		                            grid, change, reservoirs, timeStep);
		workers[2] = new FlowWorker(0, Farm.xCellCount / 2, Farm.yCellCount / 2, Farm.yCellCount, farm.zCellCount, this,
		                            grid, change, reservoirs, timeStep);
		workers[3] = new FlowWorker(Farm.xCellCount / 2, Farm.xCellCount, Farm.yCellCount / 2, Farm.yCellCount,
		                            farm.zCellCount, this, grid, change, reservoirs, timeStep);

		for(int i = 0; i < 4; i++) {
			workers[i].start();
		}
	}


	/**
	 * Runs the model for a given number of seconds
	 * @param seconds the number of seconds to run the model for. This may or may not run the model for the exact amount
	 *                of seconds because the time step may not match up evenly. In that case, it will simulate slightly
	 *                farther in the future than the given time.
	 */
	public void update(double seconds) {
		for(double i = 0; i < seconds; i += this.timeStep) {
			long time = System.currentTimeMillis();

			//Check to see if model stats should be reported
			if(simulatedTime % (timeStep * 200) == 0) {
				int avgTimeStep = 0;
				if(simulatedTime != 0) {
					avgTimeStep = (int) realTime / (simulatedTime / timeStep);
				}
				//Total up the water in the system
				double totalWater = 0;
				for(int x = 0; x < workers.length; x++) {
					totalWater += workers[x].getTotalWater();
				}
				for(int z = 0; z < reservoirs.length; z++) {
					for(int y = 0; y < reservoirs[0][0].length; y++) {
						for(int x = 0; x < reservoirs[0].length; x++) {
							totalWater += reservoirs[z][x][y];
						}
					}
				}
				Cell c = getSurfaceCell();


				println(totalWater + " mL");
				println(c.getWaterVolume() + " mL in " + c.getCoordinate());
				println(simulatedTime + " s");
				println(avgTimeStep + " ms\n");
			}
			
			//Flow between farms every 15 time steps
			if(simulatedTime % (timeStep * 15) == 0 && simulatedTime != 0) {
			  flowOutOfFarm();
			}
			
			//This tests if my rain method works correctly
			if(simulatedTime % (timeStep * 1000) == 0 && includeRain) {
			  rain(11); //11 mL per cell
			}

			synchronized(grid) {
				this.update();
			}
			synchronized(simulatedTime) {
				simulatedTime += this.timeStep;
			}
			realTime += (System.currentTimeMillis() - time);
		}
	}
	
	private Cell getSurfaceCell() {
		for(int k = 0; k < farm.zCellCount; k++) {
			for(int j = 0; j < Farm.yCellCount; j++) {
				for(int x = 0; x < Farm.xCellCount; x++) {
					if(grid[x][j][k].isSurface()) {
						return grid[x][j][k];
					}
				}
			}
		}
		return null;
	}


	/** Runs the model for one time step */
	private void update() {
		//Tell the workers to start the hydraulic head/percent saturation calculations
		for(int i = 0; i < 4; i++) {
			workers[i].startCalculations();
		}
		//Wait for calculations to complete
		while(finishedWorkers < 4) {
			try{Thread.sleep(1);}
			catch(InterruptedException e){}
		}
		finishedWorkers = 0;


		//Once heads/saturations have been calculated, tell the workers to start flow calculations
		for(int i = 0; i < 4; i++) {
			workers[i].startCalculations();
		}
		//Wait for the calculations to complete
		while(finishedWorkers < 4) {
			try{Thread.sleep(1);}
			catch(InterruptedException e){}
		}
		finishedWorkers = 0;


		//Once the flow calculations have completed, tell the workers to update the water
		for(int i = 0; i < 4; i++) {
			workers[i].startCalculations();
		}
		//Wait for the calculations to complete
		while(finishedWorkers < 4) {
			try{Thread.sleep(1);}
			catch(InterruptedException e){}
		}
		finishedWorkers = 0;


		//Zero out my arrays
		reset(change);
		reset(hydraulicHead);
		reset(percentSaturation);
	}


	/** Sends the server water that it will carry to a different farm */
	private void flowOutOfFarm() {    
		FlowData north = new FlowData(Direction.NORTH, reservoirs[0]);
		FlowData east  = new FlowData(Direction.EAST,  reservoirs[1]);
		FlowData south = new FlowData(Direction.SOUTH, reservoirs[2]);
		FlowData west  = new FlowData(Direction.WEST,  reservoirs[3]);

		flowIntoFarm(north);
		flowIntoFarm(east);
		flowIntoFarm(south);
		flowIntoFarm(west);
		
		synchronized(reservoirs) {
			reset(reservoirs);
		}
	}


	/**
	 * Sets a Double[][][] array to all 0s
	 * @param array the array to be reset
	 */
	private void reset(Double[][][] array) {
		//Reset the change holder
		for(int k = 0; k < array[0][0].length; k++) {
			for(int j = 0; j < array[0].length; j++) {
				for(int i = 0; i < array.length; i++) {
					array[i][j][k] = new Double(0);
				}
			}
		}
	}


	/**
	 * Returns the percent saturation of a specified cell
	 * @param x the X-coordinate of the cell
	 * @param y the Y-coordinate of the cell
	 * @param z the Z-coordinate of the cell
	 * @return the percent saturation of the cell
	 */
	protected double getPercentSaturation(int x, int y, int z) {
		return percentSaturation[x][y][z];
	}


	/**
	 * Sets a given cell to have a given percent saturation
	 * @param x   x coordinate of cell
	 * @param y   y coordinate of cell
	 * @param z   z coordinate of cell
	 * @param sat percent saturation of cell
	 */
	protected void setPercentSaturation(int x, int y, int z, Double sat) {
		synchronized(this.percentSaturation[x][y][z]) {
			this.percentSaturation[x][y][z] = sat;
		}
	}


	/**
	 * Returns the hydraulic head of a specified cell
	 * @param x the X-coordinate of the cell
	 * @param y the Y-coordinate of the cell
	 * @param z the Z-coordinate of the cell
	 * @return the hydraulic head of the cell
	 */
	protected double getHydraulicHead(int x, int y, int z) {
		return hydraulicHead[x][y][z];
	}


	/**
	 * Sets a given cell to have a given hydraulic head
	 * @param x    x coordinate of cell
	 * @param y    y coordinate of cell
	 * @param z    z coordinate of cell
	 * @param head hydraulic head of cell
	 */
	protected void setHydraulicHead(int x, int y, int z, Double head) {
		synchronized(this.hydraulicHead[x][y][z]) {
			this.hydraulicHead[x][y][z] = head;
		}
	}


	/**
	 * Adds a given amount of water to all surface cells, effectively simulating a rainstorm (in 1 time step)
	 * @param waterPerCell the amount of water that each surface cell receives from the rain
	 */
	public void rain(double waterPerCell) {
		for(int k = 0; k < farm.getZCellCount(); k++) {
			for(int j = 0; j < Farm.yCellCount; j++) {
				for(int i = 0; i < Farm.xCellCount; i++) {
					if(grid[i][j][k] == null || !grid[i][j][k].isSurface()) {
						continue;
					}
						
					synchronized(change[i][j][k]) {
					  change[i][j][k] += waterPerCell;
					}
				}
			}
		}
	}


	/**
	 * Takes FlowData from another Farm and puts it into this Farm
	 * @param data the water to be put into the farm
	 */
	public void flowIntoFarm(FlowData data) {
		int minX, maxX;
		int minY, maxY;

		//Flow to the opposite side that the water is coming from
		if(data.direction == Direction.NORTH) {
			minX = 0;
			maxX = Farm.xCellCount;
			minY = 0;
			maxY = 1;
		}
		else if(data.direction == Direction.EAST) {
			minX = 0;
			maxX = 1;
			minY = 0;
			maxY = Farm.yCellCount;
		}
		else if(data.direction == Direction.SOUTH) {
			minX = 0;
			maxX = Farm.xCellCount;
			minY = Farm.yCellCount - 1;
			maxY = Farm.yCellCount;
		}
		else if(data.direction == Direction.WEST) {
			minX = Farm.xCellCount - 1;
			maxX = Farm.xCellCount;
			minY = 0;
			maxY = Farm.yCellCount;
		}
		else {
			return; //There's a problem if water is flowing from somewhere besides the 4 cardinal directions
		}
		
		int maxZ = farm.zCellCount;
		for(int k = 0; k < maxZ; k++) {
			for(int j = minY; j < maxY; j++) {
				for(int i = minX; i < maxX; i++) {
					if(grid[i][j][k] == null) {
					  continue;
					}
					  
					int index;
					if(data.direction == Direction.NORTH || data.direction == Direction.SOUTH) {
						index = i;
					}
					else {
						index = j;
					}
						
					if(data.water.length <= index || data.water[0].length <= k) {
						continue;
					}

					synchronized(change[i][j][k]) {
						change[i][j][k] += data.water[index][k];
					}
				}
			}
		}
		
	}


	/** @return the amount of simulated time that has elapsed */
	public int getSimulatedTime() {
		synchronized(simulatedTime) {
			return simulatedTime;
		}
	}


	/** Lets a worker thread tell the master that it's done */
	protected synchronized void workerDone() {
		finishedWorkers++;
	}


	/** Lets all the worker threads die */
	public void kill() {
		for(int i = 0; i < workers.length; i++) {
			workers[i].kill();
		}
	}


	/** This should only be used for testing purposes */
	public static void main(String[] args) {
		long time = System.currentTimeMillis();

		println("INITIALIZATIONS");
		print("  ...topography : ");
		Farm farm = Topography.createFarm(1000, 1000); //ABQ lat/lon Topography.createFarm(35.0844, 106.6506);
		println((System.currentTimeMillis() - time) + " ms");
		println("    " + (farm.getZCellCount() * Farm.xCellCount * Farm.yCellCount) + " cells in system");

		time = System.currentTimeMillis();

		Random rand = new Random();

		print("  ...ground     : ");
		//XML_Handler.initGround(farm, "C:/Program Files (x86)/JetBrains/IntelliJ IDEA 12.1
		// .1/IDEA/Java/Groundwater_Flow/src/XML_Handler/FarmSetup.xml");
		Cell[][][] grid = farm.getGrid();
		for(int k = 0; k < farm.zCellCount; k++) {
			for(int j = 0; j < Farm.yCellCount; j++) {
				for(int i = 0; i < Farm.xCellCount; i++) {
					if(grid[i][j][k] != null) {
						grid[i][j][k].setSoil(Soil.GILASAND);
						if(startWithWater && rand.nextDouble() < .75) {
							grid[i][j][k].setWaterVolume(rand.nextInt(100));
						}
						if(grid[i][j][k].isSurface()) {
							grid[i][j][k].setPlant(Plant.CHILE);
						}
					}
				}
			}
		}
		println((System.currentTimeMillis() - time) + " ms");

		time = System.currentTimeMillis();

		print("  ...flow       : ");
		WaterFlow water = new WaterFlow(farm);
		println((System.currentTimeMillis() - time) + " ms");

		println("\nStarting model\n");
		println("Total water in system (in milliliters)");
		println("Water in a surface cell (in milliliters)");
		println("Total time simulated (in seconds)");
		println("Average calculation time per time step (in milliseconds)\n");
		try{Thread.sleep(2500);}
		catch(InterruptedException e){}

		time = System.currentTimeMillis();
		water.update(18408206); //8 months = 21037950 seconds //7 months = 18408206 seconds
		println("Simulated " + water.simulatedTime + " seconds in " + (System.currentTimeMillis() - time) / 1000 + " " +
		        "seconds");

		println("\nWaiting");
		try{Thread.sleep(2500);}
		catch(InterruptedException e){}

		water.kill();
		println("done");
	}


	/** Used as a replacement for System.out.println(); Simply calls print(s + "\n"); */
	private static void println(String s) {
		print(s + "\n");
	}

	/** Used as a replacement for System.out.print(); so that I can easily turn output on or off */
	private static void print(String s) {
		if(includeOutput) {
			System.out.print(s);
		}
	}
}
