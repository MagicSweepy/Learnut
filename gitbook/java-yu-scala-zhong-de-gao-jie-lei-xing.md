# Java 与 Scala 中的高阶类型

实际上，大部分 JVM 语言都不怎么原生支持 HKT，但 Scala 3 可以。本文以我在尝试使用 Java 编写基于 HKT 的函数式编程库时的探索为例，参考了 [Magnus Smith 的几篇博客](https://blog.scottlogic.com/magnussmith) 。

简单来说，像 Scala 里的 `Foo[F[A]]` 这种写法在 Java 里是没法的，在 Java 里最低成本的就是用接口和两个泛型来表示：

```java
public interface Kind<F, A> {}
```

这种写法的一个明显的弊端就是要有很多的话很麻烦，得一个一个创建新的接口。

~~（TODO：Scala Cats 相关举例）~~
