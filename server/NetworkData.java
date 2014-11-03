//Michael Asplund
package server;
import java.io.Serializable;

public abstract class NetworkData implements Serializable
{
  public enum Type {Name, Msg, Quit, Start, WATEROFFER, FlowWater, TimeKeeper, Rain, SellProduce, SellProduce2};//CancelOrder, SellCrop
  public Type type;
}
