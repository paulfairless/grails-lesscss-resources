<h1>less CSS Resource plugin</h1>
<p>This plugin is designed to optimise the use of .less css files. The Processing will compile the .less file into it's .css counterprt, and place the css into the processing chain to be available to the other resource plugin features.</p>


<h2>Usage</h2>
<pre><code>'less' {
        resource url:'less/test.less',attrs:[rel: "stylesheet/less", type:'css'], bundle:'bundle_less'
        resource url:'css/normal.css'
        resource url:'css/normal2.css'
    }
</code></pre>

<h2>Issues</h2>
<ul>
    <li>default bundle uses file extension not type</li>
    <li>does not compile in grails 1.3.7</li>
    <li>debug off does not honour tag attributes</li>
    <li>no mechanism to toggle the js in debug mode</li>
</ul>