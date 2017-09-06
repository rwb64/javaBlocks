When developing new spatial planning algorithms for robotic systems that will collaborate with people to complete a task (e.g., moving objects around a room), 
it is important to know how the robot’s spatial movement-based decisions converges or diverges from the human team member’s movement-based decisions. For this 
online simulation-based study, we are only looking at the human decision-making processes involved with moving virtual blocks. Participants will complete a 
simulation by moving a human avatar around a 14 x 14 grid to push blocks from set start locations to set end locations. The entire study should only take 20 
minutes to complete. Path mapping analyses will be conducted to determine if there is one or multiple “human” way(s) of completing the task.

You can download the entire code base and import it to eclipse and build/run from there or follow the directions below to create the jar file and then use the 
html file to access the applet.

I am including the entire eclipse file so if you have Eclipse you can add this project to it and run the applet directly from there.
Otherwise you have to build the jar file and run the html file.

To build the jar file open your cmd prompt

cd to the JavaBlocks directory

jar cf blockGame.jar -C bin .

Open blockGamePage.html with Internet Explorer (IE) or any browser that allows applets. 


Or you can run from online by following these directions. I have used a code signing certificate to sign the jar file so it is able to run on high or very 
high as long as the site is added to the java control panel.

Open Control Panel > Java > Java Control Panel > Security

Exception site list add

https://www.cs.drexel.edu/~rwb64/

Open this site under IE

https://www.cs.drexel.edu/~rwb64/cs530/project/blockGamePage.html

You can go through each of the sets by mving the blocks to their goals or you can do one and skip through the levels by hitting the N 
key. Once you are complete with all 4 sets any levels that were totally completed will show up in the list box and you can choose to watch them.
Once you close the applet the files are zipped and sent via php script to the uploads directory at /home/rwb64/public_html/cs530/project/uploads.

You can download a sample of the zip file that has the first two levels completed for each set. Those are level 0 and 1.
https://www.cs.drexel.edu/~rwb64/cs530/project/uploads/1503956365939.zip