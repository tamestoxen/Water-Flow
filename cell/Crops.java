package cell;

/**
 * Author: Robert Trujillo 
 * Date: 04.23.13 
 * A Crops class to create a crop on a quadrant of a farm.
 */
public class Crops {

	private Plant _plant; // holds crop plant type
	private Farm _farm;// array of Cells where the crop is located
	private int _quadrant;// Quandrant number where Farm may be plotted numbers(0-> NW, 1-> NE, 2-> SW, 3-> SE)
	private int _cropSize; // variable to keep cropsize in number of plants

	/**
	 * Sets the constant values of a Plant type.
	 * 
	 * @param plantType
	 *            type of plant for crop
	 * @param cropLocation
	 *            in array of cells within farm
	 * @param cropSize
	 *            number of plants in crop
	 * @throws Exception
	 */
	public Crops(Plant plant, Farm farm, int quadrant) throws Exception {		
		
		// check if valid Crop entered
		if (quadrant < 4) {
			this._quadrant = quadrant;
		} else
			throw new Exception("Invalid Quadrant Entered");
		
		//declare variables to set crop by quadrant
		int minX = 0, minY = 0, midX = farm.SIZE/2, midY = farm.SIZE/2, maxX = farm.SIZE, maxY = farm.SIZE;

		this._plant = plant;
		this._farm = farm;
		this._cropSize = 0;

				
		if (_quadrant == 0) {
			for (int x = minX; x < midX; x += _plant.getDistanceBetweenSeeds()) {
				for (int y = minY; y < midY; y += _plant.getDistanceBetweenSeeds()) {

					_farm.getGrid()[x][y][_plant.getDepthOfSeed()]
							.setPlant(_plant);
					_cropSize++;
				}
			}
		} else if (_quadrant == 1) {
			for (int x = midX; x < maxX; x += _plant
					.getDistanceBetweenSeeds()) {
				for (int y = minY; y < midY; y += _plant.getDistanceBetweenSeeds()) {

					_farm.getGrid()[x][y][_plant.getDepthOfSeed()]
							.setPlant(_plant);
					_cropSize++;
				}
			}
		} else if (_quadrant == 2) {
			for (int x = minX; x < midX; x += _plant.getDistanceBetweenSeeds()) {
				for (int y = midY; y < maxY; y += _plant
						.getDistanceBetweenSeeds()) {

					_farm.getGrid()[x][y][_plant.getDepthOfSeed()]
							.setPlant(_plant);
					_cropSize++;
				}
			}
		} else {
			for (int x = midX; x < maxX; x += _plant
					.getDistanceBetweenSeeds()) {
				for (int y = midY; y < maxY; y += _plant
						.getDistanceBetweenSeeds()) {

					_farm.getGrid()[x][y][_plant.getDepthOfSeed()]
							.setPlant(_plant);
					_cropSize++;
				}
			}
		}
	}

	/**
	 * @return Returns the PlantType of this crop
	 */
	public Plant getPlant() {
		return this._plant;
	}

	/**
	 * @return Returns the array of cells that this crop covers.
	 */
	public Farm getFarm()

	{
		return this._farm;
	}

	/**
	 * Method that counts living plants within crop
	 * @return Returns the CropSize in number of plants for this Crop
	 */
	public double getCropSize() {
		int currentCropSize = 0;
		Plant currentPlant = null;
		
		//declare variables to set crop by quadrant
		int minX = 0, minY = 0, midX = this._farm.SIZE/2, midY = this._farm.SIZE/2, maxX = this._farm.SIZE, maxY = this._farm.SIZE;
		
		
		if (this._quadrant == 0) {
			for (int x = minX; x < midX; x += _plant.getDistanceBetweenSeeds()) {
				for (int y = minY; y < midY; y += _plant.getDistanceBetweenSeeds()) {
					currentPlant = this._farm.getGrid()[x][y][_plant.getDepthOfSeed()].getPlant();
							if(currentPlant!= null){
								if(currentPlant.isDeadOrAlive()){
									currentCropSize++;
								}
							}
						}
					}
				} else if (_quadrant == 1) {
					for (int x = midX; x < maxX; x += _plant
							.getDistanceBetweenSeeds()) {
						for (int y = minY; y < midY; y += _plant.getDistanceBetweenSeeds()) {

							currentPlant = this._farm.getGrid()[x][y][_plant.getDepthOfSeed()].getPlant();
							if(currentPlant!= null){
								if(currentPlant.isDeadOrAlive()){
									currentCropSize++;
								}
							}
						}
					}
				} else if (_quadrant == 2) {
					for (int x = minX; x < midX; x += _plant.getDistanceBetweenSeeds()) {
						for (int y = midY; y < maxY; y += _plant
								.getDistanceBetweenSeeds()) {

							currentPlant = this._farm.getGrid()[x][y][_plant.getDepthOfSeed()].getPlant();
							if(currentPlant!= null){
								if(currentPlant.isDeadOrAlive()){
									currentCropSize++;
								}
							}
						}
					}
				} else {
					for (int x = midX; x < maxX; x += _plant
							.getDistanceBetweenSeeds()) {
						for (int y = midY; y < maxY; y += _plant
								.getDistanceBetweenSeeds()) {

							currentPlant = this._farm.getGrid()[x][y][_plant.getDepthOfSeed()].getPlant();
							if(currentPlant!= null){
								if(currentPlant.isDeadOrAlive()){
									currentCropSize++;
								}
							}
						}
					}
				}
		this._cropSize = currentCropSize;
		return this._cropSize;
	}	

	/**
	 * @return Returns the quadrant of the farm that the crop is in
	 **/
	public int get_quadrant() {
		return _quadrant;
	}	
	

}
