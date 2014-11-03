package cell;

/**
 * A Cell specifies an individual region within a 3D space, particularly a 
 * Farm region with different Soil and Plant types.
 * @author nacosta
 */
public class Cell
{
  private static double cellSize = 6400/Farm.SIZE; // Length = width in centimeters
  private double height; // In centimeters
  private boolean surface; // True if cell is a surface cell
  private double depth; // Distance to surface in centimeters
  private double waterVolume; // Water within cell in milliliters

  private Point3D coordinate; // 3D array x, y, and z indexes
  private Soil soil; // Name of soil type
  private Plant plant; // Name of plant type

  /**
   * Creates a Cell with the specified parameters as attributes.
   * 
   * @param height The height of the cell.
   * @param type The type of soil within the cell.
   * @param saturation The initial percentage of saturation.
   * @param point The location of the cell.
   */
  public Cell(double height, double depth, Point3D coordinates)
  {
    this.height = height;
    this.coordinate = coordinates;
    this.depth = depth;
  }

  /**
   * Creates a Cell with no attributes set.
   */
  public Cell()
  {
    //TODO Nothing at the moment
  }
  
  /**
   * Sets all the attributes of a Cell to have a value equal to the
   * passed in Cell's attributes'.
   * @param cell
   */
  public void copyFromCell(Cell cell)
  {
    this.height = cell.getHeight();
    this.surface = cell.isSurface();
    this.depth = cell.getDepth();
    this.waterVolume = cell.getWaterVolume();
    this.coordinate = cell.getCoordinate();
    this.soil = cell.getSoil();
    this.plant = cell.getPlant();
  }
  
  /**
   * @return the cellSize of this cell
   */
  public static double getCellSize()
  {
    return cellSize;
  }
  
  /**
   * @return the coordinate
   */
  public Point3D getCoordinate() { return coordinate; }

  /**
   * @param coordinate the coordinate to set
   */
  public void setCoordinate(Point3D coord) { this.coordinate = coord; }

  /**
   * @param height the height to set
   */
  public void setHeight(double height) { this.height = height; }

  /**
   * @return the height
   */
  public double getHeight() { return height; }

  /**
   * @return the soil
   */
  public Soil getSoil() { return soil; }

  /**
   * @param soil the soil to set
   */
  public void setSoil(Soil soil) { this.soil = soil; }

  /**
   * @return the plant
   */
  public Plant getPlant() { return plant; }

  /**
   * @param plant the plant to set
   */
  public void setPlant(Plant plant) { this.plant = plant; }
  
  /**
   * @return the waterVolume
   */
  public double getWaterVolume() { return waterVolume; }

  /**
   * @param waterVolume the waterVolume to set
   */
  public void setWaterVolume(double volume) { this.waterVolume = volume; }

  /**
   * @return the surface
   */
  public boolean isSurface() { return surface; }

  /**
   * @param surface the surface to set
   */
  public void setSurface(boolean surface) { this.surface = surface; }

  /**
   * @return the depth
   */
  public double getDepth() { return depth; }
  
  /**
   * @param depth the depth to set
   */
  public void setDepth(double depth) { this.depth = depth; }
}
