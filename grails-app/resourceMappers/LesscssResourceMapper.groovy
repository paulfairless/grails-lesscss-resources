import org.grails.plugin.resource.mapper.MapperPhase

/**
 * @author Paul Fairless
 *
 * Mapping file to compile .less files into .css files
 */
import org.codehaus.groovy.grails.plugins.support.aware.GrailsApplicationAware
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.lesscss.LessCompiler
import org.lesscss.LessException

class LesscssResourceMapper implements GrailsApplicationAware {

    GrailsApplication grailsApplication
    LessCompiler lessCompiler

    def phase = MapperPhase.GENERATION // need to run early so that we don't miss out on all the good stuff

    static defaultIncludes = ['**/*.less']

    def map(resource, config) {
        if(!lessCompiler) {
            lessCompiler = new LessCompiler()
            lessCompiler.setCompress(grailsApplication.config.grails?.resources?.mappers?.lesscss?.compress == true ?: false)
        }
        File originalFile = resource.processedFile
        File input = getOriginalFileSystemFile(resource.sourceUrl);
        File target = new File(generateCompiledFileFromOriginal(originalFile.absolutePath))

        if (log.debugEnabled) {
            log.debug "Compiling LESS file [${originalFile}] into [${target}], with compress [${grailsApplication.config.grails?.resources?.mappers?.lesscss?.compress}]"
        }
        try {
            lessCompiler.compile input, target
            // Update mapping entry
            // We need to reference the new css file from now on
            resource.processedFile = target
            // Not sure if i really need these
            resource.sourceUrlExtension = 'css'
            resource.contentType = 'text/css'
            resource.tagAttributes?.rel = 'stylesheet'
            resource.updateActualUrlFromProcessedFile()

        } catch (LessException e) {
            log.error("error compiling less file: ${originalFile}", e)
        }

    }

    private String generateCompiledFileFromOriginal(String original) {
         original + '.css'
    }

    private File getOriginalFileSystemFile(String sourcePath) {
        grailsApplication.parentContext.getResource(sourcePath).file
    }
}
