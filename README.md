# 结构型设计模式: 代理模式与企业应用实践

> 本文相关代码已上传[Github](https://github.com/MicroKibaco/basic_java_design_patterns),如果对你有帮助欢迎star一波

## 一. 代理模式
你玩过扮白脸,扮黑脸的游戏吗?你是一个白脸,提供了很好且友善的服务,但是你不希望每个人都叫你做事,所以找了黑脸控制你的访问,这就是代理模式要做的: 控制和管理访问,代理模式在Android源码有很多体现,他的作用是代理对象搬运的整个方法调用二出名,它可以代替模型懒惰的对象做一些事情。所谓的代理模式就是给另外一个对象提供一个占位符以控制对象的访问?我们怎么理解这句话呢?首先我们得搞明白代理模式的几个应用场景:

![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/3f12dd0fb47b4c34bcb60decd464c9e7~tplv-k3u1fbpfcp-watermark.webp)

#### 1.1 远程代理(Java RMI)
![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/682d26a4e4704648812908c9e6704a4f~tplv-k3u1fbpfcp-watermark.webp)

比如在斗鱼直播应用里,客户端辅助的对象不是真正的远程服务,服务端和客户端之间的通信是建立在Socket连接上,将调用的信息解包,然后正在调用服务端对象的方法,所以对服务端而已,调用是本地的,来自服务端对象,而不是远程客户端,服务端辅助的对象从服务中得到返回值,将它打包,通过Socket输出流,客户互端Helper通过对信息解包,最后返回给客户端对象,时序图如下:
![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/10b466d11a754064b8fa6d08b7194741~tplv-k3u1fbpfcp-watermark.webp)

我们来看一下代码实现吧

##### 远程服务接口,客户端和服务器都要有
```java
public interface RemoteService extends Remote {
    String sayHello() throws RemoteException;
}
```
##### 客户端实现
```java

public class Client {
    public static void main(String[] args) {
        try {
            RemoteService service = (RemoteService) Naming.lookup("rmi://localhost:1099/service");
            System.out.println(service.sayHello());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```
##### 服务端实现
```java
public class ServiceImpl extends UnicastRemoteObject implements RemoteService {
    protected ServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public String sayHello() throws RemoteException {
        System.out.println("hello");
        return "hello";
    }
}
public class Server {
    public static void main(String[] args){
        try {
            ServiceImpl service = new ServiceImpl();
            LocateRegistry.createRegistry(1099);
            java.rmi.Naming.rebind("rmi://localhost:1099/service",service);
        } catch (MalformedURLException | RemoteException e) {
            e.printStackTrace();
        }

    }
}
```

#### 1.2 虚拟代理


虚拟代理作为创建开销大的对象的代表，经常会直到我们真正需要一个对象的时候才创建它。当对象在创建前和创建中时，由虚拟代理地来扮演对象的替身。对象创建后，代理就会将请求直接委托给对象。

![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/c958348e32c74aed9175e97ad8acaadc~tplv-k3u1fbpfcp-watermark.image)

我们来看一下代码实现吧:
我们将创建一个 Image 接口和实现了 Image 接口的实体类。ProxyImage 是一个代理类，减少 RealImage 对象加载的内存占用。

![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/cf6109be294c4a6c9cc73ad27ec0e8c8~tplv-k3u1fbpfcp-watermark.image)

##### 1. 创建代理类和真正类的共同接口

```java
package proxy.virtual;

/**
 * 创建代理类和真正的类的共同接口
 */
public interface Image
{
   void display();
}
```

##### 2. 创建代理类和真正类

```java
package proxy.virtual;

/**
 * 图片代理类1
 */
public class ProxyImage implements Image
{

   private RealImage realImage;
   private String fileName;

   public ProxyImage(String fileName){
      this.fileName = fileName;
   }

   @Override
   public void display() {
      if(realImage == null){
         realImage = new RealImage(fileName);
      }
      realImage.display();
   }
}

//========================================================================================================================

/**
 * 图片代理类2
 */
public class RealImage implements Image
{

   private String fileName;

   public RealImage(String fileName){
      this.fileName = fileName;
      loadFromDisk(fileName);
   }

   @Override
   public void display() {
      System.out.println("Displaying " + fileName);
   }

   private void loadFromDisk(String fileName){
      System.out.println("Loading " + fileName);
   }
}

```

##### 3. 测试用例

```java
/**
 * 客户端模拟类
 * 当用户想要访问图片的时候，先交给代理类处理，如果图片已经创建，则直接取出图片，否则在从硬盘上下载图片，所以当图片存在时，
 * 则不需要再次创建RealImage 对象从硬盘上下载图片，避免大量创建 RealImage 对象，消耗内存
 */
public class Client {

   public static void main(String[] args) {
      Image image = new ProxyImage("timg.gif");

      // 图像将从磁盘加载
      image.display();
      System.out.println("");
      // 图像不需要从磁盘加载
      image.display();
   }
}
```

##### 4. 测试报告
![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/3344fa8cb3774d83a2b9421729d2b3ae~tplv-k3u1fbpfcp-watermark.image)

当用户想要访问图片的时候，先交给代理类处理，如果图片已经创建，则直接取出图片，否则在从硬盘上下载图片，所以当图片存在时，则不需要再次创建RealImage 对象从硬盘上下载图片，避免大量创建 RealImage 对象，消耗内存

#### 1.3 缓冲代理

为某一个操作的结果提供临时的缓存存储空间，以便在后续使用中能够共享这些结果，优化系统性能，缩短执行时间。

#### 1.4 保护代理
用于对真实对象的功能做一些访问限制, 在代理层做身份验证. 通过了验证, 才调用真实的主体对象的相应方法.

那么为什么要用代理模式呢?
- 通过代理对象间接的访问目标对象,防止直接访问目标对象给系统带来不必要的复杂性
- 通过代理对象对原有的业务进行了增强


## 二.[ 静态代理](https://github.com/MicroKibaco/CrazyMindMap/tree/master/37_design_parttern/src/main/java/com/github/microkibaco/design/proxy/statics)
 ![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/f99e328c65204993bfd566a4cf395e8b~tplv-k3u1fbpfcp-watermark.webp)
### 2.1 静态代理理解
#### 2.1.1 静态代理概念
所谓的静态代理就是给某个对象提供了一个代理对象，并由代理对象控制对原对象的引用。
#### 2.1.2 静态代理结构
- 1. 抽象对象角色
<br/>声明了目标对象和代理对象的共同接口，这样一来在任何可以使用目标对象的地方都可以使用代理对象
![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/8de6428578584e399205e2741393c365~tplv-k3u1fbpfcp-watermark.image)
- 2. 目标对象角色
<br/>定义了代理对象所代表的目标对象
![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/00859be315714bdc9fb10e4b2df424ef~tplv-k3u1fbpfcp-watermark.image)

- 3. 代理对象角色
<br/>代理对象内部含有目标对象的引用，从而可以在任何时候操作目标对象；代理对象提供一个与目标对象相同的接口，以便可以在任何时候替代目标对象

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/92021eef5cb54a5c851ea003c4973656~tplv-k3u1fbpfcp-watermark.image)
- 测试用例:
![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/8bdc7ddd2ee34f8aa90fd82151669384~tplv-k3u1fbpfcp-watermark.image)

