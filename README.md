# DiaSynth
Currently all parts works but it is still under construction (By december it should be "completed").<br/>
<br/>
It is continuation of project Music-analyser (https://github.com/RadStr/Music-Analyser).<br/>
<br/>
Is is a school project which contains, audio player, audio synthesizer (using diagrams) and audio analyzer contained in one GUI. Written in java.<br/>
It is using 2 libraries with following licenses.<br/>
The MP3SPI package (http://www.javazoom.net/mp3spi/sources.html) is licensed under LGPL.<br/>
Which are the files in libs directory under the names:<br/>
-mp3spi1.9.5.jar<br/>
-tritonus_share.jar<br/>
-jl1.0.1.jar<br/>
<br/>
JTransforms (https://sites.google.com/site/piotrwendykier/software/jtransforms) is distributed under the terms of the BSD-2-Clause license.<br/>
Copyright is found in the LICENSE file taken from https://github.com/wendykierp/JTransforms<br/>
Which are the files in libs directory under the names starting with JTransforms<br/>
<br/>
As far as I understand it including the LICENSE file with copyright for JTransforms should be enough to cover BSD-2-Clause.<br/>
<br/><br/>
The libraries (MP3SPI and tritonus) are discontinued for long time. The MP3SPI for around 10 years, the JLayer for 12 years and tritonus even for 17. So this next part really isn't necessary.<br/>
To cover the LGPL. It is enough to just say which files from library are under that license and tell user where to download the newest version and how to replace them. Replacing is simple just replace the 3 .jar files from MP3SPI package. And to download the newest version just go to the javazoom link where you will find the JLayer and MP3SPI jar files and check http://www.tritonus.org/ for new version of tritonus lib.<br/>
<br/>
As for license for whole program, currently undecided, but I guess I will just go with MIT, or any of the above, since the program still isn't done I will decide when I will be doing the last few fixes.
