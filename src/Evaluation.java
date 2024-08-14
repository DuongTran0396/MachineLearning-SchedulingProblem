import java.util.*;

public class Evaluation {

    private final DataArrays dArr;
    private final int W_minFilled;
    private final int W_pref;
    private final int W_pair;
    private final int W_secDiff;
    private final int pen_gameMin;
    private final int pen_practiceMin;
    private final int pen_notPaired;
    private final int pen_section;

    public Evaluation(DataArrays dArr, int W_minFilled, int W_pref,
                      int W_pair, int W_secDiff, int pen_gameMin, int pen_practiceMin,
                      int pen_notPaired, int pen_section){
        this.dArr = dArr;
        this.W_minFilled = W_minFilled;
        this.W_pref = W_pref;
        this.W_pair = W_pair;
        this.W_secDiff = W_secDiff;
        this.pen_gameMin = pen_gameMin;
        this.pen_practiceMin = pen_practiceMin;
        this.pen_notPaired = pen_notPaired;
        this.pen_section = pen_section;

//        System.out.println(dArr.getGenerationEval());


    }

    /*This method produce eval value for a single parent*/
    public int EvaluateParent(String[] parent){
        int eval = 0;

        eval = eval + gameAndPracticeMin(parent);
//        System.out.println(eval);
        eval = eval + preference(parent);
//        System.out.println(eval);
        eval = eval + pair(parent);
//        System.out.println(eval);
        eval = eval + sectionDifference(parent);
//        System.out.println(eval);

        return eval;

    }

    /**
    * This method checks if the assignment of the time slot violates the game/ practice min
    * @param parent a parent
    * @return Eval_minFilled
     * */
    public int gameAndPracticeMin(String[] parent){
        int evalMinFilled;
        int evalGameMin = 0;
        int evalPracticeMin = 0;

        int slotMin;
        int count;

        /*Checks for gameMin*/
        for(int gameIndex=0; gameIndex<dArr.getGameSlot().size(); gameIndex++){
            String slot = "g"+gameIndex;
            slotMin = dArr.getSlotMin(slot);
            count = Collections.frequency(List.of(parent), slot);
            if (count < slotMin){
                evalGameMin = evalGameMin + pen_gameMin * (slotMin - count);
            }
        }

        /*Checks for practiceMin*/
        for(int practiceIndex=0; practiceIndex<dArr.getPracticeSlot().size(); practiceIndex++){
            String slot = "p"+practiceIndex;
            slotMin = dArr.getSlotMin(slot);
            count = Collections.frequency(List.of(parent),slot);
            if (count < slotMin){
                evalPracticeMin = evalPracticeMin + pen_practiceMin * (slotMin - count);
            }

        }
        /*Weight has been multiplied here*/
        evalMinFilled = (evalGameMin + evalPracticeMin) * W_minFilled;

//        if(evalMinFilled == 0){
//            System.out.println("GameMin & PracticeMin passed!");
//        }

        return evalMinFilled;
    }

    /**
     * This method checks if the assignment of the time slot violates the game/ practice preference
     * @param parent a parent
     * @return Eval_pref
     * */
    public int preference(String[] parent){
        //ignoreInvalidTimeSlot();

        int evalPref = 0;

        for(int prefIndex=0; prefIndex < dArr.getPreference().size();prefIndex++){
            int slotIndex;
            String timeSlot = null;

            String trimmedIdentifier = dArr.getPreference().get(prefIndex)[2].trim();
            String[] temp = new String[2];
            temp[0] = dArr.getPreference().get(prefIndex)[0];
            temp[1] = dArr.getPreference().get(prefIndex)[1].trim();

            slotIndex = dArr.getGamesAndPractices().indexOf(trimmedIdentifier);
            if(dArr.isGame(trimmedIdentifier)){
                for(int i=0; i<dArr.getGameSlot().size(); i++){
                    if(dArr.getGameSlot().get(i)[0].equals(temp[0])){
                        if(dArr.getGameSlot().get(i)[1].equals(temp[1])){
                            timeSlot = "g"+i;
                        }
                    }
                }

            }else{
                for(int j=0; j<dArr.getPracticeSlot().size(); j++){
                    if(dArr.getPracticeSlot().get(j)[0].equals(temp[0])){
                        if(dArr.getPracticeSlot().get(j)[1].equals(temp[1])){
                            timeSlot = "p"+j;
                        }
                    }
                }
            }

            if(!parent[slotIndex].equals(timeSlot)){
                evalPref = evalPref + Integer.parseInt(dArr.getPreference().get(prefIndex)[3].trim());
            }

        }

//        dArr.displaySplitArray(7, dArr.getPreference());

        evalPref = evalPref * W_pref;

//        if(evalPref==0){
//            System.out.println("Preference passed!");
//        }

        return evalPref;
    }




    /*
    * This method check if games/practices in pair are assigned at same the same time
    * In case timeslots being compared are one game timeslot and the other one practice timeslot
    * It always compares day and the starting time of the timeslots
    * */
    public int pair(String[] parent){
        int evalPaired = 0;

        int index1;
        int index2;

        for(int pairIndex = 0;pairIndex<dArr.getPair().size();pairIndex++){
            index1 = dArr.getGamesAndPractices().indexOf(dArr.getPair().get(pairIndex)[0]);
            index2 = dArr.getGamesAndPractices().indexOf(dArr.getPair().get(pairIndex)[1].trim());

            if(!Objects.equals(dArr.getTimeSlotMap().get(parent[index1])[0], dArr.getTimeSlotMap().get(parent[index2])[0])){
                if(!Objects.equals(dArr.getTimeSlotMap().get(parent[index1])[1], dArr.getTimeSlotMap().get(parent[index2])[1])){
                    evalPaired = evalPaired + pen_notPaired;
                }
            }

        }

        evalPaired = evalPaired * W_pair;

//        if(evalPaired == 0){
//            System.out.println("Paired passed!");
//        }

        return evalPaired;

    }

    /*
    * this Method is to find if games with same age/tier group are assigned to a same
    * time slot. If they are we give penalty
    * */
    public int sectionDifference(String[] parent){
        int evalSecDiff = 0;

        String[] temp1;
        String[] temp2;

        for(int secDiffIndex=0;secDiffIndex<dArr.getGames().size()-1;secDiffIndex++){
            temp1 = dArr.getGames().get(secDiffIndex).split(" ");

            for(int compare = secDiffIndex+1; compare<dArr.getGames().size();compare++){

                temp2 = dArr.getGames().get(compare).split(" ");

                if(Objects.equals(temp1[1], temp2[1])){
//                    System.out.println(temp1[1]+" "+temp1[2]+" "+temp1[3]);
//                    System.out.println(temp2[1]+" "+temp2[2]+" "+temp2[3]);

                    if(parent[secDiffIndex]==parent[compare]){
                        evalSecDiff = evalSecDiff + pen_section;
                    }

                }
            }
        }

        evalSecDiff = evalSecDiff * W_secDiff;

//        if(evalSecDiff == 0){
//            System.out.println("Section Difference passed!");
//        }

        return evalSecDiff;
    }





}
