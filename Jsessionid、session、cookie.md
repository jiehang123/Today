### JSESSIONID、SESSION、cookie

所谓session可以这样理解：当与服务端进行会话时，比如说登陆成功后，服务端会为用户开壁一块内存区间，用以存放用户这次会话的一些内容，比如说用户名之类的。那么就需要一个东西来标志这个内存区间是你的而不是别人的，这个东西就是session id(jsessionid只是tomcat中对session id的叫法，在其它容器里面，不一定就是叫jsessionid了。),而这个内存区间你可以理解为session。
然后，服务器会将这个session id发回给你的浏览器，放入你的浏览器的cookies中（这个cookies是内存cookies，跟一般的不一样，它会随着浏览器的关闭而消失）。
之后，只有你浏览器没有关闭，你每向服务器发请求，服务器就会从你发送过来的cookies中拿出这个session id,然后根据这个session id到相应的内存中取你之前存放的数据。
但是，如果你退出登陆了，服务器会清掉属于你的内存区域，所以你再登的话，会产生一个新的session了。
  
这是一个保险措施 因为Session默认是需要Cookie支持的，但有些客户浏览器是关闭Cookie的【而jsessionid是存储在Cookie中的，
如果禁用Cookie的话，也就是说服务器那边得不到jsessionid，这样也就没法根据jsessionid获得对应的session了，获得不了session就
得不到session中存储的数据了。】这个时候就需要在URL中指定服务器上的session标识,也就是类似于“jsessionid=5F4771183629C9834F8382E23BE13C4C” 这种格式。用一个方法(忘了方法的名字)处理URL串就可以得到
这个东西，这个方法会判断你的浏览器是否开启了Cookie,如果他认为应该加他就会加上去。
session是有一定作用域的，而且是有时间限制的。
  
jsessionid是服务器那边生成的，因为cookie是服务器那边送到客户端的信息。不管能不能修改jsessionid，都不应该修改，如果你修改了，这就失去了jessionid的自身意义了，你修改的话，你让服务器那边如何找到对应的session?找不到的话，你存放在那个session中的数据不是取不到了吗？
登陆然后退出，我认为会重新生成一个jsessionid。因为退出的话，application作用域的数据都会丢失，更何况这个比它作用域还小的session？既然session都消失了，这个jsessionid有什么用？

在一些投票之类的场合，我们往往因为公平的原则要求每人只能投一票，在一些WEB开发中也有类似的情况，这时候我们通常会使用COOKIE来实现，例如如下的代码：
```
<% cookie[]cookies = request.getCookies();
if (cookies.lenght == 0 || cookies == null)
  doStuffForNewbie();
//没有访问过 
} else
{
  doStuffForReturnVisitor(); //已经访问过了
}
% >
```
这是很浅显易懂的道理，检测COOKIE的存在，如果存在说明已经运行过写入COOKIE的代码了，然而运行以上的代码后，无论何时结果都是执行doStuffForReturnVisitor()，通过控制面板-Internet选项-设置-察看文件却始终看不到生成的cookie文件，奇怪，代码明明没有问题，不过既然有cookie，那就显示出来看看。
```
cookie[]cookies = request.getCookies();
if (cookies.lenght == 0 || cookies == null)
out.println("Has not visited this website");
}

else
{
for (int i = 0; i < cookie.length; i++)
{
out.println("cookie name:" + cookies[i].getName() + "cookie value:" +
cookie[i].getValue());
}
}
```

