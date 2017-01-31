package bundler

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer

case class Item(label: String, price: Double)

class Cart {
  private val _items: ListBuffer[Item] = ListBuffer.empty
  def add(item: Item*): Unit = _items ++= item
  def remove(item: Item*): Unit = _items --= item
  def contents(): Seq[Item] = _items.toList
}

case class Bundle(items: Seq[Item], price: Double) {
  val discount: Double = items.foldLeft(0.0)(_ + _.price) - price
}

class Bundler(available: Seq[Bundle], cart: Cart) {

  case class BundledItems(bundles: Seq[Bundle], remaining: Seq[Item]) {
    lazy val total: Double = {
      val bundledTotal = bundles.foldLeft(0.0)(_ + _.price)
      val remainderTotal = remaining.foldLeft(0.0)(_ + _.price)
      bundledTotal + remainderTotal
    }
    lazy val discount: Double = bundles.foldLeft(0.0)(_ + _.discount)
  }

  lazy val contents: BundledItems = {
    val cartItems = cart.contents()
    val applicableBundles = available
      .filter(_.discount > 0)
      .filter(b => cartItems.intersect(b.items).length == b.items.length)
    val bestDealFirst = applicableBundles.sortWith(_.discount > _.discount)
    bundle(bestDealFirst, Nil, cartItems)
  }

  @tailrec
  private def bundle(all: Seq[Bundle], accumulator: List[Bundle], remaining: Seq[Item]): BundledItems = {
    all.find(found => remaining.intersect(found.items).length == found.items.length) match {
      case Some(b) => bundle(all, b :: accumulator, remaining.diff(b.items))
      case None => BundledItems(accumulator, remaining)
    }
  }


}