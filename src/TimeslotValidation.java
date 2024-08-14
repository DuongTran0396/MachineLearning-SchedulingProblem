import java.time.LocalTime;
import java.util.HashMap;

/**
 * This class handles time slot validations
 */
public class TimeslotValidation {
    private final DataArrays dArr;
    private HashMap<String, String[]> tsm;
    public TimeslotValidation(DataArrays dArr){
        this.dArr = dArr;
        tsm = this.dArr.getTimeSlotMap();
//        notOverlap("g1", "p1");
    }

    /**
     * This method converts the given String time slot to Java LocalTime datatype
     * @param tsm Time slot map
     * @param slot a time slot
     * @return a LocalTime datatype time slot
     */
    public LocalTime toTime(HashMap<String, String[]> tsm, String slot){
        String hour = tsm.get(slot)[1].trim();
        if (hour.split(":")[0].length() == 1) hour = "0" + hour;
        return LocalTime.parse(hour);
    }

    /**
     * This utility method helps to check game time overlap base on days of the week.
     * @param gDay game day
     * @param gTime game time
     * @param time time to compare
     * @return true if game time overlaps false otherwise
     */
    public boolean gameOverlap(String gDay, LocalTime gTime, LocalTime time){
        LocalTime gEndTime;
        LocalTime latest;
        // If the game slot is on Monday or Tuesday
        if (gDay.equals("MO")){
            latest = LocalTime.parse("21:00");
            gEndTime = gTime.plusHours(1);
            // Check if time overlaps after the duration of a game (1 hour) or later than the latest time
            return (gEndTime.equals(time) || gEndTime.isBefore(time)) &&
                    (gEndTime.equals(latest) || gEndTime.isBefore(latest));
        }else{
            latest = LocalTime.parse("20:00");
            LocalTime meeting = LocalTime.parse("11:00");
            gEndTime = gTime.plusMinutes(90);
            // Check if time overlaps after the duration of a game (90 minutes) or later than the latest time
            return (gEndTime.equals(time) || gEndTime.isBefore(time)) &&
                    (gEndTime.equals(latest) || gEndTime.isBefore(latest) &&
                            (gEndTime.equals(meeting) || gEndTime.isBefore(meeting)));
        }
    }

    /**
     * This utility method helps to check practice time overlap base on days of the week.
     * @param pDay practice day
     * @param pTime practice time
     * @param time time to compare
     * @return true if practice time overlaps false otherwise
     */
    public boolean practiceOverlap(String pDay, LocalTime pTime, LocalTime time){
        LocalTime pEndTime;
        LocalTime latest;
        // If the practice slot is on Monday, Tuesday or Friday
        if (pDay.equals("MO") || pDay.equals("TU")){
            latest = LocalTime.parse("21:00");
            pEndTime = pTime.plusHours(1);
            // Check if time overlaps after the duration of a practice (1 hour) or later than the latest time
            return (pEndTime.equals(time) || pEndTime.isBefore(time)) &&
                    (pEndTime.equals(latest) || pEndTime.isBefore(latest));
        }else{
            latest = LocalTime.parse("20:00");
            pEndTime = pTime.plusHours(2);
            // Check if time overlaps after the duration of a practice (2 hours) or later than the latest time
            return (pEndTime.equals(time) || pEndTime.isBefore(time)) &&
                    (pEndTime.equals(latest) || pEndTime.isBefore(latest));
        }
    }

    /**
     * This method checks for time slot overlaps
     * @param slot1 time slot
     * @param slot2 time slot
     * @return true if time overlaps false otherwise
     */
    public boolean overlap(String slot1, String slot2){
        String day1 = tsm.get(slot1)[0];
        String day2 = tsm.get(slot2)[0];
        LocalTime t1 = toTime(tsm, slot1);
        LocalTime t2 = toTime(tsm, slot2);
//        System.out.println(day1 + "_" + t1 + " " + day2 + "_" + t2);
        if (sameTimeSlot(slot1, slot2) || (day1.equals(day2) && t1.equals(t2))) return true;
        // if the slot is a game slot
        if (slot1.charAt(0) == 'g' && day1.equals(day2)){
            return gameOverlap(day1, t1, t2);
        }else if (slot1.charAt(0) == 'p' && day1.equals(day2)){
            return practiceOverlap(day1, t1, t2);
        }
        return false;
    }


    public boolean failedEveningDiv(String identifier, String slot) {
        int div = Integer.parseInt(identifier.split(" ")[3]);
        int idx = tsm.get(slot)[1].trim().indexOf(":");
        String time = tsm.get(slot)[1].trim().substring(0,idx);
        //LocalTime t = toTime(tsm, slot);
        if (div >= 9){
            if(Integer.parseInt(time) >= 18){
                return false;
            }else{
                return true;
            }
            //return t.isAfter(LocalTime.parse("18:00"));
        }else{
            return false;
        }

    }


    public boolean validSpecialBookingSlot(String identifier, String slot){
        String league = identifier.split(" ")[0] + " " + identifier.split(" ")[1];
        LocalTime t = toTime(tsm, slot);
        if ((league.equals("CMSA U12T1S") || league.equals("CMSA U13T1S"))
                && !dArr.isGame(league)){
            return t.equals(LocalTime.parse("18:00")) && tsm.get(slot)[0].equals("TU");
        }
        return true;
    }
    /**
     * This method checks if the given two time slots are the same.
     * @param slot1 First time slot
     * @param slot2 Second time slot
     * @return true if the given time slots are the same, false otherwise
     */
    public boolean sameTimeSlot(String slot1, String slot2){
        return slot1.equals(slot2);
    }

    public boolean validTimeSlots(String slot1, String slot2){
        return false;
    }
}