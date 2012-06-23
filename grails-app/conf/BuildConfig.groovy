grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.release.scm.enabled = false
//grails.project.war.file = "target/${appName}-${appVersion}.war"
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        grailsPlugins()
        grailsHome()
        grailsCentral()

        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
        //mavenLocal()
        mavenCentral()
        grailsRepo "http://grails.org/plugins"
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }

    def seleniumVersion = "2.21.0"
    def gebVersion = "0.7.0"

    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
        runtime "org.lesscss:lesscss:1.3.0"

        test("org.gmock:gmock:0.8.2") {
            export = false
        }
        test("org.seleniumhq.selenium:selenium-htmlunit-driver:$seleniumVersion") {
            export = false
            exclude "xml-apis"
        }
        test("org.seleniumhq.selenium:selenium-chrome-driver:$seleniumVersion") {
            export = false
        }
        test("org.seleniumhq.selenium:selenium-firefox-driver:$seleniumVersion") {
            export = false
        }

        // You usually only need one of these, but this project uses both
        test("org.codehaus.geb:geb-spock:$gebVersion") {
            export = false
        }
    }
    plugins {
        test(":spock:0.6") {
            export = false
        }
        test(":geb:0.7.0") {
            export = false
        }
        compile(":resources:1.1.6") {
            export = false
        }
        compile(":tomcat:$grailsVersion") {
            export = false
        }
        /*compile(":release:2.0.3") {
            export = false
        }*/
    }
}
