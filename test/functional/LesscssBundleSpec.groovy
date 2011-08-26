import geb.spock.GebSpec
/**
 * @author Paul Fairless
 *
 */

class LesscssBundleSpec extends GebSpec {
    def setupSpec() {
//        browser.getDriver().setJavascriptEnabled(true)
    }
    def "check lesscss rules rendered"(){
        when:
            go('http://localhost:8080/lesscss-resources')
        then:
        $('h1').text() == 'Less Test'
        $('h1').jquery.css('color') == 'rgb(34, 34, 251)'
    }
}