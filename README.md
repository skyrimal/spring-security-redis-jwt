# spring-security-redis-jwt
spring-security简单整合reids实现通过jwt单点登录

学习课程来自尚硅谷哔哩哔哩免费课程[尚硅谷SpringSecurity框架教程（spring security源码剖析从入门到精通）](https://www.bilibili.com/video/BV15a411A7kP)感激

最后小小吐槽一下，老师的细节讲的不是特别好，尤其是HttpSecurity。。。

学习使用的项目，所有的一切都以最简单的方法实现

目标是学习jwt令牌进行登录验证

## pom

简单的整合了web-security-mybatis-mysqlconnect-redis-jjwt

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.mybatis.spring.boot</groupId>
        <artifactId>mybatis-spring-boot-starter</artifactId>
        <version>2.2.0</version>
    </dependency>

    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-test</artifactId>
        <scope>test</scope>
    </dependency>

    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt</artifactId>
        <version>0.9.1</version>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <configuration>
                <excludes>
                    <exclude>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                    </exclude>
                </excludes>
            </configuration>
        </plugin>
    </plugins>
    <!-- xml资源目录配置 -->
    <resources>
        <resource>
            <directory>src/main/resources</directory>
            <filtering>true</filtering>
        </resource>
        <resource>
            <directory>src/main/java</directory>
            <includes>
                <include>**/*.xml</include>
            </includes>
        </resource>
    </resources>
</build>
```

## yml

yml文件，配置了端口、数据库、redis和mybatis的xml位置

```yml
server:
  port: 8888
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/cloud_test?serverTimezone=Asia/Shanghai
    username: root
    password: toor
  redis:
    host: 127.0.0.1
mybatis:
  mapper-locations: classpath*:org/learn/security_redis_login/**/mapper/xml/*.xml
```

## 项目目录结构

![image-20210819134049177](README.assets/image-20210819134049177.png)

jwt中是jwtToken的编译解析工具

redis里面是一个普通的redis存储获取的工具

security里面装的是：

​			登录、认证两个过滤器；user实体类；UserDetailsService实现类及userDetailMapper

			>这里注意一下，因为UserDetails的默认实现中没有username+password的两参构造器，且没有无参构造器，
			>
			>所以mybatis的xml进行映射实体类时会出错，这里我写的SysUser作用就是重写构造器，实际上完全可以使用跟数据库对应的实体类UserDO
			>
			>这里我直接使用继承security的user是因为重写简单，项目定义的User实体跟UserDetail同系，是因为如果涉及其他操作起来更简单，但实际上，没有其他操作了。后面再考虑改不改

![image-20210819134202660](README.assets/image-20210819134202660.png)

config里是redis通用配置和security配置

test.controller里面是测试权限和验证用的test



## ApplicationMain

```java
@SpringBootApplication
@MapperScan("org.learn.security_redis_login.**.mapper")
public class SecurityRedisLoginApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecurityRedisLoginApplication.class, args);
    }

}
```

## SecurityConfig

```java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private RedisDao redisDao;
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilter(new TokenLoginFilter(redisDao,this.authenticationManager())).addFilter(new TokenAuthFilter(this.authenticationManager(),redisDao));

        http.formLogin().permitAll().and().authorizeRequests().antMatchers("/","/login/**").permitAll().anyRequest().authenticated();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
```

## RedisConfig

```java
@Configuration
public class RedisConfig {


    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory factory){
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = this.jackson2JsonRedisSerializer();

        //String序列化
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        //key采用string的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        //hash的key采用string的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        //value序列化也采用jackson
        template.setValueSerializer(jackson2JsonRedisSerializer);
        //hash的value也采用jackson
        template.setHashValueSerializer( jackson2JsonRedisSerializer);
        template.afterPropertiesSet();

        return template;
    }

    /**
     * 自定义jackson2JsonRedisSerializer对象
     * @return jackson2JsonRedisSerializer
     */
    private Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer() {
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer =
                new Jackson2JsonRedisSerializer<>(Object.class);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.configure(MapperFeature.USE_ANNOTATIONS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // 此项必须配置，否则会报java.lang.ClassCastException: java.util.LinkedHashMap cannot be cast to XXX
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,ObjectMapper.DefaultTyping.NON_FINAL
                , JsonTypeInfo.As.PROPERTY);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        return jackson2JsonRedisSerializer;
    }
```