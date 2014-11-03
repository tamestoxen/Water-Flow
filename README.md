Water-Flow
==========

Simulate ground water flow on a 1 acre farm, down to 1 meter deep. This takes gravity and plant water consumption into account. Some cool features include simulated rain, water flow between multiple farms, use of Google's topographical data to simulate actual pieces of land (along with randomly generated farms if you don't want to use an actual location), and threading in order to optimize my running time. This was originally part of a project started in my Design of Large Programs class. The goal was to create a game where players own and operate farms that are next to each other. You could grow and sell crops, along with buying or selling water rights. My part of the assignment was to do the simulation of the water flowing. Unfortunately, the class was not able to complete the project by the end of the semester. Luckily, my portion was completed and can run all by itself. I've only included portions of the project that are necessary for the flow simulation to work.

If you are getting "java.lang.OutOfMemoryError: Java heap space" as an error when you try to run the program, try adding:

-Xmx2G

to the VM Arguments

-Xmx2G is a maximum heap size of 2 Gigabytes. If that still doesn't work, you can try amping up the maximum heap size even more. Make sure you include units, though. If you don't put M or G, it will assume you are referring to bytes.
