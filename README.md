### 适用于Spring Boot的字典字典翻译扩展

> 在常见的web应用中，常见的数据库字段会使用字典值，但是在数据查询时，我们需要将存储的字典值转换成对应的字典标签(value>>name)。常见的转换方式为从数据库查询、逻辑包装等。

#### 使用方式
1. 首先你的项目需要是一个如果你的项目是Maven的话，可以直接使用一下配置引入
```xml
<!-- https://mvnrepository.com/artifact/com.github.mujave/dict-translate-spring-boot-starter -->
<dependency>
    <groupId>com.github.mujave</groupId>
    <artifactId>dict-translate-spring-boot-starter</artifactId>
    <version>1.0.3</version>
</dependency>
```
*使用其他管控方式的项目可以到中央仓库中搜索坐标查看配置方式*

2. 第二步，需要**实现DictCacheService接口**，并将这个对象交给Spring 容器，否则扩展将不会自动生效

   ```java
   @Component
   public class DictCache implements DictCacheService {
       @Override
       public BiMap<String, String> getDictMapByName(String dictName) {
           //举例，通过字典名称返回所有的字典项
           BiMap<String, String> map = HashBiMap.create();
           switch (dictName) {
               case "sex":
                   map.put("1", "男");
                   map.put("2", "女");
                   break;
               case "car:color":
                   map.put("1", "芭比粉");
                   map.put("2", "烈焰红");
                   break;
               case "license:type":
                   map.put("A1","大型汽车");
                   map.put("C1","小型汽车");
                   map.put("C2","小型自动挡汽车");
                   map.put("D","普通三轮摩托车");
                   break;
           }
           return map;
       }
   }
   ```

3. 字典翻译

   这里描述这样一个场景，系统有定义用户1:1驾照、用户1:*车车两种关系。

   ```java
   /**
    * 车车
    */
   @Data
   @Builder
   public class Car {
       private String name;
       // 根据“car:color”这个字典进行翻译，翻译后的值name放到carColor这个字段中，targetField 默认为翻译字段名称 + Name
       @Dict(dictName = "car:color",targetField = "carColor")
       private Integer color; //汽车颜色代码，使用字典表
       private  String carColor;
   }
   ``````
   
   ```java
   /**
    * 驾照
    */
   @Data
   @Builder
   public class DrivingLicense {
       private String number;
       /*
        * 根据“license:type”这个字典进行翻译
        * multiple 表示这个字段存在多个字典值，需要根据,进行分割，一一翻译在一起放到typeName中
        */
       @Dict(dictName = "license:type", multiple = true)
       private String type;//驾驶证准驾车型代码，使用字典表
       private String typeName;
   }
   ```
   
   ```java
   /**
    * 用户
    */
   @Data
   @Builder
   public class People {
       private Integer id;
       private String name;
       @Dict(dictName = "sex") 
       private Integer sex;//用户性别代码，使用字典表
       private String sexName;
       @DictEntity  //注解说明这个属性是一个需要进行翻译的类
       private DrivingLicense drivingLicense; //用户的驾驶证  1:1
       @DictCollection  //注解说明这个属性是一个需要进行翻译的集合
       private List<Car> car; //用户的车车 1:*
   }
   ```
   
   ```java
   //@DictTranslation用于方法，说明这个方法的返回值需要进行字典翻译
   @DictTranslation
   public People testDict() {
       DrivingLicense license = DrivingLicense.builder().number("102312000311").type("C1,D").build();
       Car car1 = Car.builder().name("沃尔沃").color(1).build();
       Car car2 = Car.builder().name("大奔").color(2).build();
       People mujave = People.builder().id(1).name("mujave").sex(1).car(Arrays.asList(car1, car2)).drivingLicense(license).build();
       return mujave;
   }
   ```
   
   > testDict 返回输出如下
   >
   > People(id=1, name=mujave, sex=1, sexName=男, drivingLicense=DrivingLicense(number=102312000311, type=C1,D, typeName=小型汽车,普通三轮摩托车), car=[Car(name=沃尔沃, color=1, carColor=芭比粉), Car(name=大奔, color=2, carColor=烈焰红)])

另外对于返回值是java.util.Map类型的方法可以使用@DictMap和@DictMapper进行翻译字段的描述。其他参数可以参照下面的注解说明。其他请点击 [完整的实例demo](https://github.com/mujave/dict-traslate-starter/tree/main/dict-translate-demo)

#### 注解说明

1. @Dict

   标记一个字段进行字典翻译

| 参数名         | 含义           | 描述                                                         | 默认值          |
| -------------- | -------------- | ------------------------------------------------------------ | --------------- |
| dictName       | 翻译字典名称   | 指明翻译这个字段使用的字典名称，与数据sys_dict.name对应      | 无              |
| targetField    | 目标字段       | 用于存放字典翻译之后的name的值                               | 当前字段名+Name |
| nullValueName  | 空值默认值     | 当注解的字段为空时的默认值，与表格显示所对应                 | -               |
| undefinedValue | 空字典项默认值 | 当翻译的字段在对应的字典中未定义时的默认值                   | ""              |
| multiple       | 是否多个字典项 | 标记这个字段是否包含多个字典项，为true时将会按照","进行拆分，然后逐个翻译之后放到目标字段中 | false           |

2. @DictEntity

   标记说明类中的这个字段是一个实体类型，其中有字段进行字典翻译。

3. @DictCollection

   标记说明类中的这个字段是一个Collection类型，其中有字段进行字典翻译。

4. @DictTranslation

   使用这个注解的方法将进行字典翻译，其返回值类型需要是一个含有@Dcit、@DictCollection或@DictTranslation的类型或者集合类型

5. @DictMapper

   这个注解相比@Dict多了一个参数“fieldName”，给出翻译的源key。

6. @DictMap

   用于标记一个返回值是java.util.Map子类的方法。其中参数为@DictMapper数组
