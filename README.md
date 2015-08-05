# Distributed-Content-Harvesting-using-Thread-Pools

Web content harvesting encompassing retrieval and parsing of content is generally a pre-cursor to analysis tasks such as search, placement of ads, and relevance ranking. As of part of this project I developed a distributed content harvester that uses thread-pools to retrieve and parse content. Features that this distributed harvester supports include: duplicate task elimination, task handoffs between distributed harvesters, and configurable thresholds for sizing the thread pools and controlling recursion depths during crawling. The harvester can also detect disjoint sub-graphs and broken links within a particular web domain. (Java, Jericho HTML Parser)

###
1)In this program, I have all my code in two package structures :

cs455/harvester/* and the given jar file is in folder cs455/harvester/lib/

2) The provided make file will compile all the java files.

Enter  $make command in the directory which has the folder cs455

3)The Registry has to be run manually by executing the following command:

	$java -cp .:./lib/jericho-html-3.3.jar cs455/harvester/Crawler 53181 10 http://www.math.colostate.edu/ config.txt

4) The class ThreadPoolManager contains the logic for configuration of thread pool size. [Line:11]
