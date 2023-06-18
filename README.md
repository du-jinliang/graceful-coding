# 最佳实践

## 初始化Gradle工程，引入单元测试

### 软件运行环境：

- 语言：Java17
  - 最新的LTS版本
  - 一些新的语言特性：
    - var关键字来声明变量，Java编译器可以在编译期间就去通过类型推断来判断出类型
    - record 类似于Kotlin中的data class
- 构建工具：Gradle
  - 不像maven完全是使用XML去编写的工程配置文件
  - 可以在里面创建一些我们需要的构建脚本，更灵活
- module方式管理代码库
  - 当面对维护一个多工程的项目时，比如前端、后端、微服务可以在同一个窗口管理项目

### 初始化Gradle工程

####  使用idea创建Gradle工程

#### 使用命令行方式创建Gradle工程

> 使用Kotlin作为DSL来定义Gradle的脚本，因为Kotlin是一个静态语言，相对于动态语言，它可以在编译期间有更多的编译器带来的提示

1. 创建 `settings.gradle.kts`

2. 执行`gradle wrapper [--gradle-version=7.5.1]`命令，`gradle wrapper`会帮我们把gradle去包一层用gradlew命令去代替gradle命令，这样的话，需要更新gradle版本的时候，只需要更新wrapper的配置文件即可，这样就可以在不同的版本里面去随意切换并我的wrapper被提交到代码仓库以后，其他的协同开发者他去拉取了仓库， 可以保证不同的人在本地构建使用的Gradle版本都是一致的

3. 创建 ` build.gradle.kts` ，gradle可以左很多事情，构建是他的做核心的工作，告诉我这个脚本如何去把我的源码去编译成字节码 ，然后构建成哪些jar包，以及他里面有哪些依赖，这些都是在`build.gradle`里面去定义的。gradle包含很多插件，他预先帮我们设置了很多的gradle task，比如 Java

   - ```kotlin
     plugins {
         java
         `java-library` //编写类库
         application // 应用 增加一些应用打包的应用构建的 task
     }
     
     ```

4. 程序入口命名

   - Bootstrap
   - Application
   - Main
   - Entry

5. Gradle处理依赖

   - 第一个配置是告诉gradle要下载依赖的repository是哪，`mavenCentral()`

   - ```kotlin
     repositories {
         mavenCentral()
         jcenter() //弃用
     }
     ```

   - 导入依赖

   - ```kotlin
     dependencies {
         testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.3")
         testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.3")
     }
     ```

   - 当多个地方引用同一个版本时，常见的做法是声明一个变量去指定版本，比如JUPITER_VERSION的变量，这两个地方去引用这个变量，在gradle中可以通过bom文件去管理某一个项目组织下的所有依赖

   - ```kotlin
     dependencies {
         testImplementation(platform("org.junit:junit-bom:5.9.3"))
         testImplementation("org.junit.jupiter:junit-jupiter-api")
         testImplementation("org.junit.jupiter:junit-jupiter-engine")
     }
     
     tasks {
         test {
             useJUnitPlatform()
         }
     }	
     ```

6. 统一Java版本，如果不声明默认使用机器的Java版本

   - ```kotlin
     java {
         toolchain {
             languageVersion.set(JavaLanguageVersion.of(17))
         }
     }
     ```

   - 通过声明Java版本，不管我们在任何机器上去执行 ./gradlew build或者其他任务时Gradle都会现在当前机器上优先检索是否存在Java17版本的命令，如果没有Java17的话，他会到指定的地方去下载正确的版本并且进行运行，这样可以保证我们不管在CI上还是本机上开发都使用同样的Java版本进行构建，减少因为版本不一致导致的bug和错误

### 第一个测试

1. 第一测试通常称为冒烟测试——最基本的测试

   - ```java
     /**
      * @description: 冒烟测试
      * @author: DuJinliang
      * @create: 2023/6/17
      */
     public class SmokeTest {
         /**
          * 1+1=2
          */
         @Test
         public void one_plus_one_equals_two() {
             assertEquals(2, 1+1);
         }
     }
     ```

     

## 添加Spring支持，设置 Git Hooks

### 导入依赖

当导入springboot依赖时往往需要重复写starter的版本，可以使用之前提到的bom文件的方式导入

1. 引入springboot插件

   - ```kotlin
     plugins {
         java
         id("org.springframework.boot") version "2.6.4"
     }
     
     ```

2. 导入依赖

   - ```kotlin
     dependencies {
         implementation(platform(SpringBootPlugin.BOM_COORDINATES))
         implementation("org.springframework.boot:spring-boot-starter")
         implementation("org.springframework.boot:spring-boot-starter-web")
         implementation("org.springframework.boot:spring-boot-starter-actuator")
         testImplementation(platform("org.junit:junit-bom:5.9.3"))
         testImplementation("org.junit.jupiter:junit-jupiter-api")
         testImplementation("org.junit.jupiter:junit-jupiter-engine")
         testImplementation("org.springframework.boot:spring-boot-starter-test")
     }
     ```

     

