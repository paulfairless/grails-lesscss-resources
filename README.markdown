#less CSS Resource plugin#
This plugin is designed to optimize the use of <a href="http://www.lesscss.org">.less</a> css files. The processing will compile specified .less files into their .css counterparts, and place the css into the processing chain to be available to the other resource plugin features. The plugin uses the <a href="http://www.asual.com/lesscss/">Asual Less Engine</a> to compile the .less files using Mozilla Rhino. The plugin uses the <a href="http://www.grails.org/plugin/resources">Resources plugin</a> and plays nicely with both the zipped and cached resources plugins.

LESS extends CSS with dynamic behavior such as variables, mixins, operations and functions. <a href="http://www.lesscss.org">Read more</a>


##Installation##
<pre><code>grails install-plugin lesscss-resources</code></pre>

##Usage##
An Example of using both .less and .css files together in a bundle
<pre><code>'style' {
        resource url:'less/test.less',attrs:[rel: "stylesheet/less", type:'css'], bundle:'bundle_style'
        resource url:'css/normal.css'
        resource url:'css/normal2.css'
    }
</code></pre>

#Import#
LESS supports importing of other less/css files. This is supported in lesscss-resources
<pre><code>@import 'imported_style.less';
</code></pre>
Note that the files to be imported are not part of the resources bundle, so any changes will not trigger a rebuild of the resource.

###Required Settings for LESS###
<ul>
<li><b>url</b>: The location of the .less file</li>
<li><b>attrs[rel]</b>: should be set to stylesheet/less for compatibility reasons</li>
<li><b>attrs[type]</b>: must be set to css for resources to process</li>
<li><b>bundle</b>: Must be set as will not default correctly. To add to default bundle use 'bundle_<module name>"</li>
</ul>


See the <a href="http://www.grails.org/plugin/resources">Resources plugin</a> for more details on available configurations
##Changelog##

1.3.0 - Breaking Chnage - Asual LessCSS compiler has been replaced with <a href="https://github.com/marceloverdijk/lesscss-java">lesscss-java</a>

##Issues##
<ul>
    <li>Must specify the default bundle manually as this is calculated based on file extension by default.</li>
    <li>When debug is switched on there is currently no way to fall back to the standard LESS javascript support. The less files will be rendered unprocessed</li>
</ul>

##Special Thanks##
The guys at <a href="http://www.asual.com/">Asual<a> who created the lesscss complier, with does all the heavy lifting.