import java.util.*;

public class Validation {
    private final DataArrays dArr;
    private final TimeslotValidation tsv;
    private final DataProcess dp;

    private final boolean trigger1;
    private final boolean trigger2;

    public Validation(DataArrays dArr){
        this.dArr = dArr;
        tsv = new TimeslotValidation(dArr);
        dp = new DataProcess(dArr);
        this.trigger1 = dArr.specialBookingTriggered1();
        this.trigger2 = dArr.specialBookingTriggered2();
    }









    /**
     * This method checks if the given parent passes all hard constraints, and assigns it a score if not.
     *
     * @param parent a parent
     * @return 0 if the parent passes all hard constraints, some score otherwise
     */
    public double passedHardConstr(String[] parent){
        int count = 0;

        count += greaterThanMaxSlot(parent);

        count += unwanted(parent);

        count += eveningDiv(parent);

        if(trigger1||trigger2){
            count += validSpecialBookingSlot(parent);
        }

        count += notCompatible(parent);

        if(count != 0){
            count += 10;
            return Math.pow(10, count);
        }else{
            return 0;
        }

    }

    /**
     * This method validates if the assignment of the time slot violates the game/ practice max hard constraint
     * @param parent a parent
     * @return integer value depends on how many  games slots are violating game / practice max hard constraint
     */
    public int greaterThanMaxSlot(String[] parent){
        int maxFilledScore;
        int gameMaxScore = 0;
        int practiceMaxScore = 0;

        int slotMax;
        int count;

        /*Checks for gameMax*/
        for(int gameIndex=0; gameIndex<dArr.getGameSlot().size(); gameIndex++){
            String slot = "g"+gameIndex;
            slotMax = dArr.getSlotMax(slot);
            count = Collections.frequency(List.of(parent), slot);
            if (count > slotMax){
                gameMaxScore = gameMaxScore + (count - slotMax);
            }
        }

        /*Checks for practiceMax*/
        for(int practiceIndex=0; practiceIndex<dArr.getPracticeSlot().size(); practiceIndex++){
            String slot = "p"+practiceIndex;
            slotMax = dArr.getSlotMax(slot);
            count = Collections.frequency(List.of(parent),slot);
            if (count > slotMax){
                practiceMaxScore = practiceMaxScore  + (count - slotMax);
            }

        }

        maxFilledScore = gameMaxScore + practiceMaxScore;

        return maxFilledScore;
    }

    /**
     * This method validates if the assignment of the time slot violates the unwanted hard constraint
     * @param parent a parent
     * @return integer value depends on how many unWanted pairs are violated in a parent
     */
    public int unwanted(String[] parent){
        int unWantedScore = 0;

        for(int unwantedIndex=0; unwantedIndex < dArr.getUnwanted().size();unwantedIndex++){
            int slotIndex;
            String timeSlot = null;

            String trimmedIdentifier = dArr.getUnwanted().get(unwantedIndex)[0];
            String[] temp = new String[2];
            temp[0] = dArr.getUnwanted().get(unwantedIndex)[1].trim();  // day
            temp[1] = dArr.getUnwanted().get(unwantedIndex)[2].trim();  // time
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

            if(parent[slotIndex].equals(timeSlot)){
                unWantedScore++;
            }

        }

        return unWantedScore;
    }

    /**
     * This method checks if the game/ practice satisfies the div 9+ evening slot constraint
     * @param parent a parent
     * @return integer value depends on how many slots in the parent violate evening Division hard constraint
     */
    public int eveningDiv(String[] parent){
        int eveningScore = 0;
        for(int i=0; i<dArr.getGamesAndPractices().size();i++){
            int div = Integer.parseInt(dArr.getGamesAndPractices().get(i).split(" ")[3]);
            int idx = dArr.getTimeSlotMap().get(parent[i])[1].trim().indexOf(":");
            String time = dArr.getTimeSlotMap().get(parent[i])[1].trim().substring(0,idx);

            if(div >= 9){
                if(Integer.parseInt(time) < 18){
                    eveningScore++;
                }
            }

        }
        return eveningScore;
    }

