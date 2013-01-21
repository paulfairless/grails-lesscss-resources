import org.gmock.WithGMock
import org.junit.Test
import grails.test.GrailsUnitTestCase
import org.grails.plugin.resource.mapper.MapperPhase
import org.grails.plugin.resource.ResourceMeta
import org.lesscss.LessCompiler
import org.codehaus.groovy.grails.commons.GrailsApplication

/**
 * @author Paul Fairless
 *
 */

@WithGMock
class LesscssResourceMapperTests extends GrailsUnitTestCase {

    LesscssResourceMapper mapper
    def lessCompiler

    void setUp() {
        lessCompiler =  mock(LessCompiler, constructor())
        mapper = new LesscssResourceMapper()
        mapper.grailsApplication = mock(GrailsApplication)
        mapper.metaClass.log = [debug:{}, error:{}]
    }

    @Test
    void testMapperGeneratesCssFromLessResource() {
        String fileName = "file.less"
        def targetFile = mock(File, constructor('/var/file/file.less.css'))
        targetFile.exists().returns(true).stub()

        def originalFile = mock(File)
        def processedFile = mock(File)
        processedFile.getName().returns(fileName).stub()
        processedFile.getAbsolutePath().returns('/var/file/'+fileName).stub()

        lessCompiler.compile(originalFile, targetFile).once()

        mapper.grailsApplication.getConfig().returns([:])
        lessCompiler.setCompress(false).once()

        def config = [:]
        def resource = new ResourceMeta(contentType:'',  tagAttributes:[rel:'stylesheet/less'])
        def resourceMock = mock(resource)
        resourceMock.processedFile.returns(processedFile)
        resourceMock.setProcessedFile(targetFile)
        resourceMock.sourceUrl.returns(fileName)
        resourceMock.updateActualUrlFromProcessedFile().once()

        def mockedMapper = mock(mapper)
        mockedMapper.getOriginalFileSystemFile(fileName).returns(originalFile)

        play {
            mapper = new LesscssResourceMapper()

            mockedMapper.map (resourceMock, config)
            assertEquals 'stylesheet', resource.tagAttributes.rel
            assertEquals 'text/css', resource.contentType
        }
    }

    @Test
    void testLesscssCompression() {
        String fileName = "file.less"
        def targetFile = mock(File, constructor('/var/file/file.less.css'))
        targetFile.exists().returns(true).stub()

        def originalFile = mock(File)
        def processedFile = mock(File)
        processedFile.getName().returns(fileName).stub()
        processedFile.getAbsolutePath().returns('/var/file/'+fileName).stub()

        lessCompiler.compile(originalFile, targetFile).once()

        mapper.grailsApplication.getConfig().returns([grails:[resources:[mappers:[lesscss:[compress:true]]]]])
        lessCompiler.setCompress(true).once()

        def config = [:]
        def resource = new ResourceMeta(contentType:'',  tagAttributes:[rel:'stylesheet/less'])
        def resourceMock = mock(resource)
        resourceMock.processedFile.returns(processedFile)
        resourceMock.setProcessedFile(targetFile)
        resourceMock.sourceUrl.returns(fileName)
        resourceMock.updateActualUrlFromProcessedFile().once()

        def mockedMapper = mock(mapper)
        mockedMapper.getOriginalFileSystemFile(fileName).returns(originalFile)

        play {
            mapper = new LesscssResourceMapper()

            mockedMapper.map (resourceMock, config)
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
        assertEquals 'foo/bar.less.css', mapper.generateCompiledFileFromOriginal('foo/bar.less')
        assertEquals 'foo/bar.LESS.css', mapper.generateCompiledFileFromOriginal('foo/bar.LESS')
        assertEquals 'foo/./bar.less.css', mapper.generateCompiledFileFromOriginal('foo/./bar.less')
        assertEquals 'foo/less/bar.less.css', mapper.generateCompiledFileFromOriginal('foo/less/bar.less')
    }

}
