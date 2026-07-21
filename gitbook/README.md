# 关于 Scala 中的单子与幺半范畴

{% hint style="info" %}
本文的范畴论相关符号均以**李文威《代数学方法》两卷**为准，以个人记号为辅。
{% endhint %}

本文试图以 Scala 3 为蓝本给函数式编程里用到的单子与幺半范畴结构一个纯范畴论式的解释，我们把类型作为对象，纯函数 `A => B`  看作态射，以此构成一个范畴 $$\textsf{Scala}$$。

## 有限积与闭 Cartesian 范畴

我们先按照顺序定义构造出闭 Cartesian 范畴中的几个条件，其是从最基本的积推广而来的。

在范畴 $$\mathcal{C}$$ 中，对象 $$A, B\in\mathrm{Ob}(\mathcal{C})$$ 的**积**为三元组 $$(A\times B, \pi_{A}, \pi_{B})$$，其中：

* **积对象** $$A\times B\in\mathrm{Ob}(\mathcal{C})$$
* **投影态射** $$\pi_{A}\in \mathrm{Hom}_{\mathcal{C}}(A\times B, A)$$ 与 $$\pi_{B}: A\times B \to B$$ ，其满足：任取对象 $$C\in\mathrm{Ob}(\mathcal{C})$$ 与一对态射$$f\in\mathrm{Hom}_{\mathcal{C}}(C, A)$$ 与 $$g\in\mathrm{Hom}_{\mathcal{C}}(C, B)$$，存在态射 $$h\in\mathrm{Hom}_{\mathcal{C}}(C, A\times B)$$ 使得下图交换：

<figure><img src=".gitbook/assets/image (6).png" alt=""><figcaption></figcaption></figure>

