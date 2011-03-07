import com.asual.lesscss.LessEngine
import com.asual.lesscss.LessException

class LesscssResourceMapper {
    def priority = 10 // need to run early so that we don't miss out on all the good stuff

    static defaultExcludes = ['**/*.js','**/*.png','**/*.gif','**/*.jpg','**/*.jpeg','**/*.gz','**/*.zip']

    def map(resource, config){
        File f = resource.processedFile
        File target

        def name = f.name
        if (name.toLowerCase().endsWith('.less')) {
            LessEngine engine = new LessEngine()
            File input = f
            target = new File(input.getAbsolutePath().replace('.less', '_less.css'))

            if (log.debugEnabled) {
                log.debug "Compiling LESS file [${input}] into [${target}]"
            }
            try {
                engine.compile input, target
                // Update mapping entry
                // We need to reference the new css file from now on
                resource.processedFile = target
                resource.sourceUrlExtension = 'css'
                resource.actualUrl = resource.originalUrl.replace('.less', '.css')
                resource.contentType = 'text/css'
            } catch (LessException e) {
                log.error("error compiling less file: ${input}")
                e.printStackTrace()
            }
        }
    }
}
