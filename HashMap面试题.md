> 问：HashMap为什么扩容是2的幂：

hashCode & (n-1)是计算元素放到数组中的位置的计算方式，小n是数组的大小
假设扩容前数组的大小是2的N次方，根据上面的函数，可知index是hashCode的后N位确定的  
扩容后数组变为2的N+1次方，可知index是根据后1+N位确定的，只是比原来多了一位  
如果多加的这一位是0，那元素位置不会变  
如果多加的这一位是1，那元素位置在原来的基础上加上2的N次方。  

比如下面的例子：
```
int hashCode = 0b1101010;
System.out.println(hashCode);     //106
System.out.println(hashCode & (16 - 1));    //10; 开始时，数组的大小是 1<<4
System.out.println("--------");     
System.out.println(hashCode & (32 - 1));  //10 = 10 + 0; 这里假设数组扩容，从 1<<4 变为了 1<<5， 只需要看hashCode的倒数第五位是0还是1
System.out.println("--------");
System.out.println(hashCode & (64 - 1));  // 42 = 10 + 32 这里道理同上，所以在原来的index上加上 1<<5
```
> 问：table[i]位置的链表什么时候会转变成红黑树
当链表中的元素个数大于 TREEIFY_THRESHOLD=8 时，并且当前桶的数量(数组的size)需要大于等于64，如果小于64则会进行扩容操作，
为什么是扩容而不是直接转成红黑树呢？因为当这些元素放在一个数组位置上通过链表保存时，我认为他们只是通过hashCode计算位置得到的值相等，并不是hashCode相等，所以可以通过扩容把他们分散到数组的两个位置上。
