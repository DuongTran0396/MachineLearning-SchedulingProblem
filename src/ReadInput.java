import javax.xml.crypto.Data;
import java.io.*;
import java.util.*;

/**
 * This Class deals with reading the input file and parsing them into their corresponding arrays
 */
public class ReadInput {
    private final File f;
    private int schemeNum;
    DataArrays dArr = new DataArrays();
    /**
     * Class constructor
     * @param filename the input file
     */
    public ReadInput(String filename){
        f = new File(filename);
        this.schemeNum = 0;
        readFile();
        dArr.specialBookingGreaterThanMax();
        dArr.ignoreInvalidTimeSlot();


    }

    /**
     * This method reads the input file line-by-line,
     * and parses data after the "Name:" scheme,
     * the scheme number starts at 0, and we append arrays based on the number.
     */
    public void readFile(){
        try {
            Scanner myReader = new Scanner(getFile());
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                if (data.endsWith(":") && !(data.equals("Name:"))){
                    int sn = getSchemeNum();
                    // Parse Game slots
                    switch (sn) {
                        case 0 -> processTimeSlots('G', myReader);
                        case 1 -> {
                            processTimeSlots('P', myReader);
                            dArr.makeTimeSlotMap();
                        }
                        case 2 -> processGamesOrPractices('G', myReader);
                        case 3 -> {
                            processGamesOrPractices('P', myReader);
                            dArr.concatGamesAndPractices();
                        }
                        case 4 -> processConstr("NC", myReader);
                        case 5 -> processConstr("UW", myReader);
                        case 6 -> processConstr("PF", myReader);
                        case 7 -> processConstr("Pair", myReader);
                        default -> processConstr("PA", myReader);
                    }
                }
            }

            // DEBUG: Displays the arrays
//            dArr.displaySplitArray(0, dArr.getGameSlot());
//            dArr.displaySplitArray(1, dArr.getPracticeSlot());
//            dArr.displayIdentifierArray(2, dArr.getGames());
//            dArr.displayIdentifierArray(3, dArr.getPractices());
//            dArr.displayIdentifierArray(4, dArr.getGamesAndPractices());
//            dArr.displayTimeSlotMap();
//            dArr.displaySplitArray(5, dArr.getNotCompatible());
//            dArr.displaySplitArray(6, dArr.getUnwanted());
//            dArr.displaySplitArray(7, dArr.getPreference());
//            dArr.displaySplitArray(8, dArr.getPair());
//            dArr.displaySplitArray(9, dArr.getPartialAssignment());
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    /**
     * This method processes the game or practice slot in the input
     * @param type 'G' for game slots, 'P' for practice slots
     * @param reader Scanner for reading input lines
     */
    public void processTimeSlots(char type, Scanner reader){
        String data = reader.nextLine();
        while (!(data.length() == 0)){
            data = data.replaceAll("\\s+", "");
            String[] splitData = data.split(",");
            if (type == 'G'){
                dArr.append(dArr.getGameSlot(), splitData);
            }else{
                dArr.append(dArr.getPracticeSlot(), splitData);
            }
            data = reader.nextLine();
        }
        setSchemeNum(getSchemeNum()+1);
    }

    /**
     * This method processes the games and practices in the input
     * @param type 'G' for games, 'P' for practices
     * @param reader Scanner for reading input lines
     */
    public void processGamesOrPractices(char type, Scanner reader){
        String data = reader.nextLine();
        while (!(data.length() == 0)){
            if (type == 'G'){
                dArr.append(dArr.getGames(), data);
            }else{
                dArr.append(dArr.getPractices(), data);
            }
            data = reader.nextLine();
        }
        setSchemeNum(getSchemeNum()+1);
    }
    /**
     * This method processes the soft constraint schemes
     * @param type the type of soft constraints:
     *             - "NC": Not Compatible
     *             - "UW": Unwanted
     *             - "PF": Preference
     *             - "Pair": Pair
     *             - Default: Partial Assignment
     * @param reader Scanner for reading input lines
     */
    public void processConstr(String type, Scanner reader){
        String data = reader.nextLine();
        data = data.replaceAll("\\s+", " ");
        while (!(data.length() == 0)){
            String[] splitData = data.split(",");
            switch (type) {
                case "NC" -> dArr.append(dArr.getNotCompatible(), splitData);
                case "UW" -> dArr.append(dArr.getUnwanted(), splitData);
                case "PF" -> dArr.append(dArr.getPreference(), splitData);
                case "Pair" -> dArr.append(dArr.getPair(), splitData);
                default -> dArr.append(dArr.getPartialAssignment(), splitData);
            }
            data = reader.nextLine();
        }
        setSchemeNum(getSchemeNum()+1);
    }

    /*
     * Start of Getters and Setters
     */
    public File getFile(){
        return f;
    }
    public int getSchemeNum(){
        return schemeNum;
    }
    public void setSchemeNum(int val){
        this.schemeNum = val;
    }
    public DataArrays getdArr(){
        return dArr;
    }
    /*
     * End of Getters and Setters
     */
}
