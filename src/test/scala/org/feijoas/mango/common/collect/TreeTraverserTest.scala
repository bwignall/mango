/*
 * Copyright (C) 2013 The Mango Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * The code of this project is a port of (or wrapper around) the Guava-libraries.
 *    See http://code.google.com/p/guava-libraries/
 *
 * @author Markus Schneider
 */
package org.feijoas.mango.common.collect

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

/**
 * Tests for [[TreeTraverser]]
 *
 *  @author Markus Schneider
 *  @since 0.11 (copied from guava-libraries)
 */
class TreeTraverserTest extends AnyFunSpec with Matchers {
  case class Tree(value: Char, children: Tree*)
  case class BinaryTree(value: Char, left: BinaryTree, right: BinaryTree)

  val traverser: TreeTraverser[Tree] = TreeTraverser((node: Tree) => node.children)

  //        h
  //      / | \
  //     /  e  \
  //    d       g
  //   /|\      |
  //  / | \     f
  // a  b  c
  val a_ : Tree = Tree('a')
  val b_ : Tree = Tree('b')
  val c_ : Tree = Tree('c')
  val d_ : Tree = Tree('d', a_, b_, c_)
  val e_ : Tree = Tree('e')
  val f_ : Tree = Tree('f')
  val g_ : Tree = Tree('g', f_)
  val h_ : Tree = Tree('h', d_, e_, g_)

  //      d
  //     / \
  //    b   e
  //   / \   \
  //  a   c   f
  //         /
  //        g
  val ba_ : BinaryTree = BinaryTree('a', null, null)
  val bc_ : BinaryTree = BinaryTree('c', null, null)
  val bb_ : BinaryTree = BinaryTree('b', ba_, bc_)
  val bg_ : BinaryTree = BinaryTree('g', null, null)
  val bf_ : BinaryTree = BinaryTree('f', bg_, null)
  val be_ : BinaryTree = BinaryTree('e', null, bf_)
  val bd_ : BinaryTree = BinaryTree('d', bb_, be_)

  def treeAsString(tree: Iterable[Tree]): String = tree.foldLeft("") { case (str, tree) => str + tree.value }
  def bTreeAsString(tree: Iterable[BinaryTree]): String = tree.foldLeft("") { case (str, tree) => str + tree.value }

  describe("A TreeTraverser") {
    it("should be able traverse the tree in preOrder") {
      treeAsString(traverser.preOrderTraversal(h_)) should be("hdabcegf")
    }
    it("should be able traverse the tree in postOrder") {
      treeAsString(traverser.postOrderTraversal(h_)) should be("abcdefgh")
    }
    it("should be able traverse the tree in breadthFirstOrder") {
      treeAsString(traverser.breadthFirstTraversal(h_)) should be("hdegabcf")
    }
  }
}
