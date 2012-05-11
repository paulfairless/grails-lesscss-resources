class LesscssResourcesGrailsPlugin {
    // the plugin version
    def version = "1.3.0.3"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.3.5 > *"
    // the other plugins this plugin depends on
    def dependsOn = [resources:'1.1.6 > *']
    def loadAfter = ['resources']
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/*.gsp",
            "web-app/less/*",
            "web-app/images/*",
            "web-app/css/*",
            "web-app/js/*"
    ]

    def author = "Paul Fairless"
    def authorEmail = ""
    def title = "Less CSS Resources"
    def description = '''\\
This plugin supports server-side compilation of .less CSS files to their .css counterparts.
'''

    // URL to the plugin's documentation
    def documentation = "https://github.com/paulfairless/grails-lesscss-resources"
    def license = "APACHE"
    def issueManagement = [ system: "github", url: "https://github.com/paulfairless/grails-lesscss-resource/issues" ]
    def scm = [ url: "https://github.com/paulfairless/grails-lesscss-resource" ]

    def doWithSpring = { ->
        org.grails.plugin.resource.CSSPreprocessorResourceMapper.defaultIncludes.add('**/*.less')
        org.grails.plugin.resource.CSSRewriterResourceMapper.defaultIncludes.add('**/*.less')

        org.grails.plugin.resource.BundleResourceMapper.MIMETYPE_TO_RESOURCE_META_CLASS.put('stylesheet/less', org.grails.plugin.resource.CSSBundleResourceMeta)
        List currentTypes = new org.grails.plugin.resource.ResourceModule().bundleTypes
        org.grails.plugin.resource.ResourceModule.metaClass.getBundleTypes = {  currentTypes << 'less' }
        org.grails.plugin.resource.ResourceProcessor.DEFAULT_MODULE_SETTINGS['less'] = [disposition: 'head'  ]
        org.grails.plugin.resource.ResourceTagLib.SUPPORTED_TYPES['less'] = [
            type: "text/css",
            rel: 'stylesheet/less',
            media: 'screen, projection'
        ]

    }
}
