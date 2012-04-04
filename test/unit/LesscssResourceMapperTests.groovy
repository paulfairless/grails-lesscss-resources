import org.gmock.WithGMock
import org.junit.Test
import grails.test.GrailsUnitTestCase
import org.grails.plugin.resource.mapper.MapperPhase
import org.lesscss.LessCompiler

/**
 * @author Paul Fairless
 *
 */

@WithGMock
class LesscssResourceMapperTests extends GroovyTestCase{

    LesscssResourceMapper mapper
    def lessCompiler
    void setUp() {
        lessCompiler =  mock(LessCompiler, constructor())
        mapper = new LesscssResourceMapper(lessCompiler: lessCompiler)
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

        lessCompiler.compile(originalFile, targetFile).once()

        def resource = [processedFile:processedFile, sourceUrl:fileName, actualUrl:'', sourceUrlExtension:'less', contentType:'', originalUrl:'file.less', tagAttributes:[rel:'stylesheet/less']]
        def config = [:]

        def mockedMapper = mock(mapper)
        mockedMapper.getOriginalFileSystemFile(fileName).returns(originalFile)

        play {
            mapper = new LesscssResourceMapper()
            mockedMapper.map (resource, config)
            assertEquals 'file_less.css', resource.actualUrl
            assertEquals 'file.less', resource.sourceUrl
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
       assertEquals mapper.defaultIncludes, ['**/*.less']
    }

    @Test
    void testGeneratedFilename() {
        assertEquals 'foo/bar_less.css', mapper.generateCompiledFileFromOriginal('foo/bar.less')
        assertEquals 'foo/bar_less.css', mapper.generateCompiledFileFromOriginal('foo/bar.LESS')
        assertEquals 'foo/./bar_less.css', mapper.generateCompiledFileFromOriginal('foo/./bar.less')
        assertEquals 'foo/less/bar_less.css', mapper.generateCompiledFileFromOriginal('foo/less/bar.less')
    }

}