#### 2.1.3 静态代理缺点

静态代理在使用时,需要定义接口或者父类,被代理对象与代理对象一起实现相同的接口或者是继承相同父类。一般来说，被代理对象和代理对象是一对一的关系，当然一个代理对象对应多个被代理对象也是可以的。<br/><br/>静态代理，一对一则会出现时静态代理对象量多、代码量大，从而导致代码复杂，可维护性差的问题，一对多则代理对象会出现扩展能力差的问题。
##  三. 动态代理
### 3.1 动态代理前置知识
#### 3.1.1 反射
一般情况下,我们使用某个类的时候一定要知道它是什么类,然后它用来做什么用的。于是我们直接对这个类进行实例化,之后再对这个对象进行操作。

但是如果一开始我们就不知道我们该初始化的类的对象是什么呢?自然也无法用 new 关键字创建对象呢?这时候,我们使用 JDK 提供的反射 API 进行反射调用,反射就是在运行时才知道该类要做什么。并且在运行时可以获取该类的完整构造,并调用其对应的方法或者参数等


反射是Java动态语言的关键,反射机制允许程序在执行期借助 Reflection API 获取任何类的内部信息,并且操作任意类的对象内部属性和方法

```java
public class Person {
    String name;
    private int age;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
        System.out.println("this is setName()!");
    }
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
        System.out.println("this is setAge()!");
    }

    //包含一个带参的构造器和一个不带参的构造器
    public Person(String name, int age) {
        super();
        this.name = name;
        this.age = age;
    }
    public Person() {
        super();
    }

    //私有方法
    private void privateMethod(){
        System.out.println("this is private method!");
    }
}
```

