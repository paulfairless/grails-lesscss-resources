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

        def targetFile = mock(File, constructor('/var/file/file_less.css'))

        def processedFile = mock(File)
        processedFile.getName().returns('file.less').stub()
        processedFile.getAbsolutePath().returns('/var/file/file.less').stub()

        def lessEngine = mock(LessEngine, constructor())
        lessEngine.compile(processedFile, targetFile).once()

        def resource = [processedFile:processedFile, actualUrl:'', sourceUrlExtension:'less', contentType:'', originalUrl:'file.less', tagAttributes:[rel:'stylesheet/less']]
        def config = [:]
        play {
             mapper.map (resource, config)
            assertEquals 'file_less.css', resource.actualUrl
            assertEquals 'css', resource.sourceUrlExtension
            assertEquals 'stylesheet', resource.tagAttributes.rel
            assertEquals 'text/css', resource.contentType
        }
    }

    @Test
    void testMapperHandlesUpperCaseFileExtension() {

        def targetFile = mock(File, constructor('/var/file/file_less.css'))

        def processedFile = mock(File)
        processedFile.getName().returns('file.LESS').stub()
        processedFile.getAbsolutePath().returns('/var/file/file.LESS').stub()

        def lessEngine = mock(LessEngine, constructor())
        lessEngine.compile(processedFile, targetFile).once()

        def resource = [processedFile:processedFile, actualUrl:'', sourceUrlExtension:'LESS', contentType:'', originalUrl:'file.LESS', tagAttributes:[rel:'stylesheet/less']]
        def config = [:]
        play {
             mapper.map (resource, config)
            assertEquals 'file_less.css', resource.actualUrl
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
        def resource = [processedFile:processedFile, actualUrl:'notless.css', sourceUrlExtension:'css', contentType:'', originalUrl:'notless.css', tagAttributes:[rel:'stylesheet']]
        def config = [:]
        play {
             mapper.map (resource, config)
            assertEquals 'notless.css', resource.actualUrl
            assertEquals 'css', resource.sourceUrlExtension
            assertEquals 'stylesheet', resource.tagAttributes.rel
            assertEquals '', resource.contentType
        }

    }

    @Test
    void testGeneratedFilename() {
        assertEquals 'foo/bar_less.css', mapper.generateCompiledFileFromOriginal('foo/bar.less')
        assertEquals 'foo/bar_less.css', mapper.generateCompiledFileFromOriginal('foo/bar.LESS')
        assertEquals 'foo/./bar_less.css', mapper.generateCompiledFileFromOriginal('foo/./bar.less')
        assertEquals 'foo/less/bar_less.css', mapper.generateCompiledFileFromOriginal('foo/less/bar.less')
    }

}
