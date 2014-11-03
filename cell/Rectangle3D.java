/**
 * Author: Nathan Acosta
 * Date: Feb 16, 2013
 */
package cell;

/**
 * @author nacosta
 * A Rectangle3D specifies a 3D rectangle using an X, Y, and Z coordinate
 * system.
 */
public class Rectangle3D
{
  private Point3D point1;
  private Point3D point2;
  
  /**
   * Creates a 3D rectangle with one corner at point1 and the opposite
   * corner at point2.
   * @param point1 Corner point
   * @param point2 Opposite corner point
   */
  public Rectangle3D(Point3D point1, Point3D point2)
  {
    this.point1 = point1;
    this.point2 = point2;
  }
  
  /**
   * Intersects determines if the Point3D is within the volume of the
   * 3D rectangle by testing if each value of point is within the range
   * of the end points.
   * @param point The point to test intersection with.
   * @return True if point is within 3D rectangle, false otherwise.
   */
  public boolean intersects(Point3D point)
  {
    boolean xIntersects;
    boolean yIntersects;
    boolean zIntersects;
    
    xIntersects = this.intersects(point.x, point1.x, point2.x);
    yIntersects = this.intersects(point.y, point1.y, point2.y);
    zIntersects = this.intersects(point.z, point1.z, point2.z);
    
    if (xIntersects && yIntersects && zIntersects) return true;
    else return false;
  }
  
  /**
   * Intersects determines if value is between the two end values or
   * is equal to either of the end values.
   * @param value The value to check against the other two values.
   * @param endValueA First end value.
   * @param endValueB Second end value.
   * @return True if value intersects the two end values.
   */
  private boolean intersects(double value, double endValueA, double endValueB)
  {
    if(value == endValueA || value == endValueB)
    { return true;
    }
    else if(endValueA > endValueB)
    { if(value < endValueA && value > endValueB) return true;
    }
    else if (endValueA < endValueB)
    { if(value > endValueA && value < endValueB) return true;
    }
    return false;
  }
  
}
