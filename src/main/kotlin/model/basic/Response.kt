package model.basic

import io.ktor.http.*

sealed class Response<out A>(open val statusCode: HttpStatusCode) {
    class Data<A>(
        override val statusCode: HttpStatusCode = HttpStatusCode.OK,
        val body: A
    ): Response<A>(statusCode)
    class Error(
        override val statusCode: HttpStatusCode,
        val message: String = ""
    ): Response<Nothing>(statusCode)
}