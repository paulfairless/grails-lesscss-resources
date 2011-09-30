import org.gmock.WithGMock
import org.junit.Test
import grails.test.GrailsUnitTestCase
import com.asual.lesscss.LessEngine
import org.grails.plugin.resource.mapper.MapperPhase

/**
 * @author Paul Fairless
 *
 */

@WithGMock
class LesscssResourceMapperTests extends GroovyTestCase{

    LesscssResourceMapper mapper
    void setUp() {
        mapper = new LesscssResourceMapper()
        mapper.metaClass.log = [debug:{}, error:{}]
        mapper.grailsApplication = [ classLoader: Thread.currentThread().contextClassLoader ]
    }

    @Test
    void testMapperGeneratesCssFromLessResource() {
        String fileName = "file.less"
        def targetFile = mock(File, constructor('/var/file/file_less.css'))
        targetFile.setText(match { true }).once()

        def originalFile = mock(URL)
        def processedFile = mock(File)
        processedFile.getName().returns(fileName).stub()
        processedFile.getAbsolutePath().returns('/var/file/'+fileName).stub()

        def lessEngine = mock(LessEngine, constructor())
        lessEngine.compile(originalFile).once()

        def resource = [processedFile:processedFile, sourceUrl:fileName, actualUrl:'', sourceUrlExtension:'less', contentType:'', originalUrl:'file.less', tagAttributes:[rel:'stylesheet/less'], updateActualUrlFromProcessedFile: { actualUrl = 'file_less.css' }]
        resource.updateActualUrlFromProcessedFile.delegate = resource
        def config = [:]

        def mockedMapper = mock(mapper)
        mockedMapper.getOriginalResourceURLForURI(fileName).returns(originalFile)

        play {
            mockedMapper.map (resource, config)
            assertEquals 'file_less.css', resource.actualUrl
            assertEquals 'css', resource.sourceUrlExtension
            assertEquals 'stylesheet', resource.tagAttributes.rel
        }
    }

    @Test
    void testMapperHandlesUpperCaseFileExtension() {
        String fileName = "file.LESS"
        def targetFile = mock(File, constructor('/var/file/file_less.css'))
        targetFile.setText(match { true }).once()

        def originalFile = mock(URL)
        def processedFile = mock(File)
        processedFile.getName().returns(fileName).stub()
        processedFile.getAbsolutePath().returns('/var/file/'+fileName).stub()

        def lessEngine = mock(LessEngine, constructor())
        lessEngine.compile(originalFile).once()

        def mockedMapper = mock(mapper)
        mockedMapper.getOriginalResourceURLForURI(fileName).returns(originalFile)

        def resource = [processedFile:processedFile, sourceUrl:fileName, actualUrl:'', sourceUrlExtension:'LESS', contentType:'', originalUrl:'file.LESS', tagAttributes:[rel:'stylesheet/less'], updateActualUrlFromProcessedFile: { actualUrl = 'file_less.css' }]
        resource.updateActualUrlFromProcessedFile.delegate = resource
        def config = [:]
        play {
            mockedMapper.map (resource, config)
            assertEquals 'file_less.css', resource.actualUrl
            assertEquals 'css', resource.sourceUrlExtension
            assertEquals 'stylesheet', resource.tagAttributes.rel
        }
    }


    @Test
    void testMapperRunsEarlyInProcessingPipeline(){
       assertEquals MapperPhase.GENERATION , mapper.phase
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
        def resource = [processedFile:processedFile, sourceUrl:'notless.css', actualUrl:'notless.css', sourceUrlExtension:'css', contentType:'', originalUrl:'notless.css', tagAttributes:[rel:'stylesheet']]
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