按照 [nLab](https://ncatlab.org/nlab/show/cartesian+closed+category) 上的说法，所谓的**有限积**就是有限个对象的积。在 $$\textsf{Scala}$$ 范畴中，积对象就是元组  `(A, B)` ，投影态射就是 `_1` 和 `_2` 方法（事实上这里标准的记号应该就是 $$\pi_{1}$$ 和 $$\pi_{2}$$ 但显然我更喜欢我自己的记法）。这个泛性质就相当于在定义一个新的纯函数：

```scala
def pi[C, A, B](f: C => A, g: C => B): C => (A, B) = c => (f(c), g(c))
```

然后对于任何的 `c: C` 必须有（毕竟在 Scala 中 `(a, b)._1 == a` 大概不需要证明）：

```scala
pi(f, g).andThen(_._1) == f
pi(f, g).andThen(_._2) == g
```

按图索骥的结果也是一致的。任何满足这两个式子的纯函数 `h: C ⇒ (A, B)` 必定是 `pi(f, g)`，因为我们显而易见地可以发现：若 `h(c)._1 == f(c)` 且 `h(c)._2 == g(c)`，那这个 `h(c)` 只能是 `(f(c), g(c))`。

除了有限积以外，**闭 Cartesian 范畴**还要求拥有**终对象**（任取对象 $$B\in\mathrm{Ob}(\mathcal{C})$$ 其 Hom 集 $$\mathrm{Hom}_{\mathcal{C}}(B, A)$$ 只有1个元素，对应 Scala 里的 `Unit`，即 `A ⇒ Unit`），以及一个特殊的对象，我们称其为**指数对象**：任取对象 $$A, B\in\mathrm{Ob}(\mathcal{C})$$ 存在对象 $$B^{A}\in\mathrm{Ob}(\mathcal{C})$$ 与态射 $$e\in\mathrm{Hom}_{\mathcal{C}}(B^{A}\times A, B)$$ 使得任取 $$C\in\mathrm{Ob}(\mathcal{C})$$ 下图交换：

<figure><img src=".gitbook/assets/image (7).png" alt=""><figcaption></figcaption></figure>

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

### 有限积的伴随与切片范畴

在第一次接触时，我因为投影态射与终对象的缘故曾猜测闭 Cartesian 范畴与投射极限（逗号范畴 $$(\Delta /\beta$$) 的终对象，其中 $$\beta: \mathcal{D}^{\mathrm{op}}\to\mathcal{C}$$，对角函子 $$\Delta: \mathcal{C} \to\mathcal{C}^{\mathcal{D}}$$）有什么关系，但实际上这两个不一样。首先就是，对角函子是把一个对象复制成多个同样的对象，比如取 $$\Delta: A\mapsto A\times A$$ 的情况，此时积是它的右伴随；但是指数对象不一样，指数对象必须要固定一个 $$A$$ 的情况下的积，也就是 $$A\times-$$ 的右伴随，这个操作对应的就是我们刚才讨论过的 Curry 化。

大而泛地说，这里我们要从普通的关系提升到依赖的关系。首先我们来回顾一下逗号范畴的概念，给定两个函子 $$S: \mathcal{A}\to\mathcal{C}$$ 与 $$T: \mathcal{B}\to\mathcal{C}$$，**逗号范畴** $$(S/T)$$ 是包含如下信息的范畴：

* 对象：三元组 $$(A, B, f)$$，其中 $$A\in\mathrm{Ob}(\mathcal{A})$$，$$B\in\mathrm{Ob}(\mathcal{B})$$，$$f\in\mathrm{Hom}(SA, TB)$$。
* 态射：对象 $$(A, B, f)$$ 与 $$(A', B', f')$$ 之间的态射为二元组 $$(g, h)$$ 使得下图交换：

$$
\begin{CD}SA@>Sg>>SA'\\@VfVV@VVf'V\\TB@>>Th>TB'\end{CD}
$$

也就是说满足 $$Th\circ f = f'\circ Sg$$。考虑一个**终范畴**（只有 1 个对象，态射为这个对象的恒等态射的范畴）记作 $$\mathbf{1}$$，指定任意范畴 $$\mathcal{C}$$ 中的对象 $$A\in\mathrm{Ob}(\mathcal{C})$$ 相当于指定一个函子 $$j_{A}: \mathrm{1}\to\mathcal{C}$$，关于此的一个简单的例子是逗号范畴 $$(\mathrm{id}_{\mathcal{C}}/\mathrm{id}_{\mathcal{C}})$$，这个例子在《代数学方法》卷一中的泛性质部分也有所介绍，我们在此稍微详细介绍一下。按照定义，这相当于对象里的 $$f$$ 是 $$\mathrm{id}_{\mathcal{C}}A\to\mathrm{id}_{\mathcal{C}}B$$ 也就是 $$f: A\to B$$（任何态射与 $$\mathrm{id}_{\mathcal{C}}$$ 的合成关系显然），态射也就是两者之间的箭头：

$$
\begin{CD}A@>f>>B\\@VVV@VVV\\A'@>>f'>B'\end{CD}
$$

所以这个例子里的逗号范畴也叫**箭头范畴**。切片范畴是比箭头范畴相对复杂一些的情况，函子 $$T: \mathcal{D}\to\mathcal{C}$$ 与上述的 $$j_{A}: \mathbf{1}\to\mathcal{C}$$，我们把满足以下条件的逗号范畴 $$(T/j_{A})$$ 称为**切片范畴**并记作 $$(T/A)$$：

* 对象：任取 $$D\in\mathrm{Ob}(\mathcal{D})$$，二元组 $$(D, p: TD\to A)$$；
* 态射：态射 $$f\in\mathrm{Hom}_{\mathcal{D}}(D, D')$$ 使得下图交换：

<figure><img src=".gitbook/assets/image (8).png" alt=""><figcaption></figcaption></figure>

此时 $$p\in\mathrm{Hom}_{\mathcal{D}}(TD, A)$$ 可以想象为一族被 $$A$$ 参数化的空间，任取 $$a\in A$$，$$p^{-1}(a)$$ 这种构造因为一些几何学缘故被称为 $$D$$ 在点 $$a$$ 的**纤维**，记作 $$D_{a}$$。一般来说，我们为了简便起见以下讨论的切片范畴都是上述的 $$T$$ 取 $$\mathrm{id}_{\mathcal{C}}$$ 的情况，也就是 $$(\mathrm{id}_{\mathcal{C}}/A)$$，此时其的对象 $$(D, p: D\to A)$$ 也被称为纤维，而且表达上更直接一些（所以我们可以称其为 $$A$$ 上长出来的纤维）。

从 Scala 的角度来看，对象 $$A$$ 的切片范畴相当于描述其的依赖类型的上下文，对象是 `p: X ⇒ A`，而态射是携带标签 `p(x): A` 的类型 `x: X`（也就是一个函数 $$h: X ⇒ Y$$ 满足 `q(h(x)) = p(x)`）。

我们接下来为了讨论方便把 Scala 语境下讨论的切片范畴记作 $$(S/A)$$，可以看到，两个对象的积其实相当于它们的拉回：

$$
\begin{CD}X\times_{A}Y@>>>Y\\@VVV@VVqV\\X@>>p>A\end{CD}
$$

假若我们转化到 $$\textsf{Set}$$ 范畴上讨论，可以看出 $$X\times_{A} Y = \{ (x, y) | p(x) = q(y) \}$$。所以这个切片范畴本身就是它自己的 Cartesian 范畴，因为终对象 $$\mathrm{id}_{A}$$ 存在，拉回作为积。我们把每个切片范畴都是闭 Cartesian 范畴的范畴叫**局部闭 Cartesian范畴**，这是因为这个条件实际上相当于任取态射 $$f\in\mathrm{Hom}(X, Y)$$ 时，拉回 $$f^*: (S/Y) \to (S/X)$$ 有右伴随，此时这个右伴随记作 $$\Pi_f$$，称为**依赖积**，而其左伴随如果存在，则称为**依赖和**，记作 $$\Sigma_f$$（因为拉回给出了切片范畴的有限积，且积始终有右伴随）。当 $$X = 1$$ （终对象）时，可以看到这个切片范畴 $$(S/1)$$ 是同构于原范畴的，也就是说局部闭 Cartesian 范畴肯定也是闭 Cartesian 范畴（前者比后者更强）。还是用  $$\textsf{Set}$$ 来举例，对于 $$f\in\mathrm{Hom}_{\textsf{Set}}(X, Y)$$：

* 拉回 $$f^*:\{B_{y}\}_{y\in Y}\mapsto\{B_{f(x)}\}_{x\in X}$$，也就是把 $$Y$$ 上的一族元素拉到 $$A$$ 上。
* 依赖和 $$\Sigma_{f}: \{A_{x}\}_{x\in X}\mapsto\sum_{x\in f^{-1}(y)}A_{x}$$，也就是把 $$X$$ 上的一族元素沿 $$f$$ 求和。
* 依赖积 $$\Pi_{f}: \{A_{x}\}_{x\in X}\mapsto\prod_{x\in f^{-1}(y)}A_{x}$$，也就是把 $$X$$ 上的一族元素沿 $$f$$ 求积。

简单来说，局部这个性质相当于是让闭结构沿着每个切片成立，对一般的闭 Cartesian 范畴，只有全局的范畴（也就是本身）上有指数对象，但是在局部闭 Cartesian 范畴上，任意上下文都可以在相对其的切片范畴上获得这个闭结构，使之成为闭 Cartesian 范畴。

用 Scala 来说，局部闭 Cartesian 范畴提供的就是任意上下文中的输入依赖的函数空间（同时它保持了一些结构，比如拉回），比如 `(x: X) ⇒ a(x)`。上面提到的伴随关系 $$\Sigma_{f}\dashv f^{*} \dashv\Pi_{f}$$ 实际上就是 $$f$$ 上的存在量词（对应依赖和）与全称量词（对应依赖积）。

我们知道 $$(S/1)$$ 是同构于其自身的（上面的 $$j_{X}$$ 那段的例子），所以现在我们可以沿着刚才的讨论研究一下它的性质，唯一的态射 $$X\to 1$$ 的拉回 $$(S/1)\to(S/X)$$就是给一个对象 $$Y$$送到 $$X\times Y\to X$$，右伴随则是将任何纤维送到纤维积，对于 $$X\times Y\to X$$ 的情况，结果正好就是指数对象 $$Y^{X}$$。可以说，指数对象的那条泛性质基本上就是这个伴随关系里的特例。在 Scala 里，这个拉回相当于一个常函数。

### 关于 nLab 上的使用幺半范畴的定义的解释

这里额外说说当时学习时参考过的 [nLab](https://ncatlab.org/nlab/show/cartesian+closed+category) 上的定义，这里的定义结合上文比较好理解的就是这个内部 Hom，这点其实就是我们申明过的指数对象，但是这个 Cartesian 幺半结构比较诡异（也许因为 nLab 向来会概念套概念定义），对于不了解这部分的初学者来说很绕。

简单来说，如果选择我们刚才选取的积作为张量积 $$\otimes:\mathcal{V}\times\mathcal{V}\to\mathcal{V}$$，也就是 $$(A, B)\mapsto A\times B$$，我们已经讨论了它的泛性质，所以这里略过，幺元就是终对象，暂时略过合成约束之类的定义部分，这样就大致构成了1个幺半范畴，这个结构就是 **Cartesian 幺半范畴**（或许因为张量积是 Cartesian 积，而幺元就是终对象）。在 nLab 的定义中：

> A cartesian closed category is a category with finite products **which is closed with respect to its cartesian monoidal structure**.

这里闭 Cartesian 范畴的闭性实际上是因为选取了闭幺半范畴，也就是说函子 $$-\otimes A$$ 的右伴随（内部 Hom）$$-^{A}$$ 满足自然同构，按内部 Hom 的记法一般是记作 $$[A, -]$$ 这样，这里要求的条件就是：

$$
\mathrm{Hom}_{\mathcal{V}}(A\times B, C)\cong\mathrm{Hom}_{\mathcal{V}}(A, B^{C})
$$

所以整理一下就是，闭 Cartesian 范畴 $$\mathcal{C}$$ 是一个满足以下条件的范畴：

1. $$\mathcal{C}$$ 存在有限积
2. 任取对象 $$A\in\mathrm{Ob}(\mathcal{C})$$ 函子 $$A\times -: \mathcal{C}\to\mathcal{C}$$ 总有右伴随 $$-^{A}: \mathcal{C}\to\mathcal{C}$$。

第 2 条的等价描述就是我们上面的泛性质定义与这里讨论的闭 Cartesian 幺半范畴定义。

## 松幺半函子与 Scala Applicative

现在我们举两个幺半范畴 $$(\mathcal{V}, \otimes, 1)$$ 和 $$(\mathcal{W}, \oplus, i)$$，它们之间的所谓**松幺半函子**大抵指的是以下的结构：

* 函子 $$F: \mathcal{V}\to\mathcal{W}$$
* 自然变换 $$\alpha: F(A)\oplus F(B)\to F(A\otimes B)$$
* 幺元之间的态射：$$\mathrm{id}: i\to F(1)$$

满足相应的结合律等性质。我们接下来先介绍在 Scala 里如何定义这些结构，再对此进行直接的讨论。

参考 [Scala Cats](https://typelevel.org/cats/) 库，一个函子的定义大致是：

```scala
trait Functor[F[_]] {
  def map[A, B](fa: F[A])(f: A => B): F[B]
}
```

我们知道，在先前定义的 $$\textsf{Scala}$$ 范畴中，`Tuple2` 是运算，而幺元则是 `Unit`，所以我们自然就有一些幺半范畴的相应结构对应的运算，例如：

```scala
def associateLeft[A, B, C]: (((A, B), C)) => (A, (B, C))
  = { case ((a, b), c) => (a, (b, c)) }

def associateRight[A, B, C]: ((A, (B, C))) => ((A, B), C)
  = { case (a, (b, c)) => ((a, b), c) }
```

把 `F[_]` 看作一个自函子时，如果它有幺元化和二元运算时，其就是 `(Tuple2, Unit)` 的松幺半函子：

```scala
trait LaxMonoidal[F[_]] extends Functor[F] {
  def unit: F[Unit]
  
  def op[A, B](fa: F[A], fb: F[B]): F[(A, B)]
}
```

当然，因为我们没法在类型上强制约束其满足结合律等性质，所以我们在讨论中都忽略这部分。我们来看最具典型的 `Applicative` 结构：

```scala
trait Applicative[F[_]] extends Functor[F[_]] {
  def pure[A](a: A): F[A]
  
  def ap[A, B](ff: F[A => B])(fa: F[A]): F[B]
  
  override def map[F, B](fa: F[A])(f: A => B): F[B]
    = ap(pure(f))(fa)
}
```

可以看到，这个结构其实是可以实现出 `LaxMonoidal` 的结构的：

```scala
trait Applicative[F[_]] extends LaxMonoidal[F] {
  def pure[A](a: A): F[A]
  
  def ap[A, B](ff: F[A => B])(fa: F[A]): F[B]
  
  override def map[F, B](fa: F[A])(f: A => B): F[B]
    = ap(pure(f))(fa)
    
  override def unit: F[Unit] = pure(())
  
  override def op[A, B](fa: F[A], fb: F[B]): F[(A, B)]
    = ap(map(fa)(a => b => (a, b)))(fb)
}
```

需要注意的是，我们的实现简化了一些过程，与 [Scala Cats](https://typelevel.org/cats/) 中的有所差别（比如其让 `ap` 操作由一个 `Apply`  带来，同时 `Applicative` 自带一个 `unit` 方法）。

## 自函子范畴中的幺半群对象与 Scala Monad

忽略大小问题，可以把所有自函子作为对象，自然变换看作态射看成一个范畴，这个范畴上的幺半结构大致是函子之间的组合 `[A] =>> F[G[A]]` 与恒等函子 `Id[A] = A` 构成的，其上的幺半群对象就是一个自函子配上乘法组合与幺元的自然变换（对应 `pure`）：

```scala
trait Monad[M[_]] extends Applicative[M] {
  def flatten[A](mma: M[M[A]]): M[A]
  
  def flatMap[A, B](ma: M[A])(f: A => M[B]): M[B]
    = flatten(map(ma)(f))
  
  override def pure[A](a: A): M[A]
  
  override def map[A, B](ma: M[A])(f: A => B): M[B]
    = flatMap(ma)(a => pure(f(a)))
}
```

这里的 `flatten` 对应的就是乘法组合，你可以在 [Scala Cats](https://typelevel.org/cats/) 中的 `Monad` 实现的接口 `FlatMap` 中看到它。
