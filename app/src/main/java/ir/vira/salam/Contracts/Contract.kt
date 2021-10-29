package ir.vira.salam.Contracts

interface Contract<T> {
    fun addAll(items: List<T>?)
    fun add(item: T)
    val all: List<T>
}