运行结果:
cookie name:JSESSIONID cookie value:KWJHUG6JJM65HS2K6 为什么会有cookie呢,大家都知道，http是无状态的协议，客户每次读取web页面时，服务器都打开新的会话，而且服务器也不会自动维护客户的上下文信息，那么要怎么才能实现网上商店中的购物车呢，session就是一种保存上下文信息的机制，它是针对每一个用户的，变量的值保存在服务器端，通过SessionID来区分不同的客户,session是以cookie或URL重写为基础的，默认使用cookie来实现，系统会创造一个名为JSESSIONID的输出cookie，我们叫做session cookie,以区别persistent cookies,也就是我们通常所说的cookie,注意session cookie是存储于浏览器内存中的，并不是写到硬盘上的，这也就是我们刚才看到的JSESSIONID，我们通常情是看不到JSESSIONID的，但是当我们把浏览器的cookie禁止后，web服务器会采用URL重写的方式传递Sessionid，我们就可以在地址栏看到sessionid=KWJHUG6JJM65HS2K6之类的字符串。
明白了原理，我们就可以很容易的分辨出persistent cookies和session cookie的区别了，网上那些关于两者安全性的讨论也就一目了然了，session cookie针对某一次会话而言，会话结束session cookie也就随着消失了，而persistent cookie只是存在于客户端硬盘上的一段文本（通常是加密的），而且可能会遭到cookie欺骗以及针对cookie的跨站脚本攻击，自然不如session cookie安全了。
通常session cookie是不能跨窗口使用的，当你新开了一个浏览器窗口进入相同页面时，系统会赋予你一个新的sessionid，这样我们信息共享的目的就达不到了，此时我们可以先把sessionid保存在persistent cookie中，然后在新窗口中读出来，就可以得到上一个窗口SessionID了，这样通过session cookie和persistent cookie的结合我们就实现了跨窗口的session tracking（会话跟踪）。
在一些web开发的书中，往往只是简单的把Session和cookie作为两种并列的http传送信息的方式，session cookies位于服务器端，persistent cookie位于客户端，可是session又是以cookie为基础的，明白的两者之间的联系和区别，我们就不难选择合适的技术来开发web service了。

## cookie和session机制之间的区别与联系

具体来说cookie机制采用的是在客户端保持状态的方案，而session机制采用的是在服务器端保持状态的方案。同时我们也看到，由于采用服务器端保持状态的方案在客户端也需要保存一个标识，所以session机制可能需要借助于cookie机制来达到保存标识的目的，但实际上它还有其他选择。  
cookie机制。正统的cookie分发是通过扩展HTTP协议来实现的，服务器通过在HTTP的响应头中加上一行特殊的指示以提示浏览器按照指示生成相应的cookie。然而纯粹的客户端脚本如JavaScript或者VBScript也可以生成cookie。而cookie的使用是由浏览器按照一定的原则在后台自动发送给服务器的。浏览器检查所有存储的cookie，如果某个cookie所声明的作用范围大于等于将要请求的资源所在的位置，则把该cookie附在请求资源的HTTP请求头上发送给服务器。
cookie的内容主要包括：名字，值，过期时间，路径和域。路径与域一起构成cookie的作用范围。若不设置过期时间，则表示这个cookie的生命期为浏览器会话期间，关闭浏览器窗口，cookie就消失。这种生命期为浏览器会话期的cookie被称为会话cookie。会话cookie一般不存储在硬盘上而是保存在内存里，当然这种行为并不是规范规定的。若设置了过期时间，浏览器就会把cookie保存到硬盘上，关闭后再次打开浏览器，这些cookie仍然有效直到超过设定的过期时间。存储在硬盘上的cookie可以在不同的浏览器进程间共享，比如两个IE窗口。而对于保存在内存里的cookie，不同的浏览器有不同的处理方式  
session机制。session机制是一种服务器端的机制，服务器使用一种类似于散列表的结构（也可能就是使用散列表）来保存信息。  
    
当程序需要为某个客户端的请求创建一个session时，服务器首先检查这个客户端的请求里是否已包含了一个session标识（称为session id），如果已包含则说明以前已经为此客户端创建过session，服务器就按照session id把这个session检索出来使用（检索不到，会新建一个），如果客户端请求不包含session id，则为此客户端创建一个session并且生成一个与此session相关联的session id，session id的值应该是一个既不会重复，又不容易被找到规律以仿造的字符串，这个session id将被在本次响应中返回给客户端保存。  
保存这个session id的方式可以采用cookie，这样在交互过程中浏览器可以自动的按照规则把这个标识发挥给服务器。一般这个cookie的名字都是类似于SEEESIONID。但cookie可以被人为的禁止，则必须有其他机制以便在cookie被禁止时仍然能够把session id传递回服务器。  
经常被使用的一种技术叫做URL重写，就是把session id直接附加在URL路径的后面。还有一种技术叫做表单隐藏字段。就是服务器会自动修改表单，添加一个隐藏字段，以便在表单提交时能够把session id传递回服务器。比如：  
```
<form name="testform" action="/xxx">
<input type="hidden" name="jsessionid" value="ByOK3vjFD75aPnrF7C2HmdnV6QZcEbzWoWiBYEnLerjQ99zWpBng!-145788764">
<input type="text">
</form>
```
实际上这种技术可以简单的用对action应用URL重写来代替。
