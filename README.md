### Update (Feb 4, 2017)
The initial version [made an invalid assumption](https://github.com/andyczerwonka/bundle-exercise/issues/2) about the how to find the optimal solution. Even though the latest version now passes [the test that originally failed](https://github.com/andyczerwonka/bundle-exercise/commit/3d78f68d1d5c3d7c863ade3a12ad16fe7435ddf4), the solution is still greedy... it's just a little smarter about it. An integer linear programming formulation might be the right choice for this TSP.

# Bundle Pricing
This exercise is a common problem in e-commerce and brick-and-mortar retail systems.

A customer shops from some form of catalog, selecting items and quantities they wish to purchase. When they are ready, they “check out”, that is, complete their purchase, at which point they are given a total amount of money they owe for the purchase of a specific set of items.

In the bounds of this problem, certain groups of items can be taken together as a “bundle” with a different price. E.g. if I buy a single apple in isolation it costs $1.99, if I buy two apples it’s $2.15. More complex combinations are possible - e.g. a loaf of bread “A” purchased with two sticks of margarine “B” and the second stick of margarine is free (e.g. $0). The same item may appear in more than one bundle, e.g. any one “cart” of items might be able to be combined in more than one way.

For this exercise, produce an API and implementation for a service that accepts a collection of items the customer wishes to purchase (e.g. items and quantities), and produces the lowest possible price for that collection of items. The API is to be called by other applications in the same JVM.

## Implementation Notes

1. No shared mutable state, e.g. immutable types, i.e. no `var`, only `val`, etc.
1. Bundling is a pure stateless function, no side effects.
1. Some tests are mostly written in concrete terms according to the spec to help the reader associate the test with the requirement but are general in nature. There are also some more general tests.
1. No external dependencies. No functional libraries, e.g. scalaz, cats, etc. Just standard Scala.
1. Tests can be run via `sbt test`.
