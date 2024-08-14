import java.util.ArrayList;
import java.util.Arrays;
import java.util.*;

public class GeneticAlgorithm {
    private DataArrays dArr;
    private GeneratePopulation generatePopulation;
    private Validation validation;
    private DataProcess dp;
    private Random rand;
    private Evaluation eval;
    private int parent1Index;
    private int parent2Index;
    private ArrayList<String[]> children = new ArrayList<>();
    public GeneticAlgorithm(DataArrays dArr, Validation validation, Evaluation eval){
        this.dArr = dArr;
        this.validation = validation;
        this.eval = eval;
        dp = new DataProcess(dArr);
        rand = new Random(System.currentTimeMillis());   // generate different random sequence based on time

        // Generate initial population
        generatePopulation = new GeneratePopulation(this.dArr, this.validation);
        // Genetic Algorithm begins here


        startAlgorithm(50000, 100);
        displayResult();





    }

    /**
     * This method starts the genetic algorithm
     * @param generation number of generation
     * @param num_of_parents number of parents to generate initially
     */
    public void startAlgorithm(int generation, int num_of_parents){
        int curGen = 0;
        generatePopulation.generateInitialPopulation(dp.processPartialAssign(),num_of_parents);

        // Evaluate them based one their hard constraints and soft constraints
        for(int i=0; i < dArr.getGeneration().size(); i++){
            dArr.getGenerationEval().put(String.valueOf(i), parentFitness((dArr.getGeneration().get(i))));
            dArr.getRevGenerationEval().put(parentFitness((dArr.getGeneration().get(i))), i);
        }

        while (curGen < generation){
            createProbabilityMap();
            // Selection
            String[] parent1 = chooseParent1();
            String[] parent2 = chooseParent2();

            // Crossover
            crossover(parent1, parent2);

            String[] child1 = children.get(0);
            String[] child2 = children.get(1);
            int mutation_prob = new Random().nextInt(100);

            if(mutation_prob == 0){
                // Mutation
                mutate(child1);
                mutate(child2);
            }

            dArr.getGeneration().add(child1);
            dArr.getGeneration().add(child2);
            children.clear();

            if (dArr.getGeneration().size() >= num_of_parents*2){
//                dArr.displayEvals();
                System.out.println("Gen: "+curGen);
                // Culling
                cull();
                curGen++;
            }
        }
    }

    /**
     * This method mutates a child
     * @param child a child
     */
    public void mutate(String[] child){
        String[] pa = dp.processPartialAssign();

        String randomTimeSlot;
        int randomTimeSlotNumber;

        for(int i=0; i<child.length;i++){
            if(Objects.equals(pa[i], "$")){
                int coinFlip = rand.nextInt(2);
                if(coinFlip == 0){
                    if(dArr.isGame(dArr.getGamesAndPractices().get(i))){
                        int gameCount = 0;
                        do{
                            randomTimeSlotNumber = rand.nextInt(dArr.getGameSlot().size());
                            randomTimeSlot = "g"+randomTimeSlotNumber;
                            gameCount++;
                            if (gameCount == dArr.getGameSlot().size()) break;
                        }while(randomTimeSlot.equals(child[i]));
                        // If the chosen index is index of a Practice
                    }else{
                        int practiceCount = 0;
                        do{
                            randomTimeSlotNumber = rand.nextInt(dArr.getPracticeSlot().size()) ;
                            randomTimeSlot = "p"+randomTimeSlotNumber;
                            practiceCount++;
                            if (practiceCount == dArr.getPracticeSlot().size()) break;
                        }while(randomTimeSlot.equals(child[i]));
                    }
                    child[i] = randomTimeSlot;
                }
            }
        }
    }

    /**
     * This function perform crossover between 2 chosen parents
     * each slots of parents does a coin-flip to swap
     * @param parent1 a parent
     * @param parent2 another parent
     */
    public void crossover(String[] parent1, String[] parent2) {

        String[] child1 = new String[parent1.length];
        String[] child2 = new String[parent1.length];

        for (int i = 0; i < parent1.length; i++) {
            int chance = rand.nextInt(10);
            if (chance < 7) {
                child1[i] = parent1[i];
                child2[i] = parent2[i];
            }else{
                child1[i] = parent2[i];
                child2[i] = parent1[i];
            }
        }
        children.add(child1);
        children.add(child2);
    }

