package lambdas;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MapExample
{
    public static void main(String[] s)
    {
        // 将字符串换成大写并用逗号链接起来
        List<String> G7 = Arrays.asList("USA", "Japan", "France", "Germany", "Italy", "U.K.","Canada");
        String G7Countries = G7.stream().map(x -> x.toUpperCase()).collect(Collectors.joining(", "));
        System.out.println(G7Countries);
    }
}
