Water-Flow
==========

Ground water flow simulator written in Java

If you are getting "java.lang.OutOfMemoryError: Java heap space" as an error when you try to run the program:

In Run->Run Configuration find the Name of the class you have been running, select it, click the Arguments tab then add:

-Xms512M -Xmx1524M

to the VM Arguments section

-Xms512M is a minimum heap size of 512 Megabytes

-Xmx5G is a maximum heap size of 5 Gigabytes

If that still doesn't work, you can try amping up the maximum heap size.
Make sure you include units, though! If you don't put M, it will assume you are referring to bytes.