    /**
     * This method checks if special booking slot hard constraint is violated
     * @param parent a parent
     * @return integer value depends on how many slots in the parent violate special booking slot hard constraint
     */
    public int validSpecialBookingSlot(String[] parent){
        int specialScore = 0;

        if(trigger1){
            for(int i=0; i<dArr.getGames().size(); i++){
                String organization = dArr.getGamesAndPractices().get(i).split(" ")[0];
                String tier = dArr.getGamesAndPractices().get(i).split(" ")[1].trim();
                if(organization == "CMSA"){
                    if(tier.equals("U12T1")){
                        String day = dArr.getTimeSlotMap().get(parent[i])[0];
                        String time = dArr.getTimeSlotMap().get(parent[i])[1].trim();

                        if(day.equals("TU")&&time.equals("17:00")){
                            specialScore++;
                        }else if(day.equals("TU")&&time.equals("18:30")){
                            specialScore++;
                        }
                    }
                }
            }
            for(int i=dArr.getGames().size(); i<dArr.getPractices().size(); i++){
                String organization = dArr.getGamesAndPractices().get(i).split(" ")[0];
                String tier = dArr.getGamesAndPractices().get(i).split(" ")[1].trim();
                if(organization == "CMSA"){
                    if(tier.equals("U12T1")){
                        String day = dArr.getTimeSlotMap().get(parent[i])[0];
                        String time = dArr.getTimeSlotMap().get(parent[i])[1].trim();

                        if(day.equals("TU")&&time.equals("18:00")){
                            specialScore++;
                        }
                    }
                }
            }

        }

        if(trigger2){
            for(int i=0; i<dArr.getGames().size(); i++){
                String organization = dArr.getGamesAndPractices().get(i).split(" ")[0];
                String tier = dArr.getGamesAndPractices().get(i).split(" ")[1].trim();
                if(organization == "CMSA"){
                    if(tier.equals("U13T1")){
                        String day = dArr.getTimeSlotMap().get(parent[i])[0];
                        String time = dArr.getTimeSlotMap().get(parent[i])[1].trim();

                        if(day.equals("TU")&&time.equals("17:00")){
                            specialScore++;
                        }else if(day.equals("TU")&&time.equals("18:30")){
                            specialScore++;
                        }
                    }
                }
            }
            for(int i=dArr.getGames().size(); i<dArr.getPractices().size(); i++){
                String organization = dArr.getGamesAndPractices().get(i).split(" ")[0];
                String tier = dArr.getGamesAndPractices().get(i).split(" ")[1].trim();
                if(organization == "CMSA"){
                    if(tier.equals("U13T1")){
                        String day = dArr.getTimeSlotMap().get(parent[i])[0];
                        String time = dArr.getTimeSlotMap().get(parent[i])[1].trim();

                        if(day.equals("TU")&&time.equals("18:00")){
                            specialScore++;
                        }
                    }
                }
            }
        }

        return specialScore;

    }


    /**
     * This method checks if not compatible hard constraint is violated
     * @param parent a parent
     * @return integer value depends on how many pairs of not compatible is violated in the parent
     */
    public int notCompatible(String[] parent){
        int ncScore = 0;

        for(int i=0; i< dArr.getNotCompatible().size();i++){

            String identifier1 = dArr.getNotCompatible().get(i)[0];
            String identifier2 = dArr.getNotCompatible().get(i)[1].trim();

            int temp1 = dArr.getGamesAndPractices().indexOf(identifier1);
            int temp2 = dArr.getGamesAndPractices().indexOf(identifier2);

            String slot1 = parent[temp1];
            String slot1Day = dArr.getTimeSlotMap().get(slot1)[0];
            int idx1 = dArr.getTimeSlotMap().get(slot1)[1].trim().indexOf(":");
            String slot1Time = dArr.getTimeSlotMap().get(slot1)[1].trim().substring(0,idx1);

            String slot2 = parent[temp2];
            String slot2Day = dArr.getTimeSlotMap().get(slot2)[0];
            int idx2 = dArr.getTimeSlotMap().get(slot2)[1].trim().indexOf(":");
            String slot2Time = dArr.getTimeSlotMap().get(slot2)[1].trim().substring(0,idx2);

            //if they are both game
            if(dArr.isGame(identifier1)&&dArr.isGame(identifier2)){
                if(slot1Day.equals(slot2Day)&&slot1Time.equals(slot2Time)){
                    ncScore++;
                }
            //if they are both practices
            }else if(!dArr.isGame(identifier1)&&!dArr.isGame(identifier2)){
                if(slot1Day.equals(slot2Day)&&slot1Time.equals(slot2Time)){
                    ncScore++;
                }
            //if one of them are game and the other one is practice
            }else if(dArr.isGame(identifier1)&&!dArr.isGame(identifier2)) {
                if(slot1Day.equals("TU")&&slot2Day.equals("TU")){
                    if(slot1Time.equals(slot2Time)){
                        ncScore++;
                    }else if(Integer.parseInt(slot1Time) == Integer.parseInt(slot2Time)-1 ){
                        ncScore++;
                    }

                }else{
                    if(slot1Day.equals(slot2Day)&&slot1Time.equals(slot2Time)){
                        ncScore++;
                    }
                }

            }else if(!dArr.isGame(identifier1)&&dArr.isGame(identifier2)){
                if(slot1Day.equals("TU")&&slot2Day.equals("TU")){
                    if(slot1Time.equals(slot2Time)){
                        ncScore++;
                    }else if(Integer.parseInt(slot1Time)-1 == Integer.parseInt(slot2Time)){
                        ncScore++;
                    }

                }else{
                    if(slot1Day.equals(slot2Day)&&slot1Time.equals(slot2Time)){
                        ncScore++;
                    }
                }
            }


        }
        return ncScore;
    }
}