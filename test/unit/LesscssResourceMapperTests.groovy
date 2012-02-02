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
    }

    @Test
    void testMapperGeneratesCssFromLessResource() {
        String fileName = "file.less"
        def targetFile = mock(File, constructor('/var/file/file_less.css'))

        def originalFile = mock(File)
        def processedFile = mock(File)
        processedFile.getName().returns(fileName).stub()
        processedFile.getAbsolutePath().returns('/var/file/'+fileName).stub()

        def lessEngine = mock(LessEngine, constructor())
        lessEngine.compile(originalFile, targetFile).once()

        def resource = [processedFile:processedFile, sourceUrl:fileName, actualUrl:'', sourceUrlExtension:'less', contentType:'', originalUrl:'file.less', tagAttributes:[rel:'stylesheet/less']]
        def config = [:]

        def mockedMapper = mock(mapper)
        mockedMapper.getOriginalFileSystemFile(fileName).returns(originalFile)

        play {
            mockedMapper.map (resource, config)
            assertEquals 'file_less.css', resource.actualUrl
            assertEquals 'file_less.css', resource.sourceUrl
            assertEquals 'css', resource.sourceUrlExtension
            assertEquals 'stylesheet', resource.tagAttributes.rel
            assertEquals 'text/css', resource.contentType
        }
    }

    @Test
    void testMapperHandlesUpperCaseFileExtension() {
        String fileName = "file.LESS"
        def targetFile = mock(File, constructor('/var/file/file_less.css'))

        def originalFile = mock(File)
        def processedFile = mock(File)
        processedFile.getName().returns(fileName).stub()
        processedFile.getAbsolutePath().returns('/var/file/'+fileName).stub()

        def lessEngine = mock(LessEngine, constructor())
        lessEngine.compile(originalFile, targetFile).once()

        def mockedMapper = mock(mapper)
        mockedMapper.getOriginalFileSystemFile(fileName).returns(originalFile)

        def resource = [processedFile:processedFile, sourceUrl:fileName, actualUrl:'', sourceUrlExtension:'LESS', contentType:'', originalUrl:'file.LESS', tagAttributes:[rel:'stylesheet/less']]
        def config = [:]
        play {
            mockedMapper.map (resource, config)
            assertEquals 'file_less.css', resource.actualUrl
            assertEquals 'file_less.css', resource.sourceUrl
            assertEquals 'css', resource.sourceUrlExtension
            assertEquals 'stylesheet', resource.tagAttributes.rel
            assertEquals 'text/css', resource.contentType
        }
    }


    @Test
    void testMapperRunsEarlyInProcessingPipeline(){
       assertEquals MapperPhase.GENERATION , mapper.phase
    }

    @Test
    void testMapperIncludesLessCSS(){
       assertTrue(mapper.defaultIncludes.contains('**/*.less'))
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
            assertEquals 'notless.css', resource.sourceUrl
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
