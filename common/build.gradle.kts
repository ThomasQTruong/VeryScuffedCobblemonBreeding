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
    implementation(libs.stdlib)
    implementation(libs.reflect)

    modImplementation(libs.fabricLoader)
    modApi ("curse.maven:cobblemon-687131:4797468")
    modApi(libs.architectury)

    //shadowCommon group: 'commons-io', name: 'commons-io', version: '2.6'


    compileOnly("net.luckperms:api:5.4")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
