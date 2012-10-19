beandiff
========

BeanDiff is a library for calculating differences between Java (or more generally JVM) objects. Main features of BeanDiff include, but are not limited to:

- generating diff for large object graphs (with cycle detection)
- no impact on compared objects - you don't need to add any annotations to your objects nor prepare any other metadata to be able to calculate diff using BeanDiff
- representing differences as differential object which can be analyzed through rich API
- presenting differences in many formats (including easy to read plan text)
- powerful comparison of collections (including unsorted collections of not comparable objects) and maps (handling complex keys without equals/hashcode implemented)
- extensibility - you can specify many aspects of diff calculation process to tailor it exactly to your needs and specifics of the object graphs being compared

See more at project's homepage: http://beandiff.org
