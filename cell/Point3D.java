package cell;

/**
 * A Point3D specifies a point location within an X, Y, and Z, 3D coordinate
 * system. 
 * @author nacosta
 */
public class Point3D
{
  public int x, y, z; // The coordinates in a 3D space.
  
  /**
   * Creates a new Point3D with x, y, and z as its coordinates.
   * @param x The x coordinate.
   * @param y The y coordinate.
   * @param z The z coordinate.
   */
  public Point3D(int x, int y, int z)
  {
    this.x = x;
    this.y = y;
    this.z = z;
  }

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}
  
  
  
}
