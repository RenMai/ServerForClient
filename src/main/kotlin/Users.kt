object Users {
    val users = hashSetOf<String>()
    // Add user
    fun addUser(name: String) {
        users.add(name)
        println("Added $name")
        println("Users list: $users")

    }

    fun removeUser(name: String) {
        users.remove(name)
        println("Removed $name")
    }
}