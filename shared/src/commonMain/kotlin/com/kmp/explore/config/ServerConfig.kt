package com.kmp.explore.config

object ServerConfig {
    //    private const val SERVER_MODE = "broken"
    private const val SERVER_MODE = "working"

    private const val WORKING_SERVER = "192.168.0.229"
    private const val BROKEN_SERVER = "fake-server"

    private const val PORT = "8080"

    val EMULATOR_URL =
        if (SERVER_MODE == "working") "http://10.0.2.2:$PORT" else "http://fake-server:$PORT"
    val IOS_SIMULATOR_URL =
        if (SERVER_MODE == "working") "http://$WORKING_SERVER:$PORT" else "http://$BROKEN_SERVER:$PORT"
    val DEVICE_URL =
        if (SERVER_MODE == "working") "http://$WORKING_SERVER:$PORT" else "http://$BROKEN_SERVER:$PORT"
}