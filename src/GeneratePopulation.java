import java.util.*;

public class GeneratePopulation {
    private final DataArrays dArr;
    private final DataProcess dp;
    private final Validation validation;
    private int population_size;


    public GeneratePopulation(DataArrays dArr, Validation validation){
        this.dArr = dArr;
        this.validation = validation;
        dp = new DataProcess(dArr);
        dp.processSameDiv();
        dp.processUnwanted();
    }

    /**
     * This method fills empty game slots randomly
     * @param parent parent array
     * @param idx index of the empty game slot
     */
    public void fillGames(String[] parent, int idx){
        Random rand = new Random();
        int upperbound = dArr.getGameSlot().size();
        int int_random;
        String slot;
        do {
            int_random = rand.nextInt(upperbound);
            // grabs a random time slot from the time slot hashmap
            slot = dArr.getRevTimeSlotMap().get(dArr.getGameSlot().get(int_random));
            parent[idx] = slot;
        } while ((int_random > dArr.getGameSlot().size()));
    }

    /**
     * This method fills empty practice slots randomly
     * @param parent parent array
     * @param idx index of the empty practice slot
     */
    public void fillPractice(String[] parent, int idx){
        Random rand = new Random();
        int upperbound = dArr.getPracticeSlot().size();
        int int_random;
        String slot;
        do {
            int_random = rand.nextInt(upperbound);
            // grabs a random time slot from the time slot hashmap
            slot = dArr.getRevTimeSlotMap().get(dArr.getPracticeSlot().get(int_random));
            parent[idx] = slot;
        } while ((int_random > dArr.getPracticeSlot().size()));
    }

    /**
     * This method generates an initial population
     * @param partialParent partially assigned parent
     * @param numOfParents  number of parents in a generation
     */
    public void generateInitialPopulation(String[] partialParent, int numOfParents){
        int i = 0;
        population_size = numOfParents;
        while (i < numOfParents){
            String[] tempPartial = partialParent.clone();
            for (int j = 0; j < tempPartial.length; j++) {
                if (tempPartial[j].equals("$")) {
                    if (dArr.isGame(dArr.getGamesAndPractices().get(j))) {
                        // is a game
                        fillGames(tempPartial, j);
                    } else {
                        // is a practice
                        fillPractice(tempPartial, j);
                    }
                }
            }
            dArr.getGeneration().add(tempPartial);
            //System.out.println("here");
            i++;
        }

    }

    public int getPopulation_size() {
        return population_size;
    }


}