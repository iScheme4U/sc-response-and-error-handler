# 异常与错误码推荐的处理方式

## 1. 背景

曾经，我去面试过一家公司，面试官问我这样的问题：”你觉得在开发过程中，异常处理的代码大概占全部代码的多少比例？”

当时的我可能还是涉世未深、对异常处理的理解没有那么深刻，回答的是“30%左右吧”。

面试官当即就笑了：“小伙子，你还太年轻了，你说的可能要调转一下，正常的业务代码可能就占30%左右，其他的几乎全是异常处理。”

后面经历了多家公司的洗礼后，发现诚如当年那位大哥所说，异常处理的代码的确占据了绝大部分。

这里需要提一下"二八原则"，业务逻辑通常来说是没有太大难度的，可能就占据全部代码的 20% 左右，其他的 80% 都是用来处理异常、流量控制等提升程序健壮性、稳定性的代码。可以说异常处理是必不可少的，也是极其重要的。所以代码中就会出现大量的 try {...} catch(...) {...} finally {...} 代码块，而且很多这样的异常处理逻辑都是相似的。这不仅会导致大量的代码冗余，而且也会影响的代码的可读性。

请看如下两种风格的代码：

优化前的代码：

```Java
@RequestMapping(value = "/login", method = RequestMethod.POST)
@ResponseBody
public R login(@Validated @RequestBody UserLoginParam param) {
    try{
        String token = userService.login(param.getUsername(), param.getPassword());
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        return R.success(tokenMap);
    } catch (BusinessException e){
        log.warn("登录异常:{}", e.getMessage());
        return R.failed("登录异常:" + e.getMessage());
    } catch (Exception e){
        log.warn("登录异常:{}", e.getMessage());
        return R.failed("登录异常:" + e.getMessage());
    }
    return R.failed("登录异常");
}
```

优化后的代码：

```Java
@RequestMapping(value = "/login", method = RequestMethod.POST)
@ResponseBody
public R login(@Validated @RequestBody UserLoginParam param) {
    String token = userService.login(param.getUsername(), param.getPassword());
    Map<String, String> tokenMap = new HashMap<>();
    tokenMap.put("token", token);
    return R.success(tokenMap);
}
```

这两种风格的代码，我相信大部分的人都倾向于优化后的代码吧。可以看到，优化后的代码只处理了核心的业务逻辑，那么异常处理这一块的代码去哪里了呢？

## 2. 统一异常处理

回答上面的问题，就要提到 **Spring 3.2** 版本之后加入的注解：`@ControllerAdvice`，请看该注解的官方说明（截取一部分）：

```Java
/**
 * Specialization of @Component for classes that declare @ExceptionHandler, @InitBinder, 
 * or @ModelAttribute methods to be shared across multiple @Controller classes.
 * ...
 * By default, the methods in an @ControllerAdvice apply globally to all controllers. 
 */
```

翻译过来就是：

```Java
/**
 * 对声明了 @ExceptionHandler、@InitBinder 或 @ModelAttribute 方法的类的 @Component 进行特殊处理，
 * 将这些方法在多个 @Controller 类之间共享。
 * ...
 * 默认情况下，@ControllerAdvice 中的添加了以上注解的方法会应用于全局所有的控制器。
 */
```

我们再来看 `@ExceptionHandler` 注解的说明文档（截取一部分）：

```Java
/**
 * Annotation for handling exceptions in specific handler classes and/or handler methods.
 * Handler methods which are annotated with this annotation are allowed to have very flexible signatures. 
 * They may have parameters of the following types, in arbitrary order:
 * - An exception argument: declared as a general Exception or as a more specific exception. 
 *      This also serves as a mapping hint if the annotation itself does not narrow the exception types 
 *      through its value().
 * The following return types are supported for handler methods:
 * - @ResponseBody annotated methods (Servlet-only) to set the response content. 
 *      The return value will be converted to the response stream using message converters.
 */
```

翻译过来就是：

```Java
/**
 * 用于处理特定处理类和/或方法中的异常的注解。
 * 使用此注解注解的处理方法允许具有非常灵活的方法签名。
 * 它们可能具有以下类型的参数：
 * - Exception 参数：声明为一般异常或更具体的异常。
 *      如果注解本身没有通过它的 value 参数来缩小异常类型，这也可以作为一个映射提示。
 * 处理方法支持以下返回类型：
 * - 使用注解 @ResponseBody 注解的方法（仅限 Servlet），以设置响应内容。返回值将使用消息转换器转换为响应流。
 */
```

从以上的说明，我们可以知道，如果在程序中定义一个类，并将此类添加上 `@ControllerAdvice`
注解，然后在此类中添加一个（或多个）方法，方法的参数为想要处理的异常类，方法的返回值为需要返回的响应报文，并在方法上添加上 `@ExceptionHandler` 注解和 `@ResponseBody`
注解，那么我们就可以实现异常的统一处理了。

### 2.1 实战

上面讲的是理论，接下来进行实战。

#### 2.1.1 无统一异常处理 

1. 添加 `BusinessException`：

