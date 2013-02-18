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
