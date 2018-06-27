#### 非对称加密
>对称加密算法使用同一套密钥加密解密，而非对称加密则使用两套密钥来进行加密和解密，这两个密钥被称为公开密钥和私有密钥，如果使用公钥进行加密，则只能使用私钥进行解密，如果使用私钥加密，则只能使用公钥进行解密，因为加密解密得使用两套不同的密钥，所以这种加密方式被称为非对称加密。  
#### SHA256
>类似于md5，SHA256也是一种hash算法，hash算法是一种用于将数据转换成一个定长的唯一值的算法，而衡量hash算法的标准是出现hash碰撞的频率，当不同的数据通过hash算法得到同一个值时，这种情况就称之为hash碰撞，hash碰撞出现的频率越小，hash算法就越优秀，当前md5或SHA家族中SHA1都可以在一定计算复杂度内被破解，为了应对越来强大的计算性能，降 低被破解的概率，SHA256算法应运而生。  
#### 加签和解签
>加签和解签是一种为了保证数据的正确性以及数据完整性的手段，发送方给数据添加数据签名，接收方验证数据签名是否合法，如果合法则证明数据完整且正确。  
#### how to impl
>一般地直接对数据运用私钥加密，将加密数据作为数据标签，发送给接收方验签，这是最基础加签验签解决方法，但是在实际情况中不得不考虑数据量的问题，当数据量特别大的情况下，加密算法就十分低效，耗时长且十分占用计算资源。为了可以控制加签数据的大小，这就要用到信息摘要算法SHA256了。  
>java签名算法SHA256WithRSA就是SHA256和RSA算法的结合，发送方先使用SHA256算法将数据转换成一个定长256位的hash值，然后用RSA算法私钥加签hash值，生成一个数字签名，接收方使用公钥对数字签名验签。
#### java实现
1. 密钥对生成
>密钥对生成一般使用的是openssl工具，但操作比较复杂，推荐一个在线密钥生成工具，支持多种对称加密以及非对称加密算法。
```
http://web.chacuo.net/netrsakeypair
```
```
-----BEGIN PUBLIC KEY-----
MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC/VHCsCFVLDd4RAlBPO6BNqncA
gpacM5jSWVHmxqESqqeiumqPdvpvXmYLFTOaWr1KdVvgyL1j9dNw2q1wUMyCOndW
zssXSDxwMTjw9cFX2NMQcAOf8sBHJsSxZcPt+9kevA2I6AkJJ/nk6M5Mx/wxDZvW
kixCa1vFfcoUAjxlKQIDAQAB
-----END PUBLIC KEY-----

```
```
-----BEGIN PRIVATE KEY-----
MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAL9UcKwIVUsN3hEC
UE87oE2qdwCClpwzmNJZUebGoRKqp6K6ao92+m9eZgsVM5pavUp1W+DIvWP103Da
rXBQzII6d1bOyxdIPHAxOPD1wVfY0xBwA5/ywEcmxLFlw+372R68DYjoCQkn+eTo
zkzH/DENm9aSLEJrW8V9yhQCPGUpAgMBAAECgYEAj7qPKazY5hj0yTJDwkG4mp+D
5g4ztgPMubf/nq14Mt2gMM55XmyylRcE8S5sJXgk2tpuut4R6BWzUQP1ZnpMJfuz
7bHJedYXPPVY/Fe6db0L5bOMcCOT8Fbi6LW72LUar2mm2J/GX6rEMURdR1plIQuq
gnS4ZPhydJ3oL4IfjQECQQDkwEyGNLmY9aJTN2SDsPWSnmd6Uhc+24D93sAuTgIc
CzmbiVdduRpvpu33vfSTHPFJabEGnwiOpjgQc+x+tWiNAkEA1h79G8ojSDBTKwQz
wLveMzpi6PP9becqm0dJMfIk3CA8y4nsHmjlECk4Sm0Mn3eGZbTG8/3oqBboxsp6
fd/uDQJBALu9I1EGFsj12BqKHMGES5fgz7Zxh0h8aHpzBbE7LroLcGU5q+1szMiT
7Txlt0PB/jmtN6/id+hzfRHrwk4f3sUCQQCEYtSLggcecwJ47jgxSfawJcFR1sky
UmL9bq3Ku7QehbARCE18384Al/u9yH6tefEWoRcBdFOM2d1CS55AhVthAkBmcU+M
obn0N6CA8+AGkoWZEKoDMy6vkAr5Tb7eGQL3ICLp8OC+g7rdlBATqJhjm9RTdMHZ
rK8kgnRxJDwxQG62
-----END PRIVATE KEY-----

```
>除了使用openssl生成密钥对外，也可以直接使用java代码获取密钥对，模板代码如下：
>首先为RSA算法创建一个KeyPairGenerator对象
```
KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
```
>使用一个随机数据源初始化密钥生成对象
```
SecureRandom secureRandom = new SecureRandom();
//第一个参数代表密钥长度
keyPairGenerator.initialize(1204, secureRandom);
```
>生成密钥对
```
KeyPair keyPair = keyPairGenerator.generateKeyPair();
```
>通过密钥对生成公私钥
```
Key publicKey = keyPair.getPublic();
Key privateKey = keyPair.getPrivate();
```
>对密钥进行base64编码，生成密钥字符串
```
String publicKeyBase64 = new BASE64Encoder().encode(publicKey.getEncoded());
        String privateKeyBase64 = new BASE64Encoder().encode(privateKey.getEncoded());
```
>最后按照提示，将公钥和私钥分别存在pub.key和pri.key中，对于openssl生成的密钥文件，真正使用的时候需要将文本首行和末尾行去除，首行尾行内容并不属于密钥内容。
2. 加签操作
>加签需要先从文件中读取密钥文件，编写一个简单的文件读取方法，将读取到的内容存储在字符串中。
```
public static String loadKeyByFile(String path) throws Exception {
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String readLine;
            StringBuilder sb = new StringBuilder();
            while ((readLine = br.readLine()) != null) {
                sb.append(readLine);
            }
            br.close();
            return sb.toString();
        } catch (IOException e) {
            throw new Exception("");
        } catch (NullPointerException e) {
            throw new Exception("");
        }
    }
```
>java签名类加签,首先需要生成一个私钥对象
```
//使用非对称RSA算法
KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//生成私钥对象
PKCS8EncodedKeySpec priv_spec = new PKCS8EncodedKeySpec(loadKeyByFile("pri.key").getBytes());
PrivateKey privKey = mykeyFactory.generatePrivate(priv_spec);
```
>指定一个签名算法的签名对象
```
java.security.Signature signature = java.security.Signature
                    .getInstance("SHA256WithRSA");
```
>初始化签署签名的私钥
```
signature.initSign(privKey)
```
>更新指定byte数组的签名数据
```
signature.update("QAQ".getBytes());
```
>生成签名
```
byte[] signed = signature.sign();
//Base64使用了apache的commons-codec
String sign = new String(Base64.encodeBase64(signed));
```
3. 验证签名
>验证签名的时候同样从文件中读取公钥内容，在实际生产上，密钥可以写死在内存，也可以存在数据库中，这里为了方便直接从文件中读取。  
>公钥对象生成过程和私钥对象生成的过程类似。
```
 KeyFactory mykeyFactory = KeyFactory.getInstance("RSA");
 X509EncodedKeySpec pub_spec = new X509EncodedKeySpec(Base64.decodeBase64(loadKeyByFile("pub.key").getBytes()));
 PublicKey pubKey = mykeyFactory.generatePublic(pub_spec); 
```
>公钥验证签名和加签的过程也类似，只是在最后的处理不同。
```
java.security.Signature signature = java.security.Signature
                    .getInstance("SHA256WithRSA");
signature.initVerify(pubKey);
signature.update("QAQ".getBytes());
```
>验证签名
```
signature.verify(Base64.decodeBase64(sign.getBytes()));
```
>如果签名通过，verify返回true，否则返回false。
#### 结语
>一直对java.security中的方法比较陌生，又有平时接触的不多，视野也只局限在一个https上，直到换工作做起金融相关的系统后才对安全方面的知识重视起来，本文也只是java安全上的冰山一角，理解也只能算管中窥豹，错误之处欢迎拍砖。




