package data.utils

import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.and

fun Op<Boolean>.isNotNullOp() = this != Op.nullOp<Boolean>()

fun List<Op<Boolean>?>.foldOp() = this.fold(
    initial = null,
    operation = { acc: Op<Boolean>?, cur: Op<Boolean>? ->
        cur?.let {
            acc?.and(cur)
        } ?: acc
    }
) ?: Op.nullOp()