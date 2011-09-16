import com.asual.lesscss.LessEngine
import com.asual.lesscss.LessException
import org.grails.plugin.resource.mapper.MapperPhase
import org.codehaus.groovy.grails.plugins.GrailsPluginUtils

/**
 * @author Paul Fairless
 *
 * Mapping file to compile .less files into .css files
 */
import org.codehaus.groovy.grails.commons.ApplicationHolder as AH
import org.codehaus.groovy.grails.commons.GrailsResourceUtils
import org.springframework.util.AntPathMatcher

class LesscssResourceMapper {
    def phase = MapperPhase.GENERATION // need to run early so that we don't miss out on all the good stuff

    static defaultExcludes = ['**/*.js','**/*.png','**/*.gif','**/*.jpg','**/*.jpeg','**/*.gz','**/*.zip']
    static String LESS_FILE_EXTENSION = '.less'
    static String PLUGIN_PREFIX = '/plugins/'
    static final PATH_MATCHER = new AntPathMatcher()


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
        def resourceFile
        if(isPlugin(sourcePath)){
            def pluginName = getPluginFullName(sourcePath)
            def resourcePath = getResourceRelativePath(pluginName, sourcePath)

            for(directory in GrailsPluginUtils.getPluginBaseDirectories()){
                resourceFile = new File(directory + '/' + pluginName + '/' + GrailsResourceUtils.WEB_APP_DIR + '/' + resourcePath)
                if(resourceFile.exists()){
                    return resourceFile
                }
            }
        }

        return new File(GrailsResourceUtils.WEB_APP_DIR + sourcePath)
    }

    private getPluginFullName(sourcePath){
        def pluginMap = PATH_MATCHER.extractUriTemplateVariables(PLUGIN_PREFIX + '{plugin}/**', sourcePath)
        pluginMap.get('plugin')
    }

    private getResourceRelativePath(pluginName, sourcePath){
        PATH_MATCHER.extractPathWithinPattern(PLUGIN_PREFIX+ pluginName +'/**', sourcePath)
    }

    private isPlugin(String original){
        PATH_MATCHER.match(PLUGIN_PREFIX + '**', original)
    }
}