    /**
     * This function is used to cull the population
     * Max population = 2*size; if over we cull the first half and keeps the best 2 parents
     */
    public void cull() {
        int size = generatePopulation.getPopulation_size();
        if(dArr.getGeneration().size() >= size*2 ) {     // check if current population > double original size
            // Sort the fitness value
            Map<String, Double> unSortedMap = dArr.getGenerationEval();
            LinkedHashMap<String, Double> sortedMap = new LinkedHashMap<>();
            unSortedMap.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue())
                    .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));

            Object min_index = sortedMap.keySet().toArray()[0];
            Object snd_min_index = null;
            for (int i=1; i<sortedMap.size(); i++){
                snd_min_index = sortedMap.keySet().toArray()[i];
                if (!Objects.equals(dArr.getGenerationEval().get(String.valueOf(min_index)), dArr.getGenerationEval().get(String.valueOf(snd_min_index)))){
                    break;
                }
            }

            System.out.println("Smallest: " + dArr.getGenerationEval().get(String.valueOf(min_index)));
            System.out.println("2nd Smallest: " + dArr.getGenerationEval().get(String.valueOf(snd_min_index)));
            String[] min_parent = dArr.getGeneration().get(Integer.parseInt((String) min_index));
            assert snd_min_index != null;
            String[] snd_min_parent = dArr.getGeneration().get(Integer.parseInt((String) snd_min_index));
            int i = 0;
            while (i < size) {
                if (!Arrays.equals(dArr.getGeneration().get(i), min_parent)
                        && !Arrays.equals(dArr.getGeneration().get(i), snd_min_parent) ){
                    // delete first half of population
                    dArr.getGeneration().remove(i);
                }else if (Collections.frequency(dArr.getGeneration(), min_parent) == dArr.getGeneration().size()){
                    dArr.getGeneration().remove(0);
                }
                i++;
            }
            int j = 0;
            while(j < size){
                dArr.getGenerationEval().put(String.valueOf(j), parentFitness(dArr.getGeneration().get(j)));
                dArr.getRevGenerationEval().put(parentFitness((dArr.getGeneration().get(j))), j);
                j++;
            }
        }
    }

    /**
     * This method calculates the fitness score based on satisfied constraints.
     * @param parent a parent
     * @return a total fitness score judged by number of fulfilled soft constraints, and hard constraint.
     */
    public double parentFitness(String[] parent){
        return validation.passedHardConstr(parent) + eval.EvaluateParent(parent);
    }

    /**
     * This method calculates an inversed sum for roulette selection
     * @return a reversed sum for roulette selection
     */
    private double createRevSum(){
        double sum = createSum();
        double revSum = 0;
        for(int i=0;i<generatePopulation.getPopulation_size();i++){
            revSum = revSum + (sum - dArr.getGenerationEval().get(String.valueOf(i)));
        }
        return revSum;
    }
    /**
     * This method calculates a sum for roulette selection
     * @return a sum for roulette selection
     */
    private double createSum(){
        double sum = 0;
        for(int i=0;i<generatePopulation.getPopulation_size();i++){
            sum = sum + dArr.getGenerationEval().get(String.valueOf(i));
        }
        return sum;
    }

    /**
     * This method creates a probability map for roulette selection
     */
    public void createProbabilityMap(){
        double sum = createSum();
        ArrayList<Double[]> pieChart = new ArrayList<>();
        for(int i=0;i<generatePopulation.getPopulation_size();i++){
            Double[] iArr = new Double[2];
            if(i == 0){
                iArr[0] = Double.valueOf(0);
                iArr[1] = sum - dArr.getGenerationEval().get(String.valueOf(i))-1;
            }else{
                iArr[0] = pieChart.get(i-1)[1]+1;
                iArr[1] = sum - dArr.getGenerationEval().get(String.valueOf(i)) + pieChart.get(i-1)[1];
            }
            pieChart.add(i, iArr);
            dArr.getFitnessRange().put(i, pieChart.get(i));
        }
    }

    /**
     * This method chooses the first parent
     * @return a chosen parent
     */
    public String[] chooseParent1(){
        double revSum = createRevSum();
        double r = rand.nextDouble(revSum);
        for(int i=0; i < dArr.getFitnessRange().size(); i++){
            if (r >= dArr.getFitnessRange().get(i)[0] && r <= dArr.getFitnessRange().get(i)[1]) {
                parent1Index = i;
                break;
            }
        }
        return dArr.getGeneration().get(parent1Index);
    }
    /**
     * This method chooses the second parent that's different than the first parent
     * @return a chosen parent
     */
    public String[] chooseParent2(){
        double revSum = createRevSum();
        do{
            double r = rand.nextDouble(revSum);
            for(int i=0; i < dArr.getFitnessRange().size(); i++){
                if (r >= dArr.getFitnessRange().get(i)[0] && r <= dArr.getFitnessRange().get(i)[1]) {
                    parent2Index = i;
                    break;
                }
            }
        }while(parent1Index == parent2Index);

        return dArr.getGeneration().get(parent2Index);
    }

    /**
     * This method displays the final result
     */
    public void displayResult(){

        System.out.println(" ");
        // Sort the fitness value
        Map<String, Double> unSortedMap = dArr.getGenerationEval();
        LinkedHashMap<String, Double> sortedMap = new LinkedHashMap<>();
        unSortedMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));

        Object min_index = sortedMap.keySet().toArray()[0];
        String[] min_parent = dArr.getGeneration().get(Integer.parseInt((String) min_index));
        System.out.println(Arrays.toString(min_parent));

        if (validation.passedHardConstr(min_parent)!=0){
            System.out.println("No Solution");
        }else{
            System.out.println("Eval Value: " + dArr.getGenerationEval().get(min_index));
            for (int i=0; i<min_parent.length; i++){
                System.out.printf("%-40s:%s, %s%n", dArr.getGamesAndPractices().get(i),
                        dArr.getTimeSlotMap().get(min_parent[i])[0],
                        dArr.getTimeSlotMap().get(min_parent[i])[1]);
            }
        }


    }
}