3. 整体效果

   - ```kotlin
     import org.springframework.boot.gradle.plugin.SpringBootPlugin
     
     plugins {
         java
         id("org.springframework.boot") version "2.6.4"
     }
     
     repositories {
         mavenCentral()
     }
     
     dependencies {
         implementation(platform(SpringBootPlugin.BOM_COORDINATES))
         implementation("org.springframework.boot:spring-boot-starter")
         implementation("org.springframework.boot:spring-boot-starter-web")
         implementation("org.springframework.boot:spring-boot-starter-actuator")
         testImplementation(platform("org.junit:junit-bom:5.9.3"))
         testImplementation("org.junit.jupiter:junit-jupiter-api")
         testImplementation("org.junit.jupiter:junit-jupiter-engine")
         testImplementation("org.springframework.boot:spring-boot-starter-test")
     }
     
     java {
         toolchain {
             languageVersion.set(JavaLanguageVersion.of(17))
         }
     }
     
     tasks {
         test {
             useJUnitPlatform()
         }
     }
     
     ```

     

### 健康检查

使用 springboot actuator为项目提供健康检查

在测试类中进行健康测试

```java
package cn.wenhe9.question;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SmokeTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * 端点健康测试
     */
    @Test
    public void should_return_ok_when_request_endpoint_health() throws Exception {
        mockMvc
                .perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
        ;

    }
}	
```



### 设置 GitHooks

不管是任何的开发人员在本地进行提交代码的时候，测试总应该是通过了，我们不能允许没有通过测试的代码被提交到我们的远程仓库去。

可以使用 Git 的 Hooks 实现这个功能

.git 文件夹下有一个 hooks 文件夹里面有很多的示例文件，比如 pre-commit.sample，即在这个文件中写的bash脚本会在我们进行提交的时候被执行，如果脚本执行失败的话，那这次提交不会成功



通常在项目中建立一个 githooks 目录， 建立一个 pre-commit 文件

