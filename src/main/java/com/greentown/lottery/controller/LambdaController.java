package com.greentown.lottery.controller;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Target;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author jairy
 * @date 2019/5/6
 */
public class LambdaController {
    //-------------------------------  ->  -----------------------------------------

    public void collectionsDemo() {
        List<String> names = Arrays.asList("peter", "anna", "mike", "xenia");

        Collections.sort(names, new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                return b.compareTo(a);
            }
        });

        Collections.sort(names, (String a, String b) -> {
            return b.compareTo(a);
        });

        Collections.sort(names, (String a, String b) -> b.compareTo(a));

        names.sort((a, b) -> b.compareTo(a));
    }

    //-------------------------------  ::  -----------------------------------------

    // 只有那些函数式接口（Functional Interface）才能缩写成 Lambda 表示式。
    @FunctionalInterface
    interface Converter<F, T> {
        T convert(F from);
    }
    /**
     * 引用静态方法
     */
    public void functionalInterfaceDemo1() {
        // 只包含一个抽象方法的声明。
        Converter<String, Integer> converter = (from) -> Integer.valueOf(from);
        Integer converted = converter.convert("123");
        System.out.println(converted);    // 123
    }
    public void functionalInterfaceDemo2() {
        Converter<String, Integer> converter = Integer::valueOf;
        Integer converted = converter.convert("123");
        System.out.println(converted);   // 123
    }

    /**
     * 引用普通方法
     */
    class Something {
        String startsWith(String s) {
            return String.valueOf(s.charAt(0));
        }
    }
    public void functionalInterfaceDemo3() {
        Something something = new Something();
        Converter<String, String> converter = something::startsWith;
        String converted = converter.convert("Java");
        System.out.println(converted);    // "J"
    }

