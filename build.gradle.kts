plugins {
	java
	id("org.springframework.boot") version "3.2.5"
	id("io.spring.dependency-management") version "1.1.4"
	id("com.google.cloud.tools.jib") version "3.4.1"
}

group = "vik.telegrambots"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_21
	targetCompatibility = JavaVersion.VERSION_21
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
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	compileOnly("org.projectlombok:lombok")
	runtimeOnly("com.h2database:h2")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	implementation("org.telegram:telegrambots-spring-boot-starter:6.9.7.1")
	implementation("org.telegram:telegrambots-abilities:6.9.7.1")
	implementation("org.apache.commons:commons-collections4:4.4")
	implementation("javax.xml.bind:jaxb-api:2.4.0-b180830.0359")
	implementation("com.github.ulisesbocchio:jasypt-spring-boot-starter:3.0.5")
	implementation("org.postgresql:postgresql")
	implementation("com.github.gavlyukovskiy:datasource-proxy-spring-boot-starter:1.9.1")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