```java
public class BusinessException extends RuntimeException {
    // 此处为节省篇幅省略构造方法定义，继承RuntimeException的构造方法即可
}
```

2. 添加 `UserService` 接口：

```java
public interface UserService {
    boolean login(String username, String password);
}
```

3. 创建 `UserServiceImpl`，实现 `UserService` 接口：

```java
@Service
public class UserServiceImpl implements UserService {

    @Override
    public boolean login(String username, String password) {
        if (username == null) {
            throw new BusinessException("User name cannot be null");
        }
        if (password == null) {
            throw new BusinessException("Password cannot be null");
        }
        // 此处只做演示作用，只允许admin登录，密码为123456
        if (!"admin".equals(username) || !"123456".equals(password)) {
            throw new BusinessException("User name or password is incorrect");
        }
        return true;
    }
}
```

4. 添加 `UserController`：

```java
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Login")
    @GetMapping(value = "/login")
    public String login(@RequestParam(value = "username") String username,
                        @RequestParam(value = "password") String password) {
        boolean result = userService.login(username, password);
        if (result) {
            return "success";
        }
        return "failure";
    }

}
```

##### 2.1.1.1 测试

1. 正常使用 admin/123456 登录，应能成功：

```shell
curl -X 'GET' 'http://localhost:8080/user/login?username=admin&password=123456' -H 'accept: */*'
```

返回：`success`

2. 使用错误的用户名和密码登录，会抛出`BusinessException`异常：

```shell
curl -X 'GET' 'http://localhost:8080/user/login?username=wronguser&password=123456' -H 'accept: */*'
```

返回信息为：

```json
{
  "timestamp": "2022-04-19T06:59:34.556+00:00",
  "status": 500,
  "error": "Internal Server Error",
  "trace": "com.soulcraft.demo.errorhandling.demo1.exception.BusinessException: User name or password is incorrect...."
}
```

##### 2.1.1.2 小结

此处返回的错误信息包含了堆栈信息，如果在前端展示对用户来说是十分不友好的。

