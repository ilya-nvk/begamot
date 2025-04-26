fun main() {
    DatabaseFactory.init()  // Инициализация БД
    embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) { gson() }
        routing {
            authRoutes()
            adRoutes()
            chatRoutes()
        }
    }.start(wait = true)
}