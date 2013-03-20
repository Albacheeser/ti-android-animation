Android Animation for Titanium
================================

This Titanium Android module makes the [Honeycomb (Android 3.0) animation API][1]
available to Titanium Android apps. It uses Jake Wharton's excellent
[NineOldAndroids][2] library to make the animations available on pre-Honeycomb
devices as well.

Build
=====

The module has so far been built and tested only with Titanium 3.0.2. You build
it the same as you would build any other Titanium module for Android. The build.xml
Ant script is provided, but be sure your machine is setup to build Titanium
Android modules. You can ensure this by creating and building your own test
module using Titanium Studio. In a nutshell, you'll need the Android SDK, the
Android NDK and Python.

Full instructions on how to create and build Titanium modules for Android will
not be provided here. Nor will instructions on how to put modules in to your
Titanium app projects be provided here. Look elsewhere!

If you don't wish to build but just want the packaged module, you should be able
to find recent ZIPs of it at ... (TODO).

Usage
=====

Please see documentation for instructions on how to use the module in
your Titanium app. You can also find code samples under the example/ folder.

Meanwhile here is a very quick example of using the view property animator made
available via this module:

    var animationModule = require("com.billdawson.timodules.animation");
	
	//... setup window / views ...
    
    animationModule.viewPropertyAnimator.animate(myView).xBy(50).yBy(50);

Developed By
============

* Bill Dawson (http://billdawson.com)

License
=======

	The original code herein is...

	Copyright 2013 William Dawson.

	The NineOldAndroids library, upon which this module is built, is...

    Copyright 2012 Jake Wharton.

	Both projects:

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.



 [1]: http://android-developers.blogspot.com/2011/02/animation-in-honeycomb.html
 [2]: http://nineoldandroids.com

