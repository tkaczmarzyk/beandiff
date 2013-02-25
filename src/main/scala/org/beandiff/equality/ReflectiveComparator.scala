/**
 * Copyright (c) 2012-2013, Tomasz Kaczmarzyk.
 *
 * This file is part of BeanDiff.
 *
 * BeanDiff is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * BeanDiff is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BeanDiff; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.beandiff.equality

import java.util.Comparator
import org.beandiff.core.OldBreakCycleStrategy
import org.beandiff.core.EndOnSimpleTypeStrategy
import org.beandiff.core.ObjectWalker
import org.beandiff.core.EndOnNullStrategy
import org.beandiff.support.ClassDictionary
import org.beandiff.core.NoopTransformer
import org.beandiff.core.CompositeDescendingStrategy

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
    if (o1 == null && o2 == null) //TODO move some of those to object walker's callback
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
        
        new ObjectWalker(CompositeDescendingStrategy.allOf(new EndOnNullStrategy(), new OldBreakCycleStrategy(EndOnSimpleTypeStrategy)), //TODO add it as comparator's parameters?
            ObjectWalker.DefaultRoutePlanners.withDefault(FieldRoutePlannerWithCache),
            new ClassDictionary(NoopTransformer),
          (path, val1, val2, isLeaf) =>
            if (result == 0 && isLeaf) //FIXME change isLeaf to isComparable?
              result = compare(val1, val2)
            // TODO break otherwise
        ).walk(o1, o2)
        
        result
      }
    }
  }
}