具体代码示例可参见：[demo1](https://gitee.com/soulcraft/error-handling-demo/tree/main/demo1) 。

#### 2.1.2 异常处理进行统一处理

接下来，我们就为这个程序添加统一的异常处理。

`Controller`、`Service` 实现与上面完全一样，只需添加一个 `UnifiedExceptionHandler`，统一处理异常。

注意：需要在此类上添加了 **`@ControllerAdvice`** 注解。

```java
@Slf4j
@ControllerAdvice
public class UnifiedExceptionHandler {

    @ExceptionHandler(value = BusinessException.class)
    @ResponseBody
    public String handleBusinessException(BusinessException e) {
        log.error(e.getMessage(), e);
        return "failure: " + e.getLocalizedMessage();
    }
}
```

##### 2.1.2.1 测试

使用错误的用户名和密码登录，不再抛出 `BusinessException` 异常：

```shell
curl -X 'GET' 'http://localhost:8080/user/login?username=wronguser&password=123456' -H 'accept: */*'
```

返回信息为：

```text
failure: User name or password is incorrect
```

##### 2.1.2.2 小结

由此可见，只需添加一个统一的异常处理类即可将所有 `Controller` 中抛出的所有 `BusinessException` 统一到指定的方法中处理了。

既简单，又方便，代码又美观！何乐而不为呢？

具体代码示例可参见：[demo2](https://gitee.com/soulcraft/error-handling-demo/tree/main/demo2) 。

## 3. 使用Asserts断言

我们发现，在 `Service` 中，有许多 `if (xx == null) {}` 这样的判断逻辑存在，这样的代码块是否可以继续优化呢？

```java
public boolean login(String username, String password) {
    if (username == null) {
        throw new BusinessException("User name cannot be null");
    }
    if (password == null) {
        throw new BusinessException("Password cannot be null");
    }
    // 此处只做演示作用，只允许admin登录，密码为123456
    if (!"admin".equals(username) || !"123456".equals(password)) {
        throw new BusinessException("User name or password is incorrect");
    }
    return true;
}
```

让我们再想想，我们是否可以参照 `JUnit` 框架中的 `Assertions` 类的处理？即对程序逻辑进行断言，如果断言不成立，则抛出异常，如果断言成立，则程序继续运行下一行代码。

优化后的代码是这样：

```java
public boolean login(String username, String password) {
    Asserts.assertNotEmpty(username, "User name cannot be null or empty");
    Asserts.assertNotEmpty(password, "Password cannot be null or empty");
    // 此处只做演示作用，只允许admin登录，密码为123456
    if (!"admin".equals(username) || !"123456".equals(password)) {
        throw new BusinessException("User name or password is incorrect");
    }
    return true;
}
```

在 `Asserts.assertNotEmpty` 中，如果检查参数为 `null` 或者字符串长度为空，则抛出 `BusinessException`：

```java
public final class Asserts {

    public static void assertNotEmpty(String obj, String message) {
        if (obj == null || obj.isEmpty()) {
            throw new BusinessException(message);
        }
    }
}
```

### 3.1 测试

使用空的用户名和非空密码登录：

```shell
curl -X 'GET' 'http://localhost:8080/user/login?username=&password=123456' -H 'accept: */*'
```

返回信息为：

```text
failure: User name cannot be null or empty
```

测试通过。

### 3.2 小结

这样代码又清爽很多。

具体代码示例可参见：[demo3](https://gitee.com/soulcraft/error-handling-demo/tree/main/demo3) 。

## 4. 不同异常的处理方式

按照上面的方法，虽然代码清爽了很多，但是所有的异常抛出的都是 `BusinessException`，这样子很不好区分到底后台是发生了什么样的错误，前端需要如何进行处理。

### 4.1 创建不同的异常类

我们可以针对不同的异常场景，创建不同的异常类：

```java
public boolean login(String username, String password) {
    if (username == null) {
        throw new InvalidParameterException("User name cannot be null");
    }
    if (password == null) {
        throw new InvalidParameterException("Password cannot be null");
    }
    // 此处只做演示作用，只允许admin登录，密码为123456
    if (!"admin".equals(username) || !"123456".equals(password)) {
        throw new UserLoginException("User name or password is incorrect");
    }
    return true;
}
```

上面这段代码我们需要新建两个异常类：`InvalidParameterException`、`UserLoginException`，都继承自 `BusinessException`。这样的话，我们虽然解决了区分不同异常的问题，但是每种不同的场景，我们就需要新增一个异常类，就会造成程序中有许多异常类。并且每个异常对应的错误码也没有定义。这样的方法明显不是一个很好的方法。

那么应该如何解决这个问题，并给每个异常加上不同的错误码呢？

### 4.2 期望的效果

我们想要的效果应该是如下所示：

```java
public boolean login(String username, String password) {
    UserResponse.USERNAME_CANNOT_BE_EMPTY.assertStringNotEmpty(username);
    UserResponse.PASSWORD_CANNOT_BE_EMPTY.assertStringNotEmpty(password);
    if (!"admin".equals(username) || !"123456".equals(password)) {
        UserResponse.USER_LOGIN_FAILED.throwNewException();
    }
    return true;
}
```

1. 当用户名为空时，前端收到的返回信息为：

```json
{
   "code": 600,
   "message": "Username cannot be null or empty"
}
```

2. 当用户名不为空、密码为空时，前端收到的返回信息为：

```json
{
   "code": 601,
   "message": "Password cannot be null or empty"
}
```

3. 当用户名不为空、密码不为空，但是用户名或密码不正确时，前端收到的返回信息为：

```json
{
   "code": 602,
   "message": "User login failed"
}
```

我们可以通过错误码和错误信息很明确地知道究竟是发生了什么异常。

### 4.3 如何实现

那么应该如何实现如上描述的效果呢？

1. 首先，我们定义一个响应报应的接口 `IResponse`，它只包括两个元素，`int` 类型的 `code`（错误码）以及 `String` 类型的 `message`（错误信息）：

```java
public interface IResponse {
    int getCode();
    String getMessage();
}
```

2. 定义一个基础的实现类 `BaseResponse`：

```java
@Getter
@Setter
public abstract class BaseResponse implements IResponse {
    private int code;
    private String message;

    public BaseResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
```
3. 定义一个错误响应类 `ErrorResponse`，它继承自 `BaseResponse`，用于定义错误的返回报文：

```java
public class ErrorResponse extends BaseResponse {

    public ErrorResponse(IResponse response) {
        super(response.getCode(), response.getMessage());
    }
}
```

4. 定义一个异常基类 `BaseException`，将 `IResponse` 作为其成员变量，这样，我们就将错误码信息包含到了异常类中：

```java
@Getter
public class BaseException extends RuntimeException {
    private final IResponse response;

    public BaseException(IResponse response) {
        this(response, null);
    }

    public BaseException(IResponse response, Throwable cause) {
        super(response.getMessage(), cause);
        this.response = response;
    }
}
```

5. 修改之前的 `BusinessException` 的实现方式，改成继承自 `BaseException`：

```java
@Getter
public class BusinessException extends BaseException {

    public BusinessException(IResponse response) {
        super(response);
    }

    public BusinessException(IResponse response, Throwable cause) {
        super(response, cause);
    }
}
```

6. 定义一个 `Assert` 接口，作为断言的基础接口，在接口中实现了许多默认的方法：

```java
public interface Assert {
    /**
     * 创建异常
     *
     * @return BaseException 基础异常
     */
    BaseException newException();

    /**
     * 抛出异常
     */
    default void throwNewException() throws BaseException {
        throw newException();
    }

    /**
     * 创建异常
     *
     * @param cause 原因
     * @return BaseException 基础异常
     */
    BaseException newException(Throwable cause);

    /**
     * 抛出异常
     *
     * @param cause 原因
     */
    default void throwNewException(Throwable cause) throws BaseException {
        throw newException(cause);
    }

    /**
     * 断言条件为真，否则抛出异常
     *
     * @param condition 检查条件
     */
    default void assertTrue(boolean condition) {
        if (!condition) {
            throwNewException();
        }
    }

    /**
     * 断言条件为假，否则抛出异常
     *
     * @param condition 检查条件
     */
    default void assertFalse(boolean condition) {
        if (condition) {
            throwNewException();
        }
    }

    /**
     * 断言对象为空，否则抛出异常
     *
     * @param obj 检查的对象
     */
    default void assertNull(Object obj) {
        assertTrue(obj == null);
    }

    /**
     * 断言对象非空，否则抛出异常
     *
     * @param obj 检查的对象
     */
    default void assertNotNull(Object obj) {
        assertTrue(obj != null);
    }
    
    /**
     * 断言字符串非空，否则抛出异常
     *
     * @param str 检查元素
     */
    default void assertStringNotEmpty(String str) {
        assertTrue(str != null && !str.isEmpty());
    }
}
```

7. 仔细分析上述 `Assert` 接口可以发现，其实该接口只有两个抽象方法（用于创建具体的异常对象），其他方法都已有默认的实现了。此处我们针对 `BusinessException` 扩展一个接口 `BusinessExceptionAssert`：

```java
public interface BusinessExceptionAssert extends IResponse, Assert {

    @Override
    default BusinessException newException() {
        return new BusinessException(this);
    }

    @Override
    default BusinessException newException(Throwable cause) {
        return new BusinessException(this, cause);
    }

}
```

注意，此接口还继承了 `IResponse`，用于定义错误码及错误信息。

8. 接下来是关键步骤，我们针对用户模块定义一个 `UserResponse`，用于定义所有用户相关的错误码及错误信息，它是一个枚举，并且实现了 `BusinessExceptionAssert` 接口：

```java
@Getter
@AllArgsConstructor
public enum UserResponse implements BusinessExceptionAssert {

    USERNAME_CANNOT_BE_EMPTY(600, "Username cannot be null or empty"),
    PASSWORD_CANNOT_BE_EMPTY(601, "Password cannot be null or empty"),
    USER_LOGIN_FAILED(602, "User login failed"),
    ;
    
    private int code;
    private String message;
}
```

从前文我们可以知道，`Assert` 大部分方法已有默认实现，`BusinessExceptionAssert` 继承了 `Assert` 接口，并提供了创建异常对象的默认实现。此处 `UserResponse` 实现 `BusinessExceptionAssert` 接口，其实 `Assert` 接口端的方法均已有默认实现，它只需要实现 `IResponse` 接口的两个方法即可。

为了实现 `IResponse` 接口的两个方法，我们定义了两个成员变量：`int code` 和 `String message`，并结合 `lombok` 的 `@Getter` 注解生成对应的 get 方法。我们还使用 `@AllArgsConstructor` 注解，生成带所有成员变量作为参数的构造方法。

接下来，我们只需要在这个枚举类中定义枚举即可创建对应的异常对象，也就是说，这里面定义的每个枚举就对应一个异常（`BusinessException`）对象。无需再创建一堆的异常类了。

9. 修改异常统一处理类 `UnifiedExceptionHandler`，修改其返回类型为 `ErrorResponse`，最终如下：

```java
@Slf4j
@ControllerAdvice
public class UnifiedExceptionHandler {

    @ExceptionHandler(value = BusinessException.class)
    @ResponseBody
    public ErrorResponse handleBusinessException(BusinessException e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse(e.getResponse());
    }
}
```

#### 4.3.1 测试

1. 当用户名为空时，前端收到的返回信息为：

```json
{
   "code": 600,
   "message": "Username cannot be null or empty"
}
```

2. 当用户名不为空、密码为空时，前端收到的返回信息为：

```json
{
   "code": 601,
   "message": "Password cannot be null or empty"
}
```

3. 当用户名不为空、密码不为空，但是用户名或密码不正确时，前端收到的返回信息为：

```json
{
   "code": 602,
   "message": "User login failed"
}
```

#### 4.3.2 小结

到此，我们就算实现了前文描述的期望的效果了。代码非常简洁明了：

```java
public boolean login(String username, String password) {
    UserResponse.USERNAME_CANNOT_BE_EMPTY.assertStringNotEmpty(username);
    UserResponse.PASSWORD_CANNOT_BE_EMPTY.assertStringNotEmpty(password);
    if (!"admin".equals(username) || !"123456".equals(password)) {
        UserResponse.USER_LOGIN_FAILED.throwNewException();
    }
    return true;
}
```

`UserResponse.USERNAME_CANNOT_BE_EMPTY.assertStringNotEmpty(username)`，一行代码，即会判断 `username` 是否为空，如果为空则会抛出 `BusinessException` 异常，异常中包含了错误码及错误信息。

`UserResponse.PASSWORD_CANNOT_BE_EMPTY.assertStringNotEmpty(password)` 也是同理。

`UserResponse.USER_LOGIN_FAILED.throwNewException()` 则会直接抛出 `USER_LOGIN_FAILED` 的异常。

我们也可将检查用户名与密码是否匹配的代码修改为断言的风格，如下：

```java
public boolean login(String username, String password) {
    UserResponse.USERNAME_CANNOT_BE_EMPTY.assertStringNotEmpty(username);
    UserResponse.PASSWORD_CANNOT_BE_EMPTY.assertStringNotEmpty(password);
    boolean validated = "admin".equals(username) && "123456".equals(password);
    UserResponse.USER_LOGIN_FAILED.assertTrue(validated);
    return true;
}
```

`UserResponse.USER_LOGIN_FAILED.assertTrue(validated)`，用户名与密码校验不通过时，则会抛出 `BusinessException` 异常，异常中包含了错误码及错误信息。

具体代码示例可参见：[demo4](https://gitee.com/soulcraft/error-handling-demo/tree/main/demo4) 。

## 5. 正常响应报文的改造

仔细分析 `UserController` 的代码，

```java
@GetMapping(value = "/login")
public String login(@RequestParam(value = "username") String username,
                    @RequestParam(value = "password") String password) {
    boolean result = userService.login(username, password);
    if (result) {
        return "success";
    }
    return "failure";
}
```

我们可以发现，异常的响应报文我们处理好了（因为我们已经统一到了 `UnifiedExceptionHandler` 中进行处理），包含了错误码和错误信息，但是用户登录成功的时候，返回的信息没有包含响应码和响应信息，而只是简单的返回一个 `success`，没有统一风格。接下来我们要做的就是统一正常、异常情况下的响应报文风格。

### 5.1 如何改造

接下来，我们来说一说如何做。

1. 定义 `Response`，它继承自 `BaseResponse`，添加一个 `data` 成员变量，存储成功返回的数据，因为返回的数据可能是各种类型的，所以这里我们使用的泛型：

```java
@Getter
@Setter
public class Response<T> extends BaseResponse {
    private T data;

    protected Response(IResponse response, T data) {
        this(response.getCode(), response.getMessage(), data);
    }

    protected Response(int code, String message, T data) {
        super(code, message);
        this.data = data;
    }
    
    public static <T> Response<T> success() {
        return success(null);
    }

    public static <T> Response<T> success(T data) {
        return new Response<>(200, "SUCCESS", data);
    }

    public static <T> Response<T> failed(IResponse errorCode) {
        return failed(errorCode, null);
    }

    public static <T> Response<T> failed(IResponse errorCode, T data) {
        return new Response<>(errorCode, data);
    }
}
```

2. 修改 `UserController` 的返回值：

```java
@GetMapping(value = "/login")
public Response login(@RequestParam(value = "username") String username,
                      @RequestParam(value = "password") String password) {
    boolean result = userService.login(username, password);
    if (result) {
        return Response.success();
    }
    return Response.failed(UserResponse.USER_LOGIN_FAILED);
}
```

这样就可以了，是不是很简单？

### 5.2 测试

使用匹配的用户名和密码登录时：

```shell
curl -X GET 'http://localhost:8080/user/login?username=admin&password=123456' -H 'accept: */*'
```

前端收到的返回信息为：

```json
{
   "code": 200,
   "message": "SUCCESS",
   "data": null
}
```

测试通过。

### 5.3 返回分页的结果

很多时候查询结果需要进行分页处理，并将分页结果返回到前端。

#### 5.3.1 如何实现

1. 添加分页查询基础对象 `PageQuery`，用于接收前端传入的查询条件：

```java
@Data
public class PageQuery implements Serializable {

    @Min(value = 1, message = "[页码]参数不能小于1")
    protected int pageNum = 1;

    @Min(value = 1, message = "[分页数据条数]参数不能小于1")
    protected int pageSize = 5;

}
```

2. 添加分页数据封装类 `PageResponse`，用于返回分页查询的数据：

```java
@Data
public class PageResponse<T> {
    /**
     * 当前页
     */
    private Integer pageNum;
    /**
     * 页面大小
     */
    private Integer pageSize;
    /**
     * 总页数
     */
    private Integer totalPage;
    /**
     * 总条目数量
     */
    private Long total;
    /**
     * 条目列表
     */
    private List<T> list;

    /**
     * <pre>
     *     将MyBatis Plus 分页结果转化为通用分页结果
     * </pre>
     *
     * @param pageResult 分页结果
     * @param <T>        条目类型
     * @return 转换后的分页结果
     */
    public static <T> PageResponse<T> restPage(IPage<T> pageResult) {
        PageResponse<T> result = new PageResponse<>();
        result.setPageNum(Convert.toInt(pageResult.getCurrent()));
        result.setPageSize(Convert.toInt(pageResult.getSize()));
        result.setTotal(pageResult.getTotal());
        if (pageResult.getTotal() % pageResult.getSize() == 0) {
            result.setTotalPage(Convert.toInt(pageResult.getTotal() / pageResult.getSize()));
        } else {
            result.setTotalPage(Convert.toInt(pageResult.getTotal() / pageResult.getSize() + 1));
        }
        result.setList(pageResult.getRecords());
        return result;
    }
}
```

#### 5.3.2 测试

为了测试，我们需要添加一些测试代码：

1. `UserController` 添加 `list` 方法，查询所有用户：

```java
@RequestMapping(value = "/list", method = RequestMethod.GET)
@ResponseBody
public Response<PageResponse<String>> list(PageQuery qo) {
    IPage<String> userList = userService.list(qo);
    return Response.success(PageResponse.restPage(userList));
}
```

2. `UserService` 添加如下方法：

```java
IPage<String> list(PageQuery qo);
```

3. `UserServiceImpl` 实现上面的 `list` 方法：

```java
public IPage<String> list(PageQuery qo) {
    Page<String> page = new Page<>(qo.getPageNum(), qo.getPageSize());
    List<String> users = new ArrayList<>();
    for (int index = 1; index <= 5; ++index) {
        users.add(String.valueOf(index));
    }
    page.setTotal(users.size()).setRecords(users);
    return page;
}
```

发起测试请求：

```shell
curl -X GET 'http://localhost:8080/user/list' -H 'accept: */*'
```

前端收到的返回信息为：

```json
{
  "code": 200,
  "message": "SUCCESS",
  "data": {
    "pageNum": 1,
    "pageSize": 5,
    "totalPage": 1,
    "total": 5,
    "list": ["1", "2", "3", "4", "5"]
  }
}
```

### 5.4 小结 

至此，我们已经完成异常的统一处理，返回报文的格式统一处理及分页返回查询结果。

具体代码示例可参见：[demo5](https://gitee.com/soulcraft/error-handling-demo/tree/main/demo5) 。

## 6. 错误码信息的国际化

现在，错误的提示消息是没有做国际化支持的，国际化应该如何去做呢？`Spring` 原生就支持了国际化，做起来相对还是很简单的。

### 6.1 如何实现

让我们来看看 `UserResponse` 中定义的错误码信息：

```java
public enum UserResponse implements BusinessExceptionAssert {
    // 省略其他错误码
    USERNAME_LENGTH_IS_NOT_VALID(603, "The length of username must between {0} and {1}"),
    ;

    private int code;
    private String message;
}
```

`USERNAME_LENGTH_IS_NOT_VALID` 的错误信息："The length of username must between {0} and {1}"，应该如何去做国际化呢？我们可以为错误消息在国际化消息文件中统一一个前缀，例如：`app.ErrorMessages.`，那么可以将 `app.ErrorMessages.USERNAME_LENGTH_IS_NOT_VALID` 作为国际化消息的 Key，`messages.properties` 文件内容如下所示：

```properties
# 省略其他错误码
app.ErrorMessages.USERNAME_LENGTH_IS_NOT_VALID=The length of username must between {0} and {1}.
app.ErrorMessages.SUCCESS=Success
```

中文的 `messages_zh_CN.properties` 文件内容如下所示：

```properties
# 省略其他错误码
app.ErrorMessages.USERNAME_LENGTH_IS_NOT_VALID=用户名长度必须在 {0} 到 {1} 之间。
app.ErrorMessages.SUCCESS=成功
```

接下来怎么做呢？

1. 首先，定义一些工具类：
    
   1. `SpringApplicationContextUtil`，用来从 `ApplicationContext` 中获取指定的 `Bean`：
   
    ```java
    @Component
    public class SpringApplicationContextUtil implements ApplicationContextAware {
    
        private static ApplicationContext applicationContext;
    
        public static ApplicationContext getApplicationContext() {
            return applicationContext;
        }
    
        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            if (SpringApplicationContextUtil.applicationContext == null) {
                SpringApplicationContextUtil.applicationContext = applicationContext;
            }
        }
    
        public static <T> T getBean(Class<T> clazz) {
            return getApplicationContext().getBean(clazz);
        }
    
    }
    ```
    
    2. `MessageUtils`, 国际化工具类：

    ```java
    @Slf4j
    public class MessageUtils {
    
        private static final MessageSource messageSource = SpringApplicationContextUtil.getBean(MessageSource.class);
        private static final String MESSAGE_KEY_ERROR_MESSAGES = "app.ErrorMessages";
    
        /**
         * 获取国际化消息
         *
         * @param code 消息Key
         * @param args 消息参数
         * @return 国际化后的消息
         */
        public static String getMessage(String code, Object... args) {
            String message;
            try {
                message = messageSource.getMessage(code, args, Locale.getDefault());
            } catch (NoSuchMessageException ex) {
                log.warn("message key " + code + " not found", ex);
                return code;
            }
            if (message.isEmpty()) {
                return code;
            }
            return message;
        }
    
        /**
         * 获取错误码的国际化消息
         *
         * @param errorKey 错误码
         * @param args     消息参数
         * @return 国际化后的消息
         */
        public static String getResponseMessage(String errorKey, Object... args) {
            String code = MESSAGE_KEY_ERROR_MESSAGES + "." + errorKey;
            return getMessage(code, args);
        }
    }
    ```
   
2. 将 `BaseException` 构造函数添加国际化相关的参数：

```java
@Getter
public class BaseException extends RuntimeException {
    private final IResponse response;
    private final Object[] args;

    public BaseException(IResponse response) {
        this(response, null, response.getMessage());
    }

    public BaseException(IResponse response, Object[] args, String message) {
        this(response, args, message, null);
    }

    public BaseException(IResponse response, Object[] args, String message, Throwable cause) {
        super(message, cause);
        this.response = response;
        this.args = args;
    }
}
```
   
3. 将 `BusinessException` 构造函数添加国际化相关的参数：

```java
@Getter
public class BusinessException extends BaseException {

   public BusinessException(IResponse response, Object[] args, String message) {
      super(response, args, message);
   }

   public BusinessException(IResponse response, Object[] args, String message, Throwable cause) {
      super(response, args, message, cause);
   }
}
```

4. `Assert` 相关函数添加国际化相关的参数：

```java
public interface Assert {
    BaseException newException(Object... args);

    default void throwNewException(Object... args) throws BaseException {
        throw newException(args);
    }

    BaseException newException(Throwable cause, Object... args);

    default void throwNewException(Throwable cause, Object... args) throws BaseException {
        throw newException(cause, args);
    }

    default void assertTrue(boolean condition, Object... args) {
        if (!condition) {
            throwNewException(args);
        }
    }

    default void assertFalse(boolean condition, Object... args) {
        if (condition) {
            throwNewException(args);
        }
    }

    default void assertNull(Object obj, Object... args) {
        assertTrue(obj == null, args);
    }

    default void assertNotNull(Object obj, Object... args) {
        assertTrue(obj != null, args);
    }

    default void assertStringNotEmpty(String str, Object... args) {
        assertTrue(str != null && !str.isEmpty(), args);
    }
}
```

5. `BusinessExceptionAssert` 相关函数添加国际化相关的参数：

```java
public interface BusinessExceptionAssert extends IResponse, Assert {

    @Override
    default BusinessException newException(Object... args) {
        // 获取国际化消息
        String msg = MessageUtils.getResponseMessage(this.toString(), args);
        return new BusinessException(this, args, msg);
    }

    @Override
    default BusinessException newException(Throwable cause, Object... args) {
        // 获取国际化消息
        String msg = MessageUtils.getResponseMessage(this.toString(), args);
        return new BusinessException(this, args, msg, cause);
    }

}
```

6. `ErrorResponse` 构造函数添加国际化相关的参数：

```java
public class ErrorResponse extends BaseResponse {

    public ErrorResponse(int code, String message) {
        super(code, message);
    }

    public ErrorResponse(IResponse response) {
        this(response.getCode(), response.getMessage());
    }

    public ErrorResponse(IResponse response, String message) {
        this(response.getCode(), message);
    }
}
```

7. `UnifiedExceptionHandler`，返回 `ErrorResponse` 对象时，传入国际化后的消息：

```java
@Slf4j
@ControllerAdvice
public class UnifiedExceptionHandler {

    @ExceptionHandler(value = BusinessException.class)
    @ResponseBody
    public ErrorResponse handleBusinessException(BusinessException e) {
        log.error(e.getMessage(), e);
        // 此处 e.getLocalizedMessage() 已是国际化后的消息
        return new ErrorResponse(e.getResponse(), e.getLocalizedMessage());
    }
}
```

8. 添加 `CommonResponse` 枚举，存储常见的响应报文定义：

```java
@Getter
@AllArgsConstructor
public enum CommonResponse implements BusinessExceptionAssert {

    SUCCESS(200, "Success"),
    ;

    private int code;
    private String message;
}
```

9. `Response` 相关函数添加国际化相关的参数：

```java
@Getter
@Setter
public class Response<T> extends BaseResponse {
    private T data;

    protected Response(IResponse response, T data, Object... args) {
        this(response.getCode(), MessageUtils.getResponseMessage(response.toString(), args), data);
    }

    protected Response(int code, String message, T data) {
        super(code, message);
        this.data = data;
    }

    public static <T> Response<T> success() {
        return success(null);
    }

    public static <T> Response<T> success(T data) {
        return new Response<>(CommonResponse.SUCCESS, data);
    }

    public static <T> Response<T> failed(IResponse errorCode) {
        return failed(errorCode, null);
    }

    public static <T> Response<T> failed(IResponse errorCode, T data) {
        return new Response<>(errorCode, data);
    }
}
```

10. `UserServiceImpl` 的登录方法，添加用户名长度的校验：

```java
@Service
public class UserServiceImpl implements UserService {

   private static final int MIN_USERNAME_LENGTH = 5;
   private static final int MAX_USERNAME_LENGTH = 16;

   @Override
   public boolean login(String username, String password) {
      UserResponse.USERNAME_CANNOT_BE_EMPTY.assertStringNotEmpty(username);
      int length = username.length();
      boolean usernameValidated = length >= MIN_USERNAME_LENGTH && length <= MAX_USERNAME_LENGTH;
      UserResponse.USERNAME_LENGTH_IS_NOT_VALID.assertTrue(usernameValidated, MIN_USERNAME_LENGTH, MAX_USERNAME_LENGTH);
      UserResponse.PASSWORD_CANNOT_BE_EMPTY.assertStringNotEmpty(password);
      boolean validated = "admin".equals(username) && "123456".equals(password);
      UserResponse.USER_LOGIN_FAILED.assertTrue(validated);
      return true;
   }
}
```
    
### 6.2 测试

接下来，使用 `zh_CN` 的 `locale` 运行程序，进行测试：

1. 当用户名为空时，前端收到的返回信息为：

```json
{
   "code": 600,
   "message": "用户名不能为空"
}
```

2. 当用户名不为空、密码为空时，前端收到的返回信息为：

```json
{
   "code": 601,
   "message": "密码不能为空"
}
```

3. 当用户名不为空、密码不为空，但是用户名或密码不正确时，前端收到的返回信息为：

```json
{
   "code": 602,
   "message": "用户登录失败"
}
```

4. 当用户名长度不符合规定时，前端收到的返回信息为：

```json
{
   "code": 603,
   "message": "用户名长度必须在 5 到 16 之间。"
}
```

5. 当用户名和密码正确时，前端收到的返回信息为：

```json
{
   "code": 200,
   "message": "成功",
   "data": null
}
```

可以看到，返回的错误信息已经进行了国际化，测试通过。

### 6.3 小结

至此，我们已经完成错误码的国际化支持。

具体代码示例可参见：[demo6](https://gitee.com/soulcraft/error-handling-demo/tree/main/demo6) 。

## 7. 错误码分模块

目前为止，错误码是没有分模块的，统一使用一套错误码，并且错误码只有数字，并没有那么直观，可以考虑将错误码分模块，并将模块信息加入到错误码中。

可以将错误码分为三块：应用名称、模块名称以及错误码，例如：`COM-SRV-200`，即代表 `COM` (Common的简写)应用，`SRV` (Server的简写)模块，真实错误码为 `200`。

### 7.1 如何实现

1. 添加 `IResponseEnum`：

```java
public interface IResponseEnum {
    /**
     * 系统/应用 简称
     *
     * @return 系统/应用 简称
     */
    String getAppName();

    /**
     * 模块/组件 简称
     *
     * @return 模块/组件 简称
     */
    String getModuleName();

    /**
     * 返回码
     *
     * @return 返回码
     */
    int getCode();

    /**
     * <pre>
     * 整个错误码信息，包含：
     * 1. 系统/应用 简称
     * 2. 模块/组件 简称
     * 3. 返回码
     * </pre>
     *
     * @return 整个错误码信息
     */
    default String getFullCode() {
        return BaseResponse.getFullCode(getAppName(), getModuleName(), getCode());
    }

    /**
     * 返回消息
     *
     * @return 返回消息
     */
    String getMessage();
}
```

2. 将 `IResponse` 中的 `getCode` 返回值修改为 `String` 类型：

```java
public interface IResponse {

    /**
     * 返回码
     *
     * @return 返回码
     */
    String getCode();

    /**
     * 返回消息
     *
     * @return 返回消息
     */
    String getMessage();
}
```

3. `BusinessExceptionAssert` 修改为继承 `IResponseEnum`。
4. `CommonResponse` 添加 `getAppName` 和 `getModuleName` 两个方法：

```java
@Getter
@AllArgsConstructor
public enum CommonResponse implements BusinessExceptionAssert {

    SUCCESS(200, "Success"),
    ;

    private int code;
    private String message;

    public String getAppName() {
        return "COM";
    }

    public String getModuleName() {
        return "SRV";
    }
}
```
5. `UserResponse` 添加 `getAppName` 和 `getModuleName` 两个方法：

```java
@Getter
@AllArgsConstructor
public enum UserResponse implements BusinessExceptionAssert {

    USERNAME_CANNOT_BE_EMPTY(600, "Username cannot be null or empty"),
    PASSWORD_CANNOT_BE_EMPTY(601, "Password cannot be null or empty"),
    USER_LOGIN_FAILED(602, "User login failed"),
    USERNAME_LENGTH_IS_NOT_VALID(603, "The length of username must between {0} and {1}"),
    ;

    private int code;
    private String message;

    public String getAppName() {
        return "COM";
    }

    public String getModuleName() {
        return "USR";
    }
}
```

6. 修改其他相关类，如： `BaseResponse`、`ErrorResponse`、`Response`、`BaseException`、`BusinessException`、`UserResponse` 等。

具体修改方法可参见：[demo7](https://gitee.com/soulcraft/error-handling-demo/tree/main/demo7) 。

### 7.2 测试

1. 当用户名为空时，前端收到的返回信息为：

```json
{
   "code": "COM-USR-600",
   "message": "用户名不能为空"
}
```

2. 当用户名和密码正确时，前端收到的返回信息为：

```json
{
   "code": "COM-SRV-200",
   "message": "成功",
   "data": null
}
```

可以看到返回错误码已经包含应用名、模块名和真实的错误码。

### 7.3 小结

至此，我们已经完成错误码的分模块处理。

具体代码示例可参见：[demo7](https://gitee.com/soulcraft/error-handling-demo/tree/main/demo7) 。

## 8. 总结

这篇文章对 Java RESTful 应用的异常处理、错误码、消息报文进行了一步步的优化，最终实现了异常的统一处理、返回报文的格式统一处理、分页返回查询结果、错误码的国际化以及错误码的分模块处理。可能也不是最好的处理方案，大家可以做一个参考。

最终代码可参见：[sc-response-and-error-handler](https://gitee.com/soulcraft/sc-response-and-error-handler.git) 。