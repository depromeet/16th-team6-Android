package com.depromeet.team6.data.dataremote.util

import com.depromeet.team6.BuildConfig

object Dummy {
    const val DUMMY = "dummy"
}

object ApiConstraints {
    // Auth
    const val API = "api"
    const val AUTH = "auth"
    const val CHECK = "check"
    const val LOGIN = "login"
    const val LOGOUT = "logout"
    const val REISSUE = "reissue"
    const val SIGNUP = "sign-up"
    const val MEMBERS = "members"
    const val ME = "me"

    // Locations
    const val LOCATIONS = "locations"
    const val IS_SERVICE_REGION = "is-service-region"

    // Provider
    const val PROVIDER = "provider"

    // FcmToken
    const val FCM_TOKEN = "fcmToken"

    // Home
    const val LAST_ROUTES = "last-routes"
    const val LAST_ROUTE_ID = "lastRouteId"
    const val BUS_STARTED = "bus-started"

    const val VERSION = "v1"
    const val USERS = "users"

    // Taxi
    const val TAXIFARE = "taxi-fare"

    // TimeLeft
    const val LASTROUTES = "last-routes"
    const val DEPARTUREREMAINING = "departure-remaining"
    const val ROUTEID = "routeId"

    // Alarm
    const val NOTIFICATIONS = "notifications"
    const val USER_ROUTE = "user-routes"

    // Transits
    const val TRANSITS = "transits"
    const val ROUTES = "routes"
    const val BUS_ROUTES = "bus-routes"
    val LAST_ROUTE_CONFIG = BuildConfig.LAST_ROUTE
}
