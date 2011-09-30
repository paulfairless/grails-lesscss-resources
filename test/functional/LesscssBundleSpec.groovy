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
            go('')
        then:
        $('h1').text() == 'Less Test'
        $('h1').jquery.css('color') == 'rgb(34, 34, 251)'
        $('h2').jquery.css('color') == 'rgb(132, 34, 16)'
        $('h3').jquery.css('color') == 'rgb(34, 251, 34)'
    }
}
