plugins {
    java
    idea
    id("org.springframework.boot") version "4.0.6"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "vik.telegrambots"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.21.2")
    implementation("com.fasterxml.jackson.core:jackson-core:2.21.2")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.21")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.21.2")
    implementation("org.bouncycastle:bcprov-jdk18on:1.84")
    implementation("org.bouncycastle:bcpkix-jdk18on:1.84")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.projectlombok:lombok")
    runtimeOnly("com.h2database:h2")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.telegram:telegrambots-springboot-longpolling-starter:9.5.0") {
        exclude(group = "com.fasterxml.jackson.core")
        exclude(group = "com.fasterxml.jackson.datatype")
    }
    implementation("org.telegram:telegrambots-abilities:9.5.0") {
        exclude(group = "net.jpountz.lz4", module = "lz4")
        exclude(group = "org.eclipse.jetty.http2", module = "http2-common")
    }
    implementation("org.telegram:telegrambots-client:9.5.0")
    implementation("at.yawk.lz4:lz4-java:1.10.4")
    implementation("org.eclipse.jetty.http2:http2-common:11.0.26")
    implementation("org.apache.commons:commons-collections4:4.4")
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:4.0.2")
    implementation("com.github.ulisesbocchio:jasypt-spring-boot-starter:4.0.4")
    implementation("com.github.gavlyukovskiy:datasource-proxy-spring-boot-starter:2.0.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
