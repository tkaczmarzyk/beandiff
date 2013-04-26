## 0.2.0 (2013-04-26)

* switched to Scala 2.10
* handling `java.util.Map` targets
* calculating diffs for instances of different classes
* handling fields from supertypes
* respecting object type (entity/value) definition for elements outside collections
* introduced filters + API to set paths to exclude
* extended documentation + doc package is now in Javadoc format (thanks to genjavadoc plugin)
* new LCS implementation (bottom-up)
* removed modifier-based API of `BeanDiff` class in favour of `DiffEngineBuilder`
* `java.util.Date` and `java.lang.Class` are now default end-types
* fixes in `Set` transformations
* fixed handling of `null` values in collections
* ignoring static fields
* introduced `ElementProperty` which is now used for Sets
* configurable presentation of `ElementProperty` 
* extended configuration API of `PlainTextDiffPresenter`
* `PlainTextDiffPresenter` now displays a value deleted from a list

## 0.1.1 (2013-03-04)

* fixed too-aggressive LCS optimization
* fixed bug in `HashSet` transformation
* fixed bug in `DeepDiff.forTarget()` causing invalid output for `Set` diffs
* fixed cycle-detection (using identity instead of equality)
* first version of `DiffEngineBuilder`
* paths are now sorted in `PlainTextDiffPresenter` output
* introduced `Shift` change
* configurable depth level
* `AnnotationEqualityInvestigator`
* `SelectiveEqualityInvestigator`
* minor improvements

## 0.1.0 (2013-02-18)

* improved, LCS-based, list comparison
* ability to perform transformation of the diff target (diff=0 after the transformation)
* refined diff engine and major improvements in the model
* extended diff API
* deprecation of `Diff.hasDifference(Path)`
* removed Guava dependency
* better cycle detection mechanism
* extended `BeanDiff` facade API
* multiple fixes & improvements

## 0.0.2 (2012-12-01)

* better default support for `BigDecimal`
* equality investigator for Comparables
* `print()` method in `BeanDiff`
* `BeanDiffAssert` class with basic assertion method
* minor performance optimizations

## 0.0.1 (2012-10-18)

* basic `Path`-based api 
* deep difference calculation
* reflective comparison of instances of the same type
* transforming unsorted collections to lists with reflection-based comparator
* support for lists
* support for case-insensitive `String` comparison
* `PlainTextDiffPresenter` -- displaying differences in human-readable format
* basic syntax sugar methods in `BeanDiff` class
* basic cycle detection
