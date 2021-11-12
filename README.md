## catalogs_plugin_for_astroimagej

### AstroImageJ plugin to query AAVSO VSP or APASS9 databases

README file for Catalogs_Plugin V1.0d astroimage java plugin to create radec files.<br/>
Catalogs_Plugin is an extension of VSP_DEMO plugin https://github.com/richardflee/vsp_astroimagej_plugin <br/>

See _Introduction to Catalogs.pdf_ for an overview of the software and brief version notes.

**Software Notes**

Catalogs_Plugin is an ImageJ plugin, ref: https://imagej.net/Developing_Plugins_for_ImageJ_1.x,
developed to support AstroImageJ photometry software.
 It  is a Java 8 application, developed on Win10 OS using Eclipse IDE and 
Maven-based build automation. 
ThePOM .xlm is an edited version of the POM file supplied with Process_Pixels - ImageJ demo software.  <br/>

OS: The plugin runs  on Win10 and Linux-Ubuntu. While the software should run on MacOS this is not tested. 
As of Sept-2021, Windows and Linux AIJ  downloads are bundled with Java 7.  Refer to the VSP_DEMO page for work-around pending AIJ update to Java 8 or later.<br/>

The user form is configured as  a modal dialog to block user access to the AIJ toolbar. In testing, accessing
AIJ toolbar with the dialog still open could cause a system crash,. <br/>

**Install and Remove Plugin JAR file**

To run catalogs_plugin from AstroImageJ:</br>
- download file: jar/catalogs_plugin_ONEJAR-1.00d.jar from github repo and copy to ./AstroImageJ/plugins folder.</br>
- open AstroImageJ and from the toolbar, select Plugins => Catalogs Plugin.</br>

To remove the plugin, delete file: ./AstroImageJ/Plugins/catalogs_plugin_ONEJAR-1.00a.jar.

