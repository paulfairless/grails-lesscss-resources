import com.asual.lesscss.LessEngine
import com.asual.lesscss.LessException

class LesscssResourceMapper {
    def priority = 10 // need to run early so that we don't miss out on all the good stuff

    static defaultExcludes = ['**/*.js','**/*.png','**/*.gif','**/*.jpg','**/*.jpeg','**/*.gz','**/*.zip']
    static String LESS_FILE_EXTENSION = '.less'

    def map(resource, config){
        File originalFile = resource.processedFile
        File target

        if (originalFile.name.toLowerCase().endsWith(LESS_FILE_EXTENSION)) {
            LessEngine engine = new LessEngine()
            File input = originalFile
            target = new File(generateCompiledFileFromOriginal(originalFile.absolutePath))

            if (log.debugEnabled) {
                log.debug "Compiling LESS file [${originalFile}] into [${target}]"
            }
            try {
                engine.compile originalFile, target
                // Update mapping entry
                // We need to reference the new css file from now on
                resource.processedFile = target
                // Not sure if i really need these
                resource.sourceUrlExtension = 'css'
                resource.actualUrl = generateCompiledFileFromOriginal(resource.originalUrl)
                resource.contentType = 'text/css'
                resource.tagAttributes.rel = 'stylesheet'
            } catch (LessException e) {
                log.error("error compiling less file: ${originalFile}")
                e.printStackTrace()
            }
        }
    }

    private String generateCompiledFileFromOriginal(String original) {
         original.replace('.', '_')+".css"
    }
}
