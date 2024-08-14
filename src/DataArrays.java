import java.lang.reflect.Array;
import java.util.*;

public class DataArrays {
    private ArrayList<String[]> GameSlot = new ArrayList<>();
    private ArrayList<String[]> PracticeSlot = new ArrayList<>();
    private ArrayList<String> Games = new ArrayList<>();
    private ArrayList<String> Practices = new ArrayList<>();
    private ArrayList<String> GamesAndPractices = new ArrayList<>();
    private ArrayList<String[]> NotCompatible = new ArrayList<>();
    private ArrayList<String[]> Unwanted = new ArrayList<>();
    private ArrayList<String[]> Preference = new ArrayList<>();
    private ArrayList<String[]> Pair = new ArrayList<>();
    private ArrayList<String[]> PartialAssignment = new ArrayList<>();
    private ArrayList<String[]> generation = new ArrayList<>();
    private HashMap<String, Double> generationEval = new HashMap<>();
    private HashMap<Double, Integer> revGenerationEval = new HashMap<>();
    private HashMap<Integer, Double[]> fitnessRangeMap = new HashMap<>();
    private HashMap<Integer, String> dataArrMap = new HashMap<>();
    private HashMap<String, String[]> timeSlotMap = new HashMap<>();
    private HashMap<String[], String> revTimeSlotMap = new HashMap<>();
    private ArrayList<String> dataNames = new ArrayList<>(
            Arrays.asList(
                    "Game Slots",
                    "Practice Slots",
                    "Games",
                    "Practices",
                    "Games and Practices",
                    "Not Compatible",
                    "Unwanted",
                    "Preferences",
                    "Pair",
                    "Partial Assignment"
            ));



    /*
     * Constructor for the DataArray class
     */
    public DataArrays(){
        makeDataArrMap();
    }
    /*
     * This method maps the data names to a type code.
     */
    private void makeDataArrMap(){
        for (int i=0; i<dataNames.size(); i++)
            getDataArrMap().put(i, dataNames.get(i));
    }
    /*
     * This method maps game slots and practice slots to a string.
     */
    public void makeTimeSlotMap(){
        for (int g=0; g<getGameSlot().size(); g++){
            getTimeSlotMap().put("g"+ g, getGameSlot().get(g));
            getRevTimeSlotMap().put(getGameSlot().get(g), "g"+ g);
        }
        for (int p=0; p<getPracticeSlot().size(); p++){
            getTimeSlotMap().put("p"+ p, getPracticeSlot().get(p));
            getRevTimeSlotMap().put(getPracticeSlot().get(p), "p"+ p);
        }
    }
    /*
     * This method displays the data arrays based on the type code
     * 0: Game Slots
     * 1: Practice Slots
     * 2: Games
     * 3: Practices
     * 4: Games and Practices
     * 5: Not Compatible
     * 6: Unwanted
     * 7: Preferences
     * 8: Pair
     * 9: Partial Assignment
     */
    public void displaySplitArray(int typeCode, ArrayList<String[]> arr){
        System.out.format("%s:%n", getDataArrMap().get(typeCode));
        for (String[] s : arr) {
            System.out.println(Arrays.toString(s));
        }
        System.out.println(" ");
    }
    public void displayIdentifierArray(int typeCode, ArrayList<String> arr){
        System.out.format("%s:%n", getDataArrMap().get(typeCode));
        for (String s : arr) {
            System.out.println(s);
        }
        System.out.println(" ");
    }
    public void displayTimeSlotMap(){
        System.out.println("== Displaying Time Slots ==");
        for (String i : getTimeSlotMap().keySet()) {
            System.out.println(i + ": " + Arrays.toString(getTimeSlotMap().get(i)));
        }
        System.out.println("== End of Time Slots ==\n");
    }
    public void displayEvals(){
        System.out.println("== Displaying Eval Scores ==");
        for (String i : getGenerationEval().keySet()) {
            System.out.println(i + ": " + getGenerationEval().get(i));
        }
        System.out.println("== Displaying Eval Scores ==\n");
    }

    public void displayProbRange(){
        System.out.println("== Displaying Prob Range ==");
        for (Integer i : getFitnessRange().keySet()) {
            System.out.println(i + ": " + Arrays.toString(getFitnessRange().get(i)));
        }
        System.out.println("== Displaying Prob Range ==\n");
    }

    /*
     * This method concatenates the games and practice together
     */
    public void concatGamesAndPractices(){
        getGamesAndPractices().addAll(getGames());
        getGamesAndPractices().addAll(getPractices());
    }

    /*
     * Utility Methods
     */
    public boolean isGame(String identifier){
        return getGames().contains(identifier);
    }

    /**
     * This method returns the maximum games/ practices that can be slotted
     * @param key Game/ Practice slot key
     * @return GameMax/ PracticeMax
     */
    public int getSlotMax(String key){
        return Integer.parseInt(getTimeSlotMap().get(key)[2]);
    }

    /**
     * This method returns the minimum games/ practices that should be slotted
     * @param key Game/ Practice slot key
     * @return GameMax/ PracticeMax
     */
    public int getSlotMin(String key){
        return Integer.parseInt(getTimeSlotMap().get(key)[3].trim());
    }

    /**
     * This method finds and returns the index of the specified time slot
     * @param timeSlot a game slot or a practice slot
     * @param day the day specified
     * @param hour the hour specified
     * @return index of time slot if found, -1 otherwise
     */
    public int findSlotIDX(ArrayList<String[]> timeSlot, String day, String hour){
        for (String[] s: timeSlot){
            if (s[0].equals(day) && s[1].equals(hour)) return timeSlot.indexOf(s);
        }
        return -1;
    }