反射提供了以下功能

- 在运行时构造任意类的对象

```java
public class TestConstructor {
    /*构造器相关*/
    public void testConstructor() throws Exception{
        String className = "com.github.microkibaco.reflection.Person";
        Class<Person> clazz = (Class<Person>) Class.forName(className);

        System.out.println("获取全部Constructor对象-----");
        Constructor<Person>[] constructors
                = (Constructor<Person>[]) clazz.getConstructors();
        for(Constructor<Person> constructor: constructors){
            System.out.println(constructor);
        }


        System.out.println("获取某一个Constructor 对象，需要参数列表----");
        Constructor<Person> constructor
                = clazz.getConstructor(String.class, int.class);
        System.out.println(constructor);

        //2. 调用构造器的 newInstance() 方法创建对象
        System.out.println("调用构造器的 newInstance() 方法创建对象-----");
        Person obj = constructor.newInstance("Mark", 18);
        System.out.println(obj.getName());
    }
}
```
- 在运行时获取一个类所具有的成员变量和方法

```java
public class TestField {
    /*域相关*/
    public void testField() throws Exception{
        String className = "com.github.microkibaco.reflection.Person";
        Class clazz = Class.forName(className);

        System.out.println("获取公用和私有的所有字段，但不能获取父类字段");
        Field[] fields = clazz.getDeclaredFields();
        for(Field field: fields){
            System.out.print(" "+ field.getName());
        }
        System.out.println();
        System.out.println("---------------------------");


        System.out.println("获取指定字段");
        Field field = clazz.getDeclaredField("name");
        System.out.println(field.getName());

        Person person = new Person("ABC",12);
        System.out.println("获取指定字段的值");
        Object val = field.get(person);
        System.out.println(field.getName()+"="+val);

        System.out.println("设置指定对象指定字段的值");
        field.set(person,"DEF");
        System.out.println(field.getName()+"="+person.getName());

        System.out.println("字段是私有的，不管是读值还是写值，" +
                "都必须先调用setAccessible（true）方法");
        //     比如Person类中，字段name字段是非私有的，age是私有的
        field = clazz.getDeclaredField("age");
        field.setAccessible(true);
        System.out.println(field.get(person));
    }
}
```