![image-20230618103634630](http://tuchuang.wenhe9.cn/img/202306181036725.png)

pre-commit 内容

```shell
# usr/bin/env sh

git stash -qku
./gradlew clean check
RESULT=$?
git stash pop -q
exit $RESULT
```

`git stash -qku` 是一个 Git 命令，用于将当前未提交的更改保存到一个临时区域中，以便稍后可以恢复这些更改。 

其中，`-q` 参数表示在执行命令时不输出任何提示信息；`-k` 参数表示在执行 `git stash` 命令时，不保存已经被 Git 跟踪的文件（即忽略 `.gitignore` 文件中指定的文件）；`-u` 参数表示在执行 `git stash` 命令时，同时保存未被 Git 跟踪的文件。

`git stash pop -q` 是一个 Git 命令，它的作用是将最近一次保存在 Git 存储区（stash）中的修改应用到当前分支，并从存储区中删除该次保存。其中 `-q` 选项表示以安静模式（quiet）运行，即不在控制台输出任何信息。



更改其为可执行文件

安装 gitHooks

1. 将这个文件copy到我们的.git目录下的hooks文件夹下，之后只要成员提交代码hooks里面的代码都会被执行，但是当我们更新了这个命令，比如安全检查、安全代码扫描，那么所有成员都需要重新将这个文件copy到目录下

2. 比较推荐的是 直接将git的hooks文件夹配置到该文件夹下

   - ```shell
     git config core.hooksPath githooks
     ```

   - 这样每次提交的时候git都会从项目根目录的githooks文件夹找hooks文件并执行



## 如何管理数据库脚本，并使用JPA实现持久化层

### 持久化框架选择

关于持久化框架，目前比较主流的是JPA（Java持久化规范）和 Mybatis

- 在我们使用CQRS（命令查询分离）的模式下，面向领域的逻辑代码，应该使用JPA这样的ORM，更利用代码的建模，并且提升代码的可读性
- 但面对数据的查询代码， 应该使用Mybatis，他能更利于复杂的查询以及SQL的优化
- 在我们构建大型应用时，他们可以共同存在，分别分则不同场景下的持久化支持



如何设计持久化层以及对持久化层的测试

设计Question实体

- @GeneratedValue(strategy = GenerationType.IDENTITY)
  - 指定id生成策略为自动生成
- 无参构造器的作用是 jpa 在查询的时候使用无参构造创建对象然后使用反射为对象设置值

```java
@Entity
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    private String questionerId;

    private String title;

    private String detail;

    protected Question() {
    }

    public Question(String questionerId, String title, String detail) {
        this.questionerId = questionerId;
        this.title = title;
        this.detail = detail;
    }

    public String getId() {
        return id;
    }

    public String getQuestionerId() {
        return questionerId;
    }

    public String getTitle() {
        return title;
    }

    public String getDetail() {
        return detail;
    }
}
```



设计仓储接口

```java
public interface QuestionRepository extends JpaRepository<Question, String> {

}
```



### 测试

此时我们只需要测试仓储层的方法，所以可以用`DataJpaTest`这样一个注解来只启动跟data-jpa相关的bean,这样的话可以使我们的测试更生资源、更快速得去完成测试



引入数据库驱动包

#### 数据库迁移

引入flyway组件

- 数据库的迁移或者data的migration
- 我们可以比较方便的去定义我们每一次数据库的schema的变更，通过版本管理的方式，在每次启动应用的时候，他会去构建我们的数据库的schema

- 在资源目录下定义 db/migration文件夹，在此目录下定义sql脚本

  - ​	![image-20230618142008933](http://tuchuang.wenhe9.cn/img/202306181420013.png)

  - ```sql
    create table question
    (
        id           serial primary key,
        questionerId text not null,
        title        text not null,
        detail       text
    )
    ```

  - 注意：

    - 迁移脚本一旦被执行以后，他是不可以被修改的，因为flyway会根据对整个文件去做一个md5，然后去进行一个检查
    - 以后如果要对一个表去新增字段或者删减字段的时候，那么我们应该在第二个版本的迁移脚本里面去做这样一个操作，不要直接更新我们已经编写好的flyway脚本



在应用配置文件中配置的 application.yml 数据源

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/question_service
    username: root
    password: '0209'
```



#### 测试容器

但是application.yml只是我们在运行期间它指定的这个数据源，如果我们想要在测试环境下去指定数据源，测试环境仍然是无法连接到这个数据库的，并且我们也不应该让我们的测试去依赖一个我们本地机器上的数据库

1. spring默认的会有一个H2的数据库，h2是一个`in-memory`的数据库，他会在数据库中模拟postgres运行

   - 比较简单
   - 并且不需要额外的其他依赖，就可以立刻启动这个数据库
   - 但是H2毕竟是另外一种数据库，他并不是我们实际生产中的数据库，两种数据库的实现方式可能是完全不同的，那他有很多的语法的支持以及查询的效果都可能不一样，可能会导致我们在测试里面可以通过的代码到实际生产中却有其他的bug

2. 使用一个 叫做 test-containers 框架

   - 帮助我们在启动测试的时候通过java-docker这样一个库连接到本地的docker-socks上，并且通过docker去启动一个对应的容器，然后在测试期间去连接这个容器进行我们的测试，这个容器可以是一个真实的mysql容器，运行结束以后这个容器就会销毁

   - ```kotlin
         testImplementation("org.testcontainers:testcontainers:1.16.3")
         testImplementation("org.testcontainers:mysql:1.16.3")
     ```

   - 创建一个测试的配置类

     - 使用`waitingFor(Wait.forListeningPort())`的作用是等容器完全启动成功后再去创建`dataSource`对象，才能够连接成功

     - 当我们手动指定dataSource的时候，我们需要手动关闭spring默认的`AutoConfigureTestDatabase`，即`@AutoConfigureTestDatabase*(replace = NONE)`，注意，这个注解需要在每一个测试类上都加

     - ```java
       /**
        * @description: 数据库测试配置类
        * @author: DuJinliang
        * @create: 2023/6/18
        */
       public class DatabaseTestConfiguration {
           @Bean(initMethod = "start", destroyMethod = "stop")
           public MySQLContainer<?> mySQLContainer() {
               return new MySQLContainer<>("mysql:8")
                       .withEnv("MYSQL_ROOT_HOST", "%")
                       .withEnv("MYSQL_ROOT_PASSWORD", "0209")
                       .withUsername("root")
                       .withPassword("0209")
                       .waitingFor(Wait.forListeningPort());
           }
       
           @Bean
           @FlywayDataSource
           public DataSource dataSource(MySQLContainer<?> mySQLContainer) {
               var hikariConfig = new HikariConfig();
               hikariConfig.setJdbcUrl(mySQLContainer.getJdbcUrl());
               hikariConfig.setUsername(mySQLContainer.getUsername());
               hikariConfig.setPassword(mySQLContainer.getPassword());
       
               return new HikariDataSource(hikariConfig);
           }
       }
       ```

     - 每次编写仓储测试的时候都需要引入三个注解

       - ```java
         @DataJpaTest //jpa测试
         @Import(DatabaseTestConfiguration.class) //引入容器和自定义数据源配置
         @AutoConfigureTestDatabase(replace = NONE) //排除spring默认的testDatabase
         ```

       - 使用自定义注解的方式，统一引入三个注解

         - ```java
           /**
            * @description: jpa测试注解
            * @author: DuJinliang
            * @create: 2023/6/18
            */
           @Target(ElementType.TYPE)
           @Retention(RetentionPolicy.RUNTIME)
           @Documented
           @Inherited
           @DataJpaTest
           @Import(DatabaseTestConfiguration.class)
           @AutoConfigureTestDatabase(replace = NONE)
           public @interface JpaRepositoryTest {
           }
           ```

完整测试类

```java
@JpaRepositoryTest
class RequisitionRepositoryTest {

    @Autowired
    private QuestionRepository questionRepository;

    /**
     * 测试仓储接口能够正确保存question并注入id
     */
    @Test
    public void repository_should_successfully_save_question() {
        var question = new Question("UID_0001", "A test title", "A test detail");

        var savedQuestion = questionRepository.save(question);

        assertThat(savedQuestion.getId(), is(notNullValue()));
        assertThat(savedQuestion.getQuestionerId(), equalTo(question.getQuestionerId()));
        assertThat(savedQuestion.getTitle(), equalTo(question.getTitle()));
        assertThat(savedQuestion.getDetail(), equalTo(question.getDetail()));
    }
}
```



## 代码静态检查，测试业务方法

### 通用配置和环境配置分离

项目的Spring配置文件`application.yml`,这个文件中，我们之前配置了数据库的地址以及用户名密码，并且这个文件会被Git提交到远程仓库，但这种作法是不被推荐的

- 对于代码库，他应该是一个与环境无关的纯代码的库
- 而配置文件有很多配置是与环境相关的，比如说数据库地址，数据库用户名和密码，在不同的开发手中，他的本机端口可能不是3306，用户名密码不同
- 运行在测试环境和生产环境中的代码，，那他使用的配置也不会是现在配置的这一套东西，这一段配置理论上来说，不应该被提交到远程，

通常来讲我们会新建一个本地配置，将环境相关配置放在本地配置文件中，这个文件我们是不希望被提交到远程仓库的，我们应该在.gitignore里面去添加这个文件

```yml
### idea
/.idea
*.iml
*.ipr
/out

### gradle
/.gradle
/build

### Environment Configuration
application-*.yml
```

向仓库中提交一个可以供参考的模板配置文件，只是去指定一个模板，而不是让不同的开发使用他 `application-env.template.yml`

```yml
### idea
/.idea
*.iml
*.ipr
/out

### gradle
/.gradle
/build

### Environment Configuration
application-*.yml
!application-env.template.yml
```



### 静态检查

增加 checkStyle 插件

```kotlin
plugins {
    java
    id("org.springframework.boot") version "2.6.4"
    checkstyle
}
```

添加配置

```kotlin
checkstyle {
    maxWarnings = 0 //最大警告数,即项目里不允许有不符合checkStyle规范的代码存在
    toolVersion = "10.0"
}
```

在项目的根目录中新增checkStyle配置文件

![image-20230618192117216](http://tuchuang.wenhe9.cn/img/202306181921279.png)

```xml-dtd
<?xml version="1.0"?>
<!DOCTYPE module PUBLIC
        "-//Checkstyle//DTD Checkstyle Configuration 1.3//EN"
        "https://checkstyle.org/dtds/configuration_1_3.dtd">
<module name="Checker">
    <property name="charset" value="UTF-8"/>
    <property name="severity" value="warning"/>
    <property name="fileExtensions" value="java, properties, xml"/>
    <module name="BeforeExecutionExclusionFileFilter">
        <property name="fileNamePattern" value="module\-info\.java$"/>
    </module>
    <module name="FileTabCharacter">
        <property name="eachLine" value="true"/>
    </module>
    <module name="LineLength">
        <property name="fileExtensions" value="java"/>
        <property name="max" value="160"/>
        <property name="ignorePattern" value="^package.*|^import.*|a href|href|http://|https://|ftp://"/>
    </module>
    <module name="TreeWalker">
        <module name="UnusedImports"/>
        <module name="OuterTypeFilename"/>
        <module name="IllegalTokenText">
            <property name="tokens" value="STRING_LITERAL, CHAR_LITERAL"/>
            <property name="format" value="\\u00(09|0(a|A)|0(c|C)|0(d|D)|22|27|5(C|c))|\\(0(10|11|12|14|15|42|47)|134)"/>
            <property name="message" value="Consider using special escape sequence instead of octal value or Unicode escaped value."/>
        </module>
        <module name="AvoidEscapedUnicodeCharacters">
            <property name="allowEscapesForControlCharacters" value="true"/>
            <property name="allowByTailComment" value="true"/>
            <property name="allowNonPrintableEscapes" value="true"/>
        </module>
        <module name="AvoidStarImport"/>
        <module name="OneTopLevelClass"/>
        <module name="NoLineWrap">
            <property name="tokens" value="PACKAGE_DEF, IMPORT, STATIC_IMPORT"/>
        </module>
        <module name="EmptyBlock">
            <property name="option" value="TEXT"/>
            <property name="tokens" value="LITERAL_TRY, LITERAL_FINALLY, LITERAL_IF, LITERAL_ELSE, LITERAL_SWITCH"/>
        </module>
        <module name="LeftCurly">
            <property name="tokens" value="ANNOTATION_DEF, CLASS_DEF, CTOR_DEF, ENUM_CONSTANT_DEF, ENUM_DEF, INTERFACE_DEF, LAMBDA, LITERAL_CASE, LITERAL_CATCH,
             LITERAL_DEFAULT, LITERAL_DO, LITERAL_ELSE, LITERAL_FINALLY, LITERAL_FOR, LITERAL_IF, LITERAL_SWITCH, LITERAL_SYNCHRONIZED, LITERAL_TRY,
             LITERAL_WHILE, METHOD_DEF, OBJBLOCK, STATIC_INIT, RECORD_DEF, COMPACT_CTOR_DEF"/>
        </module>
        <module name="RightCurly">
            <property name="id" value="RightCurlySame"/>
            <property name="tokens" value="LITERAL_TRY, LITERAL_CATCH, LITERAL_FINALLY, LITERAL_IF, LITERAL_ELSE, LITERAL_DO"/>
        </module>
        <module name="RightCurly">
            <property name="id" value="RightCurlyAlone"/>
            <property name="option" value="alone"/>
            <property name="tokens"
                      value="CLASS_DEF, METHOD_DEF, CTOR_DEF, LITERAL_FOR, LITERAL_WHILE, STATIC_INIT, INSTANCE_INIT, ANNOTATION_DEF, ENUM_DEF, INTERFACE_DEF, RECORD_DEF, COMPACT_CTOR_DEF"/>
        </module>
        <module name="SuppressionXpathSingleFilter">
            <property name="id" value="RightCurlyAlone"/>
            <property name="query" value="//RCURLY[parent::SLIST[count(./*)=1] or preceding-sibling::*[last()][self::LCURLY]]"/>
        </module>
        <module name="WhitespaceAfter">
            <property name="tokens" value="COMMA, SEMI, TYPECAST, LITERAL_IF, LITERAL_ELSE, LITERAL_WHILE, LITERAL_DO, LITERAL_FOR, DO_WHILE"/>
        </module>
        <module name="WhitespaceAround">
            <property name="allowEmptyConstructors" value="true"/>
            <property name="allowEmptyLambdas" value="true"/>
            <property name="allowEmptyMethods" value="true"/>
            <property name="allowEmptyTypes" value="true"/>
            <property name="allowEmptyLoops" value="true"/>
            <property name="ignoreEnhancedForColon" value="false"/>
            <property name="tokens" value="ASSIGN, BAND, BAND_ASSIGN, BOR, BOR_ASSIGN, BSR, BSR_ASSIGN, BXOR, BXOR_ASSIGN, COLON, DIV, DIV_ASSIGN, DO_WHILE,
            EQUAL, GE, GT, LAMBDA, LAND, LCURLY, LE, LITERAL_CATCH, LITERAL_DO, LITERAL_ELSE, LITERAL_FINALLY, LITERAL_FOR, LITERAL_IF, LITERAL_RETURN,
            LITERAL_SWITCH, LITERAL_SYNCHRONIZED, LITERAL_TRY, LITERAL_WHILE, LOR, LT, MINUS, MINUS_ASSIGN, MOD, MOD_ASSIGN, NOT_EQUAL, PLUS, PLUS_ASSIGN,
            QUESTION, RCURLY, SL, SLIST, SL_ASSIGN, SR, SR_ASSIGN, STAR, STAR_ASSIGN, LITERAL_ASSERT, TYPE_EXTENSION_AND"/>
            <message key="ws.notFollowed"
                     value="WhitespaceAround: ''{0}'' is not followed by whitespace. Empty blocks may only be represented as '{}' when not part of a multi-block statement (4.1.3)"/>
            <message key="ws.notPreceded" value="WhitespaceAround: ''{0}'' is not preceded with whitespace."/>
        </module>
        <module name="OneStatementPerLine"/>
        <module name="MultipleVariableDeclarations"/>
        <module name="ArrayTypeStyle"/>
        <module name="MissingSwitchDefault"/>
        <module name="FallThrough"/>
        <module name="UpperEll"/>
        <module name="ModifierOrder"/>
        <module name="EmptyLineSeparator">
            <property name="tokens" value="PACKAGE_DEF, IMPORT, STATIC_IMPORT, CLASS_DEF, INTERFACE_DEF, ENUM_DEF, STATIC_INIT, INSTANCE_INIT, METHOD_DEF, CTOR_DEF, VARIABLE_DEF, RECORD_DEF, COMPACT_CTOR_DEF"/>
            <property name="allowNoEmptyLineBetweenFields" value="true"/>
            <property name="allowMultipleEmptyLinesInsideClassMembers" value="false"/>
            <property name="allowMultipleEmptyLines" value="false"/>
        </module>
        <module name="SeparatorWrap">
            <property name="id" value="SeparatorWrapDot"/>
            <property name="tokens" value="DOT"/>
            <property name="option" value="nl"/>
        </module>
        <module name="SeparatorWrap">
            <property name="id" value="SeparatorWrapComma"/>
            <property name="tokens" value="COMMA"/>
            <property name="option" value="EOL"/>
        </module>
        <module name="SeparatorWrap">
            <property name="id" value="SeparatorWrapEllipsis"/>
            <property name="tokens" value="ELLIPSIS"/>
            <property name="option" value="EOL"/>
        </module>
        <module name="SeparatorWrap">
            <property name="id" value="SeparatorWrapArrayDeclarator"/>
            <property name="tokens" value="ARRAY_DECLARATOR"/>
            <property name="option" value="EOL"/>
        </module>
        <module name="SeparatorWrap">
            <property name="id" value="SeparatorWrapMethodRef"/>
            <property name="tokens" value="METHOD_REF"/>
            <property name="option" value="nl"/>
        </module>
        <module name="PackageName">
            <property name="format" value="^[a-z]+(\.[a-z][a-z0-9]*)*$"/>
            <message key="name.invalidPattern" value="Package name ''{0}'' must match pattern ''{1}''."/>
        </module>
        <module name="TypeName">
            <property name="tokens" value="CLASS_DEF, INTERFACE_DEF, ENUM_DEF, ANNOTATION_DEF, RECORD_DEF"/>
            <message key="name.invalidPattern" value="Type name ''{0}'' must match pattern ''{1}''."/>
        </module>
        <module name="MemberName">
            <property name="format" value="^[a-z][a-z0-9][a-zA-Z0-9]*$"/>
            <message key="name.invalidPattern" value="Member name ''{0}'' must match pattern ''{1}''."/>
        </module>
        <module name="ParameterName">
            <property name="format" value="^[a-z]([a-z0-9][a-zA-Z0-9]*)?$"/>
            <message key="name.invalidPattern" value="Parameter name ''{0}'' must match pattern ''{1}''."/>
        </module>
        <module name="LambdaParameterName">
            <property name="format" value="^[a-z]([a-z0-9][a-zA-Z0-9]*)?$"/>
            <message key="name.invalidPattern" value="Lambda parameter name ''{0}'' must match pattern ''{1}''."/>
        </module>
        <module name="CatchParameterName">
            <property name="format" value="^[a-z]([a-z0-9][a-zA-Z0-9]*)?$"/>
            <message key="name.invalidPattern" value="Catch parameter name ''{0}'' must match pattern ''{1}''."/>
        </module>
        <module name="LocalVariableName">
            <property name="format" value="^[a-z]([a-z0-9][a-zA-Z0-9]*)?$"/>
            <message key="name.invalidPattern" value="Local variable name ''{0}'' must match pattern ''{1}''."/>
        </module>
        <module name="PatternVariableName">
            <property name="format" value="^[a-z]([a-z0-9][a-zA-Z0-9]*)?$"/>
            <message key="name.invalidPattern" value="Pattern variable name ''{0}'' must match pattern ''{1}''."/>
        </module>
        <module name="ClassTypeParameterName">
            <property name="format" value="(^[A-Z][0-9]?)$|([A-Z][a-zA-Z0-9]*[T]$)"/>
            <message key="name.invalidPattern" value="Class type name ''{0}'' must match pattern ''{1}''."/>
        </module>
        <module name="RecordTypeParameterName">
            <property name="format" value="(^[A-Z][0-9]?)$|([A-Z][a-zA-Z0-9]*[T]$)"/>
            <message key="name.invalidPattern" value="Record type name ''{0}'' must match pattern ''{1}''."/>
        </module>
        <module name="MethodTypeParameterName">
            <property name="format" value="(^[A-Z][0-9]?)$|([A-Z][a-zA-Z0-9]*[T]$)"/>
            <message key="name.invalidPattern" value="Method type name ''{0}'' must match pattern ''{1}''."/>
        </module>
        <module name="NoFinalizer"/>
        <module name="GenericWhitespace">
            <message key="ws.followed" value="GenericWhitespace ''{0}'' is followed by whitespace."/>
            <message key="ws.preceded" value="GenericWhitespace ''{0}'' is preceded with whitespace."/>
            <message key="ws.illegalFollow" value="GenericWhitespace ''{0}'' should followed by whitespace."/>
            <message key="ws.notPreceded" value="GenericWhitespace ''{0}'' is not preceded with whitespace."/>
        </module>
        <module name="Indentation">
            <property name="basicOffset" value="4"/>
            <property name="braceAdjustment" value="4"/>
            <property name="caseIndent" value="4"/>
            <property name="throwsIndent" value="4"/>
            <property name="lineWrappingIndentation" value="4"/>
            <property name="arrayInitIndent" value="4"/>
        </module>
        <module name="AbbreviationAsWordInName">
            <property name="ignoreFinal" value="false"/>
            <property name="allowedAbbreviationLength" value="0"/>
            <property name="tokens"
                      value="CLASS_DEF, INTERFACE_DEF, ENUM_DEF, ANNOTATION_DEF, ANNOTATION_FIELD_DEF, PARAMETER_DEF, VARIABLE_DEF, METHOD_DEF, PATTERN_VARIABLE_DEF, RECORD_DEF, RECORD_COMPONENT_DEF"/>
        </module>
        <module name="OverloadMethodsDeclarationOrder"/>
        <module name="VariableDeclarationUsageDistance"/>
        <module name="MethodParamPad">
            <property name="tokens" value="CTOR_DEF, LITERAL_NEW, METHOD_CALL, METHOD_DEF, SUPER_CTOR_CALL, ENUM_CONSTANT_DEF, RECORD_DEF"/>
        </module>
        <module name="NoWhitespaceBefore">
            <property name="tokens" value="COMMA, SEMI, POST_INC, POST_DEC, DOT, LABELED_STAT, METHOD_REF"/>
            <property name="allowLineBreaks" value="true"/>
        </module>
        <module name="ParenPad">
            <property name="tokens"
                      value="ANNOTATION, ANNOTATION_FIELD_DEF, CTOR_CALL, CTOR_DEF, DOT, ENUM_CONSTANT_DEF, EXPR, LITERAL_CATCH, LITERAL_DO, LITERAL_FOR, LITERAL_IF, LITERAL_NEW, LITERAL_SWITCH, LITERAL_SYNCHRONIZED, LITERAL_WHILE, METHOD_CALL, METHOD_DEF, QUESTION, RESOURCE_SPECIFICATION, SUPER_CTOR_CALL, LAMBDA, RECORD_DEF"/>
        </module>
        <module name="OperatorWrap">
            <property name="option" value="NL"/>
            <property name="tokens"
                      value="BAND, BOR, BSR, BXOR, DIV, EQUAL, GE, GT, LAND, LE, LITERAL_INSTANCEOF, LOR, LT, MINUS, MOD, NOT_EQUAL, PLUS, QUESTION, SL, SR, STAR, METHOD_REF, TYPE_EXTENSION_AND "/>
        </module>
        <module name="AnnotationLocation">
            <property name="id" value="AnnotationLocationMostCases"/>
            <property name="tokens" value="CLASS_DEF, INTERFACE_DEF, ENUM_DEF, METHOD_DEF, CTOR_DEF, RECORD_DEF, COMPACT_CTOR_DEF"/>
        </module>
        <module name="AnnotationLocation">
            <property name="id" value="AnnotationLocationVariables"/>
            <property name="tokens" value="VARIABLE_DEF"/>
            <property name="allowSamelineMultipleAnnotations" value="true"/>
        </module>
        <module name="NonEmptyAtclauseDescription"/>
<!--        <module name="InvalidJavadocPosition"/>-->
<!--        <module name="JavadocTagContinuationIndentation"/>-->
<!--        <module name="SummaryJavadoc">-->
<!--            <property name="forbiddenSummaryFragments" value="^@return the *|^This method returns |^A [{]@code [a-zA-Z0-9]+[}]( is a )"/>-->
<!--        </module>-->
<!--        <module name="JavadocParagraph"/>-->
        <module name="RequireEmptyLineBeforeBlockTagGroup"/>
        <module name="AtclauseOrder">
            <property name="tagOrder" value="@param, @return, @throws, @deprecated"/>
            <property name="target" value="CLASS_DEF, INTERFACE_DEF, ENUM_DEF, METHOD_DEF, CTOR_DEF, VARIABLE_DEF"/>
        </module>
        <module name="MethodName">
            <property name="format" value="^[a-z][a-z0-9][a-zA-Z0-9_]*$"/>
            <message key="name.invalidPattern" value="Method name ''{0}'' must match pattern ''{1}''."/>
        </module>
<!--        <module name="SingleLineJavadoc"/>-->
        <module name="EmptyCatchBlock">
            <property name="exceptionVariableName" value="expected"/>
        </module>
        <module name="CommentsIndentation">
            <property name="tokens" value="SINGLE_LINE_COMMENT, BLOCK_COMMENT_BEGIN"/>
        </module>
        <module name="SuppressionXpathFilter">
            <property name="file" value="${org.checkstyle.google.suppressionxpathfilter.config}" default="checkstyle-xpath-suppressions.xml"/>
            <property name="optional" value="true"/>
        </module>
    </module>
</module>

```

执行`./gradlew check`会去执行除了 测试方法的task方法外，还会去执行类似 checkStyle这样的一些检查插件，在check的时候就会对静态样式进行检查



### 业务开发

#### record 纪录类

对于纯pojo而言，在Java16之后可以使用 record 记录类来创建，类似于Kotlin的data class类型，使用时会自动生成他的所有字段的get方法，并且这些字段都是 immutable 的，即无法修改的

```java
/**
 * @description: 创建问题命令
 * @author: DuJinliang
 * @create: 2023/6/18
 */
public record CreateQuestionCmd(
        String questionerId,
        String title,
        String detail
) {
}
```



### spring 注入

如果使用字段注入的话，使用`@Autowired`对Spring框架的依赖时比较重的，如果没有依赖Spring框架很难去初始化这样一个对象，并且尝试对这样的bean进行测试的话，甚至都无法很好的构造出来，因为Spring可能还要通过反射去注入的，那么比较的推荐的就是通过构造器的方式注入，为了防止我们创建对应的构造器这件事，比较推荐的是将这种字段全部都声明为final类型

- 使用final的优点，没有办法在业务代码中去替换调实现
- 强制要求把参数放到构造器上，这样的话帮我们不要忘记写这个方法，有了构造方法spring才能通过构造器的方式注入bean

```java
@Transactional
@Service("questionCommandServiceImpl")
public class QuestionCommandServiceImpl implements QuestionCommandService {

    private final QuestionRepository questionRepository;

    public QuestionCommandServiceImpl(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public QuestionCreateResult createQuestion(CreateQuestionCmd cmd) {
        var question = new Question(cmd.questionerId(), cmd.title(), cmd.detail());
        questionRepository.save(question);
        return new QuestionCreateResult(question.getId());
    }
}
```



### 事务边界

添加事务边界，应用服务的每一个方法其实就可以理解为我们的一个最小的事务单元，在类上加一个 `@Transactional`注解，这样每当我们调用一个方法的时候，他就会开启一个事务

- spring data jpa 有一个默认的参数
  - open-in-view : true
  - 他的意思是指，jpa会在每个web请求进来的时候就会开启一个数据库的连接会话，开启一个事务，直到请求结束，session关闭，事务才会关闭提交，
  - 如果这个配置开启的话，我们内容创建的手动声明的事务是被覆盖的



### 测试

使用MockMvc来帮我们编写这个测试，需要添加一个注解 `@WebMvcTest`

这里使用到了Java17的多行字符串的特性 """ """"

在这个测试中，我们只启动了 WebMvc 这一层的 bean，他还需要依赖我们的应用服务，在测试的时候，我们不希望他把所有东西都启动起来，所以我们需要把应用服务 mock 掉， 所以只需要将他声明成一个 `@MockBean`即可,这样spring就直接可以把他变成一个mock的状态了，之后我们就可以使用 given 的方式给他指定返回值

但是使用这个字符串模板的话，可能会有一个缺点，如果对象比较复杂的话，会导致整个测试大段的代码都是定义了这个json对象，在测试的 resources 包下面去用文件去这些请求body,如果希望编写更多测试，也可以在编写更多json对象，也可以去继续完善这个测试，也可以写一些其他异常情况的测试也是一样的方式

![image-20230618202856687](http://tuchuang.wenhe9.cn/img/202306182028745.png)

```json
{
  "questionerId": "UID_00001",
  "title": "A title for test",
  "detail": "A detail for test"
}

```



```java
package cn.wenhe9.question.interfaces.question.face.rest;

import cn.wenhe9.question.domian.application.command.QuestionCommandService;
import cn.wenhe9.question.domian.application.result.QuestionCreateResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @description:
 * @author: DuJinliang
 * @create: 2023/6/18
 */
@WebMvcTest
class QuestionCommandRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuestionCommandService questionCommandService;

    @Test
    void should_return_ok_when_create_question() throws Exception {
        var questionId = "1";
        given(questionCommandService.createQuestion(any())).willReturn(new QuestionCreateResult(questionId));

        var requestBody = new ClassPathResource("request/question/create-question/200-ok.json").getInputStream().readAllBytes();

        mockMvc
                .perform(
                    post("/questions/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.questionId").value(questionId))
        ;
    }
}

```

