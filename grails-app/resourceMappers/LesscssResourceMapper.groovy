import com.asual.lesscss.LessEngine
import com.asual.lesscss.LessException
import org.grails.plugin.resource.mapper.MapperPhase

/**
 * @author Paul Fairless
 *
 * Mapping file to compile .less files into .css files
 */
import org.codehaus.groovy.grails.plugins.support.aware.GrailsApplicationAware
import org.codehaus.groovy.grails.commons.GrailsApplication

class LesscssResourceMapper implements GrailsApplicationAware {

    GrailsApplication grailsApplication

    def phase = MapperPhase.GENERATION // need to run early so that we don't miss out on all the good stuff

    static defaultIncludes = ['**/*.less']
    static String LESS_FILE_EXTENSION = '.less'

    def map(resource, config){
        File originalFile = resource.processedFile
        File target

        if (resource.sourceUrl && originalFile.name.toLowerCase().endsWith(LESS_FILE_EXTENSION)) {
            LessEngine engine = new LessEngine()
            File input = getOriginalFileSystemFile(resource.sourceUrl);
            target = new File(generateCompiledFileFromOriginal(originalFile.absolutePath))

            if (log.debugEnabled) {
                log.debug "Compiling LESS file [${originalFile}] into [${target}]"
            }
            try {
                engine.compile input, target
                // Update mapping entry
                // We need to reference the new css file from now on
                resource.processedFile = target
                // Not sure if i really need these
                resource.sourceUrlExtension = 'css'
                resource.actualUrl = generateCompiledFileFromOriginal(resource.originalUrl)
                resource.sourceUrl = resource.actualUrl
                resource.contentType = 'text/css'
                resource.tagAttributes.rel = 'stylesheet'
            } catch (LessException e) {
                log.error("error compiling less file: ${originalFile}")
                e.printStackTrace()
            }
        }
    }

    private String generateCompiledFileFromOriginal(String original) {
         original.replaceAll(/(?i)\.less/, '_less.css')
    }

    private File getOriginalFileSystemFile(String sourcePath) {
        grailsApplication.parentContext.getResource(sourcePath).file
    }
}
