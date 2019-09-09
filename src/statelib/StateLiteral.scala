package statelib

import scala.collection.mutable

abstract class StateLiteral[T](_name: String, _parent: StateNode, _initial: T)
	extends StateNode(_name, _parent) {

	override
	val members: mutable.HashMap[String, StateNode] = null

	var value: T = _initial

	/*
	def statelib.StateLiteral(initial: T, name: String, parent: statelib.StateComponent): Unit = {
		this.name = name
		this.root = parent.root
		this.parent = parent

		this.value = initial
	}*/

	override
	def address: String = parent.address + name

	override
	def getState: String = {
		surround(value.toString)
	}

	override
	def applyState(state: String): Unit = {
		value = parseState(state)
	}

	// we assume that .toString gives a representation that is sufficient to be parsed,
	// should this not be the case, .getState (or .toString when applicable) needs to be overwritten as well
	def parseState(state: String): T
}