- 在运行时调用修改一个类的的属性或方法
```java
public class TestMethod {
    /*方法相关*/
    public void testMethod() throws Exception{
        Class clazz = Class.forName("com.github.microkibaco.reflection.Person");

        System.out.println("获取clazz对应类中的所有方法，" +
                "不能获取private方法,且获取从父类继承来的所有方法");
        Method[] methods = clazz.getMethods();
        for(Method method:methods){
            System.out.print(" "+method.getName()+"()");
        }
        System.out.println("");
        System.out.println("---------------------------");

        System.out.println("获取所有方法，包括私有方法，" +
                "所有声明的方法，都可以获取到，且只获取当前类的方法");
        methods = clazz.getDeclaredMethods();
        for(Method method:methods){
            System.out.print(" "+method.getName()+"()");
        }
        System.out.println("");
        System.out.println("---------------------------");

        System.out.println("获取指定的方法，" +
                "需要参数名称和参数列表，无参则不需要写");
        //  方法public void setName(String name) {  }
        Method method = clazz.getDeclaredMethod("setName", String.class);
        System.out.println(method);
        System.out.println("---");

        //  方法public void setAge(int age) {  }
        /* 这样写是获取不到的，如果方法的参数类型是int型
        如果方法用于反射，那么要么int类型写成Integer： public void setAge(Integer age) {  }
        要么获取方法的参数写成int.class*/
        method = clazz.getDeclaredMethod("setAge", int.class);
        System.out.println(method);
        System.out.println("---------------------------");


        System.out.println("执行方法，第一个参数表示执行哪个对象的方法" +
                "，剩下的参数是执行方法时需要传入的参数");
        Object obje = clazz.newInstance();
        method.invoke(obje,18);

        /*私有方法的执行，必须在调用invoke之前加上一句method.setAccessible（true）;*/
        method = clazz.getDeclaredMethod("privateMethod");
        System.out.println(method);
        System.out.println("---------------------------");
        System.out.println("执行私有方法");
        method.setAccessible(true);
        method.invoke(obje);
    }
}
```
- 反射应用案例

```java
public class MutableLiveDataV2<T> extends MutableLiveData<T> {

    @Override
    public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
        super.observe(owner, observer);
        hook(observer);
    }

    private void hook(Observer<? super T> observer) {
        try{
            //1.得到mLastVersion
            Class<LiveData> liveDataClass=LiveData.class;
            Field mObserversField = liveDataClass.getDeclaredField("mObservers");
            mObserversField.setAccessible(true);
            //获取到这个成员变量对应的对象
            Object mObserversObject = mObserversField.get(this);
            //得到map
            Class<?> mObserversObjectClass = mObserversObject.getClass();
            //获取到mObservers对象的get方法
            Method get=mObserversObjectClass.getDeclaredMethod("get",Object.class);
            get.setAccessible(true);
            //执行get方法
            Object invokeEntry=get.invoke(mObserversObject,observer);
            //取到map中的value
            Object observerWraper=null;
            if(invokeEntry!=null && invokeEntry instanceof Map.Entry){
                observerWraper=((Map.Entry)invokeEntry).getValue();
            }
            if(observerWraper==null){
                throw new NullPointerException("observerWraper is null");
            }
            //得到ObserverWrapper的类对象
            Class<?> superclass=observerWraper.getClass().getSuperclass();
            Field mLastVersion = superclass.getDeclaredField("mLastVersion");
            mLastVersion.setAccessible(true);


            //2.得到mVersion
            Field mVersion = liveDataClass.getDeclaredField("mVersion");
            mVersion.setAccessible(true);

            //3.把mVersion的值填入到mLastVersion中
            Object mVersionValue=mVersion.get(this);
            mLastVersion.set(observerWraper,mVersionValue);

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
```

Java 是一门面向对象的语言。在面向对象的世界里，万事万物皆对象，既然万事万物皆对象，那么我们的类是不是对象呢？我们写的每一个类都可以看成一个对象，是 java.lang.Class 类的对象。每一个类对应的Class放在哪里呢？</br></br>当我们写完一个类的Java文件，编译成class文件的时候，编译器都会将这个类的对应的class对象放在class文件的末尾。</br></br>里面都保存了些什么？大家可以理解保存了类的元数据信息，一个类的元数据信息包括什么？有哪些属性，方法，构造器，实现了哪些接口等等，那么这些信息在Java里都有对应的类来表示。



#### 3.1.2 Class类

class 类封装了当前类的所有类信息

一个类中有属性，方法，构造器等，比如说有一个Person类，一个Order类，一个Book类，这些都是不同的类，现在需要一个类，用来描述类，这就是Class

它应该有类名，属性，方法，构造器等。Class是用来描述类的类。 Class类是一个对象照镜子的结果，对象可以看到自己有哪些属性，方法，构造器，实现了哪些接口等等 对于每个类而言，JRE 都为其保留一个不变的 Class 类型的对象。

一个 Class 对象包含了特定某个类的有关信息。 对象只能由系统建立对象，一个类（而不是一个对象）在 JVM 中只会有一个Class实例

