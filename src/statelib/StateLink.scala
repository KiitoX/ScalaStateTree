package statelib
import scala.collection.mutable

class StateLink(_name: String, _parent: StateNode, _target: StateNode)
	extends StateNode(_name, _parent) {

	// TODO currently links only make access to nodes easy, not to literals, this needs remedy
	val target: StateNode = _target

	// TODO immutable is not really gonna fly here, maybe? depends if it needs to be runtime modifiable,
	// may be nice-to-have otherwise this'll just be a compile-time ease of access
	// another use case for this could be data migration, move the node in code, place a link there and
	// when loading a previous state dump, the old data should be applied to the new node.
	// on the next dump, the new node will be saved, the link should not be included in the dump
	// this will only work if the link is done at compile-time, so maybe we need two different links?
	override
	val members: mutable.HashMap[String, StateNode] = target.members //TODO without getter (+setter) we are maybe just copying stuff? I don't know

	override
	def getState: String = ""
}