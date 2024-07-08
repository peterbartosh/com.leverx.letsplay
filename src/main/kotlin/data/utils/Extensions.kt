package data.utils

import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.and

fun Op<Boolean>.isNotNullOp() = this != Op.nullOp<Boolean>()

fun List<Op<Boolean>?>.foldAnd() = this
    .filterNotNull()
    .fold(
        initial = Op.TRUE,
        operation = { acc: Op<Boolean>, cur: Op<Boolean> ->
            acc.and(cur)
        }
    )
