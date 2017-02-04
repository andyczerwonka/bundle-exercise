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
  lazy val discount: Double = items.foldLeft(0.0)(_ + _.price) - price
}

object Bundler {

  case class BundledCart(bundles: Seq[Bundle], remaining: Seq[Item]) {
    lazy val totalPrice: Double = {
      val bundledTotal = bundles.foldLeft(0.0)(_ + _.price)
      val remainderTotal = remaining.foldLeft(0.0)(_ + _.price)
      bundledTotal + remainderTotal
    }
    lazy val totalDiscount: Double = bundles.foldLeft(0.0)(_ + _.discount)
  }

  def bundle(available: Seq[Bundle], cart: Cart): BundledCart = {
    @tailrec
    def bundle0(all: Seq[Bundle], accumulator: Seq[Bundle], remaining: Seq[Item]): BundledCart = {
      findBestDiscount(all, remaining) match {
        case Some(found) => bundle0(all, found.bundles ++ accumulator, found.remaining)
        case None => BundledCart(accumulator, remaining)
      }
    }

    bundle0(available.filter(_.discount > 0), Nil, cart.contents())
  }

  private[this] def findBestDiscount(all: Seq[Bundle], items: Seq[Item]): Option[BundledCart] = {
    def search(bundle: Bundle): BundledCart = {
      @tailrec
      def search0(bundle: Bundle, accumulator: List[Bundle], remaining: Seq[Item]): BundledCart = {
        val diff = remaining.diff(bundle.items)
        if (diff.length == remaining.length - bundle.items.length)
          search0(bundle, bundle :: accumulator, diff)
        else
          BundledCart(accumulator, remaining)
      }

      search0(bundle, Nil, items)
    }

    val candidates = all.par.map(search)
    if (candidates.nonEmpty) candidates.maxBy(_.totalDiscount) match {
      case b: BundledCart if b.totalDiscount > 0 => Some(b)
      case _ => None
    } else None
  }
  
}