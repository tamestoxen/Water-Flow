package cell;

/**
 * A Plant contains properties pertaining to its interaction
 * with water and physical structure.
 * @author: nacosta
 */
public enum Plant {
	// Plant Types and Attributes
	PINTOBEANS(21, 114, 11, 4, .37),
	SUNFLOWER(91, 25, 30, 12, .37),
	AMARANTH(14, 18, 4, 2, .19),
	CHILE(119, 61, 5, 1, .37),
	SWEETCORN(77, 46, 20, 3, .57),
	SUMMERSQUASH(49, 92, 45, 3, .37),
	WINTERSQUASH(112, 122, 120, 3, .37),
	POTATOES(119, 46, 30, 15, .37),
	SWEETPEPPER(80, 31, 3, 1, .37);

	private double _transpiration;
	private int _maturationTime;
	private int _matureDepth;
	private int _distanceBetweenSeeds;
	private int _depthOfSeed;
	private int _rootDepth;
	private double _waterConsumption;
	private int _allowableDaysWithoutWater;
	private Point3D [] _rootCellCoordinates;
	private boolean _deadOrAlive;//alive is true, dead is false

	/**
	 * Sets the constant values of a Plant type.
	 *
	 * @param maturationTime
	 *            in days
	 * @param matureDepth
	 *            in cells
	 * @param distanceBetweenSeeds
	 *            in cells
	 * @param depthOfSeed
	 *            in cells
	 * @param waterConsumption
	 *            in mL/day
	 */
	private Plant(int maturationTime, int matureDepth,
			int distanceBetweenSeeds, int depthOfSeed, double waterConsumption) {
		this._transpiration = waterConsumption*.10;//on average 10% or water required
		this._maturationTime = maturationTime;
		this._matureDepth = matureDepth;
		this._distanceBetweenSeeds = distanceBetweenSeeds;
		this._depthOfSeed = depthOfSeed;
		this._waterConsumption = waterConsumption;
		this._rootDepth = 0;
		this._allowableDaysWithoutWater = (int)(maturationTime/4);//set to 1/4 maturation time
		this._deadOrAlive = true;
		this._rootCellCoordinates = new Point3D[matureDepth];
	}
    /**
     * Returns a plant type from a String
     * @param p - String determining a plant type
     * @return Plant corresponding to a String
     */
	public static Plant getPlantType(String p)
	{
	  Plant plant = null;
	  if(p.equalsIgnoreCase("Pintobeans"))
	  {
	    plant = Plant.PINTOBEANS;  	  
	  }
	  else if(p.equalsIgnoreCase("Sunflower"))
	  {
	    plant = Plant.SUNFLOWER;	  
	  }
	  else if(p.equalsIgnoreCase("Amaranth"))
	  {
	    plant = Plant.AMARANTH;	  
	  }
	  else if(p.equalsIgnoreCase("Chile"))
	  {
	    plant = Plant.CHILE;	  
	  }
	  else if(p.equalsIgnoreCase("Sweetcorn"))
	  {
	    plant = Plant.SWEETCORN;	  
	  }
	  else if(p.equalsIgnoreCase("Summersquash"))
	  {
	    plant = Plant.SUMMERSQUASH;	  
	  }
	  else if(p.equalsIgnoreCase("Wintersquash"))
	  {
	    plant = Plant.WINTERSQUASH;	  
	  }
	  else if(p.equalsIgnoreCase("Potatoes"))
	  {
	    plant = Plant.POTATOES;	  
	  }
	  else if(p.equalsIgnoreCase("Sweetpepper"))
	  {
	    plant = Plant.SWEETPEPPER;	  
	  }
	  return plant;
	  
	}
	/**
	 * @return Returns the Transpiration of this Plant in mL/day.
	 */
	public double getTranspiration() {
		return this._transpiration;
	}

	/**
	 * @return Returns the Maturation Time of this Plant in days.
	 */
	public int getMaturationTimee() {
		return this._maturationTime;
	}

	/**
	 * @return Returns the Mature Root Depth of this Plant in number of cells.
	 */
	public int getMatureDepth() {
		return this._matureDepth;
	}

	/**
	 * @return Returns the Distance needed between seeds of this Plant in number
	 *         of cells.
	 */
	public int getDistanceBetweenSeeds() {
		return this._distanceBetweenSeeds;
	}

	/**
	 * @return Returns the Depth needed for the seed of this Plant in number of
	 *         cells.
	 */
	public int getDepthOfSeed() {
		return this._depthOfSeed;
	}

	/**
	 * @return Returns the Water Consumption of this Plant in mL/day.
	 */
	public double getWaterConsumption() {
		return this._waterConsumption;

	}

	/**
	 * @return Returns the current Root depth in cm
	 */
	public int getRootDepth() {
		return _rootDepth;
	}
	
	
	/**
	 * Method to get if plant is dead or alive
	 * @returns true if alive, false if dead
	 */
	public boolean isDeadOrAlive() {
		return _deadOrAlive;
	}
	
	/**
	 * Method to get the coordinates of the plants roots
	 * @param plantSeedCoordinate available for plants within crops
	 * @return array of Point3D's
	 */
	public Point3D[] get_rootCellCoordinates(Point3D plantSeedCoordinate) {
		int rootDepth = this._rootDepth;
		int index = 0;
	
		
		if(rootDepth > 0){
			for(int z = plantSeedCoordinate.getZ(); z < rootDepth; z++){				
				this._rootCellCoordinates[index] = new Point3D(plantSeedCoordinate.getX(),plantSeedCoordinate.getY(),z);
			}
		}
		else {
			this._rootCellCoordinates[0] = plantSeedCoordinate;
		}
		return _rootCellCoordinates;
	}

	/**
	 * Method to killPlant
	 */
	public void kill(){
		this._deadOrAlive = false;
	}	
	
	
	
	/**
	 * @param waterAvailableAlongRoot sum of water within cells that root occupies
	 * @return true it growth occurs else false
	 */
	public boolean grow(double waterAvailableAlongRoot){
		
		if(waterAvailableAlongRoot >= this._waterConsumption){
		int growthRate = (int)(this._matureDepth/this._maturationTime);
		this._rootDepth += growthRate;
			return true;
		}
		else{
			if(this._allowableDaysWithoutWater == 0){
				this.kill();
				return false;
			}
			else{
				this._allowableDaysWithoutWater--;
				return false;
			}
			
			
		}
		
	}

}
