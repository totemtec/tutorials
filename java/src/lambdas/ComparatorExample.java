package lambdas;

import java.util.*;

public class ComparatorExample
{
    public static void main(String[] strings)
    {
        // Java 8之前：
        List<String> list = Arrays.asList("c","b","a");
        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        System.out.println(list);


        //Java 8方式：
        List<String> list2 = Arrays.asList("c","b","d","a");
        Collections.sort(list2, (a, b) -> a.compareTo(b));

        System.out.println(list2);
    }
}