//    /**
//     * customerDTOS.forEach(customerRO -> {
//     *   List<Long> houseIds = houseDOS.stream().map(HouseDTO::getId).collect(Collectors.toList());
//     *   bindHouse(houseIds, customerRO.getId());
//     * });
//     */

    /**
     * 引用类的构造器
     */
    class Person {
        String firstName;
        String lastName;

        Person() {}

        Person(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }
    // Person 工厂
    interface PersonFactory<P extends Person> {
        P create(String firstName, String lastName);
    }
    // 直接引用 Person 构造器
    PersonFactory<Person> personFactory = Person::new;
    Person person = personFactory.create("Peter", "Parker");

    //--------------------------------- functionalInterface ---------------------------------------

    /**
     * 访问外部的 final 类型变量
     *
     */
    public void functionalInterfaceDemo4() {
        final int num = 1;
        Converter<Integer, String> stringConverter = (from) -> String.valueOf(from + num);

        stringConverter.convert(2);     // 3
    }
    //不必显式声明 num 变量为 final 类型
    public void functionalInterfaceDemo5() {
        int num = 1;
        Converter<Integer, String> stringConverter =
                (from) -> String.valueOf(from + num);

        stringConverter.convert(2);     // 3
    }

//    //num 变量必须为隐式的 final 类型
//    //何为隐式的 final 呢？到编译期为止，num 对象是不能被改变的
//    public void functionalInterfaceDemo6() {
//        int num = 1;
//        Converter<Integer, String> stringConverter =
//                (from) -> String.valueOf(from + num);
//        num = 3;
//    }
//    //在 lambda 表达式内部改变 num 值同样编译不通过
//    public void functionalInterfaceDemo7() {
//        int num = 1;
//        Converter<Integer, String> converter = (from) -> {
//            String value = String.valueOf(from + num);
//            num = 3;
//            return value;
//        };
//    }

    //------------------------------------------------------------------------
    //访问成员变量和静态变量
    static int outerStaticNum;
    // 成员变量
    int outerNum;

    void testScopes() {
        Converter<Integer, String> stringConverter1 = (from) -> {
            // 对成员变量赋值
            outerNum = 23;
            return String.valueOf(from);
        };

        Converter<Integer, String> stringConverter2 = (from) -> {
            // 对静态变量赋值
            outerStaticNum = 72;
            return String.valueOf(from);
        };
    }

    //访问接口的默认方法
    @FunctionalInterface
    interface Formula {
        // 计算
        double calculate(int a);

        // 求平方根
        default double sqrtaaa(int a) {
            return Math.sqrt(a);
        }
    }
//    public void functionalInterfaceDemo8() {
//        Formula formula = new Formula() {
//            @Override
//            public double calculate(int a) {
//                return sqrtaaa(a * 100);
//            }
//        };
//
//        Formula formula1 = (a) -> sqrtaaa(a * 100);
//    }

    //--------------------------------- Predicate ---------------------------------------

    /**
     * Predicate 断言
     *
     * and, or, negate
     */
    public void predicateDemo() {
        Predicate<String> predicate = (s) -> s.length() > 0;
        predicate.test("foo");              // true
        predicate.negate().test("foo");     // false

        Predicate<Boolean> nonNull = Objects::nonNull;
        Predicate<Boolean> isNull = Objects::isNull;

        Predicate<String> isEmpty = String::isEmpty;
        Predicate<String> isNotEmpty = isEmpty.negate();
    }

    //------------------------------------------------------------------------

    /**
     * 组合,链行处理(apply, compose, andThen)：
     */
    public static void functionDemo() {
        Function<String, Integer> toInteger = Integer::valueOf;
        Function<String, String> backToString = toInteger.andThen(String::valueOf);

        Integer apply = toInteger.apply("123");
        String apply1 = backToString.apply("123");
    }

//    class Person {
//        String firstName;
//        String lastName;
//
//        Person() {}
//
//        Person(String firstName, String lastName) {
//            this.firstName = firstName;
//            this.lastName = lastName;
//        }
//    }
//    /**
//     * Supplier 生产者
//     *
//     * 不接受入参，直接为我们生产一个指定的结果
//     */
//    public static void supplierDemo() {
//        Supplier<Person> personSupplier = Person::new;
//        personSupplier.get();   // new Person
//    }
//    /**
//     * Consumer 消费者
//     *
//     * 需要提供入参，用来被消费
//     */
//    public static void consumerDemo() {
//        Consumer<Person> greeter = (a) -> System.out.println("Hello, " + a.firstName);
//        greeter.accept(new Person("Luke", "Skywalker"));
//    }
//
//    /**
//     * Comparator 比较器
//     */
//    public static void comparatorDemo() {
//        Comparator<Person> comparator = (p1, p2) -> p1.firstName.compareTo(p2.firstName);
//
//        Person p1 = new Person("John", "Doe");
//        Person p2 = new Person("Alice", "Wonderland");
//
//        comparator.compare(p1, p2);             // > 0
//        comparator.reversed().compare(p1, p2);  // < 0
//    }

    //-------------------------------- Optional ----------------------------------------

    /**
     * Optional 它不是一个函数式接口
     * 设计它的目的是为了防止空指针异常（NullPointerException）
     *
     * （可能是 null, 也有可能非 null）的容器
     *
     * 这个方法返回的对象可能是空，也有可能非空的时候，就可考虑用 Optional 来包装它，是在 Java 8 被推荐使用的做法。
     */
    public static void optionalDemo() {
        Optional<String> optional = Optional.of("bam");

        optional.isPresent();           // true
        optional.get();                 // "bam"
        optional.orElse("fallback");    // "bam"

        optional.ifPresent((s) -> System.out.println(s.charAt(0)));     // "b"
    }

    //------------------------------ Stream 流 ------------------------------------------

    /**
     * java.util.Stream 对一个包含一个或多个元素的集合做各种操作。
     *
     * 这些操作可能是 中间操作 亦或是 终端操作。 终端操作会返回一个结果，而中间操作会返回一个 Stream 流。
     *
     * 你只能对实现了 java.util.Collection 接口的类做流的操作。
     * Map 不支持 Stream 流。
     *
     * Stream 流支持同步执行，也支持并发执行。
     */
    public static void streamDemo() {
        List<String> stringCollection = new ArrayList<>();
        stringCollection.add("ddd2");
        stringCollection.add("aaa2");
        stringCollection.add("bbb1");
        stringCollection.add("aaa1");
        stringCollection.add("bbb3");
        stringCollection.add("ccc");
        stringCollection.add("bbb2");
        stringCollection.add("ddd1");

        /**
         * Filter 过滤
         * Filter 的入参是一个 Predicate（Predicate 是一个断言的中间操作）
         * 它能够帮我们筛选出我们需要的集合元素。它的返参同样 是一个 Stream 流
         * 我们可以通过 foreach 终端操作，来打印被筛选的元素
         *
         * foreach 是一个终端操作，它的返参是 void, 我们无法对其再次进行流操作。
         */
        stringCollection
                .stream()
                .filter((s) -> s.startsWith("a"))
                .forEach(System.out::println);
        // "aaa2", "aaa1"

        /**
         * Sorted 排序
         *
         * 一个中间操作，它的返参是一个 Stream 流。
         * 我们可以传入一个 Comparator 用来自定义排序，如果不传，则使用默认的排序规则。
         *
         * sorted 不会对 stringCollection 做出任何改变，stringCollection 还是原有的那些个元素，且顺序不变
         */
        stringCollection
                .stream()
                .sorted()
                .filter((s) -> s.startsWith("a"))
                .forEach(System.out::println);

        // "aaa1", "aaa2"
        System.out.println(stringCollection);
        // ddd2, aaa2, bbb1, aaa1, bbb3, ccc, bbb2, ddd1

        /**
         * Map 转换
         * 中间操作 Map 能够帮助我们将 List 中的每一个元素做功能处理。
         *
         * 可以做对象之间的转换，业务中比较常用的是将 DO（数据库对象） 转换成 BO（业务对象） 。
         */
        stringCollection
                .stream()
                .map(String::toUpperCase)
                .sorted((a, b) -> b.compareTo(a))
                .forEach(System.out::println);
        // "DDD2", "DDD1", "CCC", "BBB3", "BBB2", "AAA2", "AAA1"

        /**
         * Match 匹配
         * match 用来做匹配操作，它的返回值是一个 boolean 类型。
         * 通过 match, 我们可以方便的验证一个 list 中是否存在某个类型的元素。
         */
        // 验证 list 中 string 是否有以 a 开头的, 匹配到第一个，即返回 true
        boolean anyStartsWithA =
                stringCollection
                        .stream()
                        .anyMatch((s) -> s.startsWith("a"));
        System.out.println(anyStartsWithA);      // true

        // 验证 list 中 string 是否都是以 a 开头的
        boolean allStartsWithA =
                stringCollection
                        .stream()
                        .allMatch((s) -> s.startsWith("a"));
        System.out.println(allStartsWithA);      // false

        // 验证 list 中 string 是否都不是以 z 开头的,
        boolean noneStartsWithZ =
                stringCollection
                        .stream()
                        .noneMatch((s) -> s.startsWith("z"));
        System.out.println(noneStartsWithZ);      // true

        /**
         * Count 计数
         * count 是一个终端操作，它能够统计 stream 流中的元素总数，返回值是 long 类型。
         */
        // 先对 list 中字符串开头为 b 进行过滤，让后统计数量
        long startsWithB =
                stringCollection
                        .stream()
                        .filter((s) -> s.startsWith("b"))
                        .count();
        System.out.println(startsWithB);    // 3

        /**
         * Reduce 中文翻译为：减少、缩小。
         * 通过入参的 Function，我们能够将 list 归约成一个值。它的返回类型是 Optional 类型。
         */
        Optional<String> reduced =
                stringCollection
                        .stream()
                        .sorted()
                        .reduce((s1, s2) -> s1 + "#" + s2);
        reduced.ifPresent(System.out::println);
        // "aaa1#aaa2#bbb1#bbb2#bbb3#ccc#ddd1#ddd2"
    }

    //------------------------------Parallel Stream 流 ------------------------------------------

    /**
     * stream 流是支持顺序和并行的。
     * 顺序流操作是单线程操作;
     * 并行流是通过多线程来处理的，能够充分利用物理机 多核 CPU 的优势，同时处理速度更快。
     */
    public static void parallelStreamDemo() {
        int max = 1000000;
        List<String> values = new ArrayList<>(max);
        for (int i = 0; i < max; i++) {
            UUID uuid = UUID.randomUUID();
            values.add(uuid.toString());
        }

        /**
         * 顺序流排序
         */
        // 纳秒
        long t0 = System.nanoTime();

            long count = values.stream().sorted().count();

        System.out.println(count);
        long t1 = System.nanoTime();
        // 纳秒转微秒
        long millis = TimeUnit.NANOSECONDS.toMillis(t1 - t0);
        System.out.println(String.format("顺序流排序耗时: %d ms", millis));
        // 顺序流排序耗时: 899 ms

        /**
         * 并行流排序
         */
        // 纳秒
        long pst0 = System.nanoTime();

            long pscount = values.parallelStream().sorted().count();

        System.out.println(pscount);
        long pst1 = System.nanoTime();
        // 纳秒转微秒
        long psmillis = TimeUnit.NANOSECONDS.toMillis(pst1 - pst0);
        System.out.println(String.format("并行流排序耗时: %d ms", psmillis));
        // 并行流排序耗时: 472 ms
    }

    //------------------------------ Map ------------------------------------------

    /**
     * Map 集合
     *
     * Map 是不支持 Stream 流的
     * （因为 Map 接口并没有像 Collection 接口那样，定义了 stream() 方法。
     *   但是，我们可以对其 key, values, entry 使用流操作，如 map.keySet().stream(), map.values().stream() 和 map.entrySet().stream().
     *
     */
    public static void mapDemo() {
        Map<Integer, String> map = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            // 与老版不同的是，putIfAbent() 方法在 put 之前，
            // 会判断 key 是否已经存在，存在则直接返回 value, 否则 put, 再返回 value
            map.putIfAbsent(i, "val" + i);
        }

        // forEach 可以很方便地对 map 进行遍历操作
        map.forEach((key, value) -> System.out.println(value));

        /**
         * 对某个 key 的值做相关操作
         */
        // computeIfPresent(), 当 key 存在时，才会做相关处理
        // 如下：对 key 为 3 的值，内部会先判断值是否存在，存在，则做 value + key 的拼接操作
        map.computeIfPresent(3, (num, val) -> val + num);
        map.get(3);             // val33

        // 先判断 key 为 9 的元素是否存在，存在，则做删除操作
        map.computeIfPresent(9, (num, val) -> null);
        map.containsKey(9);     // false

        // computeIfAbsent(), 当 key 不存在时，才会做相关处理
        // 如下：先判断 key 为 23 的元素是否存在，不存在，则添加
        map.computeIfAbsent(23, num -> "val" + num);
        map.containsKey(23);    // true

        // 先判断 key 为 3 的元素是否存在，存在，则不做任何处理
        map.computeIfAbsent(3, num -> "bam");
        map.get(3);             // val33

        /**
         * 删除操作
         * 只有当给定的 key 和 value 完全匹配时，才会执行删除操作
         */
        map.remove(3, "val3");
        map.get(3);             // val33

        map.remove(3, "val33");
        map.get(3);             // null

        /**
         * 添加方法
         * JDK 8 中提供了带有默认值的 getOrDefault() 方法
         */
        // 若 key 42 不存在，则返回 not found
        map.getOrDefault(42, "not found");  // not found

        // merge 方法，会先判断进行合并的 key 是否存在，不存在，则会添加元素
        map.merge(9, "val9", (value, newValue) -> value.concat(newValue));
        map.get(9);             // val9

        // 若 key 的元素存在，则对 value 执行拼接操作
        map.merge(9, "concat", (value, newValue) -> value.concat(newValue));
        map.get(9);             // val9concat
    }

    //------------------------------ 时间日期 API ------------------------------------------

    /**
     * 新的日期 API
     *
     * Java 8 中在包 java.time 下添加了新的日期 API.
     * 它和 Joda-Time 库相似，但又不完全相同。
     */
    public static void timeDemo(){
        /**
         * Clock
         *
         * Clock 提供对当前日期和时间的访问。替代 System.currentTimeMillis() 方法。
         * 另外，通过 clock.instant() 能够获取一个 instant 实例，此实例能够方便地转换成老版本中的 java.util.Date 对象。
         */
        Clock clock = Clock.systemDefaultZone();
        long millis = clock.millis();

        Instant instant = clock.instant();
        Date legacyDate = Date.from(instant);   // 老版本 java.util.Date

        /**
         * Timezones 时区
         *
         * ZoneId 代表时区类。
         * 通过静态工厂方法方便地获取它，入参我们可以传入某个时区编码。
         * 另外，时区类还定义了一个偏移量，用来在当前时刻或某时间 与目标时区时间之间进行转换。
         */
        System.out.println(ZoneId.getAvailableZoneIds());
        // prints all available timezone ids

        ZoneId zone1 = ZoneId.of("Europe/Berlin");
        ZoneId zone2 = ZoneId.of("Brazil/East");
        System.out.println(zone1.getRules());
        System.out.println(zone2.getRules());
        // ZoneRules[currentStandardOffset=+01:00]
        // ZoneRules[currentStandardOffset=-03:00]

        /**
         * LocalTime
         *
         * LocalTime 表示一个没有指定时区的时间类，例如，10 p.m.或者 17：30:15，
         * 下面示例代码中，将会使用上面创建的 时区对象创建两个 LocalTime。然后我们会比较两个时间，并计算它们之间的小时和分钟的不同。
         */
        LocalTime now1 = LocalTime.now(zone1);
        LocalTime now2 = LocalTime.now(zone2);
        System.out.println(now1.isBefore(now2));  // false

        long hoursBetween = ChronoUnit.HOURS.between(now1, now2);
        long minutesBetween = ChronoUnit.MINUTES.between(now1, now2);
        System.out.println(hoursBetween);       // -4
        System.out.println(minutesBetween);     // -299

        /**
         * LocalTime 提供多个静态工厂方法，目的是为了简化对时间对象实例的创建和操作，包括对时间字符串进行解析的操作等。
         */
        LocalTime late = LocalTime.of(23, 59, 59);
        System.out.println(late);       // 23:59:59

        DateTimeFormatter germanFormatter =
                DateTimeFormatter
                        .ofLocalizedTime(FormatStyle.SHORT)
                        .withLocale(Locale.GERMAN);
        LocalTime leetTime = LocalTime.parse("13:37", germanFormatter);
        System.out.println(leetTime);   // 13:37
    }

    /**
     * LocalDate 是一个日期对象，例如：2014-03-11。它和 LocalTime 一样是个 final 类型对象。
     */
    public static void localDateDemo(){
        /**
         * LocalDate, LocalTime, 因为是 final 类型的对象，每一次操作都会返回一个新的时间对象。
         */
        LocalDate today = LocalDate.now();
        // 今天加一天
        LocalDate tomorrow = today.plus(1, ChronoUnit.DAYS);
        // 明天减两天
        LocalDate yesterday = tomorrow.minusDays(2);

        // 2014 年七月的第四天
        LocalDate independenceDay = LocalDate.of(2014, Month.JULY, 4);
        DayOfWeek dayOfWeek = independenceDay.getDayOfWeek();
        System.out.println(dayOfWeek);    // 星期五

        /**
         * 直接解析日期字符串，生成 LocalDate 实例。（和 LocalTime 操作一样简单）
         */
        DateTimeFormatter germanFormatter =
                DateTimeFormatter
                        .ofLocalizedDate(FormatStyle.MEDIUM)
                        .withLocale(Locale.GERMAN);

        LocalDate xmas = LocalDate.parse("24.12.2014", germanFormatter);
        System.out.println(xmas);   // 2014-12-24
    }

    /**
     * LocalDateTime 是一个日期-时间对象。你也可以将其看成是 LocalDate 和 LocalTime 的结合体。操作上，也大致相同。
     *
     * LocalDateTime 同样是一个 final 类型对象。
     */
    public static void localDateTimeDemo(){
        LocalDateTime sylvester = LocalDateTime.of(2014, Month.DECEMBER, 31, 23, 59, 59);

        DayOfWeek dayOfWeek = sylvester.getDayOfWeek();
        System.out.println(dayOfWeek);      // 星期三

        Month month = sylvester.getMonth();
        System.out.println(month);          // 十二月

        // 获取改时间是该天中的第几分钟
        long minuteOfDay = sylvester.getLong(ChronoField.MINUTE_OF_DAY);
        System.out.println(minuteOfDay);    // 1439


        /**
         * 如果再加上的时区信息，LocalDateTime 还能够被转换成 Instance 实例。Instance 能够被转换成老版本中 java.util.Date 对象。
         */
        Instant instant = sylvester
                .atZone(ZoneId.systemDefault())
                .toInstant();

        Date legacyDate = Date.from(instant);
        System.out.println(legacyDate);     // Wed Dec 31 23:59:59 CET 2014

        /**
         * 格式化 LocalDateTime 对象就和格式化 LocalDate 或者 LocalTime 一样。除了使用预定义的格式以外，也可以自定义格式化输出。
         */
        DateTimeFormatter formatter =
                DateTimeFormatter
                        .ofPattern("MMM dd, yyyy - HH:mm");

        LocalDateTime parsed = LocalDateTime.parse("Nov 03, 2014 - 07:13", formatter);
        String string = formatter.format(parsed);
        System.out.println(string);     // Nov 03, 2014 - 07:13

        //注意：和 java.text.NumberFormat 不同，新的 DateTimeFormatter 类是 final 类型的，同时也是线程安全的。更多细节请查看这里

    }

    public static void main(String[] args) {
        localDateTimeDemo();
    }

    //------------------------------ Annotations ------------------------------------------

    /**
     * 注解是可以重复的
     *
     * Java 8 中，通过 @Repeatable，允许我们对同一个类使用多重注解：
     */

    @interface Hints {
        Hint[] value();
    }
    @Repeatable(Hints.class)
    @interface Hint {
        String value();
    }

