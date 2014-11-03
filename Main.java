import cell.*;
import flow.*;
import server.*;
import topo.*;

public class Main {


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