那么怎样获取 Class 呢?

获取 Class 大概有以下三个方式:

- 1. 通过类名获取      类名.class

> String.class

- 2. 通过对象获取      对象名.getClass()

> String str = new String();
<br/>str.getClass();

- 3. 通过全类名获取    Class.forName(全类名)

>Class clazz = Class.forName("com.github.microkibaco.reflection.Person");

class 类常用的方法:

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/0921f82a9be3424a8283fe96c0523008~tplv-k3u1fbpfcp-watermark.image)

#### 3.1.3 类加载机制与双亲委派模型

##### 类加载概念
Java虚拟机把描述类的数据从Class文件加载到内存，并对数据进行校验、转换解析和初始化，最终形成可以被虚拟机直接使用的Java类型，这就是虚拟机的类加载机制。

##### 类的加载时机
1. 遇到new、getstatic、putstatic或invokestatic这4条字节码指令时，如果类没有进行过初始化，则需要先触发其初始化。
2. 使用java.lang.reflect包的方法对类进行反射调用的时候，如果类没有进行过初始化，则需要先触发其初始化。
3. 当初始化一个类的时候，如果发现其父类还没有进行过初始化，则需要先触发其父类的初始化。
4. 当虚拟机启动时，用户需要指定一个要执行的主类（包含main（）方法的那个类），虚拟机会先初始化这个主类。
5. 当使用JDK 1.7的动态语言支持时，如果一个java.lang.invoke.MethodHandle实例最后的解析结果REF_getStatic、REF_putStatic、REF_invokeStatic的方法句柄，并且这个方法句柄所对应的类没有进行过初始化，则需要先触发其初始化。

##### 什么情况下不会出现类的初始化

- 1. 通过子类引用父类的静态字段，不会导致子类的初始化 只有直接定义这个类
- 2. 通过使用数组定义引用类，不会触发此类的初始化
- 3. 常量（static final）在编译阶段就会存入调用类的常量池中，本质上没有直接到定义常量的类，不会触发初始化。

##### 类的加载过程
![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/e27c77ed7f6c4c4e811bb8a8e87ef032~tplv-k3u1fbpfcp-watermark.image)

##### 常见的类加载器
![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/1370655df307401ea0dd4d4bfcbca758~tplv-k3u1fbpfcp-watermark.image)


- 应用程序类/系统类加载器（Application ClassLoader）
  - 它负责加载用户类路径（ClassPath）上所指定的类库，开发者可以直接使用这个类加载器，如果应用程序中没有自定义过自己的类加载器，一般情况下这个就是程序中默认的类加载器。

- 扩展类加载器（Extension ClassLoader）
  - 它负责加载＜JAVA_HOME＞\lib\ext目录中的，或者被java.ext.dirs系统变量所指定的路径中的所有类库，开发者可以直接使用扩展类加载器

- 启动类加载器（Bootstrap ClassLoader）
  - 在lib目录中的，被-Xbootclasspath参数所指定的路径中的
  - 可以被虚拟机识别的类库
  - 不可以直接被使用，如果需要使用，则自定义类加载器时，java.lang.ClassLoader.getClassLoader（） = null 即可



##### ClassLoader源码分析
ClassLoader 负责读取 Java 字节代码，并转换成java.lang.Class类的一个实例
![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/2b070493a47c408d8f14eb8f52297af5~tplv-k3u1fbpfcp-watermark.image)


##### 双亲委派机制

![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/73b20c065e0040ea91f9fa061d222a28~tplv-k3u1fbpfcp-watermark.image)

#### 3.1.4 从字节码看动态代理
### 3.2 动态代理实现步骤
#### 第一步: 创建 两个InvocationHandler
#### 第二步: 创建 Proxy 类并实例化 Proxy 对象
#### 第三步: 利用适当的代理包装任何 PersonBean 对象
### 3.3 动态代理实现原理
### 3.4 动态代理在Retrofit中的应用
### 3.5 动态代理优缺点
### 3.6 动态代理实现H5和原生交互

## 四. 动态代理和装饰模式区别
## 五. 总结