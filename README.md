# SplunkLog for Minecraft

![Dashboard example](https://i.imgur.com/PmH1U2V.png)

## Requirements

- Server to host [Splunk Enterprise](https://www.splunk.com/en_us/download/splunk-enterprise.html) (15GB storage recommended)
- Server to host [Minecraft Spigot](https://www.spigotmc.org/) server and [Splunk Universal Forwarder](https://www.splunk.com/en_us/download/universal-forwarder.html) (1GB storage recommended)

For this project, I decided to use AWS (free tier) and set up two EC2 instances. You can read more about the AWS free tier [here](https://aws.amazon.com/free/).

## Instructions

1. Download and setup [Splunk Enterprise](https://www.splunk.com/en_us/download/splunk-enterprise.html) on a server. Once Splunk has been set up, go into `$SPLUNK_HOME/bin/` and run `./splunk start`. Then open up the Splunk Web page (located at `http://<SERVER-IP>:8000`), and under `Settings > Forwarding and Receiving > Configure receiving > New Receiving Port`, add a listener on port `9997`. Detailed setup instructions can be found [here](https://docs.splunk.com/Documentation/MSExchange/4.0.1/DeployMSX/InstallaSplunkIndexer).
![Step 1](https://docs.splunk.com/images/5/52/Exch_31_setupfwdrecv.png)

2. Using a separate server, follow the instructions [here](https://www.spigotmc.org/wiki/buildtools/) to generate a `spigot.jar` file, and then follow the instructions [here](https://www.spigotmc.org/wiki/spigot-installation/) to use the `spigot.jar` file you just made to generate the server files. On the first run, you will need to modify the `eula.txt` file and set `eula=true`, and then run the server again. 
![Step 2](https://i.imgur.com/bVoSzVT.png)

3. Download the [SuperLog plugin](https://www.spigotmc.org/resources/superlog-async-1-7-1-15.65399/), and then place it into the `$MC_SERVER_HOME/plugins/` directory, and then restart the server. You should now see an `$MC_SERVER_HOME/plugins/SuperLog/` directory, in which you'll find a `config.yml` file. Copy the contents from [here](https://raw.githubusercontent.com/sidward35/SplunkLog/master/plugin/src/main/resources/config.yml) and paste it into the `config.yml` file (replace everything that was in the file previously). Restart the server.
![Step 3](https://i.imgur.com/ujtOtEw.png)

4. On the same system running the Minecraft server, but outside the Minecraft `$MC_SERVER_HOME` folder, download and setup the [Splunk Universal Forwarder](https://www.splunk.com/en_us/download/universal-forwarder.html), following the instructions [here](https://docs.splunk.com/Documentation/Forwarder/latest/Forwarder/HowtoforwarddatatoSplunkEnterprise).

5. Go into `$SPLUNK_HOME/bin/` and run `./splunk add forward-server <host>:9997` (replace `<host>` with the IP address of the Splunk Enterprise instance you set up in Step 1).

6. Staying in the same directory, run `./splunk add monitor $MC_SERVER_HOME/plugins/SuperLog/logs/ -sourcetype minecraft_logs` so that all logs generated by the Minecraft server get sent to the Splunk Enterprise instance (if the path doesn't exist, run `mkdir logs` inside `$MC_SERVER_HOME/plugins/SuperLog/` to manually create it). Finally, run `./splunk start` to start the Universal Forwarder. You're almost done! Your Minecraft server should now be sending logs of player activity to Splunk Enterprise.

7. After playing on the server for a bit, head over to Splunk Web (`http://<ENTERPRISE-SERVER-IP>:8000`) and login with the credentials you set up in Step 1 (username `admin` and password `changeme` by default). Extract fields from the logged events to define date/time, player name, action, etc.
![Step 7](https://i.imgur.com/gR6SWK6.png)

8. And the final piece of the puzzle: setup your dashboard! Make searches using the fields you just extracted, and create a dashboard, adding searches to it as panels.

9. You can log even more events by checking out the [SuperLog documentation](http://superlog.andross.fr/#doc) and scrolling down to the `Events` section. There you will find all the different kinds of actions and events that can be logged from Minecraft (there's a ton of them), as well as the code that needs to be added to the `config.yml` from Step 3, in order to get those events logged.
![Step 9](https://i.imgur.com/yZ0hGAe.png)

## References

- [Splunk Documentation](https://docs.splunk.com/Documentation)
- [Minecraft Spigot](https://www.spigotmc.org/)
- [SuperLog Documentation](http://superlog.andross.fr/#doc)
