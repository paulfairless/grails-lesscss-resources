
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
import org.grails.plugin.resource.AggregatedResourceMeta

class LesscssResourceMapper implements GrailsApplicationAware {

    GrailsApplication grailsApplication
    LessCompiler lessCompiler

    def phase = MapperPhase.GENERATION // need to run early so that we don't miss out on all the good stuff

    static defaultIncludes = ['**/*.less']

    def map(resource, config) {

        //do want to try and do anything to a bundled resource
        if (resource instanceof AggregatedResourceMeta){
            return
        }

        if(!lessCompiler) {
            lessCompiler = new LessCompiler()
            lessCompiler.setCompress(grailsApplication.config.grails?.resources?.mappers?.lesscss?.compress == true ?: false)
        }
        File originalFile = resource.processedFile
        File lessFile = getOriginalFileSystemFile(resource.sourceUrl);
        File cssFile = new File(generateCompiledFileFromOriginal(originalFile.absolutePath))

        try {
            log.debug "Compiling LESS file [${lessFile}] into [${cssFile}]"
            lessCompiler.compile (lessFile, cssFile)

            resource.processedFile = cssFile
            resource.contentType = 'text/css'
            resource.sourceUrlExtension = 'css'
            resource.tagAttributes.rel = 'stylesheet'
            resource.updateActualUrlFromProcessedFile()
        } catch (Exception e) {
            log.error("Error compiling less file: ${lessFile}", e)
        }

    }

    private String generateCompiledFileFromOriginal(String original) {
         original + '.css'
    }

    private File getOriginalFileSystemFile(String sourcePath) {
        grailsApplication.parentContext.getResource(sourcePath).file
    }
}
