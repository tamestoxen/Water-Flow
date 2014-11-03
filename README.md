Water-Flow
==========

Simulate ground water flow on a 1 acre farm, down to 1 meter deep. This takes gravity and plant water consumption into account. Some cool features include simulated rain, water flow between multiple farms, and use of Google's topographical data to simulate actual pieces of land (along with randomly generated farms if you don't want to use an actual location). This was originally part of a project started in my Design of Large Programs class. The goal was to create a game where players own and operate farms that are next to each other. You could grow and sell crops, along with buying or selling water rights. My part of the assignment was to do the simulation of the water flowing. Unfortunately, the class was not able to complete the project by the end of the semester. Luckily, my portion can run all by itself if you want it to.

If you are getting "java.lang.OutOfMemoryError: Java heap space" as an error when you try to run the program:

In Run->Run Configuration find the Name of the class you have been running, select it, click the Arguments tab then add:

-Xms512M -Xmx1524M

to the VM Arguments section

-Xms512M is a minimum heap size of 512 Megabytes

-Xmx1536G is a maximum heap size of 1.5 Gigabytes

If that still doesn't work, you can try amping up the maximum heap size.
Make sure you include units, though! If you don't put M, it will assume you are referring to bytes.
