## 常见java8特性举例

> lambda表达式  
```
Arrays.asList("1", "2", "3").forEach(e -> System.out.println(e));
new Thread(() ->
        System.out.println(Thread.currentThread().getName())
).start();
```

> 接口的默认方法与静态方法
```
//接口中定义默认方法
interface Animal {
    default String getName() {
        return "Animal";
    };
}
// 接口中定义静态方法，可以直接调用：System.out.println(Inter_class.getName());
interface Inter_class {
    static String getName() {
        return "inter_class";
    };
}
```

> 方法引用
```
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * java8的新特性: 方法引用
 */
public class MethodReference_3 {
    public static void main(String[] args) {
        List<User> list = initList();

        // 1、构造器方法引用(构造一个User对象)
        User newUser = User.create(User::new);
        newUser.setAge(1);
        newUser.setUserName("new");
        System.out.println("---构造器方法引用---");
        System.out.println(newUser);

        // 1.1、构造器方法引用(构造两个String对象)
        User testUser = newUser;
        System.out.println(testUser.getString_1(String::new));
        System.out.println(testUser.getString_2("hello", String::new));

        // 2、类静态方法引用
        list.forEach(User::updateUsername);
        System.out.println("\r\n---类静态方法引用---");
        list.forEach(System.out::println);

        // 3、类普通方法引用
        list.forEach(User::updateAge);
        System.out.println("\r\n---类普通方法引用---");
        list.forEach(System.out::println);

        // 4、实例方法引用
        User user = new User();
        list.forEach(user::changeAge);
        System.out.println("\r\n---实例方法引用---");
        list.forEach(System.out::println);

    }

    private static List<User> initList() {
        List<User> list = new ArrayList<>();
        list.add(new User("oaby", 10));
        list.add(new User("tom", 20));
        list.add(new User("john", 30));
        list.add(new User("jennis", 40));
        return list;
    }
}


class User {

    private String userName;
    private int age;

    public User() {}
    public User(String userName, int age) {
        this.userName = userName;
        this.age = age;
    }

    public String getString_2(String str, Function<String, String> function) {
        return function.apply(str) + " world";
    }

    public String getString_1(Supplier<String> supplier) {
        return supplier.get() + " XXXX";
    }

    static User create(Supplier<User> supplier) {
        return supplier.get();
    }

    public static void updateUsername(User user) {
        user.setUserName(user.getUserName() + " updated.");
    }

    public void updateAge() {
        this.setAge(this.getAge() + 10);
    }

    public void changeAge(User user) {
        user.setAge(user.getAge() + 10);
    }
    
    // getter and setter...
    
    @Override
    // toString method...
}
```

> Optional的使用
```
        System.out.println(Optional.ofNullable(null).orElse("defalut"));

        Map<String, List<String>> map = new HashMap<>();
        map.put("JJ", Arrays.asList("11", "22", "33"));
        map.put("HH", Arrays.asList("444", "555", "666", "777"));
        Optional<Map> optionalMap = Optional.ofNullable(map);
        // map 是将一个Option<T>转换成另一个Optional<R>
        System.out.println(optionalMap.map(value -> value.get("HH")).orElse("List is empty"));
        
        // filter 就是过滤判断
        System.out.println(Optional.ofNullable(Arrays.asList("AA", "BB", "CC")).filter(value -> value.size() > 1).get());
```

> Stream的使用
```
List<String> list = Arrays.asList("11", "22", "33", "33");

// 过滤元素
Optional<String> option_1 = list.stream().filter(val -> val.equals("22")).findAny();
System.out.println(option_1.get());

// reduce 可以节点之间的操作，比如：计数累计
Optional<String> option_2 = list.stream().reduce((val_1, val_2) -> val_1 + "--" + val_2);
System.out.println(option_2.get());

// 去重
System.out.println(list.stream().distinct().collect(Collectors.toList()));

// 跳过多个元素
System.out.println(list.stream().skip(2).collect(Collectors.toList()));
```
