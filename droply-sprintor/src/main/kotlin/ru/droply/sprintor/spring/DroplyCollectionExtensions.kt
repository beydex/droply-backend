package ru.droply.sprintor.spring

/**
 * Filters list of pair only when key is not null
 */
fun <K, V> Collection<Pair<K?, V>>.filterKeyNotNull(): Collection<Pair<K, V>> {
    return this
        .filter { (key, _) -> key != null }
        .map { (key, value) -> key!! to value }
}

/**
 * Attracts key to all of its values
 */
fun <K, V> Collection<Pair<K, V>>.attract(): Map<K, List<V>> {
    return this
        .groupingBy { (key, _) -> key }
        .fold(listOf()) { accumulator, (_, value) -> accumulator + listOf(value) }
}
