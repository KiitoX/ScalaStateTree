package statelib

class StateInt(_name: String, _parent: StateNode, initial: Int)
	extends StateLiteral[Int](_name, _parent, initial) {

	override
	def parseState(state: String): Int = state.toInt
}

class StateBool(_name: String, _parent: StateNode, initial: Boolean)
	extends StateLiteral[Boolean](_name, _parent, initial) {

	override
	def parseState(state: String): Boolean = {
		state match {
			case "true" => true
			case "false" => false
			case default => throw new UnsupportedOperationException(s"Tried to convert '$default' to a boolean and failed," +
				" perhaps the saved state is corrupted")
		}
	}
}

class StateString(_name: String, _parent: StateNode, initial: String)
	extends StateLiteral[String](_name, _parent, initial) {

	// TODO until an issue arises with our regex, we will stay without string sanitization and quoting.
	// provided that data will only interface with this code we can guarantee there will be no hanging tag pairs.
	// additionally to that names have to be unique on a node by node basis which means that
	// by always grabbing the outermost pairs using a greedy pattern, no problems should arise.
	// on the other hand, maybe I'm just too tired to think of an exploit
	override
	def getState: String = {
		surround(value)
	}

	override
	def parseState(state: String): String = {
		state
	}
}
