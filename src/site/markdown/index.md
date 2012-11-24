Introduction
============

BeanDiff is a library for calculating differences between Java (or more generally JVM) objects. Main features of BeanDiff include, but are not limited to:

- generating diff for large object graphs (with cycle detection)
- no impact on compared objects - you don't need to add any annotations to your objects nor prepare any other metadata to be able to calculate diff using BeanDiff
- representing differences as differential object which can be analyzed through rich API
- presenting differences in many formats (including easy to read plain text)
- powerful comparison of collections (including unsorted collections of not comparable objects) and maps (handling complex keys without equals/hashcode implemented)
- extensibility - you can specify many aspects of diff calculation process to tailor it exactly to your needs and specifics of the object graphs being compared


Quick start
===========

The following example will help you to start using BeanDiff. Consider the following class:

	public class Node {
		private String name;
		private Set<Node> children;

		public Node(String name, Node... children) {
			this.name = name;
			this.children = new HashSet<>(Arrays.asList(children));
		}
	}

And the instances to be compared:

	Node a = new Node("a");
	Node b = new Node("b");

	Node parent1 = new Node("parent1", a, a, b);
	Node parent2 = new Node("parent2", a, b, b);

Then you can use BeanDiff as follows:

	Diff diff = BeanDiff.diff(parent1, parent2);

That's it! Even though you can configure most of the aspects of the comparison, the <tt>BeanDiff</tt> class provides easy to use methods with common defaults. In most cases it's just what you expect.

Of course you need the possibility to inspect the result:

	diff.hasDifference(); // returns true
	diff.hasDifference("name"); // returns true
	diff.hasDifference("children[0].name"); // returns false
	diff.hasDifference("children[1]"); // returns true

If you want you can also print the diff in human-readable format using <tt>BeanDiff</tt>'s method <tt>printDiff</tt>. For the case above it would yield something similar to:

	name -- 'parent1' vs 'parent2'
	children[1].name -- 'a' vs 'b'
