package plugins.routing

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import model.basic.Response

suspend inline fun <reified T : Any> ApplicationCall.respondToCall(response: Response<T>) {
    when (response) {
        is Response.Data -> this.respond(status = response.statusCode, message = response.body)
        is Response.Error -> this.respond(status = response.statusCode, message = response.message)
    }
}

fun ApplicationCall.getTokenFromHeader() =
    this.request.header("Authorization")?.substring(7)
