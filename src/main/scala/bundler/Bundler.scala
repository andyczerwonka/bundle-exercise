package bundler

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer

case class Item(label: String, price: Double)

class Cart {
  private val _items: ListBuffer[Item] = ListBuffer.empty
  def add(item: Item*): Unit = _items ++= item
  def remove(item: Item*): Unit = _items --= item
  def contents(): List[Item] = _items.toList
}

case class Bundle(items: Seq[Item], price: Double)

class Bundler(all: Set[Bundle], cart: Cart) {

  case class Bundled(bundles: Seq[Bundle], remaining: Seq[Item]) {
    lazy val total: Double = {
      val bundledTotal = bundles.foldLeft(0.0)(_ + _.price)
      val remainderTotal = remaining.foldLeft(0.0)(_ + _.price)
      bundledTotal + remainderTotal
    }
    lazy val discount: Double = cart.contents().foldLeft(0.0)(_ + _.price) - total
  }

  lazy val contents: Bundled = {
    def min(b1: Bundled, b2: Bundled): Bundled = if (b1.total < b2.total) b1 else b2
    val cartItems = cart.contents()
    val applicableBundles = all.filter(b => cartItems.intersect(b.items).length == b.items.length)
    bundle(applicableBundles, Nil, cartItems)
  }

  @tailrec
  private def bundle(all: Set[Bundle], accumulator: List[Bundle], remaining: Seq[Item]): Bundled = {
    all.find(found => remaining.intersect(found.items).length == found.items.length) match {
      case Some(b) => bundle(all, b :: accumulator, remaining.diff(b.items))
      case None => Bundled(accumulator, remaining)
    }
  }


}