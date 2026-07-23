# Java 与 Scala 中的高阶类型

本文以我在尝试使用 Java 编写基于 HKT 的函数式编程库时的探索为例，参考了 [Magnus Smith 的几篇博客](https://blog.scottlogic.com/magnussmith) 。

## Scala 高阶类型

**高阶类型**（Higher Kinded Type）也叫结合类型构造器（Associated Type Constructor），在 Scala 里可以自如地表达，因为 Scala 的泛型是可以带类型构造器的，但是 Java 就不行（实际上，大部分 JVM 语言都不怎么原生支持）。

你可能在 Scala 里见过 `Foo[F[A]]` 这种写法，但这玩意在 Java 里是没法的，而且很可能还需要做额外的约束和检测工作，最低成本的就是用接口和两个泛型来表示：

```java
public interface Kind<F, A> {}
```

但在 Scala 写 `Foo[F[A, B]]` 的时候，你就只能再创建个新的接口了：

```java
public interface Kind2<F, A, B> {}
```

~~（TODO：其他描述）~~

### 关于原生函数式接口

我目前的大体设想是提供一套名为 `FunctionKn` 的新接口来替代旧接口的用法，新的接口会提供更多的函数式方法支持。`FunctionK0` 作为 `Supplier` 与 `Runner` 的替代物：

```java
@FunctionalInterface
public interface FunctionK0<R> {
    R apply();
}
```

大部分函数式接口的替代物是 `FunctionK1`，它仅有 `andThen` 操作（因为该情况下 Curry 化等操作平凡）：

```java
@FunctionalInterface
public interface FunctionK1<A, R> {
    R apply(A a);

    default <S> FunctionK1<A, S> andThen(FunctionK1<? super R, ? extends S> f) {
        return a -> f.apply(apply(a));
    }
}
```

大部分替代可见下表，基本参考 Scala 的功能设计（这里最大的问题是因为 Java 的 Primitive Type，不得不面对所有 Bool 值相关内容时进行装/拆箱）：

| 原生接口                  | 替代物                         |
| --------------------- | --------------------------- |
| `Funtion<T, R>`       | `FunctionK1<T, R>`          |
| `BiFunction<T, U, R>` | `FunctionK2<T, U, R>`       |
| `UnaryOperator<T>`    | `FunctionK1<T, T>`          |
| `BinaryOperator<T>`   | `FunctionK2<T, T, T>`       |
| `Predicate<T>`        | `FunctionK1<T, Boolean>`    |
| `BiPredicate<T, U>`   | `FunctionK2<T, U, Boolean>` |
| `Consumer<T>`         | `FunctionK1<T, Unit>`       |
| `Supplier<T>`         | `FunctionK0<T>`             |
| `Runnable`            | `FunctionK0<Unit>`          |

简单来说，我们给 `FunctionKn` 设计了 3 种主要新增操作：

1. Curry 化：

```java
// FunctionK2
default FunctionK1<A1, FunctionK1<A2, R>> curry() {
    return a1 -> a2 -> apply(a1, a2);
}

// FunctionK3
default FunctionK1<A1, FunctionK2<A2, A3, R>> curry() {
    return a1 -> (a2, a3) -> apply(a1, a2, a3);
}
    
default FunctionK2<A1, A2, FunctionK1<A3, R>> curry2() {
    return (a1, a2) -> a3 -> apply(a1, a2, a3);
}
```

2. 反 Curry 化

```java
// FunctionK2
default FunctionK2<A1, A2, R> uncurry(FunctionK1<A1, FunctionK1<A2, R>> f) {
    return (a1, a2) -> f.apply(a1).apply(a2);
}

// FunctionK3
default FunctionK3<A1, A2, A3, R> uncurry(FunctionK1<A1, FunctionK2<A2, A3, R>> f) {
    return (a1, a2, a3) -> f.apply(a1).apply(a2, a3);
}

default FunctionK3<A1, A2, A3, R> uncurry2(FunctionK2<A1, A2, FunctionK1<A3, R>> f) {
    return (a1, a2, a3) -> f.apply(a1, a2).apply(a3);
}
```

3. 与 \`TupleN\`的互转

```java
// FunctionK2
default FunctionK1<Tuple2<A1, A2>, R> tupled() {
    return t -> apply(t.a1(), t.a2());
}

default FunctionK2<A1, A2, R> untupled(FunctionK1<Tuple2<A1, A2>, R> f) {
    return (a1, a2) -> f.apply(new Tuple2<>(a1, a2));
}

// FunctionK3
default FunctionK1<Tuple3<A1, A2, A3>, R> tupled() {
    return t -> apply(t.a1(), t.a2(), t.a3());
}

default FunctionK3<A1, A2, A3, R> untupled(FunctionK1<Tuple3<A1, A2, A3>, R> f) {
    return (a1, a2, a3) -> f.apply(new Tuple3<>(a1, a2, a3));
}
```

设想里常规的 `TupleN` 大致上是类似 Scala 的，其中的 `Tuple1` 相当于一个 Box，然后所有的都实现一个 `ProductV` 接口（后缀 `V` 代表其是值层面的，我们暂且划定所有类型相关的后缀为 `K`，代表 Kind）：

```java
public interface ProductV {

    /**
     * The arity of the value-level product.
     *
     * @return The amount of the product arity, e.g. the arity of {@link Tuple2} is <tt>2</tt>.
     */
    int arity();

    /**
     * The <tt>n</tt>-th projection element of the product.
     *
     * @param n The number of the projection to be returned.
     * @return  Returns the <tt>n</tt>-th projection of this product if <tt>n∈[0, arity]</tt>,
     *          otherwise throws {@link IndexOutOfBoundsException}.
     */
    Object elementAt(int n);

    /**
     * An iterator over all the elements of the product.
     *
     * @return An {@link Iterator} that iterates from the <tt>1</tt>-st to the <tt>arity</tt>-th
     *         projection of this product in order.
     */
    default Iterator<Object> iterator() {
        return new Iterator<>() {
            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < arity();
            }

            @Override
            public Object next() {
                return elementAt(i++);
            }
        };
    }
}
```

这大致上就是 Scala 里的 `TupleN` 的操作了。之所以还会有所谓的 `TupleKn`，是因为我们可能会用到一些函数式结构，但我们又不希望这玩意太污染原本的，单纯作为一个 Instance 设计（例如 Mojang 的 [DataFixerUpper](https://github.com/Mojang/DataFixerUpper) 就把这种结构放在类里的内部类，我感觉不太健康），比如 `TupleKn` 可能携带某种 `Traversable` 之类的结构。
