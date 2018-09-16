# HttClient.jar基本使用
HttpClient是Apache Jakarta Common下的子项目，用来提供高效的、最新的、功能丰富的支持HTTP协议的客户端编程工具包，并且它支持HTTP协议最新的版本和建议  

关于HTTP协议的详细信息，可以参考博客：https://www.cnblogs.com/lzq198754/p/5780310.html
  
## jar包引入
commons-httpclient是之前的jar包版本，现在已经停止更新，本httpclient替代。  
httpClient最新的版本如下：
```
<!-- https://mvnrepository.com/artifact/org.apache.httpcomponents/httpclient -->
<dependency>
    <groupId>org.apache.httpcomponents</groupId>
    <artifactId>httpclient</artifactId>
    <version>4.5.6</version>
</dependency>
```

## GET请求
```
private static String doGet(String url, Map<String, String> param, String charset) {
	// DefaultHttpClient这个已经被废弃了，现在使用CloseableHttpClient， 它是一个实现了HttpClient的抽象类
	CloseableHttpClient httpClient = null;
	// 定义 response 用于接收服务器回应的内容
	CloseableHttpResponse response = null;
	// 定义 get请求对象
	HttpGet httpGet = null;
	String result = null;
	InputStream inputStream = null;
	try {
		// 创建存储cookies的一个对象，cookieStore.getCoolies()可以获取 Cookie
		CookieStore cookieStore = cookieStoreStatic != null ? cookieStoreStatic : new BasicCookieStore();
		
		// 创建httpClient，我这里还在其中设置了cookies
		httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
		URIBuilder builder = new URIBuilder(url);
		if (param != null) {
			for (String key : param.keySet()) {
				// get方法设置parameters
				builder.addParameter(key, param.get(key));
			}
		}
		URI uri = builder.build();
		// 创建 get请求对象
		httpGet = new HttpGet(uri);
		// 执行请求，并用response接收服务器回传的内容
		response = httpClient.execute(httpGet);
		// HttpEntity其实表示一个流实体
		HttpEntity httpEntity = response.getEntity();
		
		// 这是获取cookiesStore对象，这里可以省略，写这个只是为了我的程序需要
		cookieStoreStatic = cookieStore;
		
		if (response.getStatusLine().getStatusCode() == 200) {
			// 如果获取到得是一个image，就写到一个.jpeg对象内，生成一个照片(我这里的请求是去获取12306的验证码)
			if (httpEntity.getContentType().getValue().contains("image")) {
				inputStream = httpEntity.getContent();
				FileUtils.copyToFile(inputStream, new File("C:\\Users\\jiehang\\Desktop\\image.jpeg"));
			} else if (httpEntity.getContentType().getValue().contains("json")) {
				// 把内容转成字符串  
				result = EntityUtils.toString(response.getEntity(), charset);
				System.out.println(result);
			}
		}
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		try {
			if (inputStream != null)
				inputStream.close();
			if (response != null)
				response.close();
			if (httpClient != null)
				httpClient.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	return result;
}

```

## POST请求
```
// 基本和上面类似
private static String doPost(String url, Map<String, String> map, String charset) {
	CloseableHttpClient httpClient = null;
	CloseableHttpResponse response = null;
	HttpPost httpPost = null;
	String result = null;
	try {
		CookieStore cookieStore = cookieStoreStatic;
		httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
		httpPost = new HttpPost(url);
		/** 这里相当于模拟form表单，在post中设置要提交的参数 */
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, String> elem = (Entry<String, String>) iterator.next();
			list.add(new BasicNameValuePair(elem.getKey(), elem.getValue()));
		}
		if (list.size() > 0) {
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, charset);
			httpPost.setEntity(entity);
		}
    
		response = httpClient.execute(httpPost);
		result = EntityUtils.toString(response.getEntity(), charset);
		cookieStoreStatic = cookieStore;

		return result;
	} catch (Exception ex) {
		ex.printStackTrace();
	} finally {
		try {
			if (response != null)
				response.close();
			if (httpClient != null)
				httpClient.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	return null;
}
```

## 
