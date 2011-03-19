import grails.plugin.geb.GebSpec
/**
 * User: Paul Fairless
 * Date: 16/03/2011
 * Time: 22:19
 */

class LesscssBundleSpec extends GebSpec {
    def setupSpec() {
//        browser.getDriver().setJavascriptEnabled(true)
    }
    def "check lesscss rules rendered"(){
        when:
            go('/lesscss-resources')
        then:
        $('h1').text() == 'Less Test'
        js.exec("""return jQuery('h1').css('color');""") == 'rgb(34, 34, 251)'
    }
}