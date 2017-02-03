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

object Bundler {

  case class BundledItems(bundles: Seq[Bundle], remaining: Seq[Item]) {
    lazy val totalPrice: Double = {
      val bundledTotal = bundles.foldLeft(0.0)(_ + _.price)
      val remainderTotal = remaining.foldLeft(0.0)(_ + _.price)
      bundledTotal + remainderTotal
    }
    lazy val totalDiscount: Double = bundles.foldLeft(0.0)(_ + _.discount)
  }

  def bundle(available: Seq[Bundle], cart: Cart): BundledItems = {
    @tailrec
    def bundle0(all: Seq[Bundle], accumulator: List[Bundle], remaining: Seq[Item]): BundledItems = {
      all.find(bundle => remaining.intersect(bundle.items).length == bundle.items.length) match {
        case Some(found) => bundle0(all, found :: accumulator, remaining.diff(found.items))
        case None => BundledItems(accumulator, remaining)
      }
    }

    val cartItems = cart.contents()
    val applicableBundles = available.view
      .filter(_.discount > 0)
      .filter(b => cartItems.intersect(b.items).length == b.items.length)
      .sortWith(_.discount > _.discount)
    bundle0(applicableBundles, Nil, cartItems)
  }


}