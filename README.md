first make sure to enter in program argumetn port number for server : example (5000)
run->edit config->choose server application->in program argument :5000
run->edit config->choose manager application->in program argument :localhost 5000
for opencv :
Download OpenCV: Go to the OpenCV releases page and download the latest version.
Extract the downloaded file: Once the download is complete, extract the contents of the file to a location on your computer.
Set up OpenCV in IntelliJ IDEA:
Open IntelliJ IDEA and create a new project or open an existing one.
Go to File > Project Structure.
In the Project Structure dialog, select Modules from the side panel and then select the Dependencies tab.
Click the + icon on the right to add a dependency, then select JARs or directories....
Navigate to the location where you extracted OpenCV, and go to build/java. Select the opencv-***.jar file and click OK3.
Now, you also have to add the Native Library Location. To do that, double click on the opencv-***.jar (currently selected above).
in VM from modify option add :-Djava.library.path=C:\opencv\build\java\x64 (change it if your system 32)
Click OK to close the Project Structure dialog.
