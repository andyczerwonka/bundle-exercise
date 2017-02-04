package bundler

import org.scalatest._
import Bundler._

class BundlerSpec extends FlatSpec with Matchers {

  val epsilon = 1e-5

  val apple = Item("Apple", 1.99)
  val bread = Item("Bread", 3.0)
  val butter = Item("Butter", 3.0)

  val twoApplesBundle = Seq(Bundle(List(apple, apple), 2.19))
  val freeButterBundle = Seq(Bundle(List(bread, butter, butter), 6.0))

  val bothBundles: Seq[Bundle] = twoApplesBundle ++ freeButterBundle

  "The Bundler" should "charge $1.99 for one apple" in {
    val cart = new Cart()
    cart.add(apple)
    val bundled = bundle(bothBundles, cart)
    bundled.totalPrice should equal (1.99)
    bundled.totalDiscount should be (0.0)
    bundled.bundles should be (empty)
    bundled.remaining should contain only apple
  }

  it should "charge $2.19 for two apples" in {
    val cart = new Cart()
    cart.add(apple, apple)
    val bundled = bundle(bothBundles, cart)
    bundled.totalPrice should be (2.19)
    bundled.totalDiscount should be (1.79)
    bundled.remaining should be (empty)
    bundled.bundles should contain only twoApplesBundle.head
  }

  it should "allow for the sampe bundle to be applied twice" in {
    val cart = new Cart()
    cart.add(apple, apple, apple, apple)
    val bundled = bundle(bothBundles, cart)
    bundled.totalPrice should be (4.38)
    bundled.totalDiscount should be (3.58)
    bundled.bundles should contain theSameElementsAs Vector(twoApplesBundle.head, twoApplesBundle.head)
    bundled.remaining should be (empty)
  }

  it should "charge full price for items when no bundle applies" in {
    val cart = new Cart()
    cart.add(apple, apple)
    val bundled = bundle(Nil, cart)
    bundled.totalPrice should be (3.98)
    bundled.totalDiscount should be (0.0)
    bundled.bundles should be (empty)
  }

  it should "charge $6.00 for bread and two butter" in {
    val cart = new Cart()
    cart.add(bread, butter, butter)
    val bundled = bundle(bothBundles, cart)
    bundled.totalPrice should be (6.0)
    bundled.totalDiscount should be (3.0)
    bundled.bundles should contain theSameElementsAs freeButterBundle
    bundled.remaining should be (empty)
  }

  it should "charge $8.19 for bread, two apples and two butter" in {
    val cart = new Cart()
    cart.add(bread, apple, apple, butter, butter)
    val bundled = bundle(bothBundles, cart)
    bundled.totalPrice should be (8.19)
    bundled.totalDiscount should be (4.79 +- epsilon)
    bundled.bundles should contain theSameElementsAs bothBundles
    bundled.remaining should be (empty)
  }

  it should "charge $11.19 for bread, two apples and three butter" in {
    val cart = new Cart()
    cart.add(bread, apple, apple, butter, butter, butter)
    val bundled = bundle(bothBundles, cart)
    bundled.totalPrice should be (11.19)
    bundled.totalDiscount should be (4.79 +- epsilon)
    bundled.remaining should contain only butter
  }

  it should "charge $0 for an empty cart" in {
    val cart = new Cart()
    val bundled = bundle(bothBundles, cart)
    bundled.totalPrice should be (0.0)
    bundled.totalDiscount should be (0.0)
    bundled.bundles should be (empty)
    bundled.remaining should be (empty)
  }

  it should "not apply bundles that are more expensive than retail" in {
    val tomato = Item("Tomato", 1.0)
    val twoTomatoBundle = Seq(Bundle(List(tomato, tomato), 3.0))
    val cart = new Cart()
    cart.add(tomato, tomato)
    val bundled = bundle(twoTomatoBundle, cart)
    bundled.totalPrice should be (2.0)
    bundled.totalDiscount should be (0.0)
    bundled.bundles should be (empty)
    bundled.remaining should contain theSameElementsAs List(tomato, tomato)
  }

  it should "find the cheapest bundle when more than one bundle apply" in {
    val tomato = Item("Tomato", 1.0)
    val breadFreeTomatoBundle = Seq(Bundle(List(bread, tomato), 3.0))
    val sixTomatoBundle = Seq(Bundle(List(tomato, tomato, tomato, tomato, tomato, tomato), 2.0))
    val cart = new Cart()
    cart.add(bread, tomato, tomato, tomato, tomato, tomato, tomato)
    val bundled = bundle(breadFreeTomatoBundle ++ sixTomatoBundle, cart)
    bundled.totalPrice should be (5.0)
    bundled.totalDiscount should be (4.0)
    bundled.bundles should contain theSameElementsAs sixTomatoBundle
    bundled.remaining should contain only bread
  }

  it should "find the cheapest bundle even when many instances of lesser bundle out performs a better bundle" in {
    val tomato = Item("Tomato", 1.0)
    val breadBundle = Seq(Bundle(List(bread, tomato), 3.5))
    val threeTomatoBundle = Seq(Bundle(List(tomato, tomato, tomato), 2.0))
    val cart = new Cart()
    cart.add(bread, bread, bread, tomato, tomato, tomato)
    val bundled = bundle(breadBundle ++ threeTomatoBundle, cart)
    bundled.totalPrice should be (10.5)
    bundled.totalDiscount should be (1.5)
    bundled.bundles should contain theSameElementsAs List(breadBundle.head, breadBundle.head, breadBundle.head)
    bundled.remaining should be (empty)
  }

}