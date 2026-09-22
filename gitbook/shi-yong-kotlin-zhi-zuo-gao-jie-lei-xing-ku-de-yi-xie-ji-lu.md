# 使用 Kotlin 制作高阶类型库的一些记录

我们的目的是编写一个基于 HKT 的函数式编程库，在一开始，我尝试使用了 Java 进行，但我很快发现这非常痛苦，而且 Magnus Smith 已经制作了一个叫 [Higher Kinded J](https://github.com/higher-kinded-j/higher-kinded-j) 的库，这个库相当完备，但有些设计不太对我的口味，所以我想到了我比较喜欢的另一种 JVM 语言 Kotlin。我正在尝试将这些想法真正实现，目前都在个人项目 [Kato](https://github.com/MagicSweepy/Kato.git) 中。

本文参考了 [Magnus Smith 的几篇博客](https://blog.scottlogic.com/magnussmith)，以及一些同类型的库，例如 Scala 的 [Cats](https://github.com/typelevel/cats) 与 Haskell 里的一些库，比如 [Optics](https://hackage.haskell.org/package/optics) 等。

## Scala 高阶类型的优势

**高阶类型**（Higher Kinded Type）也叫结合类型构造器（Associated Type Constructor），在 Scala 里可以自如地表达，因为 Scala 的泛型是可以带类型构造器的，但是 Java 和 Kotlin 就不行（实际上，大部分 JVM 语言都不怎么原生支持）。

### 类型构造器

你可能在 Scala 里见过 `Foo[F[_]]` 这种写法，但这玩意在 Kotlin 里是没法的，而且很可能还需要做额外的约束和检测工作，最低成本的就是用接口和两个泛型来表示：

```java
interface Kind<F, A>
```

但在 Scala 写 `Foo[F[_, _]]` 的时候，你就只能再创建个新的接口了：

```java
interface Kind2<F, A, B>
```

### 类型 Lambda

在 Scala 的 [Cats](https://github.com/typelevel/cats) 里有一个插件 [Kind Projector](https://github.com/typelevel/kind-projector) 实现了类型 Lambda 的语法，比如：

```scala
SemigroupK[λ[α => F[α, α]]]
```

这里的意思就是假设这里 `SemigroupK` 需要一个一元的构造器 `G`，这个 Lambda 表示了 `G[A] = F[A, A]` 这样的规则，这是种部分应用类似的；[Cats](https://github.com/typelevel/cats) 里实现的一些例如 Kleisli Arrow 这类的都用了这样的表达。