    /*
    * This method checks if CMSA U12T1 games are in the GAMES:
    * */
    public boolean specialBookingTriggered1(){
        for(int i=0;i<getGames().size();i++){
            String organization = getGames().get(i).split(" ")[0];
            String tier = getGames().get(i).split(" ")[1].trim();

            if(organization.equals("CMSA")&&tier.equals("U12T1")){
                return true;

            }
        }
        return false;
    }
    /*
     * This method checks if CMSA U13T1 games are in the GAMES:
     * */
    public boolean specialBookingTriggered2(){
        for(int i=0;i<getGames().size();i++){
            String organization = getGames().get(i).split(" ")[0];
            String tier = getGames().get(i).split(" ")[1].trim();

            if(organization.equals("CMSA")&&tier.equals("U13T1")){
                return true;

            }
        }
        return false;
    }

    /*
    * This method checks if we need special booking slots
    * if we do, then it decrease the game max at TU 18:00
    * by how many slots we need.
    * */
    public void specialBookingGreaterThanMax(){
        int count = 0;

        if(specialBookingTriggered1()){
            count++;
        }
        if(specialBookingTriggered2()){
            count++;
        }

        for(int i=0; i<getPracticeSlot().size();i++ ){
            String day = getPracticeSlot().get(i)[0];
            String time = getPracticeSlot().get(i)[1].trim();
            if(day.equals("TU")&&time.equals("18:00")){
                getPracticeSlot().get(i)[2] = String.valueOf(Integer.parseInt(getPracticeSlot().get(i)[2].trim()) - count);
            }
        }
    }

    /*
    This method is to get rid of preference with invalid time slot
    * Also if the given games or practice at prefer time don't exist,
    * it also deletes the preference
    */
    public void ignoreInvalidTimeSlot(){
        for(int prefIndex = 0; prefIndex < getPreference().size(); prefIndex++){

            String trimmedIdentifier = getPreference().get(prefIndex)[2].trim();
            String[] temp = new String[2];
            temp[0] = getPreference().get(prefIndex)[0];
            temp[1] = getPreference().get(prefIndex)[1].trim();

            int match = 0;
            if(isGame(trimmedIdentifier)){
                for(int i=0; i<getGameSlot().size(); i++){
                    if(getGameSlot().get(i)[0].equals(temp[0])){
                        if(getGameSlot().get(i)[1].equals(temp[1])){
                            if(getGames().contains(trimmedIdentifier)){
                                match++;
                            }else {
                                System.out.println(trimmedIdentifier + " is not given in the input file.");
                            }
                        }
                    }
                }

            }else{
                for(int j=0; j<getPracticeSlot().size(); j++){
                    if(getPracticeSlot().get(j)[0].equals(temp[0])){
                        if(getPracticeSlot().get(j)[1].equals(temp[1])){
                            if(getPractices().contains(trimmedIdentifier)){
                                match++;
                            }else{
                                System.out.println(trimmedIdentifier + " is not given in the input file.");
                            }
                        }
                    }
                }
            }

            if (match == 0){
                if(isGame(trimmedIdentifier)){
                    System.out.println("Game at "+getPreference().get(prefIndex)[1].trim()+
                            " on "+ getPreference().get(prefIndex)[0]+
                            " is invalid.");
                }else{
                    System.out.println("Practice at "+getPreference().get(prefIndex)[1].trim()+
                            " on "+ getPreference().get(prefIndex)[0]+
                            " is invalid.");
                }

                getPreference().remove(prefIndex);
            }
        }
//        dArr.displaySplitArray(7, dArr.getPreference());
    }


    /*
     * Start of Getters and Setters
     */
    public ArrayList<String[]> getGameSlot(){
        return GameSlot;
    }
    public ArrayList<String[]> getPracticeSlot(){
        return PracticeSlot;
    }
    public ArrayList<String> getGames(){
        return Games;
    }
    public ArrayList<String> getPractices(){
        return Practices;
    }
    public ArrayList<String> getGamesAndPractices(){
        return GamesAndPractices;
    }
    public ArrayList<String[]> getNotCompatible(){
        return NotCompatible;
    }
    public ArrayList<String[]> getUnwanted(){
        return Unwanted;
    }
    public ArrayList<String[]> getPreference(){
        return Preference;
    }
    public ArrayList<String[]> getPair(){
        return Pair;
    }
    public ArrayList<String[]> getPartialAssignment(){
        return PartialAssignment;
    }
    public ArrayList<String[]> getGeneration(){
        return generation;
    }
    public HashMap<Double, Integer> getRevGenerationEval(){
        return revGenerationEval;
    }
    public HashMap<String, Double> getGenerationEval(){return generationEval;}
    public HashMap<Integer, Double[]> getFitnessRange(){return fitnessRangeMap;}
    public HashMap<Integer, String> getDataArrMap(){
        return dataArrMap;
    }
    public HashMap<String, String[]> getTimeSlotMap(){
        return timeSlotMap;
    }
    public HashMap<String[], String> getRevTimeSlotMap(){
        return revTimeSlotMap;
    }
    public void append(ArrayList<String[]> arr, String[] splitData){
        arr.add(splitData);
    }
    public void append(ArrayList<String> arr, String data){
        arr.add(data);
    }
    /*
     * End of Getters and Setters
     */
}