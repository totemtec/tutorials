package lambdas;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FilterExample
{
    public static void main(String[] strings)
    {
        // 创建一个字符串列表，每个字符串长度大于2
        List<String> strList = Arrays.asList("abc", "cd", "dea", "ab", "shenme");
        List<String> filtered = strList.stream().filter(x -> x.length()> 2).collect(Collectors.toList());
        System.out.printf("Original List : %s, filtered list : %s %n", strList, filtered);

        //关于 filter() 方法有个常见误解。在现实生活中，做过滤的时候，通常会丢弃部分，但使用filter()方法则是获得一个新的列表，且其每个元素符合过滤原则。
    }
}
