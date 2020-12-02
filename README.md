### 适用于Spring Boot的字典字典翻译扩展

> 在常见的web应用中，常见的数据库字段会使用字典值，但是在数据查询时，我们需要将存储的字典值转换成对应的字典标签(value>>name)。常见的转换方式为从数据库查询、逻辑包装等。

#### 使用详情

1. 第一步，**实现DictCacheService接口**，并将这个对象交给Spring 容器，否则扩展将不会自动生效

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

2. 在需要翻译的方法或字段上使用扩展中定义的标签

   详情参照[演示demo](https://github.com/mujave/dict-traslate-starter/tree/main/dict-translate-demo)

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
