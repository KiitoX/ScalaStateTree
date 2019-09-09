import statelib.{StateBool, StateInt, StateLink, StateRoot, StateString}

object Main {
	def main(args: Array[String]): Unit = {
		val state = new StateRoot()

		val cat = state.addNode("cat")
		cat.addNode("kitten")
			.addLiteral(classOf[StateInt], "test", 5)
		val msg = cat.addNode("data")
			.addLiteral(classOf[StateString], "msg",
				"reader, please be aware, that < and > will not break your </string>")


		val cow = state.addNode("cow")
		cow.addNode("calf")
		cow.addLiteral(classOf[StateInt], "size", 5)
		cow.members.put("link", new StateLink("link", cow, msg))
		cow.members.put("another", new StateLink("another", cow, cat))

		cat.addLiteral(classOf[StateBool], "youth", true)

		var storedState = state.getState

		println(storedState)

		storedState = storedState.replace("reader", "free people")

		state.applyState(storedState)

		println(state.goto("/cow/../cat/data/msg").asInstanceOf[StateString].value)
		println(state.goto("/cow/link").asInstanceOf[StateLink].target.asInstanceOf[StateString].value)
		println(state.goto("/cow/another/youth").asInstanceOf[StateBool].value)
	}
}
