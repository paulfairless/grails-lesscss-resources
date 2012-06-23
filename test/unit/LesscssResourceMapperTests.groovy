import org.grails.plugin.resource.ResourceMeta
import org.grails.plugin.resource.mapper.MapperPhase
import org.junit.Test
import org.lesscss.LessCompiler
import org.gmock.WithGMock

/**
 * @author Paul Fairless
 */
@WithGMock
class LesscssResourceMapperTests extends GroovyTestCase {

    LesscssResourceMapper mapper
    def lessCompiler

    void setUp() {
        lessCompiler = mock(LessCompiler, constructor())
        mapper = new LesscssResourceMapper()
        mapper.metaClass.log = [debug: {}, error: {}]
    }

    @Test
    void testMapperGeneratesCssFromLessResource() {
        String fileName = "file.less"
        def targetFile = mock(File, constructor('/var/file/file_less.css'))
        targetFile.exists().returns(true).stub()

        def originalFile = mock(File)
        def processedFile = mock(File)
        processedFile.getName().returns(fileName).stub()
        processedFile.getAbsolutePath().returns('/var/file/' + fileName).stub()
        lessCompiler.compress.set(false)
        lessCompiler.compile(originalFile, targetFile).once()

        def config = [:]
        def resource = new ResourceMeta(contentType: '', tagAttributes: [rel: 'stylesheet/less'])
        def resourceMock = mock(resource)
        resourceMock.processedFile.returns(processedFile)
        resourceMock.setProcessedFile(targetFile)
        resourceMock.sourceUrl.returns(fileName)
        resourceMock.updateActualUrlFromProcessedFile().once()

        mock(mapper).getOriginalFileSystemFile(fileName).returns(originalFile)

        play {
            mapper.map(resourceMock, config)
            assertEquals 'stylesheet', resource.tagAttributes.rel
            assertEquals 'text/css', resource.contentType
        }
    }

    @Test
    void testMapperRunsEarlyInProcessingPipeline() {
        assertEquals MapperPhase.GENERATION, mapper.phase
    }

    @Test
    void testMapperIncludesLessCSS() {
        assertEquals(['**/*.less'], mapper.defaultIncludes)
    }

    @Test
    void testGeneratedFilename() {
        assertEquals 'foo/bar_less.css', mapper.generateCompiledFileFromOriginal('foo/bar.less')
        assertEquals 'foo/bar_less.css', mapper.generateCompiledFileFromOriginal('foo/bar.LESS')
        assertEquals 'foo/./bar_less.css', mapper.generateCompiledFileFromOriginal('foo/./bar.less')
        assertEquals 'foo/less/bar_less.css', mapper.generateCompiledFileFromOriginal('foo/less/bar.less')
    }

}
