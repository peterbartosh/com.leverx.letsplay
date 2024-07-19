package plugins.routing

object Routes {
    const val createEvent = "api/events/create"
    const val searchEvents = "api/events/search"
    const val subscribeToEvent = "api/events/subscribe"
    const val getEventById = "api/events/single"

    const val getAbailableLocations = "api/locations/available"
    const val getLocationBookings = "api/locations/{location_id}/bookings"
    const val getUser = "api/users/{user_id}"
    const val getUserEvents = "api/user/events/{user_id}"
    const val getRemainingEvents = "api/user/events/{user_id}/remaining/count"

    const val signIn = "api/auth/sign_in"
    const val signUp = "api/auth/sign_up"
    const val logout = "api/auth/logout"
    const val refreshToken = "api/auth/token/refresh"
    const val getAuthenticatedUser = "api/auth/user"

    object Auth {
        const val authBearer = "auth-bearer"
    }
}