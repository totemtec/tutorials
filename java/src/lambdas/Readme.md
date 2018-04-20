原文：
> http://www.importnew.com/16436.html

1）lambda表达式仅能放入如下代码：预定义使用了 @Functional 注释的函数式接口，自带一个抽象函数的方法，或者SAM（Single Abstract Method 单个抽象方法）类型。这些称为lambda表达式的目标类型，可以用作返回类型，或lambda目标代码的参数。例如，若一个方法接收Runnable、Comparable或者 Callable 接口，都有单个抽象方法，可以传入lambda表达式。类似的，如果一个方法接受声明于 java.util.function 包内的接口，例如 Predicate、Function、Consumer 或 Supplier，那么可以向其传lambda表达式。

2）lambda表达式内可以使用方法引用，仅当该方法不修改lambda表达式提供的参数。本例中的lambda表达式可以换为方法引用，因为这仅是一个参数相同的简单方法调用。

> list.forEach(n -> System.out.println(n));   
> list.forEach(System.out::println);  // 使用方法引用  

然而，若对参数有任何修改，则不能使用方法引用，而需键入完整地lambda表达式，如下所示：

> list.forEach((String s) -> System.out.println("*" + s + "*"));  

事实上，可以省略这里的lambda参数的类型声明，编译器可以从列表的类属性推测出来。

3）lambda内部可以使用静态、非静态和局部变量，这称为lambda内的变量捕获。

4）Lambda表达式在Java中又称为闭包或匿名函数，所以如果有同事把它叫闭包的时候，不用惊讶。

5）Lambda方法在编译器内部被翻译成私有方法，并派发 invokedynamic 字节码指令来进行调用。可以使用JDK中的 javap 工具来反编译class文件。使用 javap -p 或 javap -c -v 命令来看一看lambda表达式生成的字节码。大致应该长这样：

> private static java.lang.Object lambda$0(java.lang.String);

6）lambda表达式有个限制，那就是只能引用 final 或 final 局部变量，这就是说不能在lambda内部修改定义在域外的变量。

> List<Integer> primes = Arrays.asList(new Integer[]{2, 3,5,7});  
> int factor = 2;  
> primes.forEach(element -> { factor++; });  


> Compile time error : "local variables referenced from a lambda expression must be final or effectively final"

另外，只是访问它而不作修改是可以的，如下所示：

> List<Integer> primes = Arrays.asList(new Integer[]{2, 3,5,7});  
> int factor = 2;  
> primes.forEach(element -> { System.out.println(factor*element); });

输出：

> 1  
> 2  
> 3  
> 4  
> 4  
> 6  
> 10  
> 14  

因此，它看起来更像不可变闭包，类似于Python。