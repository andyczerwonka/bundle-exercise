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
    val totalDiscount: Double = bundles.foldLeft(0.0)(_ + _.discount)
  }

  def bundle(available: Seq[Bundle], cart: Cart): BundledItems = {
    @tailrec
    def bundle0(all: Seq[Bundle], accumulator: Seq[Bundle], remaining: Seq[Item]): BundledItems = {
      findBestDiscount(all, remaining) match {
        case Some(found) => bundle0(all, found.bundles ++ accumulator, found.remaining)
        case None => BundledItems(accumulator, remaining)
      }
    }

    bundle0(available.filter(_.discount > 0), Nil, cart.contents())
  }

  private[this] def findBestDiscount(all: Seq[Bundle], items: Seq[Item]): Option[BundledItems] = {
    def search(bundle: Bundle): BundledItems = {
      @tailrec
      def search0(bundle: Bundle, accumulator: List[Bundle], remaining: Seq[Item]): BundledItems = {
        val diff = remaining.diff(bundle.items)
        if (diff.length == remaining.length - bundle.items.length)
          search0(bundle, bundle :: accumulator, diff)
        else
          BundledItems(accumulator, remaining)
      }

      search0(bundle, Nil, items)
    }

    val xs = all.par.map(search)
    if (xs.nonEmpty) xs.maxBy(_.totalDiscount) match {
      case b: BundledItems if b.totalDiscount > 0 => Some(b)
      case _ => None
    } else None

  }


}