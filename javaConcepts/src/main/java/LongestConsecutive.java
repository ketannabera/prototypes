import java.util.HashSet;

public class LongestConsecutive {

    public static void main(String args[]){
        longestConsecutive(new int[]{0,-1});
    }
    public static int longestConsecutive(int[] nums) {
        HashSet<Integer> uniqueNums = new HashSet<>();
        for (int num : nums) {
            uniqueNums.add(num);
        }
        int maxLen = 1;
        int len = 1;
        for(Integer i : uniqueNums){
            if (uniqueNums.contains(i+1)){
                len+=1;
                continue;
            }
            maxLen = Math.max(maxLen,len);
            len=1;
        }
        return Math.max(maxLen,len);
    }
}

