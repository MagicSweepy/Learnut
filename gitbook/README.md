# 关于 Scala 中的单子与幺半范畴

{% hint style="info" %}
本文的范畴论相关符号均以**李文威《代数学方法》两卷**为准，以个人记号为辅。
{% endhint %}

本文试图以 Scala 3 为蓝本给函数式编程里用到的单子与幺半范畴结构一个纯范畴论式的解释，我们把类型作为对象，纯函数 `A ⇒ B` 看作态射，以此构成一个范畴 $$\textsf{Scala}$$。

### 有限积与闭 Cartesian 范畴

我们先按照顺序定义构造出闭 Cartesian 范畴中的几个条件，其是从最基本的积推广而来的。

在范畴 $$\mathcal{C}$$ 中，对象 $$A, B\in\mathrm{Ob}(\mathcal{C})$$ 的**积**为三元组 $$(A\times B, \pi_{A}, \pi_{B})$$，其中：

* **积对象** $$A\times B\in\mathrm{Ob}(\mathcal{C})$$
* **投影态射** $$\pi_{A}\in \mathrm{Hom}_{\mathcal{C}}(A\times B, A)$$ 与 $$\pi_{B}: A\times B \to B$$ ，其满足：任取对象 $$C\in\mathrm{Ob}(\mathcal{C})$$ 与一对态射$$f\in\mathrm{Hom}_{\mathcal{C}}(C, A)$$ 与 $$g\in\mathrm{Hom}_{\mathcal{C}}(C, B)$$，存在态射 $$h\in\mathrm{Hom}_{\mathcal{C}}(C, A\times B)$$ 使得下图交换：

<figure><img src=".gitbook/assets/image.png" alt="" width="364"><figcaption></figcaption></figure>

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

除了有限积以外，**闭 Cartesian 范畴**还要求拥有**终对象**（任取对象 $$B\in\mathrm{Ob}(\mathcal{C})$$ 其 Hom 集 $$\mathrm{Hom}_{\mathcal{C}}(B, A)$$ 只有1个元素，对应 Scala 里的 `Unit`，即 `A ⇒ Unit`），以及一个特殊的对象，我们称其为**指数对象**：任取对象 $$A, B\in\mathrm{Ob}(\mathcal{C})$$ 存在对象 $$B^{A}\in\mathrm{Ob}(\mathcal{C})$$ 与态射 $$e\in\mathrm{Hom}_{\mathcal{C}}(B^{A}\times A, B)$$ 使得任取 $$C\in\mathrm{Ob}(\mathcal{C})$$ 下图交换：

<figure><img src=".gitbook/assets/image (2).png" alt="" width="282"><figcaption></figcaption></figure>

也就是说，这里相当于要求任取态射 $$f\in\mathrm{Hom}_{\mathcal{C}}(C\times A, B)$$ 存在唯一的态射 $$g\in\mathrm{Hom}_{\mathcal{C}}(C, B^{A})$$ 使得恒成立 $$f = e\circ (g\times \mathrm{id}_{A})$$。用 Scala 解释，指数对象 $$B^{A}$$ 就是 $$A ⇒ B$$，态射 $$e$$ 就是其的应用，即：

```scala
def eval[A, B]: ((A => B, A)) => B = { case (f, a) => f(a) }
```

这里的 $$f$$ 与 $$g$$ 对应 Curry 化和反 Curry 化，即：

```scala
def curry[A, B, C](f: ((C, A)) => B): C => (A => B) = c => a => f((c, a))

def uncurry[A, B, C](g: C => A => B): ((C, A)) => B = { case (c, a) => g(c)(a) }
```

也就是说，任取 `f: ((C, A)) ⇒ B` 存在唯一的 `curry(f)` 使得 `uncurry(curry(f)) == f`，反过来也一样，也就是说 Curry 化与反 Curry 化构成一一对应，这相当于闭 Cartesian 范畴里的**闭性**。

#### 有限积的伴随以及指数对象与对角函子的关系

在第一次接触时，我因为投影态射与终对象的缘故曾猜测闭 Cartesian 范畴与投射极限（逗号范畴 $$(\Delta /\beta$$) 的终对象，其中 $$\beta: \mathcal{D}^{\mathrm{op}}\to\mathcal{C}$$，对角函子 $$\Delta: \mathcal{C} \to\mathcal{C}^{\mathcal{D}}$$）有什么关系，但实际上这两个不一样。首先就是，对角函子是把一个对象复制成多个同样的对象，比如取 $$\Delta: A\mapsto A\times A$$ 的情况，此时积是它的右伴随；但是指数对象不一样，指数对象必须要固定一个 $$A$$ 的情况下的积，也就是 $$A\times-$$ 的右伴随，这个操作对应的就是我们刚才讨论过的 Curry 化。

~~（TODO：等找到合适的逗号范畴和函子之后尝试包装指数对象为某种投射极限）~~

#### 关于 nLab 上的使用幺半范畴的定义的解释

这里额外说说当时学习时参考过的 [nLab](https://ncatlab.org/nlab/show/cartesian+closed+category) 上的定义，这里的定义结合上文比较好理解的就是这个内部 Hom，这点其实就是我们申明过的指数对象，但是这个 Cartesian 幺半结构比较诡异（也许因为 nLab 向来会概念套概念定义），对于不了解这部分的初学者来说很绕。

简单来说，如果选择我们刚才选取的积作为张量积 $$\otimes:\mathcal{V}\times\mathcal{V}\to\mathcal{V}$$，也就是 $$(A, B)\mapsto A\times B$$，我们已经讨论了它的泛性质，所以这里略过，幺元就是终对象，暂时略过合成约束之类的定义部分，这样就大致构成了1个幺半范畴，这个结构就是 **Cartesian 幺半范畴**（或许因为张量积是 Cartesian 积，而幺元就是终对象）。在 nLab 的定义中：

> A cartesian closed category is a category with finite products **which is closed with respect to its cartesian monoidal structure**.

这里闭 Cartesian 范畴的闭性实际上是因为选取了闭幺半范畴，也就是说函子 $$-\otimes A$$ 的右伴随（内部 Hom）$$-^{A}$$ 满足自然同构，按内部 Hom 的记法一般是记作 $$[A, -]$$ 这样，这里要求的条件就是：

$$
\mathrm{Hom}_{\mathcal{V}}(A\times B, C)\cong\mathrm{Hom}_{\mathcal{V}}(A, B^{C})
$$

所以整理一下就是，闭 Cartesian 范畴 $$\mathcal{C}$$ 是一个满足：

1. $$\mathcal{C}$$ 存在有限积
2. 任取对象 $$A\in\mathrm{Ob}(\mathcal{C})$$ 函子 $$A\times -: \mathcal{C}\to\mathcal{C}$$ 总有右伴随 $$-^{A}: \mathcal{C}\to\mathcal{C}$$。

第 2 条的等价描述就是我们上面的泛性质定义与这里讨论的闭 Cartesian 幺半范畴定义。
