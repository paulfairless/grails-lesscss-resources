import com.asual.lesscss.LessEngine

class LesscssResourceMapper {
    def priority = 10

    static defaultExcludes = ['**/*.js','**/*.png','**/*.gif','**/*.jpg','**/*.jpeg','**/*.gz','**/*.zip']

    def map(resource, config){
        def f = resource.processedFile
        def target

        def name = f.name
        if (name.toLowerCase().endsWith('.less')) {
            LessEngine engine = new LessEngine()
            def input = f
            target = getOutputFile(f)
//            target.text=''
//            if (!target.exists()) {
               // if (log.debugEnabled) {
                    log.error "Compiling LESS file [${input}] into [${target}]"
                //}
                engine.compile input, target

//            } else {
                //if (log.debugEnabled) {
//                    log.error "Skipping rename of less file $f as css file exists"
               // }
//            }
            // Update mapping entry
            // NOTE: we don't change actualUrl because we want to link to the xxxx.css file
            // but transparently serve up the xxx.css.gz file
            resource.processedFile = target
            resource.sourceUrlExtension = 'css'
            resource.actualUrl = resource.originalUrl.replace('.less', '.css')
            resource.contentType = 'text/css'

        }
    }


    private File getOutputFile(File input) {
        def inputStr = input as String
        new File(inputStr.substring(0, inputStr.size() - '.less'.length()) + ".css")
    }
}
