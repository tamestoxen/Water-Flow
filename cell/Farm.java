package cell;

/**
 * Farm contains a 3D array of Cells (grid) that has a
 * latitude, longitude, and a relief along with a string
 * to display errors (errorText). The x and y cellCounts are
 * set corresponding to ten centimeters so x and y evaluate
 * to 40960000 square centimeters, a bit over one acre.
 * @author nacosta
 */
public class Farm
{
	private double money = 1000.00d;
	public static final int SIZE = 64;
	Crops[] planted = new Crops[4];
	private int waterQty = 10;
  public String errorText; //Error messages
  public static final int xCellCount = SIZE; //Width of the grid in cell amount
  public static final int yCellCount = SIZE; //Length of the grid in cell amount
  public int zCellCount; //Height of the grid in cell amount.
  
  private double latitude, longitude, relief; //Chosen location
  private Cell[][][] grid; //Contains all cells above and below the surface.
  
  /**
   * @param zCount the amount of cells in the z axis.
   */
  public void setZCellCount(int zCount)
  { this.zCellCount = zCount;
  }
  
  /**
   * @return the zCellCount
   */
  public int getZCellCount()
  { return grid[0][0].length;
  }
  public int getXCellCount()
  {
    return SIZE;	  
  }
  public int getYCellCount()
  {
    return SIZE;	  
  }
  
  /**
   * @return the latitude
   */
  public double getLatitude()
  { return latitude;
  }
  
  /**
   * @param latitude the latitude to set
   */
  public void setLatitude(double latitude)
  { this.latitude = latitude;
  }
  
  /**
   * @return the longitude
   */
  public double getLongitude()
  { return longitude;
  }
  
  /**
   * @param longitude the longitude to set
   */
  public void setLongitude(double longitude)
  { this.longitude = longitude;
  }
  
  /**
   * @return the relief
   */
  public double getRelief()
  { return relief;
  }
  
  /**
   * @param relief the relief to set
   */
  public void setRelief(double relief)
  { this.relief = relief;
  }
  
  /**
   * @return the grid
   */
  public Cell[][][] getGrid()
  { return grid;
  }
  
  /**
   * @param grid the grid to set
   */
  public void setGrid(Cell[][][] grid)
  { this.grid = grid;
  }

	public void setMoney(double money) {
		this.money = money;
	}
	public double getMoney() {
		return money;
	}
	public void setCrop(int idx, Crops c){
	  if (idx < planted.length) planted[idx] = c;
	}
	public double getCropQty(int quadrant){
		return planted[quadrant].getCropSize();
	}
	public Plant getCrop(int quadrant){
		return planted[quadrant].getPlant();
	}
	public void setWaterQty(int waterQty) {
		this.waterQty = waterQty;
	}
	
	public int getWaterQty() {
		return waterQty;
	}
}
