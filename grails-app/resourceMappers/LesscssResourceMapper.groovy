import com.asual.lesscss.LessEngine
import com.asual.lesscss.LessException
import org.grails.plugin.resource.mapper.MapperPhase

/**
 * @author Paul Fairless
 *
 * Mapping file to compile .less files into .css files
 */

class LesscssResourceMapper {

    def grailsApplication
    def resourceService

    def phase = MapperPhase.GENERATION // need to run early so that we don't miss out on all the good stuff
    def operation = "compile"

    static defaultExcludes = ['**/*.js','**/*.png','**/*.gif','**/*.jpg','**/*.jpeg','**/*.gz','**/*.zip']
    static defaultIncludes = ['**/*.less']                                                                                                    
    static String LESS_FILE_EXTENSION = '.less'

    def map(resource, config){
        File originalFile = resource.processedFile
        File target

        if (resource.sourceUrl && originalFile.name.toLowerCase().endsWith(LESS_FILE_EXTENSION)) {
            LessEngine engine = new LessEngine()
            URL input = getOriginalResourceURLForURI(resource.sourceUrl)
            target = new File(generateCompiledFileFromOriginal(originalFile.absolutePath))

            // save current context classloader and set the classloader from the 
            // Grails application, otherwise we have trouble accessing the original 
            // resources when reloading 
            def thread = Thread.currentThread()
            def saveCL = thread.contextClassLoader
            thread.contextClassLoader = grailsApplication.classLoader

            if (log.debugEnabled) {
                log.debug "Compiling LESS file [${originalFile}] into [${target}]"
            }
            try {
                target.text = engine.compile(input)
                
                // Update mapping entry
                // We need to reference the new css file from now on
                resource.processedFile = target
                resource.updateActualUrlFromProcessedFile()
                
                // Change the source extension so the compiled CSS gets into the
                // .css bundle, not a separate .less bundle
                resource.sourceUrlExtension = 'css'
                // fixup the rel attribute
                resource.tagAttributes.rel = 'stylesheet'

            } catch (LessException e) {
                log.error("error compiling less file: ${originalFile}")
                e.printStackTrace()
            } finally {
                // restore saved classloader
                thread.contextClassLoader = saveCL
            }
        }
    }

    private String generateCompiledFileFromOriginal(String original) {
        original.replaceAll(/(?i)\.less/, '_less.css')
    }

    private URL getOriginalResourceURLForURI(String sourceUri) {
        resourceService.getOriginalResourceURLForURI(sourceUri);
    }
}
