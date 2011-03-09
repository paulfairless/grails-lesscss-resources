#less CSS Resource plugin#
This plugin is designed to optimise the use of <a href="http://www.lesscss.org">.less</a> css files. The Processing will compile specified .less files into their .css counterprt, and place the css into the processing chain to be available to the other resource plugin features. The plugin uses the <a href="http://www.asual.com/lesscss/">Asual Less Engine</a> to compile the .less files using Mozilla Rhino.

LESS extends CSS with dynamic behavior such as variables, mixins, operations and functions. <a href="http://www.lesscss.org">Read more</a>


##Installation##
coming soon ...
<!--<pre><code>grails install-plugin lesscss-resources</code></pre>-->

##Usage##
An Example of using both .less and .css files together in a bundle
<pre><code>'style' {
        resource url:'less/test.less',attrs:[rel: "stylesheet/less", type:'css'], bundle:'bundle_style'
        resource url:'css/normal.css'
        resource url:'css/normal2.css'
    }
</code></pre>
###Required Settings for LESS###
<ul>
<li><b>url</b>: The location of the .less file</li>
<li><b>attrs[rel]</b>: should be set to stylesheet/less for compatibility reasons</li>
<li><b>attrs[type]</b>: must be set to css for resources to process</li>
<li><b>bundle</b>: Must be set as will not default correctly. To add to default bundle use 'bundle_<module name>'</li>
</ul>

See the <a href="http://www.grails.org/plugin/resources">Resources plugin</a> for more details on available configurations

##Issues##
<ul>
    <li>Must specify the default bundle manually as this is calculated based on file extension by default.</li>
    <li>When debug is switched on there is currently no way to fall back to the standard LESS javascript support. The less files will be rendered unprocessed</li>
</ul>