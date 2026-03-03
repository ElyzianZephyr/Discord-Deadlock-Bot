plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version "8.1.1" // Для сборки fat JAR
}

group = "com.deadlock"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    // JDA - Discord API
    implementation("net.dv8tion:JDA:5.0.2")

    // OkHttp - HTTP клиент
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // Jackson - JSON парсинг
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.0")
    implementation("com.fasterxml.jackson.core:jackson-core:2.17.0")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.17.0")

    // Caffeine - In-memory кэш
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")

    // Логирование
    implementation("ch.qos.logback:logback-classic:1.5.6")
    implementation("org.slf4j:slf4j-api:2.0.13")

    // Lombok (опционально, для уменьшения boilerplate кода)
    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")
}

application {
    mainClass.set("com.deadlock.bot.BotApplication")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.shadowJar {
    archiveClassifier.set("")
    mergeServiceFiles()
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }
}