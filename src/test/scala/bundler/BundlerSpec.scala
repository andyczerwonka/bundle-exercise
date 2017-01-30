package bundler

import org.scalatest._

class BundlerSpec extends FlatSpec with Matchers {

  val epsilon = 1e-5

  val apple = Item("Apple", 1.99)
  val bread = Item("Bread", 3.0)
  val butter = Item("Butter", 3.0)

  val twoApplesBundle = Set(Bundle(List(apple, apple), 2.19))
  val freeButterBundle = Set(Bundle(List(bread, butter, butter), 6.0))

  val bothBundles = twoApplesBundle ++ freeButterBundle

  "The Bundler" should "charge $1.99 for one apple" in {
    val cart = new Cart()
    cart.add(apple)
    val bundler = new Bundler(bothBundles, cart)
    bundler.contents.total should equal (1.99)
  }

  it should "charge $2.19 for two apples" in {
    val cart = new Cart()
    cart.add(apple, apple)
    val bundler = new Bundler(bothBundles, cart)
    bundler.contents.total should be (2.19)
    bundler.contents.discount should be (1.79)
    bundler.contents.remaining should be (empty)
  }

  it should "charge $4.38 for four apples" in {
    val cart = new Cart()
    cart.add(apple, apple, apple, apple)
    val bundler = new Bundler(bothBundles, cart)
    bundler.contents.total should be (4.38)
    bundler.contents.discount should be (3.58)
    bundler.contents.bundles should contain theSameElementsAs Vector(twoApplesBundle.head, twoApplesBundle.head)
    bundler.contents.remaining should be (empty)
  }

  it should "charge $3.98 for two apples without the apples bundle" in {
    val cart = new Cart()
    cart.add(apple, apple)
    val bundler = new Bundler(Set.empty, cart)
    bundler.contents.total should be (3.98)
    bundler.contents.discount should be (0.0)
    bundler.contents.bundles shouldBe empty
  }

  it should "charge $6.00 for bread and two butter" in {
    val cart = new Cart()
    cart.add(bread, butter, butter)
    val bundler = new Bundler(bothBundles, cart)
    bundler.contents.total should be (6.0)
    bundler.contents.discount should be (3.0)
    bundler.contents.bundles should contain theSameElementsAs freeButterBundle
  }

  it should "charge $8.19 for bread, two apples and two butter" in {
    val cart = new Cart()
    cart.add(bread, apple, apple, butter, butter)
    val bundler = new Bundler(bothBundles, cart)
    bundler.contents.total should be (8.19)
    bundler.contents.discount should be (4.79 +- epsilon)
    bundler.contents.bundles should contain theSameElementsAs bothBundles
    bundler.contents.remaining should be (empty)
  }

  it should "charge $11.19 for bread, two apples and three butter" in {
    val cart = new Cart()
    cart.add(bread, apple, apple, butter, butter, butter)
    val bundler = new Bundler(bothBundles, cart)
    bundler.contents.total should be (11.19)
    bundler.contents.discount should be (4.79 +- epsilon)
    bundler.contents.remaining should contain only (butter)
  }

  it should "charge $0 for an empty cart" in {
    val cart = new Cart()
    val bundler = new Bundler(bothBundles, cart)
    bundler.contents.total should be (0.0)
    bundler.contents.discount should be (0.0)
    bundler.contents.bundles should be (empty)
    bundler.contents.remaining should be (empty)
  }

  it should "find the cheapest bundle, which in this case is the six-tomatoe bundle" in {
    val tomato = Item("Tomato", 1.0)
    val breadFreeTomatoBundle = Set(Bundle(List(bread, tomato), 3.0))
    val sixTomatoBundle = Set(Bundle(List(tomato, tomato, tomato, tomato, tomato, tomato), 2.0))
    val cart = new Cart()
    cart.add(bread, tomato, tomato, tomato, tomato, tomato, tomato)
    val bundler = new Bundler(breadFreeTomatoBundle ++ sixTomatoBundle, cart)
    bundler.contents.total should be (5.0)
    bundler.contents.discount should be (4.0)
    bundler.contents.bundles should contain theSameElementsAs sixTomatoBundle
    bundler.contents.remaining should contain only (bread)
  }

}