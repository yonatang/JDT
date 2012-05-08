Delaunay Triangulation 3D
 
Introduction
This component of the java Delaunay Triangulation project displays a three-dimensional Delaunay Triangulation. The application, based on Java3D, displays a 3D virtual world containing the triangulation, it allows the user to explore the 3D triangulation and dynamically manipulate it.
The application's main features are:
•	3D display of Delaunay Triangulations as Surface \ Grid \ points
•	Dynamic manipulation: adding or removing a triangulation point
•	Support for loading/saving triangulations
•	Full mouse and keyboard control of the virtual world

1.	Software Requirements 
The application requires the installation of Java3D package available at http://java.sun.com/javase/technologies/desktop/java3d/

2.	User Guide
The following sections describes how to use the 3D Delaunay Triangulation application

2.1 Running the application
Double click on 3djdt.jar (under gui3D\binary) to start the application. The application does not require any input to start.

2.2 Virtual World navigation
The application allows the user to tour the 3D world, use the mouse and keyboard buttons to move around.
Mouse Controls: Click and hold the right mouse button to move, left mouse button to rotate and the middle mouse button to zoom in and out.
Keyboard Controls: Use the arrow keys to move, up and down to move forward and backward, left and right to rotate the display. Use page up to look down and page up to look up.

2.3 Opening an input file
The application allows the user to load input from tsin and smf files, choose File=>Open to open and display a triangulation from file.

2.4 Saving a triangulation
The application allows the user to save the current triangulation. Choose File=>Save smf /Save tsin to save the triangulation in the desired format.

2.5 Changing display settings
The user can change the display settings of the triangulation. Choose View=>Surface to view the triangulation as a surface. Choose View=>Grid to switch to a grid display and choose View=>points to switch to a point display.

2.6 Adding a point
The user can add a point to the triangulation dynamically. Choose Tools=>Add point to add a point. After choosing the option from the menu click on the location in which you would like to add a point.  You will than need to specify the height of the point.

2.7 Deleting a point
In the order to delete a point from the triangulation dynamically choose Tools=>Delete point. After choosing the option from the menu you will need to select a point. Select a point by clicking on its green sphere. The point will be deleted from the triangulation after clicking on it.

2.8 Scaling Height
In order to scale the height of the triangulation choose Tools=>Scale Z. Enter a value for the scale when prompt to do so.

