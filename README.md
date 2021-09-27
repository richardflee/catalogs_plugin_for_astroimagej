## catalogs_plugin_for_astroimagej

### AstroImageJ plugin to query AAVSO VSP or APASS9 databases

README file for Catalogs_Plugin astroimage java plugin to create radec files.<br/>
Catalogs_Plugin is an extension of VSP_DEMO plugin https://github.com/richardflee/vsp_astroimagej_plugin <br/>

See _Introduction to Catalogs.pdf_ for an overview of the software.

**Software Notes**

Catalogs_Plugin is an ImageJ plugin, ref: https://imagej.net/Developing_Plugins_for_ImageJ_1.x,
developed to support AstroImageJ photometry software.
 It  is a Java 8 application, developed on Win10 OS using Eclipse IDE and 
Maven-based build automation. It uses an edited POM.xlm file supplied with Process_Pixels - ImageJ demo software.  <br/>

OS: The plugin runs  on Win10 and Linux-Ububntu. Should also run on MacOS but this is not tested. 
Currently, Windows and Linux AIJ  downloads are bundled with Java 7.  Refer VSP_DEMO page for work-around pending AJJ update to Java8 or later.<br/>

The user form is configured as  a modal dialog to block user access to the AIJ toolbar. In testing, accessing
AIJ toolbar with the dialog still open could cause a complete system crash, requiring a power cycle to reset. <br/>
