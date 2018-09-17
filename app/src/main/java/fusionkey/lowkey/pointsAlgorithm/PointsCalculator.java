package fusionkey.lowkey.pointsAlgorithm;

import java.util.ArrayList;

/**
 *  @author Sandru Sebastian
 *  @date 12 sep 18
 *  @To-DO under dev
 */
public class PointsCalculator {

    private static double adRvenue=0.05;
    private static double userPercentage=0.25;
    private static double payPerPoint=0.005;
    /**
     * I used the Harmonic Means because in a set of non-equal numbers, it will strongly tend toward the least numbers of the set
     * So, when we measure the string value , it will always tend to be as valuable as the weakest part of it.
     */
    public static double calculateStringsValue(int stringSent, ArrayList<Integer> eachSize, int timeSpent) {
        return (2/(1/getStringMean(stringSent,eachSize) + 1/timeSpent))/10;
    }
    /**
     * Most fair to use Arithmetic Mean to measure the Mean of : how many strings you have sent and how big they are
     */
    private static double getStringMean(int stringSent,ArrayList<Integer> eachSize){

        int sum=0;
        for(int i : eachSize)
            sum+=i;
        if(sum==stringSent) //if is spam 1 string with 1 size
            return 0.0;
        else
        return (stringSent+sum)/(eachSize.size());
    }
    public static double calculatePointsForMoney(double points){
        return points*payPerPoint;
    }
}
