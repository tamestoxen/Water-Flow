/**
 * Author: Nathan Acosta
 * Date: Feb 24, 2013
 */
package cell;

/**
 * @author nacosta
 * A Soil contains specific properties corresponding to water. 
 */
public enum Soil
{
  GILASAND
  (.00000917, 0.17, 7.6, 0.3, 6),
  
  GILAFINESANDYLOAM
  (.00000917, 0.17, 7.6, 0.3, 9),
  
  GILALOAM
  (.00000917, 0.18, 7.3, 0.3, 10),
  
  GILACLAYLOAM
  (.000008027, 0.17, 9.4, 0.2, 11),
  
  GILACLAY
  (.000008027, 0.17, 9.4, 0.15, 8),
  
  RIVERWASH
  (0,0,0,0,0),
  
  AIR
  (0,0,0,0,0);
  
  private double hydraulicConductivity; //Measurement in ...
  private double waterCapacity; //Measurement in ...
  private double waterAdhesion; //Measurement in ...
  private double infiltrationRate; //Measurement in ...
  private double evaporationRate; //Measurement in ...
  
  /**
   * Sets each soil type or enum to have its own constant attributes.
   * @param hc Hydraulic Conductivity in ...
   * @param wc Water Capacity in ...
   * @param wa Water Adhesion in ...
   * @param ir Infiltration Rate in ...
   * @param er Evaporation Rate in ...
   */
  private Soil(double hc, double wc, double wa, double ir, double er)
  {
    this.hydraulicConductivity = hc;
    this.waterCapacity = wc;
    this.waterAdhesion = wa;
    this.infiltrationRate = ir;
    this.evaporationRate = er;
  }
  
  /**
   * @return Returns the Hydraulic Conductivity of this Soil.
   */
  public double getHydraulicConductivity()
  { return this.hydraulicConductivity;
  }
  
  /**
   * @return Returns the Water Capacity of this Soil.
   */
  public double getWaterCapacity()
  { return this.waterCapacity;
  }
  
  /**
   * @return Returns the Water Adhesion of this Soil.
   */
  public double getWaterAdhesion()
  { return this.waterAdhesion;
  }
  
  /**
   * @return Returns the Infiltration Rate of this Soil.
   */
  public double getInfiltrationRate()
  { return this.infiltrationRate;
  }
  
  /**
   * @return Returns the Evaporation Rate of this Soil.
   */
  public double getEvaporationRate()
  { return this.evaporationRate;
  }
  
}
