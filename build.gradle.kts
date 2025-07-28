plugins {
    kotlin("jvm") version "2.1.21"
}

group = "gold.eggs"
version = "v1.0.0-SNAPSHOT-1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

repositories {
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
}

tasks.jar {
    from(configurations.runtimeClasspath.get().map { zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.register<Copy>("copyToServer") {
    dependsOn("jar")  // 依赖打包任务

    // 源文件路径（根据实际项目名调整）
    val jarFile = tasks.jar.get().outputs.files.singleFile
    from(jarFile)

    // 目标路径（修改为你的服务器 plugins 路径）
    into("D:\\MC\\develop\\server\\1.21.4paper\\plugins")

    // 可选：移除版本号重命名（按需启用）
//    rename { fileName ->
//        fileName.replace("-${project.version}", "")
//    }
}