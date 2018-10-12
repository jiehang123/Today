# spring中构建XmlBeanFactory

## 容器的基本实现
* 封装资源文件
> * 对资源文件的编码进行处理 
> * 获取输入流。从Resource中获取对应的InputStream并构造InputSource。  
* 获取对XML文件的验证模式(DTD或者是XSD)
* 加载XML并得到对应的Document
* 根据Document解析及注册BeanDefinition

```
public class XmlBeanFactory extends DefaultListableBeanFactory {

  // xmlBeanFactory中定义了XML读取器XmlBeanDefinitionReader，该类是从资源文件读取并转换为BeanDefinition的功能。
	private final XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(this);

	public XmlBeanFactory(Resource resource) throws BeansException {
		this(resource, null);
	}
  
	public XmlBeanFactory(Resource resource, BeanFactory parentBeanFactory) throws BeansException {
		super(parentBeanFactory);
		this.reader.loadBeanDefinitions(resource);
	}
}
```

跟踪到XmlBeanDefinitionReader中的代码
```
public class XmlBeanDefinitionReader extends AbstractBeanDefinitionReader {
  
  // 省略掉这个类中的部分代码，精简之后的核心代码如下
  public int loadBeanDefinitions(EncodedResource encodedResource) throws BeanDefinitionStoreException {
    InputStream inputStream = encodedResource.getResource().getInputStream();
    InputSource inputSource = new InputSource(inputStream);
    if (encodedResource.getEncoding() != null) {
      inputSource.setEncoding(encodedResource.getEncoding());
    }
    return doLoadBeanDefinitions(inputSource, encodedResource.getResource());
  }
  
  // 继续调用该类中的函数doLoadBeanDefinitions(..)
  protected int doLoadBeanDefinitions(InputSource inputSource, Resource resource) {
    // 后面的代码就不继续看了，这个方法的作用就是解析资源生成一个Document对象
    Document doc = doLoadDocument(inputSource, resource);
    return registerBeanDefinitions(doc, resource);
  }
  
  public int registerBeanDefinitions(Document doc, Resource resource) throws BeanDefinitionStoreException {
    // 这里创建的是 DefaultBeanDefinitionDocumentReader 对象，它的作用是读取Document并注册BeanDefinition
    BeanDefinitionDocumentReader documentReader = createBeanDefinitionDocumentReader();
    // 这一步是核心，就是解析Document对象
    documentReader.registerBeanDefinitions(doc, createReaderContext(resource));
    return getRegistry().getBeanDefinitionCount() - countBefore;
  }
}
```
documentReader.registerBeanDefinitions(doc, createReaderContext(resource))
上面的方法会从doc中获取到根元素对象，然后开始遍历根元素root
```
public class DefaultBeanDefinitionDocumentReader implements BeanDefinitionDocumentReader {
  protected void parseBeanDefinitions(Element root, BeanDefinitionParserDelegate delegate) {
		if (delegate.isDefaultNamespace(root)) {
			NodeList nl = root.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				Node node = nl.item(i);
				if (node instanceof Element) {
					Element ele = (Element) node;
          // 如果是spring的默认标签(import、alias、beans、beans)就直接解析，否则进行自定义标签解析
					if (delegate.isDefaultNamespace(ele)) {
						parseDefaultElement(ele, delegate);
					}
					else {
						delegate.parseCustomElement(ele);
					}
				}
			}
		}
		else {
			delegate.parseCustomElement(root);
		}
	}
}
```
