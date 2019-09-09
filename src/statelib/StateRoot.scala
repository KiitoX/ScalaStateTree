package statelib

import scala.collection.mutable
import scala.util.matching.Regex

class StateRoot extends StateNode("root", null) {
	// the "root" name is somewhat special, in that it has to be omitted in the address,
	// we only use it (perhaps foolishly) in our state output

	override
	def address: String = "/"

	override
	def applyState(state: String): Unit = {
		// the root node needs to handle entry itself
		val pattern: Regex = "(?s)^<root>(.*)</root>$".r

		state match {
			case pattern(content) =>
				super.applyState(content)
		}
	}
}
