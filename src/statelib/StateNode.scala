package statelib

import java.security.InvalidKeyException

import scala.collection.mutable
import scala.util.matching.Regex

class StateNode(_name: String, _parent: StateNode) {

	val name: String = _name
	// special cases for root nodes
	val root: StateNode = if (_parent != null) _parent.root else this
	// having root.parent be itself makes some things a lot easier,
	// it also means that ../../ chains are possible for as long as you like
	val parent: StateNode = if (_parent != null) _parent else this

	val members: mutable.HashMap[String, StateNode] = new mutable.HashMap()

	def addNode(name: String): StateNode = {
		val member = new StateNode(name, this)
		// TODO here we hide if another member was overwritten, we may want to communicate/forbid that
		members.put(name, member)
		member
	}

	// TODO currently this is a solid but ugly generic method, it might be more valuable to make one for each,
	// or even just scrap this altogether, perhaps I can find another way to avoid duplication of parentage
	// (which would be a huge avenue for mistakes)
	def addLiteral[T, C <: StateLiteral[T]](cls: Class[C], name: String, value: T): StateLiteral[T] = {
		val literal: C = cls.getConstructors.head.newInstance(name, this, value).asInstanceOf[C]
		// TODO here again, think about obfuscation and if we want to allow overwrites (probably actually not)
		members.put(name, literal)
		literal
	}

	def address: String = parent.address + name + "/"

	def getState: String = surround((members.values map {x => x.getState}).mkString(""))

	def surround(inner: String): String = s"<$name>$inner</$name>"

	def applyState(state: String): Unit = {
		extract(state).foreach({
			case (memberName, inner) =>
				members.get(memberName) match {
					case Some(member) =>
						member.applyState(inner)
					case None => throw new InvalidKeyException(s"Component '$address' received data for member" +
						s"'$memberName', which does not exist, your data may be outdated or corrupted")
						// TODO we may want to handle this differently
				}
		})
	}

	def extract(tree: String): mutable.HashMap[String, String] = {
		val pattern: Regex = "(?s)^<(\\w+)>(.*)</\\1>(.*)$".r

		val result: mutable.HashMap[String, String] = new mutable.HashMap()

		var content = tree

		while (content.nonEmpty) {
			content match {
				case pattern(name, inner, tail) =>
					result.put(name, inner)
					content = tail
			}
		}

		result
	}

	/**
	 * This is the heart of this operation, currently the plan is, that address will basically be a unix-like path
	 * for that reason alone we need parent and root, not sure right now that this all is necessary, but yeh
	 *
	 * as of now a trailing slash is just ignored, no matter if you end up on a node or a literal
	 * @param address
	 * @return
	 */
	def goto(address: String): StateNode = {
		val reRoot: Regex = "^/(.*)$".r
		val reCont: Regex = "^((?:\\w+|\\.\\.?))(?:/(.*))?$".r

		var location = this
		var continue = address

		while (continue.nonEmpty) {
			continue match {
				case reRoot(tail) =>
					location = root
					continue = tail
				case reCont(name, tail) =>
					name match {
						case "." => location = location
						case ".." => location = location.parent
						case name => location = location.members(name)
					}
					// necessary when continue ends without trailing /
					continue = if (tail == null) "" else tail
			}
		}

		location
	}
}
