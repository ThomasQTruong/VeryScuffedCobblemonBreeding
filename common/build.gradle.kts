plugins {
    id("cobblemon.base-conventions")
    id("cobblemon.publish-conventions")
}

architectury {
    common()
}

repositories {
    maven {
        url = uri("https://cursemaven.com")
        content {
            includeGroup("curse.maven")
        }
    }
    mavenLocal()
}

dependencies {
    // testRuntimeOnly("dev.architectury", "architectury-transformer", "9.1.12", classifier = "agent")
    // testRuntimeOnly("dev.architectury", "architectury-transformer", "9.1.12", classifier = "runtime")

    implementation(libs.stdlib)
    implementation(libs.reflect)

    modImplementation(libs.fabricLoader)
    modApi ("curse.maven:cobblemon-687131:5336539")
    modApi(libs.architectury)

    //shadowCommon group: 'commons-io', name: 'commons-io', version: '2.6'


    compileOnly("net.luckperms:api:5.4")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
