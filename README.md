# Scriptify Kotlin Script
This library adds Kotlin Script implementation for the [Scriptify](https://github.com/Instancify/Scriptify) library.

## Example of usage
Kotlin example
```kotlin
import com.instancify.scriptify.common.script.constant.CommonConstantManager
import com.instancify.scriptify.common.script.function.CommonFunctionManager
import kotlin.script.experimental.api.ResultValue

fun main() {
    val script = KtsScript()
    script.functionManager = CommonFunctionManager()
    script.constantManager = CommonConstantManager()

    val result = script.eval("randomInt(min = 1, max = 5)")
    if (result?.returnValue is ResultValue.Value) {
        println("Random value: ${result.returnValue}")
    }
}
```
Java example
```java
import com.instancify.scriptify.api.exception.ScriptException;
import com.instancify.scriptify.common.script.constant.CommonConstantManager;
import com.instancify.scriptify.common.script.function.CommonFunctionManager;
import kotlin.script.experimental.api.EvaluationResult;
import kotlin.script.experimental.api.ResultValue;

public class Test {

    public static void main(String[] args) throws ScriptException {
        KtsScript script = new KtsScript();
        script.setFunctionManager(new CommonFunctionManager());
        script.setConstantManager(new CommonConstantManager());

        EvaluationResult result = script.eval("randomInt(min = 1, max = 5)");
        if (result != null) {
            if (result.getReturnValue() instanceof ResultValue.Value value) {
                System.out.println("Random value: " + value.getValue());
            }
        }
    }
}
```

This is a simple example of using the random number function. You can register your own functions and constants, or use ready-made ones from the [common module](https://github.com/Instancify/Scriptify/tree/master/common).

## Maven
Adding repo:
```xml
<repositories>
    <repository>
        <id>instancify-repository-snapshots</id>
        <url>https://repo.instancify.app/snapshots</url>
    </repository>
</repositories>
```

Adding dependency:
```xml
<dependency>
    <groupId>com.instancify.scriptify.kts</groupId>
    <artifactId>script</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## Gradle
Adding repo:
```groovy
maven {
    name "instancifyRepositorySnapshots"
    url "https://repo.instancify.app/snapshots"
}
```

Adding dependency:
```groovy
implementation "com.instancify.scriptify.kts:script:1.0.0-SNAPSHOT"
```