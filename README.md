# Prototype 3-Part Structure

Prototype for Scala 2 and 3 cross-compilation when using Chisel's SourceInfoTransform.


This repository is illustrating an approach for Scala 2 and Scala 3 cross-compilation that preserves Chisel's `SourceInfoTransform` macro for the Scala 2 version while keeping the Scala 3 version cleaner with the new `using` construct.
The `SourceInfoTransform` is used by Chisel to allow users to chain a call to `apply` after various method calls that take an `implicit` `SourceInfo` argument.

For example:
```scala
val x = Wire(UInt(8.W))
val y = Wire(UInt(8.W))
// Bit extract followed by immediate bit extraction
val z = (x & y)(6, 0)
```

Without the macro, the Scala compiler would error on the bit extraction (call to `apply`) because it assumes the user is trying to pass the implicit `SourceInfo` explicitly.
The macro solves the ambiguity allowing the user to write the code they expect to work.

Scala 3 solves this basic issue with the new `using` replacement for `implicit` arguments.
Passing a `using` argument explicitly requires using the `using` keyword, so there is no ambiguity that the user is intending to call `.apply` in this instance.

To cross-compile Chisel for Scala 2 and Scala 3, we need to preserve the `SourceInfoTransform` for the Scala 2 side, but we do not want it in Scala 3.

## Cross-Compilation Strategy

The basic strategy is to use Scala 2 and Scala 3 specific directories (`src/main/scala-2` and `src/main/scala-3` respectively) to define public API "veneer" classes that implement the public API either with or without the macro, while keeping the bulk of the shared "business" logic in `src/main/scala` which will cross-compile for Scala 2 and Scala 3.

Consider the code in this repository:
```
$ tree core
core
└── src
    └── main
        ├── scala
        │   ├── Bits.scala
        │   └── UInt.scala
        ├── scala-2
        │   ├── Bits.scala
        │   └── UInt.scala
        └── scala-3
            ├── Bits.scala
            └── UInt.scala

6 directories, 6 files
```

The business logic for classes `Bits` and `UInt` live in `core/src/main/scala`.
For example, see [core/src/main/scala/UInt.scala](core/src/main/scala/UInt.scala).
This package private trait contains the implementation of various methods, no public methods requiring an implicit `SourceInfo`.

You then have a Scala 2 veneer in [core/src/main/scala-2/UInt.scala](core/src/main/scala-2/UInt.scala) which exposes the public class `UInt` with public APIs in the Scala 2 way using `SourceInfoTransform`.
You similarly have a Scala 3 veneer in [core/src/main/scala-3/UInt.scala](core/src/main/scala-3/UInt.scala) which exposes much simpler implementation of the public `UInt` with public methods that take `using` arguments.

The Scala 2 and Scala 3 source code that uses `class UInt` are almost entirely source-compatible, so it is easy for users of Chisel to upgrade from Scala 2 to Scala 3.

## Development Approaches

With the end goal in mind, there is still the question of what development process we use to get there.
There is way too much code that needs to be changed to do it in a single commit.

### Approach 1 - Split files one-by-one into "core implementation" and "Scala 2 veneer"

This basic idea is that business logic always stays in `src/main/scala`, and files (or possibly individual classes) have public veneers for Scala 2 split out one-by-one.
Scala 3 cross-compilation cannot happen until all Scala 2-specific logic (i.e. the veneers) are split out from `src/main/scala` into `src/main/scala-2`.

This has the advantage of being the simplest from a `git` history perspective, but the negative that Scala 3 compilation cannot be enabled until the end.

You can see this process followed for this repository in branch [approach-1](https://github.com/jackkoenig/prototype-3-part-structure/tree/approach-1/split-out-veneer-from-shared).
Also see [`git blame` for `src/main/scala/Bits.scala`](https://github.com/jackkoenig/prototype-3-part-structure/blame/approach-1/split-out-veneer-from-shared/core/src/main/scala/Bits.scala).

### Approach 2 - Move all files to `src/main/scala-2`, then split "core implementation" back into `src/main/scala`

In this approach, Scala 2 always compiles and Scala 3 starts compiling as soon as possible.
All code is first moved to `src/main/scala-2`, then bit-by-bit, shared business logic is moved back into `src/main/scala` while the Scala 2 veneer is left behind in `src/main/scala-2`.

This has the advantage of having Scala 3 always compiling (after the initial move), but a negative in that the git blame history has a clean break for business logic when it is moved back into `src/main/scala`.

You can see this process followed in branch [approach-2](https://github.com/jackkoenig/prototype-3-part-structure/tree/approach-2/split-out-shared-from-scala-2).
Also see [`git blame` for `src/main/scala/Bits.scala`](https://github.com/jackkoenig/prototype-3-part-structure/blame/approach-2/split-out-shared-from-scala-2/core/src/main/scala/Bits.scala).

### Approach 3 - Hybrid

In this approach, Scala 2 always compiles and Scala 3 starts compiling as soon as possible, but Scala 3 compilation will be broken on intermediate commits.
Like Approach 2, all code is first moved to `src/main/scala-2`.
Then, when moving shared business logic back into `src/main/scala`, you first `git mv` the full file back and make a commit.
Then in a _subsequent_ commit, you split out the veneer logic into `src/main/scala-2` while leaving the shared business logic in `src/main/scala`.

One very important aspect of this approach is that _both commits must be preserved_.
When using Pull Requests, squash-and-merge cannot be used because it will destory the initial `git mv` commit.
On the intermediate `git mv` commit, Scala 3 compilation will be broken, but it will then work again once the Scala 2 veneer is split out.

This approach has the advantage of Scala 3 always compiling (excluding intermediate commits), while preserving git blame history similarly to Approach 0.
It does have another negative that it requires careful use of `git`.

You can see this process followed in branch [approach-3](https://github.com/jackkoenig/prototype-3-part-structure/tree/approach-3/first-move-to-shared-then-split).
Also see [`git blame` for `src/main/scala/Bits.scala`](https://github.com/jackkoenig/prototype-3-part-structure/blame/approach-3/first-move-to-shared-then-split/core/src/main/scala/Bits.scala).


## Basic Use

You can run the code for Scala 2.13 and Scala 3.3 with the following:

```bash
./mill core[2.13.14].run
./mill core[3.3.3].run
```

It is also useful sometimes to just compile:

```bash
./mill core[2.13.14].compile
./mill core[3.3.3].compile
```
