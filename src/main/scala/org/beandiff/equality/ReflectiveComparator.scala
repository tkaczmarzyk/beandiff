package org.beandiff.equality

import java.util.Comparator

import org.beandiff.core.BreakCycleStrategy
import org.beandiff.core.EndOnSimpleTypeStrategy
import org.beandiff.core.ObjectWalker

/**
 * <p>
 *   Compares two objects using reflection and the following rules:
 *   <ol>
 *     <li>if objects are {@link Comparable}s themselves, it uses their {@code compareTo} methods</li>
 *     <li>otherwise searches for a comparable field and uses it to compare the objects</li>
 *     <li>if there are no comparable fields, it uses its {@code compare} method on for field comparison</li>
 *   </ol>
 * </p>
 *
 * <p>If 2nd or 3rd strategy is used and result of field comparison is zero,
 * then it tries to proceed with another field.</p>
 *
 * <p>
 *   For example, consider following classes:
 *   <pre>{@code
 * class A {
 *    private String name1;
 *    private String name2;
 *
 *    public A(String name1, String name2) {
 *      this.name1 = name1;
 *      this.name2 = name2;
 *    }
 * }
 *
 * class B {
 *    private A a1
 *
 *    public B(A a1) {
 *      this.a1 = a1;
 *    }
 * }</pre>
 *
 *   Then the comparator would work as follows:
 *   <br/><br/>
 *   <table border="1">
 *     <thead>
 *     <tr>
 *       <th>obj1</th><th>obj2</th><th>comparison result</th>
 *     </tr>
 *     </thead>
 *     <tbody>
 *     <tr>
 *       <td>{@code "abc"}</td>
 *       <td>{@code "def"}</td>
 *       <td>{@code "abc".compareTo("def")}</td>
 *     </tr>
 *     <tr>
 *       <td>{@code new A("aa", "bb")}</td>
 *       <td>{@code new A("cc", "dd")}</td>
 *       <td>{@code "aa".compareTo("bb")}</td>
 *     </tr>
 *     <tr>
 *       <td>{@code new A("aa", "bb")}</td>
 *       <td>{@code new A("aa", "XX")}</td>
 *       <td>{@code "bb".compareTo("XX")}</td>
 *     </tr>
 *     <tr>
 *       <td>{@code new B(new A("aa", "bb"))}</td>
 *       <td>{@code new B(new A("AA", "BB"))}</td>
 *       <td>{@code "aa".compareTo("AA")}</td>
 *     </tr>
 *     </tbody>
 *   </table>
 * </p>
 *
 * @author Tomasz Kaczmarzyk
 */
class ReflectiveComparator extends Comparator[Any] {

  override def compare(o1: Any, o2: Any): Int = {
    if (o1 == null && o2 == null)
      0
    else if (o1 == null && o2 != null)
      1
    else if (o1 != null && o2 == null)
      -1
    else {
      require(o1.getClass == o2.getClass) //FIXME what with List and ArrayList?

      if (o1.isInstanceOf[Comparable[_]]) {
        val c1 = o1.asInstanceOf[Comparable[Any]]
        val c2 = o2.asInstanceOf[Comparable[Any]]
        c1.compareTo(c2)
      } else { //FIXME var and dependency to core
        var result = 0
        
        new ObjectWalker(new BreakCycleStrategy(EndOnSimpleTypeStrategy), //TODO add it as comparator's parameters?
            ObjectWalker.DefaultRoutePlanners.withDefault(FieldRoutePlannerWithCache),
          (path, val1, val2, isLeaf) =>
            if (result == 0)
              result = compare(val1, val2)
            // TODO break otherwise
        ).walk(o1, o2)
        
        result
      }
    }
  }
}