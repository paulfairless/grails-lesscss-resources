import org.gmock.WithGMock
import org.junit.Test
import grails.test.GrailsUnitTestCase
import com.asual.lesscss.LessEngine

@WithGMock
class LesscssResourceMapperTests extends GroovyTestCase{

    LesscssResourceMapper mapper
    void setUp() {
        mapper = new LesscssResourceMapper()
        mapper.metaClass.log = [debug:{}, error:{}]
    }

    @Test
    void testMapperGeneratesCssFromLessResource() {

        def targetFile = mock(File, constructor('/var/file/notless_less.css'))

        def processedFile = mock(File)
        processedFile.getName().returns('notless.less').stub()
        processedFile.getAbsolutePath().returns('/var/file/notless.less').stub()

        def lessEngine = mock(LessEngine, constructor())
        lessEngine.compile(processedFile, targetFile).once()

        def resource = [processedFile:processedFile, actualUrl:'', sourceUrlExtension:'less', contentType:'', originalUrl:'notless.less', tagAttributes:[rel:'stylesheet/less']]
        def config = [:]
        play {
             mapper.map (resource, config)
            assertEquals 'notless_less.css', resource.actualUrl
            assertEquals 'css', resource.sourceUrlExtension
            assertEquals 'stylesheet', resource.tagAttributes.rel
            assertEquals 'text/css', resource.contentType
        }
    }

    @Test
    void testMapperRunsEarlyInProcessingPipeline(){
       assertEquals 10, mapper.priority
    }

    @Test
    void testMapperExcludesAllButCSS(){
       assertTrue(mapper.defaultExcludes.contains('**/*.js'))
       assertTrue(mapper.defaultExcludes.contains('**/*.png'))
       assertTrue(mapper.defaultExcludes.contains('**/*.gif'))
       assertTrue(mapper.defaultExcludes.contains('**/*.jpg'))
       assertTrue(mapper.defaultExcludes.contains('**/*.jpeg'))
       assertTrue(mapper.defaultExcludes.contains('**/*.gz'))
       assertTrue(mapper.defaultExcludes.contains('**/*.zip'))
    }

    @Test
    void testMapperOnlyProcessesLessFiles(){
        def lessEngine = mock(LessEngine)
        def processedFile = mock(File)
        processedFile.getName().returns('notless.css')
        def resource = [processedFile:processedFile, actualUrl:'notless.less', sourceUrlExtension:'less', contentType:'', originalUrl:'notless.less', tagAttributes:[rel:'stylesheet/less']]
        def config = [:]
        play {
             mapper.map (resource, config)
            assertEquals 'notless.less', resource.actualUrl
            assertEquals 'less', resource.sourceUrlExtension
            assertEquals 'stylesheet/less', resource.tagAttributes.rel
            assertEquals '', resource.contentType
        }

    }

}
