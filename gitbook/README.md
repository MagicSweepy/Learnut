# 关于 Scala 中的单子与幺半范畴

{% hint style="info" %}
本文的范畴论相关符号均以**李文威《代数学方法》两卷**为准，以个人记号为辅。
{% endhint %}

本文试图以 Scala 3 为蓝本给函数式编程里用到的单子与幺半范畴结构一个纯范畴论式的解释，我们把类型作为对象，纯函数 `A ⇒ B` 看作态射，以此构成一个范畴 $$\textsf{Scala}$$。我们先按照顺序定义构造出闭 Cartesian 范畴中的几个条件，其是从最基本的积推广而来的。

在范畴 $$\mathcal{C}$$ 中，对象 $$A, B\in\mathrm{Ob}(\mathcal{C})$$ 的**积**为三元组 $$(A\times B, \pi_{A}, \pi_{B})$$，其中：

* **积对象** $$A\times B\in\mathrm{Ob}(\mathcal{C})$$
* **投影态射** $$\pi_{A}\in \mathrm{Hom}_{\mathcal{C}}(A\times B, A)$$ 与 $$\pi_{B}: A\times B \to B$$ ，其满足：任取对象 $$C\in\mathrm{Ob}(\mathcal{C})$$ 与一对态射$$f\in\mathrm{Hom}_{\mathcal{C}}(C, A)$$ 与 $$g\in\mathrm{Hom}_{\mathcal{C}}(C, B)$$，存在态射 $$h\in\mathrm{Hom}_{\mathcal{C}}(C, A\times B)$$ 使得下图交换：

<figure><img src=".gitbook/assets/image.png" alt="" width="182"><figcaption></figcaption></figure>

按照 [nLab](https://ncatlab.org/nlab/show/cartesian+closed+category) 上的说法，所谓的**有限积**就是有限个对象的积。在 $$\textsf{Scala}$$ 范畴中，积对象就是元组  `(A, B)` ，投影态射就是 `_1` 和 `_2` 方法（事实上这里标准的记号应该就是 $$\pi_{1}$$ 和 $$\pi_{2}$$ 但显然我更喜欢我自己的记法）。这个泛性质就相当于在定义一个新的纯函数：

```scala
def pi[C, A, B](f: C => A, g: C => B): C => (A, B) = c => (f(c), g(c))
```

然后对于任何的 `c: C` 必须有（毕竟在 Scala 中 `(a, b)._1 == a` 大概不需要证明）：

```scala
pi(f, g).andThen(_._1) == f
pi(f, g).andThen(_._2) == g
```

按图索骥的结果也是一致的。任何满足这两个式子的纯函数 `h: C ⇒ (A, B)` 必定是 `pi(f, g)`，因为我们显而易见地可以发现：若 `h(c)._1 == f(c)` 且 \`h(c).\_2 == g(c)\`，那这个 \`h(c)\` 只能是 `(f(c), g(c))`。
