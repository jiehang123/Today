# 本文主题：讲解java中常见的正则表达式

\b：退格
\n：换行
\t：制表符，相当于tab键
\r：回车
\\：表示反斜杠
\'：表示单引号
\"：表示双引号

* : 零次或多次匹配前面的字符或子表达式。例如，zo* 匹配"z"和"zoo"。* 等效于 {0,}。
+ : 一次或多次匹配前面的字符或子表达式。例如，"zo+"与"zo"和"zoo"匹配，但与"z"不匹配。+ 等效于 {1,}。
? : 零次或一次匹配前面的字符或子表达式。例如，"do(es)?"匹配"do"或"does"中的"do"。? 等效于 {0,1}。
{n} : n 是非负整数。正好匹配 n 次。例如，"o{2}"与"Bob"中的"o"不匹配，但与"food"中的两个"o"匹配。
{n,} : n 是非负整数。至少匹配 n 次。例如，"o{2,}"不匹配"Bob"中的"o"，而匹配"foooood"中的所有 o。"o{1,}"等效于"o+"。"o{0,}"等效于"o*"。
{n,m} : M 和 n 是非负整数，其中 n <= m。匹配至少 n 次，至多 m 次。例如，"o{1,3}"匹配"fooooood"中的头三个 o。'o{0,1}' 等效于 'o?'。注意：您不能将空格插入逗号和数字之间。
. : 匹配除"\r\n"之外的任何单个字符。若要匹配包括"\r\n"在内的任意字符，请使用诸如"[\s\S]"之类的模式。

x|y : 匹配 x 或 y。例如，'z|food' 匹配"z"或"food"。'(z|f)ood' 匹配"zood"或"food"。
[xyz] : 字符集。匹配包含的任一字符。例如，"[abc]"匹配"plain"中的"a"。
[^xyz] : 反向字符集。匹配未包含的任何字符。例如，"[^abc]"匹配"plain"中"p"，"l"，"i"，"n"。
[a-z] : 字符范围。匹配指定范围内的任何字符。例如，"[a-z]"匹配"a"到"z"范围内的任何小写字母。
[^a-z] : 反向范围字符。匹配不在指定的范围内的任何字符。例如，"[^a-z]"匹配任何不在"a"到"z"范围内的任何字符

\s : 匹配任何空白字符，包括空格、制表符、换页符等。与 [ \f\n\r\t\v] 等效。
\S : 匹配任何非空白字符。与 [^ \f\n\r\t\v] 等效。
\d : 数字字符匹配。等效于 [0-9]。
\D : 非数字字符匹配。等效于 [^0-9]。
\w : 匹配任何字类字符，包括下划线。与"[A-Za-z0-9_]"等效。
\W : 与任何非单词字符匹配。与"[^A-Za-z0-9_]"等效。
\b : 匹配一个字边界，即字与空格间的位置。例如，"er\b"匹配"never"中的"er"，但不匹配"verb"中的"er"。
\B : 非字边界匹配。"er\B"匹配"verb"中的"er"，但不匹配"never"中的"er"。


正则表达式基本使用方法：
```
public static void main(String[] args) {
        Pattern p = Pattern.compile("\\d+");

        Matcher m = p.matcher("aaa2223bb");
        System.out.println(m.find());//匹配2223
        System.out.println(m.start());//返回3
        System.out.println(m.end());//返回7,返回的是2223后的索引号
        System.out.println(m.group());//返回2223
        System.out.println("------");
        Matcher m2 = p.matcher("2223bb");
        System.out.println(m2.lookingAt());   //匹配2223
        System.out.println(m2.start());   //返回0,由于lookingAt()只能匹配前面的字符串,所以当使用lookingAt()匹配时,start()方法总是返回0
        System.out.println(m2.end());   //返回4
        System.out.println(m2.group());   //返回2223
        System.out.println("------");
        Matcher m3 = p.matcher("3453454");
        System.out.println(m3.matches());   //匹配整个字符串
        System.out.println(m3.start());   //返回0,原因相信大家也清楚了
        System.out.println(m3.end());   //返回7,原因相信大家也清楚了,因为matches()需要匹配所有字符串
        System.out.println(m3.group());   //返回3453454

        System.out.println("##############");
        Pattern pattern = Pattern.compile("([a-z]+)(\\d+)");
        Matcher matcher = pattern.matcher("aaa2223bb");
        System.out.println(matcher.find());//匹配aaa2223
        System.out.println(matcher.groupCount());   //返回2,因为有2组
        System.out.println(matcher.start(1));   //返回0 返回第一组匹配到的子字符串在字符串中的索引号
        System.out.println(matcher.start(2));   //返回3
        System.out.println(matcher.end(1));   //返回3 返回第一组匹配到的子字符串的最后一个字符在字符串中的索引位置.
        System.out.println(matcher.end(2));   //返回7
        System.out.println(matcher.group(1));   //返回aaa,返回第一组匹配到的子字符串
        System.out.println(matcher.group(2));   //返回2223,返回第二组匹配到的子字符串
        System.out.println(matcher.group());    //返回 aaa2223

    }
```



常用正则表达式：

ip: ip地址中每个数字都不能大于255，另外，ip地址也是可以包含前导0的，所以01.02.03.04也算IP地址
正则表达式：
```
^(([01]?\d\d?|2[0-4]\d|25[0-5])\.){3}([01]?\d\d?|2[0-4]\d|25[0-5])$
```
