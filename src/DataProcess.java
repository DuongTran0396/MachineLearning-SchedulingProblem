import java.util.*;

/**
 * This Class processes the parsed data for better utility
 */
public class DataProcess {
    private final ArrayList<String[]> pa;
    private final ArrayList<String[]> uw;
    private final DataArrays dArr;
    public DataProcess(DataArrays dArr){
        this.dArr = dArr;
        this.pa = this.dArr.getPartialAssignment();
        this.uw = this.dArr.getUnwanted();
    }

    /**
     * This method processes the given partial assign to match the parent format
     * @return returns a parent that is already assigned with partial assign
     */
    public String[] processPartialAssign(){
        int ID_IDX = 0;
        int DAY_IDX = 1;
        int HOUR_IDX = 2;
        String[] parent = new String[dArr.getGamesAndPractices().size()];
        Arrays.fill(parent,"$");
        DataArrayToSlots(ID_IDX, DAY_IDX, HOUR_IDX, parent, pa);
        return parent;
    }

    /**
     * This method processes the same division of games and practices and adds them
     * to the not compatible list.
     */
    public void processSameDiv(){
        ArrayList<String> games = dArr.getGames();
        ArrayList<String> practices = dArr.getPractices();
        String str = null;
        int ORGANIZATION_IDX = 0;
        int LEAGUE_IDX = 1;
        int DIV_IDX = 2;
        int DIV_ID_IDX = 3;
        for (String p: practices){
            // all division case. e.g. CSMA U17T1 PRC 01
            String[] ps = p.split("\\s");
            if (ps.length < 6){
                str = String.join(" ", ps[ORGANIZATION_IDX], ps[LEAGUE_IDX]);
            }else{
                str = String.join(" ", ps[ORGANIZATION_IDX], ps[LEAGUE_IDX], ps[DIV_IDX], ps[DIV_ID_IDX]);
            }
            for (String g: games){
                if (g.contains(str)) {
                    // add to notCompatible list if they are the same division
                    String s = g + ", " + p;
                    dArr.getNotCompatible().add(s.split(","));
                }
            }
        }
    }

    /**
     * This method processes the not compatible list into a hash map
     * @return processed not compatible list as a hash map
     */
    public HashMap<String, ArrayList<String>> processNotCompatible() {
        HashMap<String, ArrayList<String>> out = new HashMap<>();
        for (String[] sArr : dArr.getNotCompatible()) {
            String key = String.valueOf(dArr.getGamesAndPractices().indexOf(sArr[0]));
            String value = String.valueOf(dArr.getGamesAndPractices().indexOf(sArr[1].trim()));
            ArrayList<String> val_arr = new ArrayList<>();
            if (out.containsKey(key)){
                val_arr = out.get(key);
                val_arr.add(value);
            }else{
                val_arr.add(value);
                out.put(key, val_arr);
            }
        }
//        for (String i : out.keySet()) {
//            System.out.println(i + ": " + out.get(i));
//        }
        return out;
    }
    /**
     * This method processes the given unwanted identifier to time slots to match the parent format
     * @return returns a set of time slots that is unwanted for the games/ practice
     */
    public String[] processUnwanted(){
        int ID_IDX = 0;
        int DAY_IDX = 1;
        int HOUR_IDX = 2;
        String[] res = new String[dArr.getGamesAndPractices().size()];
        Arrays.fill(res, "_");
        DataArrayToSlots(ID_IDX, DAY_IDX, HOUR_IDX, res, uw);
        return res;
    }

    /**
     * This utility method will take in a data array and map it to match the parent formats.
     * @param ID_IDX Identifier index
     * @param DAY_IDX day index
     * @param HOUR_IDX hour index
     * @param res result of the conversion
     * @param sArr a string array
     */
    private void DataArrayToSlots(int ID_IDX, int DAY_IDX, int HOUR_IDX, String[] res, ArrayList<String[]> sArr) {
        for (String[] s : sArr){
            int i;
            String[] slot;
            // Checks if the identifier is a game or practice
            if (dArr.isGame(s[ID_IDX])){
                // Gets the index of the time slot for the given day and hour
                i = dArr.findSlotIDX(dArr.getGameSlot(), s[DAY_IDX].trim(), s[HOUR_IDX].trim());
                // Gets the time slot with the index
                slot = dArr.getGameSlot().get(i);
            }else{
                i = dArr.findSlotIDX(dArr.getPracticeSlot(), s[DAY_IDX].trim(), s[HOUR_IDX].trim());
                slot = dArr.getPracticeSlot().get(i);
            }
            // Assign the time slot to the index of game/ practice
            res[dArr.getGamesAndPractices().indexOf(s[ID_IDX])] = dArr.getRevTimeSlotMap().get(slot);
        }
    }
}