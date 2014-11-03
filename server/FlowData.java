package server;

import cell.Direction;
import java.io.Serializable;

/**
 * This will be passed from the Client to the Server and then back to a different Client. To pass it from
 *  the Server to a client, simply call the Client's WaterFlow class and say waterFlow.externalFlow(flowData);
 * @author: Max Ottesen
 */
public class FlowData extends NetworkData implements Serializable {
	public final Type       type = Type.FlowWater;
	public final Direction  direction;
	public final Double[][] water;

	public FlowData(Direction direction, Double[][] water) {
		this.direction = direction;
		this.water = water;
	}
}