//    /**
//     * 第一种形态：使用注解容器（老方法）
//     */
//    @Hints({@Hint("hint1"), @Hint("hint2")})
//    class Person {}
//    /**
//     * 第二种形态：使用可重复注解（新方法）
//     *
//     * Java 编译器能够在内部自动对 @Hint 进行设置。这对于需要通过反射来读取注解信息时，是非常重要的。
//     */
//    @Hint("hint1")
//    @Hint("hint2")
//    class Person {}
//
//    public static void annotationsDemo(){
//        Hint hint = Person.class.getAnnotation(Hint.class);
//        System.out.println(hint);                   // null
//
//        Hints hints1 = Person.class.getAnnotation(Hints.class);
//        System.out.println(hints1.value().length);  // 2
//
//        Hint[] hints2 = Person.class.getAnnotationsByType(Hint.class);
//        System.out.println(hints2.length);          // 2
//
//    }

    /**
     * 尽管我们绝对不会在 Person 类上声明 @Hints 注解，但是它的信息仍然是可以通过 getAnnotation(Hints.class) 来读取的。
     * 并且，getAnnotationsByType 方法会更方便，因为它赋予了所有 @Hints 注解标注的方法直接的访问权限。
     */
    @Target({ElementType.TYPE_PARAMETER, ElementType.TYPE_USE})
    @interface MyAnnotation {}

}
